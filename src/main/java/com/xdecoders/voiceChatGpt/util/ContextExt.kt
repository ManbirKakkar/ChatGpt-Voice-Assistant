package com.xdecoders.voiceChatGpt.util

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.provider.Settings
import com.xdecoders.voiceChatGpt.ui.MainActivity

fun Context.startSpeechRecognizerActivity() {
//    if (isRunning()) return

    startActivity(getSpeechRecognizerActivityIntent())
}

fun Context.getSpeechRecognizerActivityIntent(): Intent {
    return Intent(this, MainActivity::class.java).apply {
        flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
    }
}


fun Context.isSetAsDefaultAssistant(): Boolean {
    val setting = Settings.Secure.getString(contentResolver, "assistant")
    return if (setting != null) {
        ComponentName.unflattenFromString(setting)?.packageName == this.packageName
    } else false
}

private fun Context.isRunning(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)
    for (task in tasks) {
        if (packageName.equals(task.baseActivity!!.packageName, ignoreCase = true)) return true
    }
    return false
}