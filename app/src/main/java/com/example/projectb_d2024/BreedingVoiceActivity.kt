package com.example.projectb_d2024

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class BreedingVoiceActivity : AppCompatActivity() {

    private lateinit var strVoice: ImageButton
    private lateinit var endVoice: ImageButton
    private lateinit var textMemo: TextView
    private var voiceRecognizer: Any? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        setContentView(R.layout.activity_main)

        strVoice = findViewById(R.id.strVoice)
        endVoice = findViewById(R.id.endVoice)
        textMemo = findViewById(R.id.textMemo)

        // Pythonのインスタンス化
        val python = Python.getInstance()
        val pythonFile = python.getModule("vosk_recognizer")
        val modelPath = "path_to_model/vosk-model-small-ja-0.22"  // モデルのパス
        voiceRecognizer = pythonFile.callAttr("VoiceRecognizer", modelPath)

        // strVoiceボタンが押されたときに音声認識を開始
        strVoice.setOnClickListener {
            pythonFile.callAttr("start_recognition", voiceRecognizer, object : PythonCallable {
                override fun call(python: Python, args: Array<out Any>): Any {
                    val resultText = args[0] as String
                    runOnUiThread {
                        textMemo.text = resultText
                    }
                    return ""
                }
            })
        }

        // endVoiceボタンが押されたときに音声認識を停止
        endVoice.setOnClickListener {
            pythonFile.callAttr("stop_recognition", voiceRecognizer)
        }
    }
}