package com.rsd96.drive

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Ramshad on 12/15/17.
 */
class Alert() : Parcelable{
    init {

    }
    var message : String = ""
    var vehicle : String = ""
    var time : String = ""
    var from : String = ""
    var uid : String = ""
    var fromId : String = ""

    constructor(parcel: Parcel) : this() {
        message = parcel.readString()
        vehicle = parcel.readString()
        time = parcel.readString()
        from = parcel.readString()
        uid = parcel.readString()
        fromId = parcel.readString()
    }


//    constructor(m : String, v: String, t: String, f: String) {
//        message = m
//        vehicle = v
//        time = t
//        from = f
//    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeString(vehicle)
        parcel.writeString(time)
        parcel.writeString(from)
        parcel.writeString(uid)
        parcel.writeString(fromId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alert> {
        override fun createFromParcel(parcel: Parcel): Alert {
            return Alert(parcel)
        }

        override fun newArray(size: Int): Array<Alert?> {
            return arrayOfNulls(size)
        }
    }

}