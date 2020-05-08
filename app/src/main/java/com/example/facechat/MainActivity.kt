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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

    }
    private val navigationItemSelectedListener
            = BottomNavigationView.OnNavigationItemSelectedListener{
        when(it.itemId)
        {
            R.id.navigation_home ->{
                startActivity(Intent(this,SettingsActivity::class.java)
            }
        }
    }

}
