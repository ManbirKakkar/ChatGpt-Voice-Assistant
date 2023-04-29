package com.xdecoders.voiceChatGpt.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog


fun postDelayed(delayMillis: Long, task: () -> Unit) {
    Handler().postDelayed(task, delayMillis)
}

fun View.toggleVisibility(boolean: Boolean) = run { visibility = if (boolean) View.VISIBLE else View.GONE }


fun Context.isConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    connectivityManager?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnectedOrConnecting
        }
    }
    return false
}


fun Context.buildDialog(onOkClicked:()->Unit): AlertDialog.Builder {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("No Internet Connection")
    builder.setMessage("Please check your internet connection.")
    builder.setPositiveButton(
        "OK"
    ) { dialog, which -> onOkClicked.invoke() }
    return builder
}

fun EditText.addTextChangedListenerWithAction(onTextChanged: (text: String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
           onTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // TODO Auto-generated method stub
        }

        override fun afterTextChanged(s: Editable?) {
            // TODO Auto-generated method stub
        }
    })
}