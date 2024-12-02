package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MedicalRecordActivity : AppCompatActivity() {

    private lateinit var animalNumberTextView: TextView
    private lateinit var typeEditText: TextView
    private lateinit var nameEditText: TextView
    private lateinit var breedEditText: TextView
    private lateinit var nicknameEditText: TextView
    private lateinit var furiganaEditText: TextView
    private lateinit var romanizedNameEditText: TextView
    private lateinit var genderTextView: TextView
    private lateinit var birthDateEditText: TextView
    private lateinit var doctorEditText: TextView
    private lateinit var noteEditText: TextView

    private var currentAnimalNumber: Int = -1 // デフォルト値を設定

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_record)

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

        // View の初期化
        animalNumberTextView = findViewById(R.id.animalNumberTextView)
        typeEditText = findViewById(R.id.typeEditText)
        nameEditText = findViewById(R.id.nameEditText)
        breedEditText = findViewById(R.id.breedEditText)
        nicknameEditText = findViewById(R.id.nicknameEditText)
        furiganaEditText = findViewById(R.id.furiganaEditText)
        romanizedNameEditText = findViewById(R.id.romanizedNameEditText)
        genderTextView = findViewById(R.id.genderTextView)
        birthDateEditText = findViewById(R.id.birthDateEditText)
        doctorEditText = findViewById(R.id.doctorEditText)
        noteEditText = findViewById(R.id.noteEditText)

        // Intent から animalNumber を取得
        currentAnimalNumber = intent.getIntExtra("animalNumber", -1)
        if (currentAnimalNumber == -1) {
            Toast.makeText(this, "データが見つかりません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // データベースからデータを取得して表示
        loadAnimalData()

    }

    private fun loadAnimalData() {
        val dbHelper = AnimalDatabaseHelper(this)
        val animal = dbHelper.getAnimalByNumber(currentAnimalNumber)

        if (animal != null) {
            animalNumberTextView.text = animal.animalNumber.toString()
            typeEditText.text = animal.type
            nameEditText.text = animal.name
            breedEditText.text = animal.breed
            nicknameEditText.text = animal.nickname
            furiganaEditText.text = animal.furigana
            romanizedNameEditText.text = animal.romanizedName
            genderTextView.text = when (animal.gender) {
                0 -> "オス"
                1 -> "メス"
                else -> "不明"
            }
            birthDateEditText.text = animal.birthDate
            doctorEditText.text = animal.doctor
            noteEditText.text = animal.note
        } else {
            Toast.makeText(this, "データが見つかりませんでした", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveAnimalData() {
        val dbHelper = AnimalDatabaseHelper(this)

        val updatedAnimal = AnimalDatabaseHelper.AnimalData(
            animalNumber = currentAnimalNumber,
            type = typeEditText.text.toString(),
            name = nameEditText.text.toString(),
            breed = breedEditText.text.toString(),
            nickname = nicknameEditText.text.toString(),
            furigana = furiganaEditText.text.toString(),
            romanizedName = romanizedNameEditText.text.toString(),
            gender = when (genderTextView.text.toString()) {
                "オス" -> 0
                "メス" -> 1
                else -> -1
            },
            birthDate = birthDateEditText.text.toString(),
            doctor = doctorEditText.text.toString(),
            note = noteEditText.text.toString(),
            age = 0
        )

        val textColor = Color.parseColor("#FFFFFF")
        animalNumberTextView.setTextColor(textColor)
        typeEditText.setTextColor(textColor)
        nameEditText.setTextColor(textColor)
        breedEditText.setTextColor(textColor)
        nicknameEditText.setTextColor(textColor)
        furiganaEditText.setTextColor(textColor)
        romanizedNameEditText.setTextColor(textColor)
        genderTextView.setTextColor(textColor)
        birthDateEditText.setTextColor(textColor)
        doctorEditText.setTextColor(textColor)
        noteEditText.setTextColor(textColor)

        val id = dbHelper.insertOrUpdateAnimal(updatedAnimal)
        if (id != -1L) {
            Toast.makeText(this, "データを保存しました", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "保存に失敗しました", Toast.LENGTH_SHORT).show()
        }
    }




    }