package com.rsd96.drive

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Ramshad on 12/18/17.
 */
class ChatSession() : Parcelable {

    var user1 = ""
    var user2 = ""
    var lastMessage = Message()

    constructor(parcel: Parcel) : this() {
        user1 = parcel.readString()
        user2 = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user1)
        parcel.writeString(user2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatSession> {
        override fun createFromParcel(parcel: Parcel): ChatSession {
            return ChatSession(parcel)
        }

        override fun newArray(size: Int): Array<ChatSession?> {
            return arrayOfNulls(size)
        }
    }
}