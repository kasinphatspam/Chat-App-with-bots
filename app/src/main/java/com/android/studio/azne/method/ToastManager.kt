package com.android.studio.azne.method

import android.content.Context
import android.widget.Toast
import com.android.studio.azne.R

class ToastManager (private val context: Context){

    fun notNull(){
        toastMakeTextLengthShort(R.string.notnull)
    }

    fun signInIsNotComplete(){
        toastMakeTextLengthShort(R.string.auth_signin_not_complete)
    }

    private fun toastMakeTextLengthShort(text : Int){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
    }
}