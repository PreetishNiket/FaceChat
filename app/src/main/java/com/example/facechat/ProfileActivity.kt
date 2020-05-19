package com.example.facechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private var receiverUserId:String=""
    private var receiverUserImage:String=""
    private var receiverUserName:String=""
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    var id: String? = ""
    var currentStats:String="new"

    private val friendRequestRef by lazy {
        FirebaseDatabase.getInstance()
            .reference .child("Friend Requests")
    }
    private val contactsRef by lazy {
        FirebaseDatabase.getInstance()
            .reference .child("Contacts")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        receiverUserId=intent.extras!!.get("visit_user_id").toString()
       // receiverUserImage=intent.extras!!.get("profile_image").toString()
        receiverUserName=intent.extras!!.get("profile_name").toString()
       // Picasso.get().load(receiverUserImage).into(background_profile_view)
        name_profile.text=receiverUserName

        id = auth.currentUser?.uid    //senderUserId

        manageClickEvents()
    }

    private fun manageClickEvents() {
        friendRequestRef.child(id!!).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserId))
                {
                    var requestType=dataSnapshot.child(receiverUserId).child("request_type").value.toString()
                    if (requestType=="sent")
                    {
                        currentStats="request_sent"
                        add_friend.text="Cancel Friend Request"

                    }
                    else if (requestType=="received")
                    {
                        currentStats="request_received"
                        add_friend.text="Accept Friend Request"
                        decline_friend_request.visibility=View.VISIBLE
                                decline_friend_request.setOnClickListener {
                                    cancelFriendRequest()
                                }
                    }
                }
                else
                {
                    contactsRef.child(id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.hasChild(receiverUserId))
                            {
                                currentStats="friends"
                                add_friend.text="Delete Contact"
                            }
                            else
                            {
                                currentStats="new"
                            }
                        }
                    })
                }
            }
        })

        if(id==receiverUserId)
        {
            add_friend.visibility=View.GONE
            name_profile.text="YOU"
        }
        else{
            add_friend.setOnClickListener {
                when (currentStats) {
                    "new" -> {
                        sendFriendRequest()
                    }
                    "request_sent" -> {
                        cancelFriendRequest()
                    }
                    "request_received" -> {
                        acceptFriendRequest()
                    }
                }
            }
        }
    }

    private fun acceptFriendRequest() {
        //25:12  8
        contactsRef.child(id!!).child(receiverUserId)
            .child("Contact").setValue("Saved")
            .addOnCompleteListener { it ->
                if (it.isSuccessful)
                {
                    contactsRef.child(receiverUserId).child(id!!)
                        .child("Contact").setValue("Saved")
                        .addOnCompleteListener {task->
                            if (task.isSuccessful)
                            {
                                friendRequestRef.child(id!!).child(receiverUserId)
                                    .removeValue()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful)
                                        {
                                            friendRequestRef.child(receiverUserId).child(id!!)
                                                .removeValue()
                                                .addOnCompleteListener {taskId->
                                                    if (taskId.isSuccessful)
                                                    {
                                                        add_friend.text="Delete Contact"
                                                        decline_friend_request.visibility=View.GONE
                                                        currentStats="friends"
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
    }

    private fun cancelFriendRequest() {
        friendRequestRef.child(id!!).child(receiverUserId)
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                    friendRequestRef.child(receiverUserId).child(id!!)
                        .removeValue()
                        .addOnCompleteListener {task->
                            if (task.isSuccessful)
                            {
                                add_friend.text="Add Friend"
                                currentStats="new"
                            }
                        }
                }
            }
    }

    private fun sendFriendRequest() {
        friendRequestRef.child(id!!).child(receiverUserId).child("request_type").setValue("sent")
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                    friendRequestRef.child(receiverUserId).child(id!!)
                        .child("request_type").setValue("received")
                        .addOnCompleteListener {task->
                            if (task.isSuccessful)
                            {
                                currentStats="request_sent"
                                add_friend.text="Cancel Friend Request"
                                Toast.makeText(this,"Friend Request Sent",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }
}
