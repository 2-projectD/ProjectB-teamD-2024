package com.example.projectb_d2024

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GetDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_data)

        // レイアウト内の TextView を取得
        val textAnimalNumber = findViewById<TextView>(R.id.textAnimalNumber)
        val textString1 = findViewById<TextView>(R.id.textString1)
        val textString2 = findViewById<TextView>(R.id.textString2)
        val textInt1 = findViewById<TextView>(R.id.textInt1)
        val textInt2 = findViewById<TextView>(R.id.textInt2)

        // Intent からデータを取得
        val animalNumber = intent.getIntExtra("animalNumber", -1)
        val string1 = intent.getStringExtra("string1") ?: ""
        val string2 = intent.getStringExtra("string2") ?: ""
        val int1 = intent.getIntExtra("int1", 0)
        val int2 = intent.getIntExtra("int2", 0)

        // TextView にデータをセット
        textAnimalNumber.text = "Animal Number: $animalNumber"
        textString1.text = "String 1: $string1"
        textString2.text = "String 2: $string2"
        textInt1.text = "Int 1: $int1"
        textInt2.text = "Int 2: $int2"
    }
}
