package com.fbiego.tweet.app

interface EventListener {
    fun onTest()
    fun onRetweet()
}

class EventReceiver {
    companion object {
        private lateinit var mListener: EventListener
        var isInit = false
        fun bindListener(listener: EventListener){
            mListener = listener
            isInit = true
        }
        fun unBindListener(){
            isInit = false
        }
    }

    fun sendRetweet(){
        if(isInit) {
            mListener.onRetweet()
        }
    }
    fun sendTest(){
        if(isInit) {
            mListener.onTest()
        }
    }
}