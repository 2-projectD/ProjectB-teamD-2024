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
    private var speechRecognizer : SpeechRecognizer? = null
    private lateinit var textMemo: TextView
    private lateinit var strVoice: ImageButton
    private lateinit var endVoice: ImageButton

    @SuppressLint("MissingInflatedId", "MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice)

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


        //以下音声認識のプログラム

        textMemo = findViewById(R.id.textMemo)
        strVoice = findViewById(R.id.strVoice)
        endVoice = findViewById(R.id.endVoice)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val granted = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream { textMemo.text = it })
        strVoice.setOnClickListener {
            speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
        }
        endVoice.setOnClickListener { speechRecognizer?.stopListening() }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
    }

    private fun createRecognitionListenerStringStream(onResult : (String)-> Unit) : RecognitionListener {
        return object : RecognitionListener {
            // The sound level in the audio stream has changed.
            override fun onRmsChanged(rmsdB: Float) {}

            // Called when the endpointer is ready for the user to start speaking.
            override fun onReadyForSpeech(params: Bundle) {
                onResult("onReadyForSpeech")
            }

            // More sound has been received.
            override fun onBufferReceived(buffer: ByteArray) {
                onResult("onBufferReceived")
            }

            // Called when partial recognition results are available.
            override fun onPartialResults(partialResults: Bundle) {
                onResult("onPartialResults")
            }

            // Reserved for adding future events.
            override fun onEvent(eventType: Int, params: Bundle) {
                onResult("onEvent")
            }

            // The user has started to speak.
            override fun onBeginningOfSpeech() {
                onResult("onBeginningOfSpeech")
            }

            // Called after the user stops speaking.
            override fun onEndOfSpeech() {
                onResult("onEndOfSpeech")
            }

            // A network or recognition error occurred.
            override fun onError(error: Int) {
                onResult("onError")
            }

            // Called when recognition results are ready.
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
                onResult("onResults " + stringArray.toString())
            }
        }
    }

    companion object {
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }
}
