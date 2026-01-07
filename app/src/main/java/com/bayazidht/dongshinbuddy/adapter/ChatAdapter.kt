package com.bayazidht.dongshinbuddy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayazidht.dongshinbuddy.model.ChatMessage
import com.bayazidht.dongshinbuddy.databinding.ItemChatBinding

class ChatAdapter(private val chatList: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]

        with(holder.binding) {
            if (chat.isUser) {
                userContainer.visibility = View.VISIBLE
                buddyContainer.visibility = View.GONE
                userText.text = chat.message
            } else {
                buddyContainer.visibility = View.VISIBLE
                userContainer.visibility = View.GONE
                aiText.text = chat.message
            }
        }
    }

    override fun getItemCount() = chatList.size
}