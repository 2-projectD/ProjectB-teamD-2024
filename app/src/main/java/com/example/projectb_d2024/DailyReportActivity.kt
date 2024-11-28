package com.example.projectb_d2024

import android.annotation.SuppressLint
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DailyReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_report)

        // AnimalDatabaseHelperのインスタンスを作成
        val dbHelper = AnimalDatabaseHelper(this)

        // 動物データをデータベースに挿入 (すでに挿入したデータがあればスキップ)
        val animalsData = listOf(
            AnimalDatabaseHelper.AnimalData(112,"カメ", "アオウミガメ", "爬虫綱カメ目ウミガメ科アオウミガメ属", "亀子","kameko","Chelonia mydas",2,"2004/12/06","山田","",0),
            AnimalDatabaseHelper.AnimalData(203,"サメ","シロワニ", "シロワニ", "シャークマン","kameko","Chelonia mydas",1,"2016/02/19","長谷川","",0),
            AnimalDatabaseHelper.AnimalData(802,"ペンギン","ケープペンギン", "ケープペンギン", "ささみ","kameko","Chelonia mydas",2,"2016/02/19","長野","",0),
            AnimalDatabaseHelper.AnimalData(125,"ペンギン", "アデリーペンギン","アデリーペンギン", "カイト","kameko","Chelonia mydas",1,"2020/03/15","山田","",0),
            AnimalDatabaseHelper.AnimalData(501,"ペンギン","オウサマペンギン", "オウサマペンギン", "りりな","kameko","Chelonia mydas",2,"2000/06/18","長谷川","",0),
            AnimalDatabaseHelper.AnimalData(111,"ペンギン","ジェンツーペンギン", "ジェンツーペンギン", "こころ","kameko","Chelonia mydas",2,"2015/01/01","山田","",0),
            AnimalDatabaseHelper.AnimalData(130,"ペンギン","ヒゲペンギン", "ヒゲペンギン", "イナゴ","kameko","Chelonia mydas",1,"2020/08/29","長野","",0),
            AnimalDatabaseHelper.AnimalData(350,"ミミズク","アフリカンミミズク", "アフリカンミミズク", "ミミ","kameko","Chelonia mydas",2,"2024/01/09","山田","",0),
            AnimalDatabaseHelper.AnimalData(566,"インコ","ルリコンゴインコ", "ルリコンゴインコ", "たま","kameko","Chelonia mydas",1,"2018/11/12","長野","",0),
            AnimalDatabaseHelper.AnimalData(250,"タカ","モモアカノスリ", "モモアカノスリ", "タカコ","kameko","Chelonia mydas",2,"2021/07/11","長谷川","",0)

        )

        // データベースに挿入
        for (animal in animalsData) {
            val id = dbHelper.insertAnimal(animal)
            if (id != -1L) {
                println("Inserted animal with ID: $id")
            } else {
                Log.e("DatabaseError", "Failed to insert animal: ${animal.name}")
            }
        }

        // 動物データを取得
        val animals = dbHelper.getAllAnimals().distinctBy { it.animalNumber }

        // ListViewのセットアップ
        val listView: ListView = findViewById(R.id.animalListView)
        val adapter = AnimalDatabaseHelper.AnimalAdapter(this, animals)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

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
        const val COLUMN_ANIMAL_NUMBER = "animalNumber"
        const val COLUMN_TYPE = "type"
        const val COLUMN_NAME = "name"
        const val COLUMN_BREED = "breed"
        const val COLUMN_NICKNAME = "nickname"
        const val COLUMN_FURIGANA = "furigana"
        const val COLUMN_ROMANIZED_NAME = "romanizedName"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_BIRTH_DATE = "birthDate"
        const val COLUMN_DOCTOR = "doctor"
        const val COLUMN_NOTE = "note"


        private const val CREATE_TABLE_ANIMALS = """
            CREATE TABLE $TABLE_ANIMALS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ANIMAL_NUMBER INTEGER NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_BREED TEXT NOT NULL,
                $COLUMN_NICKNAME TEXT NOT NULL,
                $COLUMN_FURIGANA TEXT NOT NULL,
                $COLUMN_ROMANIZED_NAME TEXT NOT NULL,
                $COLUMN_GENDER INTEGER NOT NULL,
                $COLUMN_BIRTH_DATE TEXT NOT NULL,
                $COLUMN_DOCTOR TEXT NOT NULL,
                $COLUMN_NOTE TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("Database", "onCreate called")
        db.execSQL(CREATE_TABLE_ANIMALS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("Database", "onUpgrade called: $oldVersion -> $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALS")
        onCreate(db)
    }

    // **修正: insertAnimalメソッドから年齢計算を削除**
    fun insertAnimal(animal: AnimalData): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COLUMN_ANIMAL_NUMBER, animal.animalNumber)
                put(COLUMN_TYPE, animal.type)
                put(COLUMN_NAME, animal.name)
                put(COLUMN_BREED, animal.breed)
                put(COLUMN_NICKNAME, animal.nickname)
                put(COLUMN_FURIGANA, animal.furigana)
                put(COLUMN_ROMANIZED_NAME, animal.romanizedName)
                put(COLUMN_GENDER, animal.gender)
                put(COLUMN_BIRTH_DATE, animal.birthDate)
                put(COLUMN_DOCTOR, animal.doctor)
                put(COLUMN_NOTE, animal.note)
            }
            db.insert(TABLE_ANIMALS, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error inserting data", e)
            -1L
        } finally {
            db.close()
        }
    }

    // **修正: getAllAnimalsで年齢を動的に計算**
    fun getAllAnimals(): List<AnimalData> {
        val db = readableDatabase
        val animals = mutableListOf<AnimalData>()
        val cursor = db.query(
            TABLE_ANIMALS,
            arrayOf(
                COLUMN_ANIMAL_NUMBER, COLUMN_TYPE, COLUMN_NAME, COLUMN_BREED,
                COLUMN_NICKNAME, COLUMN_FURIGANA, COLUMN_ROMANIZED_NAME, COLUMN_GENDER,
                COLUMN_BIRTH_DATE, COLUMN_DOCTOR, COLUMN_NOTE
            ),
            null, null, null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                val birthDate = it.getString(it.getColumnIndexOrThrow(COLUMN_BIRTH_DATE))
                val calculatedAge = calculateAge(birthDate) // 動的に年齢を計算

                val animal = AnimalData(
                    animalNumber = it.getInt(it.getColumnIndexOrThrow(COLUMN_ANIMAL_NUMBER)),
                    type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                    name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    breed = it.getString(it.getColumnIndexOrThrow(COLUMN_BREED)),
                    nickname = it.getString(it.getColumnIndexOrThrow(COLUMN_NICKNAME)),
                    furigana = it.getString(it.getColumnIndexOrThrow(COLUMN_FURIGANA)),
                    romanizedName = it.getString(it.getColumnIndexOrThrow(COLUMN_ROMANIZED_NAME)),
                    gender = it.getInt(it.getColumnIndexOrThrow(COLUMN_GENDER)),
                    birthDate = birthDate,
                    age = calculatedAge, // 年齢を計算してセット
                    doctor = it.getString(it.getColumnIndexOrThrow(COLUMN_DOCTOR)),
                    note = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTE))
                )
                animals.add(animal)
            }
        }
        db.close()
        return animals
    }

    // **変更: calculateAgeメソッドを年齢計算のみに使用**
    private fun calculateAge(birthDate: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            val birthLocalDate = LocalDate.parse(birthDate, formatter)
            val today = LocalDate.now()
            ChronoUnit.YEARS.between(birthLocalDate, today).toInt()
        } catch (e: Exception) {
            Log.e("DateError", "Invalid birthDate format: $birthDate", e)
            0
        }
    }

    // **修正: AnimalDataクラスのageを動的プロパティに**
    data class AnimalData(
        val animalNumber: Int,
        val type: String,
        val name: String,
        val breed: String,
        val nickname: String,
        val furigana: String,
        val romanizedName: String,
        val gender: Int,
        val birthDate: String,
        val doctor: String,
        val note: String,
        val age: Int // 動的に設定
    )

    class AnimalAdapter(context: Context, private val animals: List<AnimalData>) :
        ArrayAdapter<AnimalData>(context, 0, animals) {

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_animal, parent, false)

            val animal = animals[position]

            val animalNumberTextView: TextView = view.findViewById(R.id.animalNumber)
            val typeTextView: TextView = view.findViewById(R.id.animalType)
            val breedTextView: TextView = view.findViewById(R.id.animalBreed)
            val ageTextView: TextView = view.findViewById(R.id.animalAge)

            animalNumberTextView.text = animal.animalNumber.toString()
            typeTextView.text = animal.type
            breedTextView.text = animal.breed
            ageTextView.text = "${animal.age}"

            return view
        }
    }

}

