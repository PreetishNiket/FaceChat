package com.example.facechat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : AppCompatActivity() {
    private val galleryPick = 100
    private val storage by lazy {
        FirebaseStorage.getInstance()
            .reference
    }
    private val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Users")
    }

     private var imageUri:Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settings_profile_image.setOnClickListener{
            val galleryIntent=Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type="image/*"
            startActivityForResult(galleryIntent,galleryPick)
        }
        //First Branch
        val userProfileImgRef= storage.child("Profile Images")

        save_settings_btn.setOnClickListener {
            val getUserName=username_settings.text.toString()
            val getUserStatus=bio_settings.text.toString()
            //settings_profile_image.drawable.constantState==resources.getDrawable(R.drawable.profile_image).constantState
            when {
                imageUri==null -> {
                    db.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(dataSnapshot: DatabaseError) {
                        }
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                           if (dataSnapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).hasChild("image"))
                           {
                               savInfoOnlyWithoutImage()
                           }
                            else{
                               Toast.makeText(this@SettingsActivity,"Please Select Profile Photo",Toast.LENGTH_SHORT).show()
                           }
                        }

                    })
                }
                getUserName == "" -> {
                    username_settings.error="UserName Is Mandatory"
                }
                getUserStatus=="" -> {
                    bio_settings.error="Bio is Mandatory"
                }
                else -> {
                    val filepath=userProfileImgRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    val uploadTask=filepath.putFile(imageUri!!)

                    uploadTask.continueWithTask(object :Continuation<UploadTask.TaskSnapshot, Task<Uri>>{
                        override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            return filepath.downloadUrl
                        }

                    }).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val downloadUrl=it.result.toString()
                            val profileMap=HashMap<String,String>()
                            profileMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                            profileMap["name"] = getUserName
                            profileMap["status"]=getUserStatus
                            profileMap["image"]=downloadUrl

                            db.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(profileMap).addOnCompleteListener { task->
                                if (task.isSuccessful) {
                                    startActivity(Intent(this,ContactsActivity::class.java))
                                    Toast.makeText(this,"Settings Updated",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        retrieveUserInfo()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==galleryPick &&requestCode== Activity.RESULT_OK&&data!=null)
        {
            imageUri=data.data
            settings_profile_image.setImageURI(imageUri)
        }
    }
    private fun savInfoOnlyWithoutImage() {
        val getUserName=username_settings.text.toString()
        val getUserStatus=bio_settings.text.toString()
        if (getUserName == "")
        {
            username_settings.error="UserName Is Mandatory"
        }
        else if (getUserStatus=="")
        {
            bio_settings.error="Bio is Mandatory"
        }
        else{
            //Progress Dialog video6 26:30
            val profileMap=HashMap<String,String>()
            profileMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
            profileMap["name"] = getUserName
            profileMap["status"]=getUserStatus
            db.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(profileMap).addOnCompleteListener { task->
                if (task.isSuccessful) {
                    startActivity(Intent(this,ContactsActivity::class.java))
                    Toast.makeText(this,"Settings Updated",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun retrieveUserInfo(){
        db.child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                {
                    val imageDb=dataSnapshot.child("image").value.toString()
                    val nameDb=dataSnapshot.child("name").value.toString()
                    val bioDb=dataSnapshot.child("status").value.toString()
                    username_settings.setText(nameDb)
                    bio_settings.setText(bioDb)
                    Picasso.get().load(imageDb).placeholder(R.drawable.profile_image).into(settings_profile_image)
                }
            }
        })
    }
}
