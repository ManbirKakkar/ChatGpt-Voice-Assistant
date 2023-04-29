package com.xdecoders.voiceChatGpt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xdecoders.voiceChatGpt.databinding.ChatItemBinding
import com.xdecoders.voiceChatGpt.util.toggleVisibility

class MessageAdapter(private var messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.ChatItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatItemHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class ChatItemHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            when (message.sentBy) {
                Message.SEND_BY_ME-> {
                    binding.leftChatView.toggleVisibility(false)
                    binding.rightChatView.toggleVisibility(true)
                    binding.rightChatTextView.text = message.message
                }
                else -> {
                    binding.leftChatView.toggleVisibility(true)
                    binding.rightChatView.toggleVisibility(false)
                    binding.leftChatTextView.text = message.message
                }
            }
        }
    }
}
