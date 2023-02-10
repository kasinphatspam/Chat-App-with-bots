package com.android.studio.azne.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class Chat : Serializable {
    var content: String? = null
    var dateSent: String? = null
    var timeSent: String? = null
    var messageId: String? = null
    var sender: String? = null
    var receiver: String? = null

    constructor(){}

    constructor (Content: String, DateSent: String, TimeSent: String, MessageId: String , Sender: String , Receiver: String ){
        content = Content
        dateSent = DateSent
        timeSent = TimeSent
        messageId = MessageId
        sender = Sender
        receiver = Receiver
    }

    fun getChat(dataSnapshot: DataSnapshot){

        val chat = dataSnapshot.getValue(Chat::class.java) as Chat

        content = chat.content
        dateSent = chat.dateSent
        timeSent = chat.timeSent
        messageId = chat.messageId
        sender = chat.sender
        receiver = chat.receiver

    }
}