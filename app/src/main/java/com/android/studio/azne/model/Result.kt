package com.android.studio.azne.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class Result : Serializable {
    var date: String? = null
    var time: String? = null
    var dangerLevel: String? = null
    var accuracy: String? = null
    var imageUrl: String? = null
    var finalImageUrl: String? = null
    var description: String? = null

    constructor(){}

    constructor (Date: String, Time: String, DangerLevel: String, Accuracy: String, ImageUrl: String, FinalImageUrl: String,Description: String){
        date = Date
        time = Time
        dangerLevel = DangerLevel
        accuracy = Accuracy
        imageUrl = ImageUrl
        finalImageUrl = FinalImageUrl
        description = Description
    }

    fun getResult(dataSnapshot: DataSnapshot){

        val result = dataSnapshot.getValue(Result::class.java) as Result

        date = result.date
        time = result.time
        dangerLevel = result.dangerLevel
        accuracy = result.accuracy
        imageUrl = result.imageUrl
        finalImageUrl = result.finalImageUrl
        description = result.description

    }
}