package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.Utils
import com.example.chatapp.modal.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatListRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatList():LiveData<List<RecentChats>> {

        val mainChatList = MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversation").document(Utils.getUiLoggedIn())
            .collection("LastMess")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener{ value, error ->

                if ( error !=  null ){
                    return@addSnapshotListener
                }

                val chatList = mutableListOf<RecentChats>()
                value?.forEach{document ->
                    val recentModel = document.toObject(RecentChats::class.java)

                    if ( recentModel.sender.equals(Utils.getUiLoggedIn()) ){
                        recentModel.let{
                            chatList.add(it)
                        }
                    }
                }
                mainChatList.value = chatList
            }
        return mainChatList
    }
}