package com.rsd96.drive

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

/**
 * Created by Ramshad on 12/18/17.
 */
class ChatFeedAdapter(internal var context: Context, internal var chatList : MutableList<ChatSession>) : BaseAdapter() {


    var TAG = "ChatFeedAdapter"

    override fun getView(pos: Int, convertView: View?, viewGroup: ViewGroup?): View? {


         var from = ""
         var myId = ""

        val user1 = chatList[pos].user1
        var user2 = chatList[pos].user2
        Log.d(TAG, "${chatList[pos].user1} : ${chatList[pos].user2}")
        var v: View? = convertView
        var viewHolder = ChatFeedAdapter.ViewHolder()

        var database = FirebaseDatabase.getInstance().reference

        if (v == null) {
            val inflater = LayoutInflater.from(context)
            v = inflater.inflate(R.layout.chat_list_content, null)

            viewHolder.ivProfile = v.findViewById(R.id.iv_chat_list_profile)
            viewHolder.tvFrom = v.findViewById(R.id.tv_chat_list_from)
            viewHolder.tvMessage = v.findViewById(R.id.tv_chat_list_message)
            v.tag = viewHolder
        } else {
            viewHolder = v.tag as ChatFeedAdapter.ViewHolder
        }

        database.child("chats").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot?) {

                var finalMessage = Message()
                if (snap != null) {
                    Log.d(TAG, " CHILD : ${snap.child("${user1}${user2}").child("user1").value}")
                    if ( snap.child("${user1}${user2}").child("user1").value == FirebaseAuth.getInstance().currentUser?.uid) {
                        myId = snap.child("${user1}${user2}").child("user1").value.toString()
                        from = snap.child("${user1}${user2}").child("user2").value.toString()
                    } else {
                        myId = snap.child("${user1}${user2}").child("user2").value.toString()
                        from = snap.child("${user1}${user2}").child("user1").value.toString()
                    }

                    finalMessage = snap.child("${user1}${user2}").child("lastMessage").getValue(Message::class.java) as Message
                    viewHolder.tvMessage?.text = finalMessage.message
                }
                Log.d(TAG, "myId = ${myId} | from = ${from}")


                var storage: FirebaseStorage? = FirebaseStorage.getInstance()
                var storageReference: StorageReference? = storage?.reference
                Log.d(TAG, "${from}")
                val ref = storageReference?.child("profiles/${from}_profile.jpg")



                ref?.downloadUrl?.addOnSuccessListener { uri ->
                    Picasso
                            .with(context)
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
                            .into(viewHolder.ivProfile)
                }

                var fromName = ""
                database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot?) {
                        fromName = snap?.child("${from}")?.child("user_name")?.value.toString()
                        Log.d(TAG, "db : ${snap?.child("${from}")?.child("user_name")?.value.toString()} actual : ${fromName}")
                        viewHolder.tvFrom?.text = fromName
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })

                //viewHolder.tvMessage?.text = message

            }


            override fun onCancelled(p0: DatabaseError?) {

            }
        })






        return v
    }

    override fun getItem(pos: Int): Any {
        return chatList[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getCount(): Int = chatList.size


    internal class ViewHolder {
        var ivProfile: ImageView? = null
        var tvMessage: TextView? = null
        var tvFrom: TextView? = null
    }
}