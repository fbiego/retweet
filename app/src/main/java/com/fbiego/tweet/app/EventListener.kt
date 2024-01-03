package com.fbiego.tweet.app

interface EventListener {
    fun onTest(){}
    fun onRetweet(tweetData: TweetData){}
}

class EventReceiver {
    companion object {
        private var listeners = ArrayList<EventListener>()

        fun bindListener(listener: EventListener){
            listeners.add(listener)
        }
        fun unBindListener(listener: EventListener): Boolean{
            return listeners.remove(listener)
        }
    }

    fun sendRetweet(tweetData: TweetData){
        listeners.forEach{
            it.onRetweet(tweetData)
        }
    }
    fun sendTest(){
        listeners.forEach{
            it.onTest()
        }
    }
}