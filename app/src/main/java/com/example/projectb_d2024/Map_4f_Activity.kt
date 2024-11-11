package com.example.projectb_d2024

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Map_4f_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map4f)

        //1) Viewの取得(5Fボタン)
        val imageButton8 : ImageButton = findViewById(R.id.imageButton8)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton8.setOnClickListener{
            val intent = Intent(this, Map_5f_Activity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(3Fボタン)
        val imageButton5 : ImageButton = findViewById(R.id.imageButton5)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton5.setOnClickListener{
            val intent = Intent(this, Map_4f_Activity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(1Fボタン)
        val imageButton3 : ImageButton = findViewById(R.id.imageButton3)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton3.setOnClickListener{
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(メイン画面へボタン)
        val imageButton : ImageButton = findViewById(R.id.imageButton)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}