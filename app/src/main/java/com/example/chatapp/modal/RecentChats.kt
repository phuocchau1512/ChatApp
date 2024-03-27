package com.example.chatapp.modal

import android.os.Parcel
import android.os.Parcelable

class RecentChats(
    val friendId: String? ="",
    val name: String? ="",
    val time: String? ="",
    val friendImage: String?="",
    val sender: String? ="",
    val message: String? ="",
    val person: String? ="",
    val status: String? =""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(friendId)
        parcel.writeString(name)
        parcel.writeString(time)
        parcel.writeString(friendImage)
        parcel.writeString(sender)
        parcel.writeString(message)
        parcel.writeString(person)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentChats> {
        override fun createFromParcel(parcel: Parcel): RecentChats {
            return RecentChats(parcel)
        }

        override fun newArray(size: Int): Array<RecentChats?> {
            return arrayOfNulls(size)
        }
    }
}