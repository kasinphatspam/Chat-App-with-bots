package com.android.studio.azne.method

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import com.android.studio.azne.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatManager(private val context: Context, private val userUid: String){

    private var mRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("user").child(userUid)

    @SuppressLint("SimpleDateFormat")
    fun pushMessage(content: String) {
        val hashMap = HashMap<String,String>()
        val messageId = mRef.push().key.toString()
        //Date Format
        val simpleTimeFormat = SimpleDateFormat("hh:mm")
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val currentTime = simpleTimeFormat.format(Date())
        val currentDate = simpleDateFormat.format(Date())
        hashMap["messageId"] = messageId
        hashMap["content"] = content
        hashMap["dateSent"] = currentDate
        hashMap["timeSent"] = currentTime
        hashMap["sender"] = userUid
        hashMap["receiver"] = "Ai"
        mRef.child("chat-ai").child(messageId).setValue(hashMap)
    }

    fun updateMessage(){

    }

    fun deleteMessage(){

    }

    //Ai Message (Backend for chat bot)

    fun pushMessageAi(content: String) {
        val hashMap = HashMap<String,String>()
        val messageId = mRef.push().key.toString()
        //Date Format
        val simpleTimeFormat = SimpleDateFormat("hh:mm")
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val currentTime = simpleTimeFormat.format(Date())
        val currentDate = simpleDateFormat.format(Date())
        hashMap["messageId"] = messageId
        hashMap["content"] = content
        hashMap["dateSent"] = currentDate
        hashMap["timeSent"] = currentTime
        hashMap["sender"] = "Ai"
        hashMap["receiver"] = userUid
        mRef.child("chat-ai").child(messageId).setValue(hashMap)
    }
}