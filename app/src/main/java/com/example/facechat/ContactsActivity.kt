package com.example.facechat

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        //7contacts_list.setHasFixedSize(true)
        contacts_list.layoutManager=LinearLayoutManager(applicationContext)
        find_people_btn.setOnClickListener {
            startActivity(Intent(this,FindPeopleActivity::class.java))
        }
    }
    private val navigationItemSelectedListener
            = BottomNavigationView.OnNavigationItemSelectedListener{
        when(it.itemId)
        {
            R.id.navigation_home -> {
                startActivity(Intent(this,ContactsActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                startActivity(Intent(this,SettingsActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                startActivity(Intent(this,Notification::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,Registration::class.java))
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false

    }

}
