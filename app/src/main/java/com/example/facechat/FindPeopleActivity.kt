package com.example.facechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_find_people.*

class FindPeopleActivity : AppCompatActivity() {
     private var str=""
   private val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_people)
        find_friends_list.layoutManager=LinearLayoutManager(applicationContext)
        search_user_text.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (search_user_text.text.toString()=="")
                {
                    Toast.makeText(this@FindPeopleActivity,"Please enter the name of the user",Toast.LENGTH_SHORT).show()
                }
                else{
                    str=s.toString()
                    onStart()
                }
            }

        })
    }

    override fun onStart() {
        super.onStart()
        var options: FirebaseRecyclerOptions<Contacts>?
        if (str=="")
        {
           options=FirebaseRecyclerOptions.Builder<Contacts>()
               .setQuery(db,Contacts::class.java)
               .build()
        }
        else
        {
             options=FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(db.orderByChild("name").startAt(str).endAt(str+"\uf8ff"),Contacts::class.java)
                .build()
        }

        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendsViewHolder {
                val itemView=LayoutInflater.from(parent.context).inflate(R.layout.contacts_design,parent,false)
                return FindFriendsViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: FindFriendsViewHolder, position: Int, model: Contacts) {
                holder.userNameTxt.text=model.getName()
           //    // Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImageView)
                holder.videoCallBtn!!.visibility=View.GONE
                holder.itemView.setOnClickListener {
                    val visitUserId=getRef(position).key
                    var i=Intent(this@FindPeopleActivity,ProfileActivity::class.java)
                    i.putExtra("visit_user_id",visitUserId)
                 //   //i.putExtra("profile_image",model.getImage())
                    i.putExtra("profile_name",model.getName())
                    startActivity(i)
                }
            }
        }
        find_friends_list.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class FindFriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val userNameTxt: TextView = itemView.findViewById(R.id.name_contacts)
            val videoCallBtn: Button? = itemView.findViewById(R.id.call_btn)
            val profileImageView: ImageView? = itemView.findViewById(R.id.image_contacts)
           // val cardView: CardView? = itemView.findViewById(R.id.card_view1)
    }
}
