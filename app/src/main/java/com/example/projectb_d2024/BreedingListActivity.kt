package com.example.projectb_d2024

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BreedingListActivity : AppCompatActivity() {

    private lateinit var animalListView01: ListView
    private lateinit var breedingVoiceDatabase: BreedingVoiceDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breeding_list)

        //1) Viewの取得(連絡ボタン)
        val contact : ImageButton = findViewById(R.id.contact)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        contact.setOnClickListener{
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        animalListView01 = findViewById(R.id.animalListView01)
        breedingVoiceDatabase = BreedingVoiceDatabase(this)

        // データベースから全てのデータを取得
        val breedingVoiceRecords = breedingVoiceDatabase.getAllRecords()

        if (breedingVoiceRecords.isNotEmpty()) {
            // リストの表示
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                breedingVoiceRecords.map { record ->
                    "${record.string1}\n ${record.string2}\n ${record.int1}"
                }
            )
            animalListView01.adapter = adapter
        } else {
            Toast.makeText(this@BreedingListActivity, "データがありません", Toast.LENGTH_SHORT).show()
        }

        // リスト項目をタップした際の処理
        animalListView01.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedRecord = breedingVoiceRecords[position]
            val intent = Intent(this, GetDataActivity::class.java)
            intent.putExtra("animalNumber", selectedRecord.animalNumber)
            intent.putExtra("string1", selectedRecord.string1)
            intent.putExtra("string2", selectedRecord.string2)
            intent.putExtra("int1", selectedRecord.int1)
            intent.putExtra("int2", selectedRecord.int2)
            startActivity(intent)
        }
    }
}
