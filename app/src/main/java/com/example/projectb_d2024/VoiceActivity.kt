package com.example.projectb_d2024

import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.widget.TextView
import android.widget.Toast
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.content.Intent
import android.widget.Button
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Selection
import android.text.Spannable
import android.text.SpannableStringBuilder

class VoiceActivity : AppCompatActivity() {
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var textMemo: TextView
    private lateinit var strVoice: ImageButton
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var restartSpeechRecognitionRunnable: Runnable
    private var isListening = false


    @SuppressLint("MissingInflatedId", "MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice)

        //buttonの初期化
        textMemo = findViewById(R.id.textMemo)
        strVoice = findViewById(R.id.strVoice)
        val deleteButton: ImageButton = findViewById(R.id.deleteButton)
        val saveButton: ImageButton = findViewById(R.id.saveButton)

        // ボタンのクリックリスナー設定
        strVoice.setOnClickListener { startSpeechRecognition() }
        deleteButton.setOnClickListener { deleteSelectedText() }

        // CSV保存ボタンのクリックリスナー
        saveButton.setOnClickListener {
            val textToSave = textMemo.text.toString()
            if (textToSave.isNotEmpty()) {
                saveToCSV(textToSave)
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

        //csvの画面へ
        val nextButton : ImageButton = findViewById(R.id.nextButton)
        //val intent = Intent(this,遷移先::class.java)
        nextButton.setOnClickListener{
            val intent = Intent(this, CsvActivity::class.java)
            startActivity(intent)
        }

        // 音声認識の初期化
        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Toast.makeText(this@VoiceActivity, "音声認識の準備完了", Toast.LENGTH_SHORT).show()
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
                    isListening = false
                    initializeSpeechRecognizer() // エラー後に再初期化して再スタート準備
                }

                override fun onEndOfSpeech() { }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        val recognizedText = matches[0]

                        // 選択範囲の取得
                        val start = textMemo.selectionStart
                        val end = textMemo.selectionEnd
                        val existingText = textMemo.text.toString()

                        if (start >= 0 && end >= 0 && start != end) {
                            val editableText = SpannableStringBuilder(existingText)
                            editableText.replace(start, end, recognizedText)
                            textMemo.text = editableText
                        } else {
                            // 選択範囲がない場合は最後に追加
                            textMemo.text = existingText + "\n" + recognizedText
                        }
                    }
                    isListening = false
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun startSpeechRecognition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }

        if (!isListening) {
            initializeSpeechRecognizer() // 初期化
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                // 無音のタイムアウトを長く設定
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 60000L) // 1分
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 60000L) // 1分
            }

            isListening = true
            speechRecognizer?.startListening(intent)
        }
    }

    private fun deleteSelectedText() {
        val spannableText = textMemo.text as? Spannable ?: return // Spannableにキャスト
        val start = Selection.getSelectionStart(spannableText)
        val end = Selection.getSelectionEnd(spannableText)

        if (start != -1 && end != -1 && start != end) {
            val editableText = spannableText.toString().toEditable() // SpannableをEditableに変換
            editableText.delete(start, end)
            textMemo.text = editableText // 更新後のテキストをセット
            Toast.makeText(this, "選択したテキストを削除しました", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "削除する選択範囲がありません", Toast.LENGTH_SHORT).show()
        }
    }

    // String拡張関数でEditableに変換
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun saveToCSV(data: String) {
        val fileName = "speech_recognition_results.csv"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        try {
            val maxLines = 10
            val newContent = mutableListOf<String>()
            if (file.exists()) {
                file.forEachLine { line ->
                    newContent.add(line)
                }
            }
            newContent.add(data.replace("\n", ""))
            while (newContent.size > maxLines) {
                newContent.removeAt(0)
            }
            FileWriter(file).use { writer ->
                newContent.forEach { line ->
                    writer.append(line).append("\n")
                }
            }
            Toast.makeText(this, "結果をCSVファイルに保存しました", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "エラーが発生しました: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}