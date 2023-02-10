package com.android.studio.azne.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.studio.azne.R
import com.android.studio.azne.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlin.collections.ArrayList

class ChatAdapter (private val chat : ArrayList<Chat>, private val context: Context, private val listener: EventListener)
    : RecyclerView.Adapter<ChatAdapter.ImageViewHolder>() {

    private lateinit var mRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1
    private val MSG_TYPE_LEFT_CHOICE = 2
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        if (viewType == MSG_TYPE_LEFT) {
            val v = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false)
            return ImageViewHolder(v)
        } else if (viewType == MSG_TYPE_LEFT_CHOICE) {
            val v =
                LayoutInflater.from(context).inflate(R.layout.chat_item_left_choice, parent, false)
            return ImageViewHolder(v)
        } else {
            val v = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false)
            return ImageViewHolder(v)
        }
    }

    override fun getItemCount(): Int {
        return chat.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent = chat[position]
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("chat-ai")
            .child(uploadCurrent.messageId.toString())
            .child("content")

        if (uploadCurrent.content == "MSG_CHOICE") {
            holder.messageTextView.text = context.getString(R.string.msg_choice)
        } else if (uploadCurrent.content == "MSG_CHOICE_SUCCESS") {
            holder.messageTextView.text = context.getString(R.string.msg_choice)
        } else if (uploadCurrent.content == "MSG_CHOICE_FAIL") {
            holder.messageTextView.text = context.getString(R.string.msg_choice_fail)
        } else if (uploadCurrent.content == "MSG_HISTORY_SHOW") {
            holder.messageTextView.text = context.getString(R.string.msg_history_show)
        } else if (uploadCurrent.content == "MSG_HISTORY_SHOW_ALREADY") {
            holder.messageTextView.text = context.getString(R.string.msg_history_show)
        } else if (uploadCurrent.content == "MSG_GRAPH_SHOW") {
            holder.messageTextView.text = context.getString(R.string.msg_graph_show)
        } else if (uploadCurrent.content == "MSG_GRAPH_SHOW_ALREADY") {
            holder.messageTextView.text = context.getString(R.string.msg_graph_show)
        } else {
            holder.messageTextView.text = uploadCurrent.content
        }

        holder.photoButton.setOnClickListener {
            listener.onPhotoButtonClick(true,uploadCurrent.messageId.toString())
        }

        holder.galleryButton.setOnClickListener {
            listener.onGalleryButtonClick(true,uploadCurrent.messageId.toString())
        }
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
        val photoButton = view.findViewById<Button>(R.id.photoButton)
        val galleryButton = view.findViewById<Button>(R.id.galleryButton)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        if (chat[position].sender.equals(firebaseUser.uid)) {
            return MSG_TYPE_RIGHT
        } else if (chat[position].content == "MSG_CHOICE") {
            return MSG_TYPE_LEFT_CHOICE
        } else {
            return MSG_TYPE_LEFT
        }
    }

    interface EventListener {
        fun onPhotoButtonClick(data: Boolean,messageId: String)
        fun onGalleryButtonClick(data: Boolean,messageId: String)
    }
}