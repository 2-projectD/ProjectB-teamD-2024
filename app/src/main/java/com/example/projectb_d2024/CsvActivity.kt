package com.example.projectb_d2024

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class CsvActivity : AppCompatActivity() {
    private lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_csv)

        list = findViewById(R.id.list)

        val back : ImageButton = findViewById(R.id.back)

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

        // 戻るボタン
        back.setOnClickListener {
            finish()
        }

        // CSVファイルを読み込む
        val results = readCSV()
        // アダプターを作成してListViewにセット
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, results)
        list.adapter = adapter
    }

    // CSVファイルを読み込むメソッド
    private fun readCSV(): List<String> {
        val fileName = "speech_recognition_results.csv"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val resultList = mutableListOf<String>()

        if (file.exists()) {
            file.forEachLine { line ->
                resultList.add(line) // 各行をリストに追加
            }
        }
        return resultList // 読み込んだ結果を返す
    }
}