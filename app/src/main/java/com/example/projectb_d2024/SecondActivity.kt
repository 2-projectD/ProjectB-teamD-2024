package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val btnBack :Button = findViewById(R.id.btnBack)

        //3)戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener{
            finish()
        }
    }
}
