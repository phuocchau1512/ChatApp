package com.example.chatapp

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Utils {

    companion object{
        private val auth = FirebaseAuth.getInstance()
        private var userid: String= ""

        fun getUiLoggedIn():String{
            if ( auth.currentUser != null ){
                userid = auth.currentUser!!.uid
            }
            return userid
        }

        fun getCurrentTime():String {
            val currentTime = Calendar.getInstance().time
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.getDefault())
            return formatter.format(currentTime)
        }

        fun getDisplayTime(sendTime:String):String {
            val currentTime = Calendar.getInstance().time
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.getDefault())
            val currentStringTime = formatter.format(currentTime)

            return if ( currentStringTime.substring(0,10) == sendTime.substring(0,10) ) sendTime.substring(11,16)
            else (sendTime.substring(11,16)+","+sendTime.substring(0,10))
        }
    }
}