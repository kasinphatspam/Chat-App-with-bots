package com.android.studio.azne.fragment

import ai.api.AIConfiguration
import ai.api.AIDataService
import ai.api.AIListener
import ai.api.AIServiceException
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.app.Fragment
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.studio.azne.GraphActivity
import com.android.studio.azne.HistoryActivity
import com.android.studio.azne.MainActivity
import com.android.studio.azne.R
import com.android.studio.azne.adapter.ChatAdapter
import com.android.studio.azne.method.ChatManager
import com.android.studio.azne.method.CheckSenverityAcneManager
import com.android.studio.azne.method.KeyboardManager
import com.android.studio.azne.method.ToastManager
import com.android.studio.azne.model.Chat
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.chat_bottomsheet.*
import kotlinx.android.synthetic.main.fragment_azne_chat_bot.*
import org.jetbrains.anko.runOnUiThread
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AzneChatBotFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AzneChatBotFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AzneChatBotFragment : Fragment(),AIListener,ChatAdapter.EventListener{

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mContext: Context
    private lateinit var toastManager: ToastManager
    private lateinit var chatManager: ChatManager
    private lateinit var mAuth: FirebaseAuth
    //QueryChat
    private lateinit var mUploads: ArrayList<Chat>
    private lateinit var mShowUploads: ArrayList<Chat>
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRef: DatabaseReference
    //Dialog Flow
    private lateinit var aiService: AIService
    private lateinit var aiDataAIService: AIDataService
    private val aiRequest = AIRequest()
    //Request Camera and Gallery
    private var filePath: Uri? = null
    private val REQUEST_TAKE_PHOTO = 1
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 2
    //BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var keyboardManager: KeyboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mContext = activity.applicationContext
        return inflater.inflate(R.layout.fragment_azne_chat_bot, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        initDialogFlowAPI()

        sendImageButton.setOnClickListener {
            sendMessage()
        }

        cameraImageButton.setOnClickListener {
            askCameraPermission()
        }

        galleryImageButton.setOnClickListener {
            selectImageInAlbum()
        }

        aboutConstraintLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            Handler().postDelayed({
                val text = activity.getString(R.string.msg_about)
                chatManager.pushMessage(text)
                aiRequestMessages(text)
            },200)
        }

        howToUseConstraintLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            Handler().postDelayed({
                val text = activity.getString(R.string.msg_how_to_use)
                chatManager.pushMessage(text)
                aiRequestMessages(text)
            },200)
        }

        historyConstraintLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            Handler().postDelayed({
                val text = activity.getString(R.string.msg_history)
                chatManager.pushMessage(text)
                aiRequestMessages(text)
            },200)
        }

        graphConstraintLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            Handler().postDelayed({
                val text = activity.getString(R.string.msg_graph)
                chatManager.pushMessage(text)
                aiRequestMessages(text)
            },200)
        }

        acneConstraintLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            Handler().postDelayed({
                val text = activity.getString(R.string.msg_acne)
                chatManager.pushMessage(text)
                aiRequestMessages(text)
            },200)
        }

        checkConstraintLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            Handler().postDelayed({
                val text = activity.getString(R.string.msg_check)
                chatManager.pushMessage(text)
                aiRequestMessages(text)
            },200)
        }

        menuImageButton.setOnClickListener {
            keyboardManager.hideKeyboard(activity)
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN){
                Handler().postDelayed({
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                },100)
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        inputEditText.setOnTouchListener { v, event ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            keyboardManager.showSoftKeyboard(v,activity)
            return@setOnTouchListener true
        }
    }

    private fun init(view: View?) {
        mAuth = FirebaseAuth.getInstance()
        toastManager = ToastManager(mContext)
        keyboardManager = KeyboardManager()
        chatManager = ChatManager(mContext, mAuth.currentUser!!.uid)
        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("chat-ai")

         val bottomSheet = view!!.findViewById<ConstraintLayout>(R.id.chatConstraintLayout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        val fabric = Fabric.Builder(mContext)
            .kits(Crashlytics())
            .debuggable(true) // Enables Crashlytics debugger
            .build()
        Fabric.with(fabric)
        queryChat()
    }

    private fun initDialogFlowAPI() {
        val aiConfiguration = ai.api.android.AIConfiguration("8609a18550a344c6bb33610b8bc69bf3",
            AIConfiguration.SupportedLanguages.English,
            ai.api.android.AIConfiguration.RecognitionEngine.System)

        aiService = AIService.getService(mContext, aiConfiguration)
        aiDataAIService = AIDataService(aiConfiguration)
    }

    private fun queryChat() {
        mRecyclerView = view!!.findViewById(R.id.azneChatBotRecyclerView)

        mUploads = ArrayList()

        mRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(mContext)
        linearLayoutManager.stackFromEnd = true
        mRecyclerView.layoutManager = linearLayoutManager

        mRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                //...
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUploads.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val upload = postSnapshot.getValue(Chat::class.java)
                    mUploads.add(upload!!)

                    if(upload.content == "MSG_CHOICE"){
                        val timer = object: CountDownTimer(10000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {}

                            override fun onFinish() {
                                mRef.child(upload.messageId.toString())
                                    .addListenerForSingleValueEvent(object : ValueEventListener{

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val chat = Chat()
                                        chat.getChat(dataSnapshot)
                                        if(chat.content == "MSG_CHOICE"){
                                            mRef.child(upload.messageId.toString())
                                                .child("content")
                                                .setValue("MSG_CHOICE_FAIL")
                                        }

                                    }

                                    override fun onCancelled(p0: DatabaseError) {}

                                })
                            }
                        }
                        timer.start()

                    } else if (upload.content == "MSG_HISTORY_SHOW"){

                        mRef.child(upload.messageId.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener{

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val chat = Chat()
                                chat.getChat(dataSnapshot)

                                if(chat.content == "MSG_HISTORY_SHOW"){
                                    val intent = Intent(mContext,HistoryActivity::class.java)
                                    mRef.child(upload.messageId.toString())
                                        .child("content")
                                        .setValue("MSG_HISTORY_SHOW_ALREADY")
                                    startActivity(intent)
                                }

                            }

                            override fun onCancelled(p0: DatabaseError) {}

                        })

                    } else if (upload.content == "MSG_GRAPH_SHOW") {
                        mRef.child(upload.messageId.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener {

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val chat = Chat()
                                chat.getChat(dataSnapshot)

                                if (chat.content == "MSG_GRAPH_SHOW") {
                                    val intent = Intent(mContext, GraphActivity::class.java)
                                    mRef.child(upload.messageId.toString())
                                        .child("content")
                                        .setValue("MSG_GRAPH_SHOW_ALREADY")
                                    startActivity(intent)
                                }

                            }

                            override fun onCancelled(p0: DatabaseError) {}

                        })
                    }
                }
                mShowUploads = ArrayList(mUploads)
                mAdapter = ChatAdapter(mShowUploads, mContext,this@AzneChatBotFragment)
                mRecyclerView.adapter = mAdapter
                mAdapter.notifyDataSetChanged()

                if(dataSnapshot.value == null){
                    chatManager.pushMessageAi("Hello I'm Azne bot")
                    chatManager.pushMessageAi("Azne is an application that helps diagnose acne\n" +
                            "\n" +
                            "** How to use **\n" +
                            "1. Open your camera\n" +
                            "2. Position your face to the centre of camera\n" +
                            "3. Take the photo\n" +
                            "4. Wait a seconds for processing\n" +
                            "5. Preview the result")
                }
            }
        })
    }

    private fun sendMessage() {
        if(checkNotNull(inputEditText)){
            val text = inputEditText.text.toString()
            chatManager.pushMessage(text)
            inputEditText.setText("")

            aiRequestMessages(text)

        }else{
            toastManager.notNull()
            Crashlytics.getInstance()
            Crashlytics.log("Test")
            aiService.startListening()
        }
    }

    private fun aiRequestMessages(text: String){
        //Ai request
        aiRequest.setQuery(text)

        object : AsyncTask<AIRequest, Void, AIResponse>() {

            override fun doInBackground(vararg aiRequests: AIRequest): AIResponse? {
                try {
                    return aiDataAIService.request(aiRequest)
                } catch (e: AIServiceException) { }

                return null
            }

            override fun onPostExecute(response: AIResponse?) {
                runOnUiThread {
                    if (response != null) {
                        val messageCount = response.result.fulfillment.messages.size
                        for (i in 0 until messageCount){
                            val result = response.result.fulfillment.speech
                            chatManager.pushMessageAi(result.toString())

                            if(result.toString() == activity.getString(R.string.msg_open_camera)){
                                askCameraPermission()
                            }else if ( result.toString() == activity.getString(R.string.msg_open_gallery)){
                                selectImageInAlbum()
                            }
                        }
                    }
                }
            }
        }.execute(aiRequest)
    }

    private fun checkNotNull(editText : EditText): Boolean {
        return (editText.length() != 0)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AzneChatBotFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AzneChatBotFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //Response Listener (DialogFlow API)
    override fun onResult(response: AIResponse?) {

        runOnUiThread {
            if (response != null) {
                val messageCount = response.result.fulfillment.messages.size
                for (i in 0 until messageCount){
                    val result = response.result.fulfillment.speech
                    chatManager.pushMessageAi(result.toString())
                }
            }
        }

    }

    override fun onListeningStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAudioLevel(level: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(error: AIError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListeningCanceled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListeningFinished() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //Interface between fragment and adapter

    override fun onPhotoButtonClick(data: Boolean, messageId: String) {
        if(data){
            mRef.child(messageId).child("content").setValue("MSG_CHOICE_SUCCESS")
            chatManager.pushMessage("Photo")
            aiRequestMessages("Photo")
        }
    }

    override fun onGalleryButtonClick(data: Boolean, messageId: String) {
        if(data){
            mRef.child(messageId).child("content").setValue("MSG_CHOICE_SUCCESS")
            chatManager.pushMessage("Gallery")
            aiRequestMessages("Gallery")
        }
    }

    //Function get image in gallery
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(mContext.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    //Function get image with camera
    private fun askCameraPermission(){
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                override fun onPermissionRationaleShouldBeShown
                            (permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?
                             , token: PermissionToken?) {

                    AlertDialog.Builder(mContext)
                        .setTitle(
                            "Permissions Error!")
                        .setMessage(
                            "Please allow permissions to take photo with camera")
                        .setNegativeButton(
                            android.R.string.cancel
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token?.cancelPermissionRequest()
                        }
                        .setPositiveButton(android.R.string.ok
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token?.continuePermissionRequest()
                        }
                        .setOnDismissListener {
                            token?.cancelPermissionRequest() }
                        .show()
                }


                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        //once permissions are granted, launch the camera
                        launchCamera()
                    } else {
                        Toast.makeText(mContext
                            , "All permissions need to be granted to take photo"
                            , Toast.LENGTH_LONG).show()
                    }
                }

            }).check()
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        filePath = mContext.contentResolver
            .insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(mContext.packageManager) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_CANCELED) {

            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
                try {

                    val checkSenverityAcneManager = CheckSenverityAcneManager(mContext)
                    checkSenverityAcneManager.uploadDefaultValueAndImage(filePath!!)

                } catch (e: IOException) {
                    Crashlytics.log(e.toString())
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }

            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == AppCompatActivity.RESULT_OK) {
                try {
                    filePath = data?.data!!

                    val checkSenverityAcneManager = CheckSenverityAcneManager(mContext)
                    checkSenverityAcneManager.uploadDefaultValueAndImage(filePath!!)

                } catch (e: IOException) {
                    Crashlytics.log(e.toString())
                }

            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
            } else {
                Toast.makeText(mContext, "Not allowed", Toast.LENGTH_SHORT).show()
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
