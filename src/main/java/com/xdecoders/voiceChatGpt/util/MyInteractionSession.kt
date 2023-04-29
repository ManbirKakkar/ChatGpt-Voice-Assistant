package com.xdecoders.voiceChatGpt.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import androidx.annotation.RequiresApi
import com.xdecoders.voiceChatGpt.util.getSpeechRecognizerActivityIntent

@RequiresApi(Build.VERSION_CODES.O)
class MyInteractionSession(context: Context) : VoiceInteractionSession(context) {

    override fun onPrepareShow(args: Bundle?, showFlags: Int) {
        super.onPrepareShow(args, showFlags)
        setUiEnabled(false)
    }

    override fun onHandleAssist(state: AssistState) {
        super.onHandleAssist(state)
        startAssistantActivity(context.getSpeechRecognizerActivityIntent())
    }
}