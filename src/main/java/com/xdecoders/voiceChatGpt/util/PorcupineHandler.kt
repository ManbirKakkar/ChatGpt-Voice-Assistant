package com.xdecoders.voiceChatGpt.util

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineException
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.content.Context
import android.util.Log


object PorcupineHandler {

    private var porcupineManager: PorcupineManager? = null

    fun createPorcupine(
        context: Context,
        callback: PorcupineManagerCallback
    ) {
        try {
            porcupineManager = PorcupineManager.Builder()
                .setAccessKey(ACCESS_KEY)
                .setKeywords(
                    arrayOf(
                        Porcupine.BuiltInKeyword.ALEXA,
                        Porcupine.BuiltInKeyword.HEY_GOOGLE
                    )
                )
                .setSensitivities(floatArrayOf(0.7f, 0.7f))
                .build(context, callback)

            porcupineManager?.start()
        } catch (e: PorcupineException) {
            Log.e(TAG, e.toString())
        }
    }

    fun release() {
        try {
            porcupineManager?.apply {
                stop()
                delete()
            }
        } catch (e: PorcupineException) {
            Log.e(TAG, e.toString())
        } finally {
            porcupineManager = null
        }
    }

    private const val ACCESS_KEY = "K9cjEITZTsjV9uIbe0U25QCeatp77Ljh9ONW11anFVja9VCBNLjymg==" //K9cjEITZTsjV9uIbe0U25QCeatp77Ljh9ONW11anFVja9VCBNLjymg==
    private const val TAG = "PorcupineBuilder"
}

