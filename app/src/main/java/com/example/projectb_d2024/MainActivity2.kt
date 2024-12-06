package com.example.projectb_d2024

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)


        //1) Viewの取得(連絡ボタン)
        val contact = findViewById<ImageButton>(R.id.contact)
        //2) ボタンを押したら次の画面へ
        contact.setOnClickListener{
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(連絡ボタン)
        val btn_nippo = findViewById<ImageButton>(R.id.btn_nippo)
        //2) ボタンを押したら次の画面へ
        btn_nippo.setOnClickListener{
            val intent = Intent(this, AllCheckActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(連絡ボタン)
        val back = findViewById<ImageButton>(R.id.back)
        //2) ボタンを押したら次の画面へ
        back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(連絡ボタン)
        val check_it = findViewById<ImageButton>(R.id.check_it)
        //2) ボタンを押したら次の画面へ
        check_it.setOnClickListener{
            val intent = Intent(this, BreedingListActivity::class.java)
            startActivity(intent)

        }

    }
}