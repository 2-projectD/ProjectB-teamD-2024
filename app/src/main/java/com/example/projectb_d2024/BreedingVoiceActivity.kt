package com.example.projectb_d2024

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import com.ibm.icu.text.Transliterator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.Selection
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class BreedingVoiceActivity : AppCompatActivity() {


    private lateinit var breedingVoiceDatabase: BreedingVoiceDatabase
    private lateinit var animalListView01: ListView
    private lateinit var animalDatabaseHelper: AnimalDatabaseHelper
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var textMemo: TextView
    private lateinit var strVoice: ImageButton
    private var isListening = false
    private lateinit var voiceFilePath: String
    private var animalNumber: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_breeding_voice)

        // インスタンス初期化
        breedingVoiceDatabase = BreedingVoiceDatabase(this)
        animalDatabaseHelper = AnimalDatabaseHelper(this)
        animalListView01 = ListView(this)

        //buttonの初期化
        textMemo = findViewById(R.id.textMemo)
        strVoice = findViewById(R.id.strVoice)
        val deleteButton: ImageButton = findViewById(R.id.deleteButton)
        val saveButton: ImageButton = findViewById(R.id.saveButton)

        // ボタンのクリックリスナー設定
        strVoice.setOnClickListener { startSpeechRecognition() }
        deleteButton.setOnClickListener { deleteSelectedText() }
        saveButton.setOnClickListener {
            saveRecognizedTextToDatabase()
            refreshListView()
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

        // DailyReportActivityからanimalNumberを受け取る
        animalNumber = intent.getIntExtra("animalNumber", -1)

        // 音声認識の初期化
        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Toast.makeText(this@BreedingVoiceActivity, "音声認識を開始", Toast.LENGTH_SHORT).show()
                }

                override fun onBeginningOfSpeech() {
                    Toast.makeText(this@BreedingVoiceActivity, "音声認識作動中", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@BreedingVoiceActivity, "エラーが発生しました: $errorMessage", Toast.LENGTH_SHORT).show()
                    isListening = false
                }

                override fun onEndOfSpeech() {}

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        val recognizedText = matches[0]
                        updateTextMemo(recognizedText)
                    }
                    isListening = false
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun updateTextMemo(recognizedText: String) {
        val start = textMemo.selectionStart
        val end = textMemo.selectionEnd
        val existingText = textMemo.text.toString()
        if (start >= 0 && end >= 0 && start != end) {
            val editableText = SpannableStringBuilder(existingText)
            editableText.replace(start, end, recognizedText)
            textMemo.text = editableText
        } else {
            textMemo.text = existingText + "\n" + recognizedText
        }
    }

    private fun startSpeechRecognition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }

        if (!isListening) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            isListening = true
            speechRecognizer?.startListening(intent)
        }
    }

    private fun saveRecognizedTextToDatabase() {
        val speechText = textMemo.text.toString().trim()
        val parts = speechText.split("\\s+".toRegex())
        if (parts.size < 3) {
            Toast.makeText(this, "入力データが不完全です", Toast.LENGTH_SHORT).show()
            return
        }

        val string1 = parts[0]
        val string2 = parts[1]
        val int1 = parts[2].toIntOrNull() ?: 0
        val int2 = 0
        val animalNumber = animalDatabaseHelper.getAnimalNumberByNickName(convertToHiragana(string1))

        if (animalNumber != -1) {
            breedingVoiceDatabase.insertRecord(animalNumber, string1, string2, int1, int2)
            Toast.makeText(this, "データを保存しました", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "一致する動物が見つかりませんでした", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshListView() {
        val records = breedingVoiceDatabase.getAllRecords()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, records.map { it.toString() })
        animalListView01.adapter = adapter
    }

    private fun deleteSelectedText() {
        val spannableText = textMemo.text as? Spannable ?: return
        val start = Selection.getSelectionStart(spannableText)
        val end = Selection.getSelectionEnd(spannableText)

        if (start != -1 && end != -1 && start != end) {
            val editableText = spannableText.toString().toEditable()
            editableText.delete(start, end)
            textMemo.text = editableText
            Toast.makeText(this, "選択したテキストを削除しました", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "削除する選択範囲がありません", Toast.LENGTH_SHORT).show()
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun convertToHiragana(input: String): String {
        val normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFKC)
        val katakanaToHiraganaTransliterator = Transliterator.getInstance("Katakana-Hiragana")
        return katakanaToHiraganaTransliterator.transliterate(normalized)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}