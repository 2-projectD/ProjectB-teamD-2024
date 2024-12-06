package com.example.projectb_d2024

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserSettingActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)

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

        // ユーザーマネージャの初期化
        userManager = UserManager(this)

        // 各ビューを取得
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val workInput = findViewById<EditText>(R.id.workInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // 登録ボタンが押されたときの処理
        registerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val work = workInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || work.isEmpty()) {
                Toast.makeText(this, "すべてのフィールドを入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (work != "医者" && work != "管理者" && work != "飼育員") {
                Toast.makeText(this, "職業は「医者」「管理者」「飼育員」のいずれかを入力してください", Toast.LENGTH_SHORT).show()
                workInput.text.clear()
                return@setOnClickListener
            }

            val isSuccess = userManager.registerUser(username, password, work)
            if (isSuccess) {
                Toast.makeText(this, "ユーザー登録成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "ユーザー登録失敗", Toast.LENGTH_SHORT).show()
            }
        }

        // ログインボタンが押されたときの処理
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "ユーザー名とパスワードを入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val work = userManager.authenticateUser(username, password)
            if (work != null) {
                Toast.makeText(this, "ログイン成功", Toast.LENGTH_SHORT).show()
                when (work) {
                    "管理者" -> {
                        val intent = Intent(this, MainActivity2::class.java)
                        startActivity(intent)
                    }
                    "医者", "飼育員" -> {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        Toast.makeText(this, "不明な職業です", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "ログイン失敗: ユーザー名またはパスワードが一致しません", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// データベースヘルパークラス
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password_hash TEXT,
                work TEXT
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "secure_database.db"
        const val DATABASE_VERSION = 1
    }
}

// ユーザーマネージャークラス
class UserManager(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // ユーザー登録
    fun registerUser(username: String, password: String, work: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password_hash", hashPassword(password))
            put("work", work)
        }
        return try {
            db.insert("users", null, values) != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // ユーザー認証
    fun authenticateUser(username: String, password: String): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT password_hash, work FROM users WHERE username = ?", arrayOf(username))

        var work: String? = null
        if (cursor.moveToFirst()) {
            val storedHash = cursor.getString(0)
            val inputHash = hashPassword(password)
            if (storedHash == inputHash) {
                work = cursor.getString(1) // 職業を取得
            }
        }

        cursor.close()
        db.close()
        return work
    }

    // パスワードをハッシュ化するメソッド（SHA-256）
    private fun hashPassword(password: String): String {
        return password // 必要であればハッシュ化処理を追加
    }
}
