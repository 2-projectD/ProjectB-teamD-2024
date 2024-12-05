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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class BreedingVoiceActivity : AppCompatActivity() {


    private lateinit var breedingVoiceDatabase: BreedingVoiceDatabase
    private lateinit var animalDatabaseHelper: AnimalDatabaseHelper

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var textMemo: TextView
    private lateinit var strVoice: ImageButton
    private var isListening = false
    private lateinit var dbHelper: AnimalDatabaseHelper
    private lateinit var voiceFilePath: String
    private var animalNumber: Int = -1
    private var string1: String = ""
    private var string2: String = ""
    private var int1: Int = 0
    private val int2: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_breeding_voice)

        // インスタンス初期化
        breedingVoiceDatabase = BreedingVoiceDatabase(this)
        animalDatabaseHelper = AnimalDatabaseHelper(this)

        //buttonの初期化
        textMemo = findViewById(R.id.textMemo)
        strVoice = findViewById(R.id.strVoice)
        val deleteButton: ImageButton = findViewById(R.id.deleteButton)
        val saveButton: ImageButton = findViewById(R.id.saveButton)

        // ボタンのクリックリスナー設定
        strVoice.setOnClickListener { startSpeechRecognition() }
        deleteButton.setOnClickListener { deleteSelectedText() }
        saveButton.setOnClickListener { saveRecognizedTextToDatabase() }

        // DailyReportActivityからanimalNumberを受け取る
        animalNumber = intent.getIntExtra("animalNumber", -1)

        //データベース
        dbHelper = AnimalDatabaseHelper(this)
        voiceFilePath = "${filesDir.path}/voice_${animalNumber}.3gp" // 録音ファイルのパス

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
                    initializeSpeechRecognizer() // エラー後に再初期化して再スタート準備
                }

                override fun onEndOfSpeech() {}

                @SuppressLint("SetTextI18n")
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }

        if (!isListening) {
            initializeSpeechRecognizer()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 60000L)
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                    60000L
                )
            }

            isListening = true
            speechRecognizer?.startListening(intent)
        }
    }

    private fun convertToHiragana(input: String): String {
        // 入力を正規化（全角・半角を統一）
        val normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFKC)

        // カタカナをひらがなに変換
        val katakanaToHiraganaTransliterator = Transliterator.getInstance("Katakana-Hiragana")
        val kanaConverted = katakanaToHiraganaTransliterator.transliterate(normalized)

        // 漢字を含む文字をひらがなに変換
        val kanjiToHiraganaTransliterator = Transliterator.getInstance("Any-Hiragana")
        return kanjiToHiraganaTransliterator.transliterate(kanaConverted)
    }


    private fun getSpeechText(): String {
        return textMemo.text.toString()
    }

    private fun saveRecognizedTextToDatabase() {
        try {
            val speechText = getSpeechText().trim() // 音声認識結果を取得

            // 入力の分割処理（空白で分割）
            val parts = speechText.split("\\s+".toRegex())
            if (parts.size < 3) {
                Toast.makeText(this, "入力データが不完全です", Toast.LENGTH_SHORT).show()
                return
            }

            val string1 = parts[0] // string(1) => nickName
            val string2 = parts[1] // string(2) => その他の文字列
            val int1 = parts[2].toIntOrNull() ?: 0 // int(1)
            val int2 = 0

            // string1 をひらがなに変換してデータベースを照合
            val animalNumber = animalDatabaseHelper.getAnimalNumberByNickName(convertToHiragana(string1))

            if (animalNumber != -1) {
                // 照合成功時にデータを保存
                breedingVoiceDatabase.insertRecord(animalNumber, string1, string2, int1, int2)
                Toast.makeText(this, "データを保存しました", Toast.LENGTH_SHORT).show()
            } else {
                // 照合失敗時の処理
                Toast.makeText(this, "一致する動物が見つかりませんでした", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // エラー処理
            Toast.makeText(this, "エラーが発生しました: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
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

    override fun onDestroy() {
        super.onDestroy()
    }
}


