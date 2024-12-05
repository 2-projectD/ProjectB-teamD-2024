package com.example.projectb_d2024

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BreedingVoiceDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "breeding_voice.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "voice_data"
        private const val COLUMN_ANIMAL_NUMBER = "animalNumber"
        private const val COLUMN_STRING1 = "string1"
        private const val COLUMN_STRING2 = "string2"
        private const val COLUMN_INT1 = "int1"
        private const val COLUMN_INT2 = "int2"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ANIMAL_NUMBER INTEGER,
                $COLUMN_STRING1 TEXT,
                $COLUMN_STRING2 TEXT,
                $COLUMN_INT1 INTEGER,
                $COLUMN_INT2 INTEGER
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRecord(animalNumber: Int, string1: String, string2: String, int1: Int, int2: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ANIMAL_NUMBER, animalNumber)
            put(COLUMN_STRING1, string1)
            put(COLUMN_STRING2, string2)
            put(COLUMN_INT1, int1)
            put(COLUMN_INT2, int2) // 初期値を0に設定(認証に使うから)
        }

        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}