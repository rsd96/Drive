package com.rsd96.drive

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat_feed.*

/**
 * Created by Ramshad on 12/18/17.
 */
class ChatFeedFragment: Fragment() {



    lateinit var database: DatabaseReference
    var user: FirebaseUser? = null
    var chatList = mutableListOf<ChatSession>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_chat_feed, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        database.child("chats").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snap: DataSnapshot?) {
                if (pb_chat_feed != null)
                    pb_chat_feed.visibility = View.VISIBLE
                chatList.clear()
                if (snap != null) {
                    for (x in snap.children) {

                        if ( x.child("user1").value == user?.uid  || x.child("user2").value == user?.uid) {
                            var chatSession = ChatSession()
                            chatSession.user1 = x.child("user1").value.toString()
                            chatSession.user2 = x.child("user2").value.toString()
                            chatList.add(chatSession)
                        }
                    }

                    if (pb_chat_feed != null)
                        pb_chat_feed.visibility = View.GONE
                    var adapter = ChatFeedAdapter(activity, chatList)
                    lv_chat_feed.adapter = adapter
                }
            }
            override fun onCancelled(snap: DatabaseError?) {

            }
        })


        lv_chat_feed.setOnItemClickListener({ adapterView, view, i, l ->
            val adapter = adapterView.adapter
            val obj : ChatSession = adapter.getItem(i) as ChatSession


            var intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("chat_parcel", obj)
            startActivity(intent)

        })
    }
}

