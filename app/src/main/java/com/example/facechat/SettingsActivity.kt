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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
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

            if(settings_profile_image.drawable.constantState==resources.getDrawable(R.drawable.profile_image).constantState)
            {

            }
            else if (getUserName == "")
            {
                username_settings.error="UserName Is Mandatory"
            }
            else if (getUserStatus=="")
            {
                bio_settings.error="Bio is Mandatory"
            }
            else{
                    val filepath=userProfileImgRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                val uploadTask=filepath.putFile(imageUri!!)
                uploadTask.continueWithTask(object :Continuation<UploadTask.TaskSnapshot, Task<Uri>>{
                    override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                        if (!task.isSuccessful)
                        {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return filepath.downloadUrl
                    }

                }).addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        val downloadUrl=it.result.toString()
                        val profileMap=HashMap<String,String>()
                        profileMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                        profileMap["name"] = getUserName
                        profileMap["status"]=getUserStatus
                        profileMap["image"]=downloadUrl

                        db.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(
                            profileMap as Map<String, Any>
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==galleryPick &&requestCode== Activity.RESULT_OK&&data!=null)
        {
            imageUri=data.data
            settings_profile_image.setImageURI(imageUri)
        }
    }
}
