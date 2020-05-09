package com.example.facechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_find_people.*

class FindPeopleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_people)
        find_friends_list.layoutManager=LinearLayoutManager(applicationContext)
    }
    class FindFriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun FindFriendsViewHolder(itemView: View)
        {
            val userNameTxt=itemView.findViewById<TextView>(R.id.name_contacts)
            val videoCallBtn=itemView.findViewById<Button>(R.id.call_btn)
            val profileImageView=itemView.findViewById<ImageView>(R.id.image_contacts)
            val cardView=itemView.findViewById<CardView>(R.id.card_view1)
        }

    }
}
