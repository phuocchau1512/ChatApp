package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.Utils
import com.example.chatapp.modal.Messages
import com.example.chatapp.modal.RecentChats
import com.example.chatapp.modal.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message =  MutableLiveData<String>().apply { value="" }
    private val fireStore = FirebaseFirestore.getInstance()

    private val usersRepo = UsersRepo()
    private val messageRepo = MessageRepo()
    private val chatListRepo = ChatListRepo()


    init{
        getCurrentUser()
    }

    fun getUsers(): LiveData<List<Users>>{
        return usersRepo.getUsers()
    }

    private fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {


        fireStore.collection("User").document(Utils.getUiLoggedIn())
            .addSnapshotListener{ value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                if (value != null && value.exists()){
                    val user = value.toObject(Users::class.java)
                    name.value = user?.username!!
                    imageUrl.value = user.imageUrl!!
                }
            }
    }



    fun sendMessage( friend: Users )
        = viewModelScope.launch(Dispatchers.IO) {



        val uniqueId = listOf( Utils.getUiLoggedIn(), friend.userid.toString() ).sorted()
        uniqueId.joinToString(separator = "")

        val mess = message.value!!

        val chatHashMap = hashMapOf<String,Any>(
            "sender" to Utils.getUiLoggedIn(),
            "receiver" to friend.userid!!,
            "content" to mess,
            "time" to Utils.getCurrentTime()
        )

        fireStore.collection("Message").document(uniqueId.toString())
            .collection("Chat").document(Utils.getCurrentTime()).set(chatHashMap)
        message.postValue("")

        val conversationHashMap = hashMapOf<String,Any>(
            "friendId" to friend.userid.toString(),
            "time" to Utils.getCurrentTime(),
            "name" to friend.username.toString(),
            "friendImage" to friend.imageUrl.toString(),
            "sender" to Utils.getUiLoggedIn(),
            "message" to mess,
            "person" to "you"
        )

        fireStore.collection("Conversation").document(Utils.getUiLoggedIn())
            .collection("LastMess").document(friend.userid)
            .set(conversationHashMap)
    }

    fun getMessages( friendId: Users):LiveData<List<Messages>> {
        return messageRepo.getMessage(friendId)
    }

    fun getRecentChat(): LiveData<List<RecentChats>> {
        return chatListRepo.getAllChatList()
    }

}