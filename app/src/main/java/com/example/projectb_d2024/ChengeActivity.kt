package com.example.projectb_d2024

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChengeActivity : AppCompatActivity() {

    private lateinit var databaseHelper: AnimalDatabaseHelper
    private lateinit var animalData: AnimalDatabaseHelper.AnimalData
    private var animalNumber: Int = -1

    private lateinit var editType: EditText
    private lateinit var editName: EditText
    private lateinit var editBreed: EditText
    private lateinit var editNickname: EditText
    private lateinit var editFurigana: EditText
    private lateinit var editRomanizedName: EditText
    private lateinit var editGender: EditText
    private lateinit var editBirthDate: EditText
    private lateinit var editDoctor: EditText
    private lateinit var editNote: EditText
    private lateinit var editAquaNumber: EditText
    private lateinit var editSpan: EditText
    private lateinit var editBodyWeight: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chenge)

        val back : ImageButton = findViewById(R.id.back)
        // 戻るボタン
        back.setOnClickListener {
            finish()
        }

        // データベースインスタンスを作成
        databaseHelper = AnimalDatabaseHelper(this)

        // 動物番号を取得
        animalNumber = intent.getIntExtra("animalNumber", -1)

        // UIの初期化
        initUI()

        // 動物データを取得してUIにセット
        fetchAndSetAnimalData()

        // 保存ボタンのリスナーを設定
        btnSave.setOnClickListener {
            updateAnimalData()
        }
    }

    private fun initUI() {
        editType = findViewById(R.id.editType)
        editName = findViewById(R.id.editName)
        editBreed = findViewById(R.id.editBreed)
        editNickname = findViewById(R.id.editNickname)
        editFurigana = findViewById(R.id.editFurigana)
        editRomanizedName = findViewById(R.id.editRomanizedName)
        editGender = findViewById(R.id.editGender)
        editBirthDate = findViewById(R.id.editBirthDate)
        editDoctor = findViewById(R.id.editDoctor)
        editNote = findViewById(R.id.editNote)
        editAquaNumber = findViewById(R.id.editAquaNumber)
        editSpan = findViewById(R.id.editSpan)
        editBodyWeight = findViewById(R.id.editBodyWeight)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun fetchAndSetAnimalData() {
        // 動物データをデータベースから取得
        animalData = databaseHelper.getAnimalByNumber(animalNumber) ?: return

        // 各フィールドに値をセット
        editType.setText(animalData.type)
        editName.setText(animalData.name)
        editBreed.setText(animalData.breed)
        editNickname.setText(animalData.nickname)
        editFurigana.setText(animalData.furigana)
        editRomanizedName.setText(animalData.romanizedName)
        editGender.setText(animalData.gender.toString())
        editBirthDate.setText(animalData.birthDate)
        editDoctor.setText(animalData.doctor)
        editNote.setText(animalData.note)
        editAquaNumber.setText(animalData.aquaNumber.toString())
        editSpan.setText(animalData.span ?: "")
        editBodyWeight.setText(animalData.bodyWeight ?: "")
    }

    private fun updateAnimalData() {
        // UIから入力値を取得して新しいデータを作成
        val updatedAnimal = AnimalDatabaseHelper.AnimalData(
            animalNumber = animalNumber,
            type = editType.text.toString(),
            name = editName.text.toString(),
            breed = editBreed.text.toString(),
            nickname = editNickname.text.toString(),
            furigana = editFurigana.text.toString(),
            romanizedName = editRomanizedName.text.toString(),
            gender = editGender.text.toString().toIntOrNull() ?: 0,
            birthDate = editBirthDate.text.toString(),
            doctor = editDoctor.text.toString(),
            note = editNote.text.toString(),
            age = "", // 年齢は計算されるため空
            aquaNumber = editAquaNumber.text.toString().toIntOrNull() ?: 0,
            span = editSpan.text.toString(),
            bodyWeight = editBodyWeight.text.toString()
        )

        // データベースを更新
        val result = databaseHelper.insertOrUpdateAnimal(updatedAnimal)

        if (result != -1L) {
            Toast.makeText(this, "データが更新されました！", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "更新に失敗しました。", Toast.LENGTH_SHORT).show()
        }
    }
}

