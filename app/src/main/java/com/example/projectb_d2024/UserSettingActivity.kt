package com.example.projectb_d2024

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.security.MessageDigest

// UserSettingActivity
class UserSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_setting)

        // ウィンドウインセットの調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

// DatabaseHelper
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password_hash TEXT
            )
            """
        )
        db.execSQL(
            """
            CREATE TABLE data (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                content TEXT
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS data")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "secure_database.db"
        const val DATABASE_VERSION = 1
    }
}

// UserManager
class UserManager(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // パスワードをハッシュ化するメソッド（SHA-256）
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return Base64.encodeToString(hash, Base64.DEFAULT).trim()
    }

    // ユーザー登録
    fun registerUser(username: String, password: String): Boolean {
        val db = dbHelper.writableDatabase
        val passwordHash = hashPassword(password)

        val values = ContentValues().apply {
            put("username", username)
            put("password_hash", passwordHash)
        }

        return try {
            db.insert("users", null, values)
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    // ユーザー認証
    fun authenticateUser(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT password_hash FROM users WHERE username = ?", arrayOf(username))

        var isAuthenticated = false
        if (cursor.moveToFirst()) {
            val storedHash = cursor.getString(0)
            val inputHash = hashPassword(password)
            isAuthenticated = storedHash == inputHash
        }

        cursor.close()
        db.close()
        return isAuthenticated
    }
}

// DataManager
class DataManager(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addData(content: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("content", content)
        }
        db.insert("data", null, values)
        db.close()
    }

    fun viewData(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT content FROM data", null)

        val data = mutableListOf<String>()
        while (cursor.moveToNext()) {
            data.add(cursor.getString(0))
        }

        cursor.close()
        db.close()
        return data
    }
}

// UMainActivity
class UMainActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)

        userManager = UserManager(this)
        dataManager = DataManager(this)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val addDataButton = findViewById<Button>(R.id.addDataButton)
        val viewDataButton = findViewById<Button>(R.id.viewDataButton)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            if (userManager.registerUser(username, password)) {
                Toast.makeText(this, "ユーザー登録成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "ユーザー登録失敗", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            if (userManager.authenticateUser(username, password)) {
                Toast.makeText(this, "ログイン成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "ログイン失敗", Toast.LENGTH_SHORT).show()
            }
        }

        addDataButton.setOnClickListener {
            val content = "保存するデータ"
            dataManager.addData(content)
            Toast.makeText(this, "データ保存成功", Toast.LENGTH_SHORT).show()
        }

        viewDataButton.setOnClickListener {
            val data = dataManager.viewData()
            Toast.makeText(this, "データ: ${data.joinToString(", ")}", Toast.LENGTH_LONG).show()
        }
    }
}