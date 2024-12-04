package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class DailyReportActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_report)

        val back : ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        //1) Viewの取得(連絡ボタン)
        val contact = findViewById<ImageButton>(R.id.contact)

        //2) ボタンを押したら次の画面へ
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

        //1) Viewの取得(メモ画面へボタン)
        val btnVoice : ImageButton = findViewById(R.id.btnVoice)

        //2) ボタンを押したら次の画面へ
        btnVoice.setOnClickListener{
            val intent = Intent(this, VoiceActivity::class.java)
            startActivity(intent)
        }

        // AnimalDatabaseHelperのインスタンスを作成
        val dbHelper = AnimalDatabaseHelper(this)

        // 動物データをデータベースに挿入 (すでに挿入したデータがあればスキップ)
        val animalsData = listOf(
            AnimalDatabaseHelper.AnimalData(112,"カメ", "アオウミガメ", "爬虫綱カメ目ウミガメ科アオウミガメ属", "亀子","kameko","Chelonia mydas",2,"2004/12/06","山田","","0", 1, "50", "150"),
            AnimalDatabaseHelper.AnimalData(203,"サメ","シロワニ", "シロワニ", "シャークマン","kameko","Chelonia mydas",1,"","長谷川","","0", 3, "70", "200"),
            AnimalDatabaseHelper.AnimalData(802,"ペンギン","ケープペンギン", "ケープペンギン", "ささみ","kameko","Chelonia mydas",2,"2015/01/19","長野","","0", 1, "", ""),
            AnimalDatabaseHelper.AnimalData(125,"ペンギン", "アデリーペンギン","アデリーペンギン", "カイト","kameko","Chelonia mydas",1,"2020/03/15","山田","","0", 1, "80", "120"),
            AnimalDatabaseHelper.AnimalData(501,"ペンギン","オウサマペンギン", "オウサマペンギン", "りりな","kameko","Chelonia mydas",2,"2000/06/18","長谷川","","0", 3, "50","95"),
            AnimalDatabaseHelper.AnimalData(111,"ペンギン","ジェンツーペンギン", "ジェンツーペンギン", "こころ","kameko","Chelonia mydas",2,"2015/01/01","山田","","0", 2, "20", "30"),
            AnimalDatabaseHelper.AnimalData(130,"ペンギン","ヒゲペンギン", "ヒゲペンギン", "イナゴ","kameko","Chelonia mydas",1,"2020/08/29","長野","","0", 5, "50", "120"),
            AnimalDatabaseHelper.AnimalData(350,"ミミズク","アフリカンミミズク", "アフリカンミミズク", "ミミ","kameko","Chelonia mydas",2,"2024/01/09","山田","","0", 5, "120", "190"),
            AnimalDatabaseHelper.AnimalData(566,"インコ","ルリコンゴインコ", "ルリコンゴインコ", "たま","kameko","Chelonia mydas",1,"2018/11/12","長野","","0", 2, "60", "130"),
            AnimalDatabaseHelper.AnimalData(250,"タカ","モモアカノスリ", "モモアカノスリ", "タカコ","kameko","Chelonia mydas",2,"2021/07/11","長谷川","","0", 1, "100", "120")

        )

        // データベースに挿入
        for (animal in animalsData) {
            val id = dbHelper.insertOrUpdateAnimal(animal)
            if (id != -1L) {
                Log.d("InsertCheck", "Inserted or updated animal with ID: $id")
            } else {
                Log.e("InsertError", "Failed to insert or update animal: ${animal.name}")
            }
        }


        // データ取得
        val animals = dbHelper.getAllAnimals().distinctBy { it.animalNumber }

        // ログで確認
        for (animal in animals) {
            Log.d("DatabaseCheck", "Animal: $animal")
        }

        // ListView設定
        val listView: ListView = findViewById(R.id.animalListView)
        val adapter = AnimalDatabaseHelper.AnimalAdapter(this, animals)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedAnimal = adapter.getItem(position)
            if (selectedAnimal != null) {
                val intent = Intent(this, MedicalRecordActivity::class.java)
                intent.putExtra("animalNumber", selectedAnimal.animalNumber) // データを渡す
                startActivity(intent) // MedicalRecordActivity を起動
            } else {
                Log.e("DailyReportActivity", "Selected animal is null!")
            }
        }


        // 更新通知
        adapter.notifyDataSetChanged()
    }
}

class AnimalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AnimalDatabase.db"
        private const val DATABASE_VERSION = 2

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
        const val COLUMN_AQUANUMBER = "aquaNumber"
        const val COLUMN_SPAN = "span"
        const val COLUMN_BODYWEIGHT = "bodyWeight"


        private const val CREATE_TABLE_ANIMALS = """
    CREATE TABLE $TABLE_ANIMALS (
        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_ANIMAL_NUMBER INTEGER NOT NULL UNIQUE, -- ユニークキーを設定
        $COLUMN_TYPE TEXT NOT NULL,
        $COLUMN_NAME TEXT NOT NULL,
        $COLUMN_BREED TEXT NOT NULL,
        $COLUMN_NICKNAME TEXT NOT NULL,
        $COLUMN_FURIGANA TEXT NOT NULL,
        $COLUMN_ROMANIZED_NAME TEXT NOT NULL,
        $COLUMN_GENDER INTEGER NOT NULL,
        $COLUMN_BIRTH_DATE TEXT NOT NULL,
        $COLUMN_DOCTOR TEXT NOT NULL,
        $COLUMN_NOTE TEXT NOT NULL,
        $COLUMN_AQUANUMBER INTEGER NOT NULL,
        $COLUMN_SPAN TEXT DEFAULT NULL,
        $COLUMN_BODYWEIGHT TEXT DEFAULT NULL
    )"""
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("Database", "onCreate called")
        db.execSQL(CREATE_TABLE_ANIMALS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // テーブルにユニークキーを追加するため再作成
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALS")
            onCreate(db)
        }
    }


    fun insertOrUpdateAnimal(animal: AnimalData): Long {
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
                put(COLUMN_AQUANUMBER, animal.aquaNumber)
                put(COLUMN_SPAN, animal.span)
                put(COLUMN_BODYWEIGHT, animal.bodyWeight)
            }

            // INSERT OR REPLACE を使用して既存データを更新
            db.insertWithOnConflict(TABLE_ANIMALS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error inserting or updating data", e)
            -1L
        } finally {
            db.close()
        }
    }

    // aquaNumberを使って検索(マップに使うやつ)
    fun getAnimalByAquaNumber(aquaNumber: Int): AnimalData? {
        val db = readableDatabase
        val cursor = db.query(
            "animals",
            null,
            "aquaNumber = ?",
            arrayOf(aquaNumber.toString()),
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val animalNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANIMAL_NUMBER))
            val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val breed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREED))
            val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
            val furigana = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FURIGANA))
            val romanizedName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROMANIZED_NAME))
            val gender = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
            val birthDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE))
            val doctor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR))
            val note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE))
            val age = calculateAge(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)))
            val span = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPAN))
            val bodyWeight = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BODYWEIGHT))

            cursor.close()

            // AnimalDataのインスタンスを作成して返す
            return AnimalData(animalNumber,type,name, breed, nickname, furigana, romanizedName,gender, birthDate, doctor, note, age, aquaNumber, span, bodyWeight)
        }

        cursor?.close()
        return null
    }



    fun getAllAnimals(): List<AnimalData> {
        val db = readableDatabase
        val animals = mutableListOf<AnimalData>()
        val cursor = db.query(
            TABLE_ANIMALS,
            arrayOf(
                COLUMN_ANIMAL_NUMBER, COLUMN_TYPE, COLUMN_NAME, COLUMN_BREED,
                COLUMN_NICKNAME, COLUMN_FURIGANA, COLUMN_ROMANIZED_NAME, COLUMN_GENDER,
                COLUMN_BIRTH_DATE, COLUMN_DOCTOR, COLUMN_NOTE, COLUMN_AQUANUMBER ,COLUMN_SPAN, COLUMN_BODYWEIGHT
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
                    note = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTE)),
                    aquaNumber = it.getInt(it.getColumnIndexOrThrow(COLUMN_AQUANUMBER)),
                    span = it.getString(it.getColumnIndexOrThrow(COLUMN_SPAN)),
                    bodyWeight = it.getString(it.getColumnIndexOrThrow(COLUMN_BODYWEIGHT))


                )
                animals.add(animal)
            }
        }
        db.close()
        return animals
    }


    private fun calculateAge(birthDate: String?): String {
        return if (birthDate.isNullOrBlank()) {
            "-" // 誕生日が空の場合
        } else {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                val birthLocalDate = LocalDate.parse(birthDate, formatter)
                val today = LocalDate.now()
                val years = ChronoUnit.YEARS.between(birthLocalDate, today).toInt()
                "$years 歳" // 正常に年齢を計算
            } catch (e: Exception) {
                Log.e("DateError", "Invalid birthDate format: $birthDate", e)
                "年齢なし" // フォーマットエラーの場合
            }
        }
    }


    fun getAnimalsByAquaNumber(aquaNumber: Int): List<AnimalData> {
        val db = readableDatabase
        val animals = mutableListOf<AnimalData>()

        val cursor = db.query(
            TABLE_ANIMALS,
            null, // 全ての列を取得
            "$COLUMN_AQUANUMBER = ?", // aquaNumber が一致かどうか
            arrayOf(aquaNumber.toString()),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                animals.add(
                    AnimalData(
                        animalNumber = it.getInt(it.getColumnIndexOrThrow(COLUMN_ANIMAL_NUMBER)),
                        type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                        name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                        breed = it.getString(it.getColumnIndexOrThrow(COLUMN_BREED)),
                        nickname = it.getString(it.getColumnIndexOrThrow(COLUMN_NICKNAME)),
                        furigana = it.getString(it.getColumnIndexOrThrow(COLUMN_FURIGANA)),
                        romanizedName = it.getString(it.getColumnIndexOrThrow(COLUMN_ROMANIZED_NAME)),
                        gender = it.getInt(it.getColumnIndexOrThrow(COLUMN_GENDER)),
                        birthDate = it.getString(it.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)),
                        doctor = it.getString(it.getColumnIndexOrThrow(COLUMN_DOCTOR)),
                        note = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTE)),
                        age = calculateAge(it.getString(it.getColumnIndexOrThrow(COLUMN_BIRTH_DATE))),
                        aquaNumber = it.getInt(it.getColumnIndexOrThrow(COLUMN_AQUANUMBER)),
                        span = it.getString(it.getColumnIndexOrThrow(COLUMN_SPAN)),
                        bodyWeight = it.getString(it.getColumnIndexOrThrow(COLUMN_BODYWEIGHT))
                    )
                )
            }
        }
        return animals
    }


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
        val age: String,
        val aquaNumber: Int,
        val span: String,
        val bodyWeight: String
    )

    class AnimalAdapter(
        private val context: Context,
        private val animals: List<AnimalData>
    ) : ArrayAdapter<AnimalData>(context, 0, animals) {

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_animal, parent, false)

            val animal = animals[position]

            val animalNumberTextView: TextView = view.findViewById(R.id.animalNumber)
            val breedTextView: TextView = view.findViewById(R.id.animalBreed)
            val nicknameTextView: TextView = view.findViewById(R.id.animalNickname)

            animalNumberTextView.text = "動物番号：${animal.animalNumber}"
            breedTextView.text = animal.breed
            nicknameTextView.text = animal.nickname

            val backgroundColor = if (position % 2 == 0) {
                Color.parseColor("#7AB0D1") // 偶数行
            } else {
                Color.parseColor("#808080") // 奇数行
            }
            view.setBackgroundColor(backgroundColor)

            val textColor = Color.parseColor("#FFFFFF")
            animalNumberTextView.setTextColor(textColor)
            breedTextView.setTextColor(textColor)
            nicknameTextView.setTextColor(textColor)

            // 項目クリック時の動作を設定
            view.setOnClickListener {
                val intent = Intent(context, MedicalRecordActivity::class.java).apply {
                    putExtra("animalNumber", animal.animalNumber)
                }
                context.startActivity(intent)
            }

            return view
        }
    }

    data class Animal(
        val animalNumber: String,
        val name: String,
        val nickname: String
    )
    class AnimalAdapterMap(private val context: Context, private val animals: List<Animal>) : BaseAdapter() {

        override fun getCount(): Int = animals.size

        override fun getItem(position: Int): Any = animals[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            val animal = getItem(position) as Animal

            val animalNumberText = view.findViewById<TextView>(R.id.animalNumberText)
            val animalNameText = view.findViewById<TextView>(R.id.animalNameText)
            val nicknameText = view.findViewById<TextView>(R.id.nicknameText)

            animalNumberText.text = "動物番号: ${animal.animalNumber}"
            animalNameText.text = "動物種: ${animal.name}"
            nicknameText.text = "名前: ${animal.nickname}"

            return view
        }
    }
}