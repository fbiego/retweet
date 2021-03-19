package com.fbiego.tweet.utils

import android.app.Notification
import androidx.core.app.NotificationCompat
import timber.log.Timber

object NotificationUtils {

    fun getClickAction(n: Notification, text: String): Int?{

        for (i in 0 until NotificationCompat.getActionCount(n)){
            val action = NotificationCompat.getAction(n, i)
            Timber.w("Action = ${action.title}")
            if (action.remoteInputs != null) {
                for (element in action.remoteInputs) {
                    Timber.w("action no:$i  ${element.resultKey}")
                }
            }
        }

        for (i in 0 until NotificationCompat.getActionCount(n)) {
            val action = NotificationCompat.getAction(n, i)
            if (action.title.toString().equals(text, ignoreCase = true)){
                return i
            }
        }
        return null
    }

}