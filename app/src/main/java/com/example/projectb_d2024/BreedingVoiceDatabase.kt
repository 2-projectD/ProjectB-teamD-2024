package com.example.projectb_d2024

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BreedingVoiceDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "breeding_voice.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
        CREATE TABLE breeding_voice_table (
            animalNumber INTEGER,
            string1 TEXT,
            string2 TEXT,
            int1 INTEGER,
            int2 INTEGER
        )
    """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS breeding_voice_table")
            onCreate(db)
        }
    }

    fun insertRecord(animalNumber: Int, string1: String, string2: String, int1: Int, int2: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("animalNumber", animalNumber)
            put("string1", string1)
            put("string2", string2)
            put("int1", int1)
            put("int2", int2)
        }
        val result = db.insert("BreedingVoiceTable", null, values)
        return result != -1L
    }

    fun getAllRecords(): List<BreedingVoiceRecord> {
        val records = mutableListOf<BreedingVoiceRecord>()
        val db = readableDatabase

        try {
            db.query(
                "breeding_voice_table",
                arrayOf("animalNumber", "string1", "string2", "int1", "int2"),
                null, null, null, null, null
            ).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val record = BreedingVoiceRecord(
                            animalNumber = cursor.getInt(cursor.getColumnIndexOrThrow("animalNumber")),
                            string1 = cursor.getString(cursor.getColumnIndexOrThrow("string1")),
                            string2 = cursor.getString(cursor.getColumnIndexOrThrow("string2")),
                            int1 = cursor.getInt(cursor.getColumnIndexOrThrow("int1")),
                            int2 = cursor.getInt(cursor.getColumnIndexOrThrow("int2"))
                        )
                        records.add(record)
                    } while (cursor.moveToNext())
                } else {
                    // データが空の場合のログ
                    println("No records found in breeding_voice_table.")
                }
            }
        } catch (e: Exception) {
            // 例外が発生した場合のログ
            e.printStackTrace()
        }
        return records
    }


    data class BreedingVoiceRecord(
        val animalNumber: Int,
        val string1: String,
        val string2: String,
        val int1: Int,
        val int2: Int
    )


}
