package com.android.studio.azne.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class User : Serializable{

    var email: String? = null
    var username: String? = null
    var telephone: String? = null
    var key: String? = null

    constructor(){}

    constructor (Email: String, Username: String, Telephone: String, Key: String){
        email = Email
        username = Username
        telephone = Telephone
        key = Key
    }

    fun getProfile (dataSnapshot: DataSnapshot){
        val user = dataSnapshot.getValue(User::class.java) as User

        email = user.email
        username = user.username
        telephone = user.telephone
        key = user.key
    }
}