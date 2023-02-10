package com.android.studio.azne.method

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.android.studio.azne.MainActivity
import com.android.studio.azne.dialog.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthManager(val activity: Activity,val context: Context) {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mRef: DatabaseReference
    private var loadingDialog = LoadingDialog(context)
    private var toastManager: ToastManager =
        ToastManager(context)

    fun signIn(email: String ,password: String){
        loadingDialog.show()

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                val intent = Intent(context,MainActivity::class.java)
                intent.putExtra("userUid",mAuth.currentUser!!.uid)
                loadingDialog.cancel()
                activity.finish()
                context.startActivity(intent)
            }else{
                toastManager.signInIsNotComplete()
                loadingDialog.cancel()
            }
        }
    }

    fun signUp(email: String,password: String,username: String, telephone: String){
        loadingDialog.show()

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                pushUserDetails(email,username,telephone)
                val intent = Intent(context,MainActivity::class.java)
                intent.putExtra("userUid",mAuth.currentUser!!.uid)
                loadingDialog.cancel()
                activity.finish()
                context.startActivity(intent)
            }else{
                toastManager.signInIsNotComplete()
                loadingDialog.cancel()
            }
        }
    }

    private fun pushUserDetails(
        email: String,
        username: String,
        telephone: String
    ) {
        mRef = FirebaseDatabase.getInstance().reference.child("user")
        val key = mAuth.currentUser!!.uid

        val hashMap = HashMap<String,String>()
        hashMap["key"] = key
        hashMap["email"] = email
        hashMap["username"] = username
        hashMap["telephone"] = telephone

        mRef.child(key).setValue(hashMap)
    }
}