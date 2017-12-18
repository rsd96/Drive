package com.rsd96.drive

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat.*


/**
 * Created by Ramshad on 12/18/17.
 */
class ChatActivity: AppCompatActivity() {


    var chatSession = ChatSession()
    var database = FirebaseDatabase.getInstance().reference
    var TAG = "ChatActivity"

    // list to hold messages
    var chatList = mutableListOf<Message>()
    private var mAdapter: ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        // Get all data passed
        var data = intent.extras
        chatSession = data.getParcelable<ChatSession>("chat_parcel")
        var from = data.getString("from")
        var fromId = chatSession.user2
        var myId = chatSession.user1

        var mySendId = ""

        if (chatSession.user1 == FirebaseAuth.getInstance().currentUser?.uid) {
            mySendId = chatSession.user1
        } else {
            mySendId = chatSession.user2
        }

        Log.d(TAG, "$from")
        Log.d(TAG, "$fromId")

        // setup actionbar
        setSupportActionBar(toolbar_chat_view)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "$from"


        rv_chat_message_board?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)


        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));
        mAdapter = ChatAdapter(applicationContext, chatList)
        rv_chat_message_board?.setAdapter(mAdapter)

        // Set icon (error : doesnt resize)
        /*
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()
        var storageReference: StorageReference? = storage?.reference
        Log.d(TAG, "${from}")
        val ref = storageReference?.child("profiles/${fromId}_profile.jpg")


        ref?.downloadUrl?.addOnSuccessListener { uri ->
            Picasso
                    .with(applicationContext)
                    .load(uri.toString())
//                    .networkPolicy(NetworkPolicy.OFFLINE)
//                    .into(viewHolder.ivProfile, object : Callback {
//                        override fun onSuccess() {
//                        }
//
//                        override fun onError() {
//                            Log.v("Picasso","Could not fetch image")
//                        }
//                    })
                    .into(object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                        }

                        override fun onBitmapFailed(errorDrawable: Drawable?) {

                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            val d = BitmapDrawable(resources, bitmap)
                            supportActionBar?.setIcon(d)
                        }
                    })
        }

        */

        btn_chat_send.setOnClickListener( { view ->
            var message = et_chat_type.text.toString().trim()

            if (message.isNotEmpty()) {
                var m = Message()
                m.message = message
                m.id = mySendId

                database.child("chats").child("${myId}${fromId}").child("messages").push().setValue(m)
                et_chat_type.text.clear()
            }
        })


        database.child("chats").child("${myId}${fromId}").child("messages").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snap: DataSnapshot?, p1: String?) {

                if (snap != null && snap.value != null) {
                    try {

                        Log.d(TAG, "new message ... ${snap.getValue().toString()}")
                        val model = snap.getValue(Message::class.java)
                        Log.d(TAG, "new message ... ${model?.message}")

                        if (model != null ) {

                            Log.d(TAG, "new message ... ${model?.message}")
                            chatList.add(model)
                            rv_chat_message_board?.scrollToPosition(chatList.size - 1)
                            mAdapter?.notifyItemInserted(chatList.size - 1)
                            mAdapter?.refreshData(chatList)
                            database.child("chats").child("${myId}${fromId}").child("lastMessage").setValue(model)
                        }
                    } catch (ex: Exception) {
                        Log.e(TAG, ex.message)
                    }

                }
            }

            override fun onChildRemoved(snap: DataSnapshot?) {

            }

            override fun onChildChanged(snap: DataSnapshot?, p1: String?) {

            }

            override fun onChildMoved(snap: DataSnapshot?, p1: String?) {

            }
            override fun onCancelled(snap: DatabaseError?) {

            }
        })

    }
}