package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class BreedingListActivity : AppCompatActivity() {
    private lateinit var animalListView01: ListView
    private lateinit var breedingVoiceDatabase: BreedingVoiceDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breeding_list)

        animalListView01 = findViewById(R.id.animalListView01)
        breedingVoiceDatabase = BreedingVoiceDatabase(this)

        // データベースからデータを取得
        val dataList = breedingVoiceDatabase.getAllRecords()

        // リストの表示
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            dataList.map { it.string1 } // 表示したい項目（例：string1 を表示）
        )
        animalListView01.adapter = adapter

        // リスト項目をタップした際の処理
        animalListView01.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedRecord = dataList[position]
            val intent = Intent(this,  GetDataActivity::class.java)
            intent.putExtra("animalNumber", selectedRecord.animalNumber)
            intent.putExtra("string1", selectedRecord.string1)
            intent.putExtra("string2", selectedRecord.string2)
            intent.putExtra("int1", selectedRecord.int1)
            intent.putExtra("int2", selectedRecord.int2)
            startActivity(intent)
        }
    }
}