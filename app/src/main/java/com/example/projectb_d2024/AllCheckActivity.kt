package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AllCheckActivity : AppCompatActivity() {

    private lateinit var animalDatabaseHelper: AnimalDatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: AnimalDatabaseHelper.AnimalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_check)

        val back : ImageButton = findViewById(R.id.back)
        // 戻るボタン
        back.setOnClickListener {
            finish()
        }

        //1) Viewの取得(連絡ボタン)
        val contact : ImageButton = findViewById(R.id.contact)
        //2) ボタンを押したら次の画面へ
        contact.setOnClickListener{
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        // ListView の設定
        listView = findViewById(R.id.AlllistView) // 他のアクティビティと ID を重複させない
        animalDatabaseHelper = AnimalDatabaseHelper(this)

        // 動物データを取得して表示
        val animals = animalDatabaseHelper.getAllAnimals()
        adapter = AnimalDatabaseHelper.AnimalAdapter(this, animals)
        listView.adapter = adapter

        // リスト項目のクリックリスナー
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedAnimal = adapter.getItem(position)
            if (selectedAnimal != null) {
                val intent = Intent(this, ChengeActivity::class.java)
                intent.putExtra("animalNumber", selectedAnimal.animalNumber)
                startActivity(intent)
            } else {
                Log.e("AllCheckActivity", "Selected animal is null!")
            }
        }
    }
}
