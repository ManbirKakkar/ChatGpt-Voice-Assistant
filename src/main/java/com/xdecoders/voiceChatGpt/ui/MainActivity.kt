package com.xdecoders.voiceChatGpt.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.xdecoders.voiceChatGpt.API
import com.xdecoders.voiceChatGpt.API_URL
import com.xdecoders.voiceChatGpt.adapter.Message
import com.xdecoders.voiceChatGpt.adapter.MessageAdapter
import com.xdecoders.voiceChatGpt.databinding.ActivityMainBinding
import com.xdecoders.voiceChatGpt.util.addTextChangedListenerWithAction
import com.xdecoders.voiceChatGpt.util.buildDialog
import com.xdecoders.voiceChatGpt.util.isConnected
import com.xdecoders.voiceChatGpt.util.isSetAsDefaultAssistant
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val messageList = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter

    var textToSpeech: TextToSpeech? = null

    private val JSON = "application/json; charset=utf-8".toMediaType()

    companion object {
        private const val ASR_PERMISSION_REQUEST_CODE = 0
    }

    private val voiceRecognitionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                results?.get(0)?.let { spokenText ->
                    onVoiceRecognized(spokenText)
                }
            }
        }

    private fun onVoiceRecognized(spokenText: String) {
        val question = spokenText.trim()
        addToChat(question, Message.SEND_BY_ME)
        callAPI(question)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        verifyAudioPermissions()


        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    stackFromEnd = true
                }
                messageAdapter = MessageAdapter(messageList)
                adapter = messageAdapter
            }

            sendBtn.isEnabled = false
            sendBtn.setOnClickListener {
                val question = messageTextText.text.toString().trim()
                addToChat(question, Message.SEND_BY_ME)
                messageTextText.text?.clear()
                callAPI(question)
            }

            messageTextText.addTextChangedListenerWithAction {
                sendBtn.isEnabled = it.trim().isNotEmpty()
            }

            record.setOnClickListener {
                startSpeechRecognitionIfPossible()
            }

        }

        textToSpeech = TextToSpeech(applicationContext) { i ->
            // if No error is found then only it will run
            if (i != TextToSpeech.ERROR) {
                textToSpeech!!.language = Locale.getDefault()
            }
        }

        if (!isConnected()) {
            buildDialog {
                finishAffinity()
            }.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ASR_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // audio permission granted
                Toast.makeText(this, "You can now use voice commands!", Toast.LENGTH_LONG).show()
            } else {
                // audio permission denied
                Toast.makeText(this, "Please provide microphone permission to use voice.", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun verifyAudioPermissions() {
        if (checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                ASR_PERMISSION_REQUEST_CODE
            )
        }else{
            startSpeechRecognitionIfPossible()
        }
    }

    private fun startSpeechRecognitionIfPossible() {
        when {
            isSetAsDefaultAssistant().not() -> {
                openVoiceInputSetting()
            }
            else -> {
                displaySpeechRecognizer()
            }
        }
    }


    private fun openVoiceInputSetting() {
        startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
    }

    private fun displaySpeechRecognizer() {
        val language = Locale.getDefault().language

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language)

        }

        try {
            voiceRecognitionResult.launch(intent)
        } catch (ignored: Exception) {
            Log.d("TAG", "displaySpeechRecognizer: $ignored")
            //getString(R.string.not_compatible)
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SEND_BY_BOT)
    }

    private fun callAPI(question: String) {
        messageList.add(Message("Typing...", Message.SEND_BY_BOT))

        val jsonBody = JSONObject().apply {
            put("model", "text-davinci-003")
            put("prompt", question)
            put("max_tokens", 4000)
            put("temperature", 0)
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        val requestBody = RequestBody.create(JSON, jsonBody.toString())
        val request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer $API")
            .post(requestBody)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val jsonObject = JSONObject(response.body?.string())
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.trim())

                        textToSpeech?.let {
                            it.speak(result,TextToSpeech.QUEUE_FLUSH,null);
                        }
                      //  startSpeechRecognitionIfPossible()
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                } else {
                    addResponse("Failed to load response due to ${response.body?.string()}")
                }
            }
        })
    }

}
