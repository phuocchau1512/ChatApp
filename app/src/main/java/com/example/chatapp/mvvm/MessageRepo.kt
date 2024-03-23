package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.Utils
import com.example.chatapp.modal.Messages
import com.example.chatapp.modal.Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class MessageRepo {

    private val fireStore = FirebaseFirestore.getInstance()

    fun getMessage(friendId:Users):LiveData<List<Messages>>{

        val messages = MutableLiveData<List<Messages>>()

        val uniqueId = listOf(Utils.getUiLoggedIn(),friendId.userid!!).sorted()
        uniqueId.joinToString(separator = "")

        fireStore.collection("Message").document(uniqueId.toString())
            .collection("Chat")
            .orderBy("time",Query.Direction.ASCENDING).addSnapshotListener{value,error->
                if ( error != null ){

                    return@addSnapshotListener
                }

                val messageList = mutableListOf<Messages>()

                if ( !value!!.isEmpty ){

                    value.documents.forEach{document->
                        val messageMode = document.toObject(Messages::class.java)
                        messageMode.let { messageList.add(it!!) }
                    }

                }

                messages.value=messageList
            }

        return messages
    }


}