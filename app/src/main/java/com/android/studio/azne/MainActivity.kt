package com.android.studio.azne

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.multidex.MultiDex
import com.android.studio.azne.fragment.AzneChatBotFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AzneChatBotFragment.OnFragmentInteractionListener {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        backImageButton.setOnClickListener {
            signOut()
        }

        reportImageButton.setOnClickListener {
            mRef.removeValue()
        }
    }

    private fun init() {
        setUpFirstFragment()

        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("chat-ai")
    }

    private fun setUpFirstFragment() {
        //Setup default fragment when this activity is open
        val fragment = AzneChatBotFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment,"Home")
        fragmentTransaction.commit()
    }

    private fun signOut() {
        if(mAuth.currentUser != null){
            mAuth.signOut()
            finish()
        }else{
            finish()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(baseContext)
    }
}
