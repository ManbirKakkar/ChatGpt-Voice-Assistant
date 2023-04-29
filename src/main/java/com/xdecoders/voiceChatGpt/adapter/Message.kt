package com.xdecoders.voiceChatGpt.adapter

class Message(var message: String, var sentBy: String) {

    companion object {
        @JvmField
        var SEND_BY_ME = "me"
        @JvmField
        var SEND_BY_BOT = "bot"
    }
}
