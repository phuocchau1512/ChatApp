package com.example.chatapp.modal

import android.os.Parcel
import android.os.Parcelable

data class Messages(
    val sender: String? ="",
    val receiver:String? ="",
    val content:String? ="",
    val time:String? =""
):Parcelable{
    val id:String get() = "$sender-$receiver-$content-$time"

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sender)
        parcel.writeString(receiver)
        parcel.writeString(content)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Messages> {
        override fun createFromParcel(parcel: Parcel): Messages {
            return Messages(parcel)
        }

        override fun newArray(size: Int): Array<Messages?> {
            return arrayOfNulls(size)
        }
    }
}