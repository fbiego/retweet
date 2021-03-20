package com.fbiego.tweet.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION ) {

    companion object {
        private const val DATABASE_NAME = "tweet_data"
        private const val DATABASE_VERSION = 1

        const val RETWEET_TABLE = "retweetTable"
        const val FOLLOW_TABLE = "followTable"

        const val COLUMN_ID = "_id"
        const val COLUMN_TIME = "time"
        const val COLUMN_ORIGIN = "origin"
        const val COLUMN_RETWEET = "retweet"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val createRetweetTable = ("CREATE TABLE $RETWEET_TABLE ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_ORIGIN TEXT, " +
                "$COLUMN_TIME INTEGER, $COLUMN_RETWEET TEXT)")

        val createFollowTable = ("CREATE TABLE $FOLLOW_TABLE ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_ORIGIN TEXT," +
                " $COLUMN_TIME INTEGER, $COLUMN_RETWEET TEXT)")
        p0?.execSQL(createRetweetTable)
        p0?.execSQL(createFollowTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun insertRetweet(tweetData: TweetData){
        val values = ContentValues()
        values.put(COLUMN_ID, System.currentTimeMillis())
        values.put(COLUMN_ORIGIN, tweetData.origin)
        values.put(COLUMN_TIME, tweetData.time)
        values.put(COLUMN_RETWEET, tweetData.retweet)

        val db = this.writableDatabase
        db.replace(RETWEET_TABLE, null, values)
        db.close()
    }

    fun insertFollow(tweetData: TweetData){
        val values = ContentValues()
        values.put(COLUMN_ID, System.currentTimeMillis())
        values.put(COLUMN_ORIGIN, tweetData.origin)
        values.put(COLUMN_TIME, tweetData.time)
        values.put(COLUMN_RETWEET, tweetData.retweet)

        val db = this.writableDatabase
        db.replace(FOLLOW_TABLE, null, values)
        db.close()
    }

    fun getLastRt(): ArrayList<TweetData>{
        val data = ArrayList<TweetData>()

        val query = "SELECT * FROM $RETWEET_TABLE ORDER BY $COLUMN_TIME DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()){
            data.add(TweetData(cursor.getLong(2), cursor.getString(1), cursor.getString(3)))
        }
        cursor.close()
        db.close()

        return data
    }

}