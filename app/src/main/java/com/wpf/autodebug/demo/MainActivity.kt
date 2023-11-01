package com.wpf.autodebug.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_layout)
//        Toast.makeText(this, "修补成功", Toast.LENGTH_LONG).show()
    }
}