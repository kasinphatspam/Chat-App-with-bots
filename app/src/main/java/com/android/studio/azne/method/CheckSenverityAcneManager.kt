package com.android.studio.azne.method

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.android.studio.azne.senverity.CheckSenverityActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CheckSenverityAcneManager (val context: Context) {

    private lateinit var mRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorageReference: StorageReference
    private lateinit var mFireStorage: FirebaseStorage

    fun uploadMoreDetails(strline: String){
        val sb = StringBuilder()

        for (c in strline.toCharArray()) {
            if (sb.isEmpty() || Character.isDigit(c)) sb.append(c)
        }

        val amount = Integer.parseInt(sb.toString())
    }

    @SuppressLint("SimpleDateFormat")
    fun uploadDefaultValueAndImage(filePath: Uri){

        mAuth = FirebaseAuth.getInstance()
        mFireStorage = FirebaseStorage.getInstance()
        mStorageReference = mFireStorage.reference

        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("result")

        val uuid = UUID.randomUUID().toString()
        val ref = mStorageReference.child("profile/$uuid")
        ref.putFile(filePath).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {

                //Date format
                val simpleTimeFormat = SimpleDateFormat("hh:mm")
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

                val time = simpleTimeFormat.format(Date())
                val date = simpleDateFormat.format(Date())
                val imageUrl = it.toString()
                val key = mRef.push().key.toString()
                val dangerLevel = "-"
                val percent = "-"
                val finalImageUrl = "-"
                val description = "-"

                val hashMap = HashMap<String,String>()
                hashMap["date"] = date
                hashMap["time"] = time
                hashMap["key"] = key
                hashMap["dangerLevel"] = dangerLevel
                hashMap["percent"] = percent
                hashMap["imageUrl"] = imageUrl
                hashMap["finalImageUrl"] = finalImageUrl
                hashMap["description"] = description

                mRef.child(key).setValue(hashMap).addOnCompleteListener { upload->
                    if(upload.isSuccessful){
                        val intent = Intent(context,
                            CheckSenverityActivity::class.java)
                        intent.putExtra("key",key)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }else{
                        Crashlytics.log(upload.exception.toString())
                    }
                }


            }
        }

    }
}