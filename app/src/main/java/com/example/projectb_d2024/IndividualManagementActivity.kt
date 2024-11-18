package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IndividualManagementActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_individual_management)

        val back : ImageButton = findViewById(R.id.back)

        //3)戻るボタン（アクティビティの終了）
        back.setOnClickListener{
            finish()
        }

        //1) Viewの取得(連絡ボタン)
        val contact : ImageButton = findViewById(R.id.contact)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        contact.setOnClickListener{
            val intent = Intent(this, ContactActivity::class.java)
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

        //1) Viewの取得(メニュー画面へボタン)
        val btnVoice : ImageButton = findViewById(R.id.btnVoice)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        btnVoice.setOnClickListener{
            val intent = Intent(this, VoiceActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(健康チェック画面へボタン)
        val feedRecord : ImageButton = findViewById(R.id.feedRecord)

        //2) ボタンを押したら次の画面へ
        feedRecord.setOnClickListener{
            val intent = Intent(this, BreedingVoiceActivity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(健康チェック画面へボタン)
        val healthCheck : ImageButton = findViewById(R.id.healthCheck)

        //2) ボタンを押したら次の画面へ
        healthCheck.setOnClickListener{
            val intent = Intent(this, HealthChecksActivity::class.java)
            startActivity(intent)
        }

    }
}