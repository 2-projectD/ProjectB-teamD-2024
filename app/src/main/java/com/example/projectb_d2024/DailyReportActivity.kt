package com.example.projectb_d2024

import android.os.Bundle
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DailyReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_report)

        // AnimalDatabaseHelperのインスタンスを作成
        val dbHelper = AnimalDatabaseHelper(this)

        // 10種類の動物データをリストとして定義
        val animalsData = listOf(
            Triple("カメ", "アオウミガメ", "元気"),
            Triple("ペンギン", "ケープペンギン", "普通"),
            Triple("ペンギン", "アデリーペンギン", "良好"),
            Triple("ペンギン", "オウサマペンギン", "元気"),
            Triple("ペンギン", "ジェンツーペンギン", "普通"),
            Triple("ペンギン", "ヒゲペンギン", "健康"),
            Triple("ミミズク", "アフリカンミミズク", "良好"),
            Triple("インコ", "ルリコンゴインコ", "元気"),
            Triple("タカ", "モモアカノスリ", "元気"),
            Triple("サメ", "シロワニ", "普通")
        )

        // 動物データをデータベースに挿入
        for ((type, name, healthStatus) in animalsData) {
            val id = dbHelper.insertAnimal(type, name, healthStatus)
            if (id != -1L) {
                println("Inserted animal with ID: $id")
            } else {
                Log.e("DatabaseError", "Failed to insert animal: $name")
            }
        }

        // 挿入後に動物データを取得して表示
        val animals = dbHelper.getAllAnimals()
        for (animal in animals) {
            println("ID: ${animal.id}, Type: ${animal.type}, Name: ${animal.name}, Health Status: ${animal.healthStatus}")
        }

        // ステータスバーのインセット処理
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

class AnimalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AnimalDatabase.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ANIMALS = "animals"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_NAME = "name"
        const val COLUMN_HEALTH_STATUS = "health_status"

        private const val CREATE_TABLE_ANIMALS = """
            CREATE TABLE $TABLE_ANIMALS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_HEALTH_STATUS TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ANIMALS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALS")
        onCreate(db)
    }

    // データの挿入メソッド
    fun insertAnimal(type: String, name: String, healthStatus: String): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COLUMN_TYPE, type)
                put(COLUMN_NAME, name)
                put(COLUMN_HEALTH_STATUS, healthStatus)
            }
            db.insert(TABLE_ANIMALS, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error inserting data", e)
            -1
        } finally {
            db.close()
        }
    }

    // データの取得メソッド
    fun getAllAnimals(): List<Animal> {
        val db = readableDatabase
        val animals = mutableListOf<Animal>()
        val cursor = db.query(
            TABLE_ANIMALS,
            arrayOf(COLUMN_ID, COLUMN_TYPE, COLUMN_NAME, COLUMN_HEALTH_STATUS),
            null, null, null, null, null
        )

        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val healthStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEALTH_STATUS))
                animals.add(Animal(id, type, name, healthStatus))
            }
        }
        db.close()
        return animals
    }
}

// 動物のデータクラス
data class Animal(
    val id: Int,
    val type: String,
    val name: String,
    val healthStatus: String
)

class AnimalAdapter(context: Context, private val animals: List<Animal>) : ArrayAdapter<Animal>(context, 0, animals) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_animal, parent, false)

        val animal = animals[position]

        val typeTextView: TextView = view.findViewById(R.id.animalType)
        val nameTextView: TextView = view.findViewById(R.id.animalName)
        val healthStatusTextView: TextView = view.findViewById(R.id.animalHealthStatus)

        typeTextView.text = animal.type
        nameTextView.text = animal.name
        healthStatusTextView.text = animal.healthStatus

        return view
    }
}

class DailyReportActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_report)

        // AnimalDatabaseHelperのインスタンスを作成
        val dbHelper = AnimalDatabaseHelper(this)

        // 動物データをデータベースに挿入 (すでに挿入したデータがあればスキップ)
        val animalsData = listOf(
            Triple("カメ", "アオウミガメ", "元気"),
            Triple("ペンギン", "ケープペンギン", "普通"),
            Triple("ペンギン", "アデリーペンギン", "良好"),
            Triple("ペンギン", "オウサマペンギン", "元気"),
            Triple("ペンギン", "ジェンツーペンギン", "普通"),
            Triple("ペンギン", "ヒゲペンギン", "健康"),
            Triple("ミミズク", "アフリカンミミズク", "良好"),
            Triple("インコ", "ルリコンゴインコ", "元気"),
            Triple("タカ", "モモアカノスリ", "元気"),
            Triple("サメ", "シロワニ", "普通")
        )

        // データベースに挿入
        for ((type, name, healthStatus) in animalsData) {
            val id = dbHelper.insertAnimal(type, name, healthStatus)
            if (id != -1L) {
                println("Inserted animal with ID: $id")
            } else {
                Log.e("DatabaseError", "Failed to insert animal: $name")
            }
        }

        // 動物データを取得
        val animals = dbHelper.getAllAnimals()

        // ListViewのセットアップ
        val listView: ListView = findViewById(R.id.animalListView)
        val adapter = AnimalAdapter(this, animals)
        listView.adapter = adapter
    }
}
