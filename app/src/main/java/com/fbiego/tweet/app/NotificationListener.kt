package com.fbiego.tweet.app

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.SpannableString
import androidx.preference.PreferenceManager
import com.fbiego.tweet.utils.NotificationUtils
import timber.log.Timber
import java.util.*
import com.fbiego.tweet.MainActivity as MN


class NotificationListener : NotificationListenerService() {

    /**
     * Implement this method to learn about new notifications as they are posted by apps.
     *
     * @param sbn A data structure encapsulating the original [android.app.Notification]
     * object as well as its identifying information (tag and id) and source
     * (package name).
     */

    private var bd = ""
    private var ttl = ""

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val ticker = notification?.tickerText
        val bundle: Bundle? = notification?.extras
        val title: String = when (val titleObj = bundle?.get("android.title")) {
            is String -> titleObj
            is SpannableString -> titleObj.toString()
            else -> null.toString()
        }
        val body: String = bundle?.getCharSequence("android.text").toString()

        if (sbn.packageName == "com.twitter.android"){
            val appInfo = applicationContext.packageManager.getApplicationInfo(
                sbn.packageName,
                PackageManager.GET_META_DATA
            )
            val appName = applicationContext.packageManager.getApplicationLabel(appInfo)
            Timber.w("onNotificationPosted {app=${appName},id=${sbn.id},ticker=$ticker,title=$title,body=$body,posted=${sbn.postTime},package=${sbn.packageName}}")


            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            var cleared = false

            if (pref.getBoolean(MN.PREF_AUTO_RETWEET, false)) {
                cleared = retweet(sbn, title, body)
            }
            if (pref.getBoolean(MN.PREF_AUTO_LIKE, false)) {
                cleared = like(sbn, title, body)
            }
            if (pref.getBoolean(MN.PREF_AUTO_FOLLOW, false)) {
                cleared = follow(sbn, title, body)
            }
            if(pref.getBoolean(MN.PREF_REMOVE, false) && !cleared){
                this.cancelNotification(sbn.key)
            }

        }
        if (sbn.packageName == "com.fbiego.tweet"){
            Timber.w("onNotificationPosted from retweet :id=${sbn.id},ticker=$ticker,title=$title,body=$body,posted=${sbn.postTime},package=${sbn.packageName}}")
            EventReceiver().sendTest()
        }

    }

    private fun String.hasWord(keywords: List<String>): Boolean {
        for (keyword in keywords) {
            if (this.contains(keyword, true)) return true
        }
        return false
    }

    private fun retweet(sbn: StatusBarNotification, title: String, body: String): Boolean{
        val click : Int? = NotificationUtils.getClickAction(sbn.notification, "retweet")
        if (click != null){
            Timber.w("Found retweet button")
            val delay = (1000..5000).shuffled().last().toLong()
            Handler(Looper.getMainLooper()).postDelayed({
                this.cancelNotification(sbn.key)
                //val ignore = body.hasWord(list)
                sbn.notification.actions[click].actionIntent.send()

                if (title != ttl && body != bd) {

                        val pref = PreferenceManager.getDefaultSharedPreferences(this)
                        val cur = pref.getInt(MN.PREF_RETWEETS, 0)
                        pref.edit().putInt(MN.PREF_RETWEETS, (cur + 1)).apply()

                    DBHandler(this, null, null, 1).insertRetweet(
                        TweetData(
                            System.currentTimeMillis(),
                            title,
                            body
                        )
                    )
                    bd = body
                    ttl = title
                }
            }, delay)
            return true
        }
        return false
    }

    private fun follow(sbn: StatusBarNotification, title: String, body: String): Boolean{
        val click : Int? = NotificationUtils.getClickAction(sbn.notification, "follow")
        if (click != null){
            Timber.w("Found follow button")

            val delay = (1000..5000).shuffled().last().toLong()
            Handler(Looper.getMainLooper()).postDelayed({
                this.cancelNotification(sbn.key)
                sbn.notification.actions[click].actionIntent.send()
                if (title != ttl && body != bd) {
                    val pref = PreferenceManager.getDefaultSharedPreferences(this)
                    val cur = pref.getInt(MN.PREF_FOLLOWS, 0)
                    pref.edit().putInt(MN.PREF_FOLLOWS, (cur + 1)).apply()
                    DBHandler(this, null, null, 1).insertFollow(
                        TweetData(
                            System.currentTimeMillis(),
                            title,
                            body
                        )
                    )

                    bd = body
                    ttl = title
                }
            }, delay)
            return true
        }
        return false
    }

    private fun like(sbn: StatusBarNotification, title: String, body: String): Boolean{
        val click : Int? = NotificationUtils.getClickAction(sbn.notification, "like")
        if (click != null){
            Timber.w("Found like button")

            val delay = (1000..5000).shuffled().last().toLong()
            Handler(Looper.getMainLooper()).postDelayed({
                this.cancelNotification(sbn.key)
                sbn.notification.actions[click].actionIntent.send()
                if (title != ttl && body != bd) {

                    bd = body
                    ttl = title
                }
            }, delay)
            return true
        }
        return false
    }

}