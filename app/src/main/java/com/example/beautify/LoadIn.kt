package com.example.beautify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class LoadIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_main)

        //Run mogo recovery code until success

        val intent = Intent(this, MapsActivity::class.java).apply {
            finish()
        }
        startActivity(intent)
    }
}