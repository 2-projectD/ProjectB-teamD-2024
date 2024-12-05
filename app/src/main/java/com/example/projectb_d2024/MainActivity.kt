package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //1) Viewの取得(連絡ボタン)
        val contact = findViewById<ImageButton>(R.id.contact)

        //2) ボタンを押したら次の画面へ
        contact.setOnClickListener{
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(連絡ボタン)
        val kanrisya = findViewById<ImageButton>(R.id.kanrisya)

        //2) ボタンを押したら次の画面へ
        kanrisya.setOnClickListener{
            val intent = Intent(this, UserSettingActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(メニュー画面へボタン)
        val btnMenu : ImageButton = findViewById(R.id.btnMenu)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        btnMenu.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(メモ画面へボタン)
        val btnVoice : ImageButton = findViewById(R.id.btnVoice)

        //2) ボタンを押したら次の画面へ
        btnVoice.setOnClickListener{
            val intent = Intent(this, VoiceActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(メモ画面へボタン)
        val btnVoice1 : ImageButton = findViewById(R.id.btnVoice1)

        //2) ボタンを押したら次の画面へ
        btnVoice1.setOnClickListener{
            val intent = Intent(this, VoiceActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(日報へボタン)
        val btn_nippo : ImageButton = findViewById(R.id.btn_nippo)

        //2) ボタンを押したら次の画面へ
        btn_nippo.setOnClickListener{
            val intent = Intent(this, DailyReportActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(メモ画面へボタン)
        val check_it : ImageButton = findViewById(R.id.check_it)

        //2) ボタンを押したら次の画面へ
        check_it.setOnClickListener{
            val intent = Intent(this, IndividualManagementActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(マップへボタン)
        val btnTenpure : ImageButton = findViewById(R.id.btnTenpure)

        //2) ボタンを押したら次の画面へ
        btnTenpure.setOnClickListener{
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }


    }
}