package com.example.facechat

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : AppCompatActivity() {
    private val galleryPick = 100

    private val db by lazy {
        FirebaseDatabase.getInstance()
            .reference
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    var id: String? = ""
    private var imageUri:Uri?=null
    private var userProfileImgRef: StorageReference? = null
    private var firebaseStore:FirebaseStorage?=null
    //private var myUrl = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        id = auth.currentUser?.uid
        firebaseStore= FirebaseStorage.getInstance()
        userProfileImgRef = FirebaseStorage.getInstance().reference
           // .child("Profile Images")
        

        settings_profile_image.setOnClickListener {
            val galleryIntent=Intent()
            galleryIntent.type="image/*"
            galleryIntent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"),galleryPick)
//            CropImage.activity()
//                .setAspectRatio(1, 1)
//                .start(this)
        }

        save_settings_btn.setOnClickListener {
            val getUserName = username_settings.text.toString()
            val getUserStatus = bio_settings.text.toString()
            if (getUserName == "")
            {
                username_settings.error="UserName Is Mandatory"
            }
            else if (getUserStatus=="")
            {
                bio_settings.error="Bio is Mandatory"
            }
            else if (imageUri!=null) {
                    val filepath = userProfileImgRef!!.child("Profile Images/-"+id!! + ".jpg")
                    var uploadTask: StorageTask<*>
                    uploadTask = filepath.putFile(imageUri!!)
                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation filepath.downloadUrl
                    }).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result.toString()
                            val dbRef = FirebaseDatabase.getInstance().reference.child("Users")

                            val profileMap = HashMap<String, Any>()
                            profileMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                            profileMap["name"] = getUserName
                            profileMap["status"] = getUserStatus
                            profileMap["image"] = downloadUrl

                            dbRef.child(id!!).updateChildren(profileMap)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        startActivity(Intent(this, ContactsActivity::class.java))
                                        Toast.makeText(this, "Settings Updated", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                        }
                    }
                }
             else
                {
                    //Toast.makeText(this,"Please Upload an Image",Toast.LENGTH_SHORT).show()
                    savInfoOnlyWithoutImage()
                }

            }
        retrieveUserInfo()
        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==galleryPick &&requestCode== Activity.RESULT_OK)
        {
            //&&data!=null
           // CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
//            val result=CropImage.getActivityResult(data)
//            imageUri=result.uri
//            settings_profile_image.setImageURI(imageUri)
            if (data==null||data.data==null)
            {
                return
            }
            imageUri=data.data
            try {
                val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
                settings_profile_image.setImageBitmap(bitmap)

            }
            catch (e:IOException)
            {
                e.printStackTrace()
            }
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
            val profileMap=HashMap<String,Any>()
            profileMap["uid"] = id!!
            profileMap["name"] = getUserName
            profileMap["status"]=getUserStatus
            db.child("Users")
                .child(id!!).setValue(profileMap).addOnCompleteListener { task->
                if (task.isSuccessful) {
                    startActivity(Intent(this,ContactsActivity::class.java))
                    Toast.makeText(this,"Settings Updated",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun retrieveUserInfo(){
        db.child("Users")
            .child(id!!).addValueEventListener(object :ValueEventListener{
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
