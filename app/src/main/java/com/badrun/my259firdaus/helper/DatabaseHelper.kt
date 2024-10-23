package com.badrun.my259firdaus.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PAYMENTS")
        onCreate(db)
    }

    // Method to check if an orderId exists
    private fun orderIdExists(orderId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PAYMENTS, arrayOf(COLUMN_ORDER_ID),
            "$COLUMN_ORDER_ID=?", arrayOf(orderId),
            null, null, null
        )
        val exists = (cursor.count > 0)
        cursor.close()
        return exists
    }

    // Method to insert or update a payment record
    fun insertOrUpdatePayment(orderId: String, status: String?, grossAmount: String?, paymentType: String, timeOrder: String, expiryOrder: String, snapToken:String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ORDER_ID, orderId)
            put(COLUMN_STATUS, status)
            put(COLUMN_GROSS_AMOUNT, grossAmount)
            put(COLUMN_PAYMENT_TYPE, paymentType)
            put(COLUMN_TIME_ORDER, timeOrder)
            put(COLUMN_EXPIRY_ORDER, expiryOrder)
            put(COLOMN_SNAP_TOKEN, snapToken)
        }

        if (orderIdExists(orderId)) {
            // Update existing record
            db.update(TABLE_PAYMENTS, values, "$COLUMN_ORDER_ID=?", arrayOf(orderId))
        } else {
            // Insert new record
            db.insert(TABLE_PAYMENTS, null, values)
        }

        db.close()
    }

    val allPayments: Cursor
        get() {
            val db = this.readableDatabase
            return db.query(TABLE_PAYMENTS, null, null, null, null, null, null)
        }

    companion object {
        private const val DATABASE_NAME = "payment.db"
        private const val DATABASE_VERSION = 1

        // Table name
        const val TABLE_PAYMENTS = "payments"

        // Table columns
        const val COLUMN_ID = "_id"
        const val COLUMN_ORDER_ID = "order_id"
        const val COLUMN_STATUS = "status"
        const val COLUMN_GROSS_AMOUNT = "gross_amount"
        const val COLUMN_PAYMENT_TYPE = "payment_type"
        const val COLOMN_SNAP_TOKEN = "snap_token"
        const val COLUMN_TIME_ORDER = "transaction_time"
        const val COLUMN_EXPIRY_ORDER = "expiry_time"

        // Create table SQL statement
        private const val TABLE_CREATE =
            "CREATE TABLE $TABLE_PAYMENTS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_ORDER_ID TEXT UNIQUE, " +
                    "$COLUMN_STATUS TEXT, " +
                    "$COLUMN_GROSS_AMOUNT TEXT, " +
                    "$COLUMN_PAYMENT_TYPE TEXT, " +
                    "$COLUMN_TIME_ORDER TEXT, " +
                    "$COLUMN_EXPIRY_ORDER TEXT," +
                    "$COLOMN_SNAP_TOKEN TEXT" +
                    ");"
    }
}