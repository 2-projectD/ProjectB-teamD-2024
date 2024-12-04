package com.example.projectb_d2024

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MapActivity : AppCompatActivity() {

    private lateinit var animalList: MutableList<String>
    private lateinit var dbHelper: AnimalDatabaseHelper
    private lateinit var animalListView: ListView
    private lateinit var imageButton17: ImageButton
    private lateinit var imageButton26: ImageButton
    private lateinit var imageButton22: ImageButton
    private lateinit var imageButton15: ImageButton
    private lateinit var imageButton39: ImageButton
    private lateinit var imageButton23: ImageButton
    private lateinit var imageButton19: ImageButton
    private lateinit var imageButton25: ImageButton
    private lateinit var imageButton24: ImageButton
    private lateinit var imageButton18: ImageButton
    private lateinit var imageButton21: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)

        // データベースヘルパーの初期化
        dbHelper = AnimalDatabaseHelper(this)

        // ビューの初期化
        animalListView = findViewById(R.id.animalListView)
        animalList = mutableListOf()

        val emptyAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, animalList)
        animalListView.adapter = emptyAdapter

        imageButton17 = findViewById(R.id.imageButton17)
        imageButton26 = findViewById(R.id.imageButton26)
        imageButton22 = findViewById(R.id.imageButton22)
        imageButton15 = findViewById(R.id.imageButton15)
        imageButton39 = findViewById(R.id.imageButton39)
        imageButton23 = findViewById(R.id.imageButton23)
        imageButton19 = findViewById(R.id.imageButton19)
        imageButton25 = findViewById(R.id.imageButton25)
        imageButton24 = findViewById(R.id.imageButton24)
        imageButton18 = findViewById(R.id.imageButton18)
        imageButton21 = findViewById(R.id.imageButton21)


        //1) Viewの取得(5Fボタン)
        val imageButton8: ImageButton = findViewById(R.id.imageButton8)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton8.setOnClickListener {
            val intent = Intent(this, Map_5f_Activity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(4Fボタン)
        val imageButton6: ImageButton = findViewById(R.id.imageButton6)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton6.setOnClickListener {
            val intent = Intent(this, Map_4f_Activity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(3Fボタン)
        val imageButton5: ImageButton = findViewById(R.id.imageButton5)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton5.setOnClickListener {
            val intent = Intent(this, Map_3f_Activity::class.java)
            startActivity(intent)
        }

        //1) Viewの取得(メイン画面へボタン)
        val imageButton: ImageButton = findViewById(R.id.imageButton)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        imageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //リストビューの調整
        val screenHeight = resources.displayMetrics.heightPixels
        val marginBottom = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f, resources.displayMetrics
        ).toInt()
        val newHeight = screenHeight - marginBottom
        val layoutParams = animalListView.layoutParams
        layoutParams.height = newHeight
        animalListView.layoutParams = layoutParams

        // スクロールビューを最下部に設定
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.post {
            scrollView.scrollTo(0, scrollView.getChildAt(0).height)
        }


        // ボタン21のクリックイベント（水槽番号1）
        val imageButton21 = findViewById<ImageButton>(R.id.imageButton21)
        imageButton21.setOnClickListener {
            val aquaNumber = 1
            searchAndDisplayData(aquaNumber)
        }

        // ボタン15のクリックイベント(水槽番号2)
        val imageButton15 = findViewById<ImageButton>(R.id.imageButton15)
        imageButton15.setOnClickListener {
            val aquaNumber = 2
            searchAndDisplayData(aquaNumber)
        }

        // ボタン19のクリックイベント（水槽番号3）
        val imageButton19 = findViewById<ImageButton>(R.id.imageButton19)
        imageButton19.setOnClickListener {
            val aquaNumber = 3
            searchAndDisplayData(aquaNumber)
        }

        // ボタン39のクリックイベント（水槽番号5）
        val imageButton39 = findViewById<ImageButton>(R.id.imageButton39)
        imageButton39.setOnClickListener {
            val aquaNumber = 5
            searchAndDisplayData(aquaNumber)
        }

        // animalListView のクリックリスナーを設定
        animalListView.setOnItemClickListener { _, _, position, _ ->
            // animalListから選択されたアイテムを取得
            val selectedAnimal = animalList[position]

            // 動物番号を抽出 (想定: "動物番号: X" の形式)
            val animalNumber = selectedAnimal.substringAfter("動物番号: ").substringBefore("\n").toIntOrNull()

            if (animalNumber != null) {
                // Intentを作成して次のActivityに遷移
                val intent = Intent(this, MedicalRecordActivity::class.java)
                intent.putExtra("animalNumber", animalNumber) // animalNumberを渡す
                startActivity(intent)
            } else {
                // 動物番号が抽出できなかった場合のやつ
                Toast.makeText(this, "動物番号の取得に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun searchAndDisplayData(aquaNumber: Int) {
        val animalDataList = dbHelper.getAnimalsByAquaNumber(aquaNumber)

        // 検索結果に応じてリストを更新
        if (animalDataList.isNotEmpty()) {
            animalList.clear()
            animalDataList.forEach { animal ->
                animalList.add("動物番号: ${animal.animalNumber}\n動物種: ${animal.name}\n名前: ${animal.nickname}")
            }
        } else {
            animalList.clear()
            animalList.add("該当するデータはありません")
        }

        // アダプターを更新
        val animalListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, animalList)
        animalListView.adapter = animalListAdapter


    }


}