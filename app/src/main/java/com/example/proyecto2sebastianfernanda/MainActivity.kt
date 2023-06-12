package com.example.proyecto2sebastianfernanda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var miFragmento: MainFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.myToolbar))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)
        if (savedInstanceState == null) {
            miFragmento = MainFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, miFragmento!!)
                .commitNow()
        }
    }
}