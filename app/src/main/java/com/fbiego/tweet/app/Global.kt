package com.fbiego.tweet.app

fun time(millis: Long): String{
    val sec = (System.currentTimeMillis() - millis)/1000
    return when {
        sec >= 3600 -> {
            "${sec/3600}h ${(sec%3600)/60}m"
        }
        sec >= 60 -> {
            "${sec/60}m ${sec%60}s"
        }
        else -> {
            "${sec}s"
        }
    }
}