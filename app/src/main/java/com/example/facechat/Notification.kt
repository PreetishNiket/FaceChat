package com.example.facechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.facechat.Class.Contacts
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.find_friends_design.view.*

class Notification : AppCompatActivity() {
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Users")
    }
    private val friendRequestRef by lazy {
        FirebaseDatabase.getInstance()
            .reference .child("Friend Requests")
    }
    private val contactsRef by lazy {
        FirebaseDatabase.getInstance()
            .reference .child("Contacts")
    }
    var id: String? = ""
   // var listUserId: String? =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        notification_list.layoutManager=LinearLayoutManager(applicationContext)
        id = auth.currentUser?.uid
    }

    override fun onStart() {
        super.onStart()
        var options: FirebaseRecyclerOptions<Contacts>?
        options=FirebaseRecyclerOptions.Builder<Contacts>()
            .setQuery(friendRequestRef.child(id!!), Contacts::class.java)
            .build()
        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<Contacts, NotificationViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
                val itemView= LayoutInflater.from(parent.context).inflate(R.layout.find_friends_design,parent,false)
                return NotificationViewHolder(itemView)

            }
            override fun onBindViewHolder(holder: NotificationViewHolder, position: Int, model: Contacts) {
               holder.acceptBtn.visibility=View.VISIBLE
                holder.cancelBtn.visibility=View.VISIBLE
                 val listUserId=getRef(position).key
                val requestTypeRef=getRef(position).child("request_type").ref
                requestTypeRef.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                      if (dataSnapshot.exists())
                      {
                          val type= dataSnapshot.value.toString()
                          if (type=="received")
                          {
                              holder.cardView.visibility=View.VISIBLE
                              db.child(listUserId!!).addValueEventListener(object :ValueEventListener{
                                  override fun onCancelled(p0: DatabaseError) {
                                  }
                                  override fun onDataChange(dataSnapshot: DataSnapshot) {

                                      if (dataSnapshot.hasChild("image")) {
                                          val imageStr=dataSnapshot.child("image").value.toString()
                                          Picasso.get().load(imageStr).into(holder.profileImageView)
                                      }
                                      val nameStr=dataSnapshot.child("name").value.toString()
                                      holder.userNameTxt.text=nameStr

                                      holder.acceptBtn.setOnClickListener {
                                          contactsRef.child(id!!).child(listUserId)
                                              .child("Contact").setValue("Saved")
                                              .addOnCompleteListener { it ->
                                                  if (it.isSuccessful)
                                                  {
                                                      contactsRef.child(listUserId).child(id!!)
                                                          .child("Contact").setValue("Saved")
                                                          .addOnCompleteListener {task->
                                                              if (task.isSuccessful)
                                                              {
                                                                  friendRequestRef.child(id!!).child(listUserId)
                                                                      .removeValue()
                                                                      .addOnCompleteListener {
                                                                          if (it.isSuccessful)
                                                                          {
                                                                              friendRequestRef.child(listUserId).child(id!!)
                                                                                  .removeValue()
                                                                                  .addOnCompleteListener {taskId->
                                                                                      if (taskId.isSuccessful)
                                                                                      {
                                                                                          Toast.makeText(this@Notification,"New Contact Saved",Toast.LENGTH_SHORT).show()
                                                                                      }
                                                                                  }
                                                                          }
                                                                      }
                                                              }
                                                          }
                                                  }
                                              }
                                      }
                                      holder.cancelBtn.setOnClickListener {
                                          friendRequestRef.child(id!!).child(listUserId)
                                              .removeValue()
                                              .addOnCompleteListener {
                                                  if (it.isSuccessful)
                                                  {
                                                      friendRequestRef.child(listUserId).child(id!!)
                                                          .removeValue()
                                                          .addOnCompleteListener {task->
                                                              if (task.isSuccessful)
                                                              {
                                                                  Toast.makeText(this@Notification,"Contact Deleted",Toast.LENGTH_SHORT).show()
                                                              }
                                                          }
                                                  }
                                              }
                                      }

                                  }

                              })
                          }
                          else
                          {
                              holder.cardView.visibility=View.GONE
                          }
                      }
                    }
                })

            }
        }
        notification_list.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val userNameTxt:TextView=itemView.findViewById(R.id.name_notification)
            val acceptBtn: Button =itemView.findViewById(R.id.request_accept_btn)
            val cancelBtn:Button=itemView.findViewById(R.id.request_decline_btn)
            val profileImageView:ImageView=itemView.findViewById(R.id.image_notification)
            val cardView:CardView=itemView.findViewById(R.id.card_view)

    }
}
