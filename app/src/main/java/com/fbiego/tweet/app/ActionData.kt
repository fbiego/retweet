package com.fbiego.tweet.app

class ActionData (
    var id: Int,
    var text: String,
    var buttonText: String,
    var buttonIcon: Int,
    var actionable: Boolean,
    var complete: Boolean
        ){
    var longClick = false
}