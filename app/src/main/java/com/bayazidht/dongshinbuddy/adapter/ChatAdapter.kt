package com.bayazidht.dongshinbuddy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bayazidht.dongshinbuddy.model.ChatMessage
import com.bayazidht.dongshinbuddy.databinding.ItemChatBinding
import io.noties.markwon.Markwon

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
        val message = chat.message
        val markwon = Markwon.create(holder.itemView.context)

        with(holder.binding) {
            if (chat.isUser) {
                userContainer.visibility = View.VISIBLE
                aiContainer.visibility = View.GONE
                userText.text = message
            } else {
                aiContainer.visibility = View.VISIBLE
                userContainer.visibility = View.GONE

                if (message == "Thinking...") {
                    animateTyping(aiText)
                } else {
                    aiText.clearAnimation()
                    markwon.setMarkdown(aiText, message)
                }
            }
        }
    }

    private fun animateTyping(textView: TextView) {
        textView.text = "● ● ●"
        val bounceAnim = TranslateAnimation(
            0f, 0f,
            0f, -15f
        ).apply {
            duration = 400
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
            interpolator = CycleInterpolator(0.5f)
        }
        textView.startAnimation(bounceAnim)
    }


    override fun getItemCount() = chatList.size
}