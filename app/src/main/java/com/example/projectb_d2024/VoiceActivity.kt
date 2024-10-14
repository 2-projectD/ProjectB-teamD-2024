package com.example.projectb_d2024

import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileWriter
import java.io.IOException
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.app.AlertDialog
import android.widget.TextView
import android.widget.Toast
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.content.Intent
import android.view.View
import android.widget.Button
import java.util.*
import androidx.activity.enableEdgeToEdge
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class VoiceActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textView: TextView
    private lateinit var saveButton: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice)

        val back : ImageButton = findViewById(R.id.back)

        //3)戻るボタン（アクティビティの終了）
        back.setOnClickListener{
            finish()
        }

        //1) Viewの取得(連絡ボタン)
        val contact : ImageButton = findViewById(R.id.contact)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
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

        //1) Viewの取得(メニュー画面へボタン)
        val btnVoice : ImageButton = findViewById(R.id.btnVoice)

        //2) ボタンを押したら次の画面へ
        //val intent = Intent(this,遷移先::class.java)
        btnVoice.setOnClickListener{
            val intent = Intent(this, VoiceActivity::class.java)
            startActivity(intent)
        }


        //以下音声認識のプログラム

            val startButton: ImageButton = findViewById(R.id.strVoice)
            val stopButton: ImageButton = findViewById(R.id.endVoice)
            textView = findViewById(R.id.textMemo)

            // 戻るボタン
            back.setOnClickListener {
                finish()
            }

            // 音声認識の初期化
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Toast.makeText(this@VoiceActivity, "音声認識の準備完了", Toast.LENGTH_SHORT).show()
                }

                override fun onBeginningOfSpeech() {
                    Toast.makeText(this@VoiceActivity, "話し始めてください", Toast.LENGTH_SHORT).show()
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    Toast.makeText(this@VoiceActivity, "話し終わりました", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: Int) {
                    Toast.makeText(this@VoiceActivity, "エラーが発生しました: $error", Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        textView.text = matches[0] // 認識したテキストを表示
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            // ボタンのクリックリスナー設定
            startButton.setOnClickListener {
                startSpeechRecognition()
            }

            stopButton.setOnClickListener {
                stopSpeechRecognition()
            }



            // ウィンドウインセットの設定
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }




        private fun startSpeechRecognition() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                return
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP") // 日本語
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer.startListening(intent)
        }

        private fun stopSpeechRecognition() {
            speechRecognizer.cancel()
            Toast.makeText(this, "音声認識を停止しました", Toast.LENGTH_SHORT).show()
        }

        override fun onDestroy() {
            super.onDestroy()
            speechRecognizer.destroy() // SpeechRecognizerのリソースを解放
        }
}