package com.android.studio.azne.senverity

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.studio.azne.R
import com.android.studio.azne.model.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_check_senverity.*
import java.text.SimpleDateFormat
import java.util.*

class CheckSenverityActivity : AppCompatActivity() {

    private lateinit var mRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_senverity)

        val cal = Calendar.getInstance()

        init(cal)
        setDropDownHowMuchOfAcne()
        setDropDownHowLong()
        setClickableSpan()

        nextButton.setOnClickListener {
            val howLong = howLongSpinner.selectedItem.toString()
            val howMuch = makeupSpinner.selectedItem.toString()

        }

        backImageButton.setOnClickListener {
            finish()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun init(cal: Calendar) {
        mAuth = FirebaseAuth.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            timeEditText.text = SimpleDateFormat("HH:mm").format(cal.time)
        }

        timeEditText.setOnClickListener {
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }

    private fun setClickableSpan() {
        val text = "I don't want to answer this question. Skip"
        val ss = SpannableString(text)

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@CheckSenverityActivity, "One", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = true
            }
        }

        ss.setSpan(clickableSpan1, 38, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        skipTextView.text = ss
        skipTextView.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun setDropDownHowLong() {
        val num = arrayOf("1 day","2 day","3 day","4 day","5 day","6 day","more than")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,num)

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        howLongSpinner.adapter = adapter
    }

    private fun setDropDownHowMuchOfAcne() {
        val num = arrayOf("Yes","No")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,num)

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        makeupSpinner.adapter = adapter
    }

    private fun listenerResultValue(key: String?) {
        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("result")
            .child(key!!)

        mRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = Result()
                result.getResult(dataSnapshot)
            }

        })
    }
}
