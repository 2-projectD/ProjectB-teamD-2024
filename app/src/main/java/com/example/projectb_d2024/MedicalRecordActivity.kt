package com.example.projectb_d2024

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_record)

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

        val id = dbHelper.insertOrUpdateAnimal(updatedAnimal)
        if (id != -1L) {
            Toast.makeText(this, "データを保存しました", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "保存に失敗しました", Toast.LENGTH_SHORT).show()
        }
    }
}