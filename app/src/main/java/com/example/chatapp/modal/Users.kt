package com.example.chatapp.modal

import android.os.Parcel
import android.os.Parcelable

data class Users(
    val imageUrl: String? = "",
    val status: String? = "",
    val userid: String? = "",
    val username: String? = ""
    // ghi giống tên các thành phần trong firestore và thư viện parcelable dùng để up gửi tin nhắn cho người dùng
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(status)
        parcel.writeString(imageUrl)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}