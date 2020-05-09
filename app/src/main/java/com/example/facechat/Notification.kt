package com.example.facechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.find_friends_design.view.*

class Notification : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
    }
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun NotificationViewHolder(itemView: View)
        {
            val userNameTxt=itemView.findViewById<TextView>(R.id.name_notification)
            val acceptBtn=itemView.findViewById<Button>(R.id.request_accept_btn)
            val cancelBtn=itemView.findViewById<Button>(R.id.request_decline_btn)
            val profileImageView=itemView.findViewById<ImageView>(R.id.image_notification)
            val cardView=itemView.findViewById<CardView>(R.id.card_view)
        }
    }
}
