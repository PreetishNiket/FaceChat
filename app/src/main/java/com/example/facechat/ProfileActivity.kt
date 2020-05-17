package com.example.facechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private var receiverUserId:String=""
    private var receiverUserImage:String=""
    private var receiverUserName:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        receiverUserId=intent.extras!!.get("visit_user_id").toString()
       // receiverUserImage=intent.extras!!.get("profile_image").toString()
        receiverUserName=intent.extras!!.get("profile_name").toString()
       // Picasso.get().load(receiverUserImage).into(background_profile_view)
        name_profile.text=receiverUserName

    }
}
