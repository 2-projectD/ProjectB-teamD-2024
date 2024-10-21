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
import android.Manifest.permission.RECORD_AUDIO
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
import android.media.Image
import androidx.core.app.ActivityCompat
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build

class VoiceActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textMemo: TextView
    private lateinit var strVoice: ImageButton
    private lateinit var endVoice: ImageButton

    @SuppressLint("MissingInflatedId", "MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice)

        //buttonの初期化
        textMemo = findViewById(R.id.textMemo)
        strVoice = findViewById(R.id.strVoice)
        endVoice = findViewById(R.id.endVoice)
        val saveButton: ImageButton = findViewById(R.id.saveButton)

        // ボタンのクリックリスナー設定
        strVoice.setOnClickListener { startSpeechRecognition()}
        endVoice.setOnClickListener { stopSpeechRecognition() }
        // CSV保存ボタンのクリックリスナー
        saveButton.setOnClickListener {
            val textToSave = textMemo.text.toString()
            if (textToSave.isNotEmpty()) {
                saveToCSV(textToSave) // CSVに保存
            } else {
                Toast.makeText(this, "保存するテキストがありません。", Toast.LENGTH_SHORT).show()
            }
        }

        val back : ImageButton = findViewById(R.id.back)

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

        // 戻るボタン
        back.setOnClickListener {
            finish()
        }



        /// 音声認識の初期化
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(this@VoiceActivity, "音声認識を開始", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {
                Toast.makeText(this@VoiceActivity, "話し始めてください", Toast.LENGTH_SHORT).show()
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "音声入力中にエラーが発生しました"
                    SpeechRecognizer.ERROR_CLIENT -> "クライアントエラーが発生しました"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "権限が不足しています"
                    SpeechRecognizer.ERROR_NETWORK -> "ネットワークエラーが発生しました"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ネットワークタイムアウトです"
                    SpeechRecognizer.ERROR_NO_MATCH -> "一致する結果がありません"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "認識システムがビジー状態です"
                    SpeechRecognizer.ERROR_SERVER -> "サーバーエラーが発生しました"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "音声入力のタイムアウトです"
                    else -> "不明なエラーが発生しました"
                }
                Toast.makeText(this@VoiceActivity, "エラーが発生しました: $errorMessage", Toast.LENGTH_SHORT).show()
            }

            override fun onEndOfSpeech() {}

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val existingText = textMemo.text.toString()
                    val newText = if (existingText.isNotEmpty()) {
                        "$existingText\n${matches[0]}"
                    } else {
                        matches[0]
                    }
                    textMemo.text = newText
                } else {
                    Toast.makeText(this@VoiceActivity, "一致する結果がありません。もう一度話してください。", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // ウィンドウインセットの設定
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // CSVファイルに保存する関数
    private fun saveToCSV(data: String) {
        // CSVファイル名
        val fileName = "speech_recognition_results.csv"

        // 外部ストレージへのパスを取得
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            // FileWriterを作成。trueを指定すると追記モードになります。
            val fileWriter = FileWriter(file, true)
            // データを追加
            fileWriter.append(data) // CSV形式でデータを追加
            fileWriter.append("\n") // 改行を追加
            fileWriter.flush() // バッファに残っているデータを強制的に書き出す
            fileWriter.close() // ファイルを閉じる

            // 保存完了メッセージ
            Toast.makeText(this, "結果をCSVファイルに保存しました", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace() // エラーのスタックトレースを表示
            Toast.makeText(this, "エラーが発生しました: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    // 音声認識を開始する
    private fun startSpeechRecognition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP") // 日本語
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000L) // 20秒の無音でも終了しない
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 20000L) // 20秒の無音でも終了しない
        }
        speechRecognizer.startListening(intent)
    }

    // 音声認識を停止する
    private fun stopSpeechRecognition() {
        speechRecognizer.stopListening()
        Toast.makeText(this, "音声認識を停止しました", Toast.LENGTH_SHORT).show()  // 停止メッセージ表示
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy() // SpeechRecognizerのリソースを解放
    }
}