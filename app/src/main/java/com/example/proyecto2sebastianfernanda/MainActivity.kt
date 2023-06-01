package com.example.proyecto2sebastianfernanda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.myToolbar))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)
        val miFragmento = MainFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, miFragmento)
            .commit();
    }
}