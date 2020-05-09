package com.example.facechat

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }
    private val navigationItemSelectedListener
            = BottomNavigationView.OnNavigationItemSelectedListener{
        when(it.itemId)
        {
            R.id.navigation_home -> {
                startActivity(Intent(this,MainActivity::class.java))
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
