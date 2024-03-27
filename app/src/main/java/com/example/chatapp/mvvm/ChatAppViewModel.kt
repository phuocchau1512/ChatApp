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

        val recentChat = hashMapOf<String,Any>(
            "friendId" to friend.userid.toString(),
            "time" to Utils.getCurrentTime(),
            "name" to friend.username.toString(),
            "friendImage" to friend.imageUrl.toString(),
            "sender" to Utils.getUiLoggedIn(),
            "message" to mess,
            "person" to "you",
            "status" to "Offline"
        )

        fireStore.collection("Conversation").document(Utils.getUiLoggedIn())
            .collection("LastMess").document(friend.userid)
            .set(recentChat)

        fireStore.collection("Conversation").document(friend.userid)
            .collection("LastMess").document(Utils.getUiLoggedIn())
            .update("message",mess,"time",Utils.getCurrentTime(),"person",name.value!!)
    }

    fun sendMessage( recentChats: RecentChats )
            = viewModelScope.launch(Dispatchers.IO) {



        val uniqueId = listOf( Utils.getUiLoggedIn(), recentChats.friendId.toString() ).sorted()
        uniqueId.joinToString(separator = "")

        val mess = message.value!!

        val chatHashMap = hashMapOf<String,Any>(
            "sender" to Utils.getUiLoggedIn(),
            "receiver" to recentChats.friendId!!,
            "content" to mess,
            "time" to Utils.getCurrentTime()
        )

        fireStore.collection("Message").document(uniqueId.toString())
            .collection("Chat").document(Utils.getCurrentTime()).set(chatHashMap)
        message.postValue("")

        val recentChat = hashMapOf<String,Any>(
            "friendId" to recentChats.friendId.toString(),
            "time" to Utils.getCurrentTime(),
            "name" to recentChats.name.toString(),
            "friendImage" to recentChats.friendImage.toString(),
            "sender" to Utils.getUiLoggedIn(),
            "message" to mess,
            "person" to "you",
            "status" to "Offline"
        )

        fireStore.collection("Conversation").document(Utils.getUiLoggedIn())
            .collection("LastMess").document(recentChats.friendId)
            .set(recentChat)

        fireStore.collection("Conversation").document(recentChats.friendId)
            .collection("LastMess").document(Utils.getUiLoggedIn())
            .update("message",mess,"time",Utils.getCurrentTime(),"person",name.value!!)
    }

    fun getMessages( friendId: Users):LiveData<List<Messages>> {
        return messageRepo.getMessage(friendId)
    }

    fun getMessages( friendId: RecentChats):LiveData<List<Messages>> {
        return messageRepo.getMessage(friendId)
    }

    fun getRecentChat(): LiveData<List<RecentChats>> {
        return chatListRepo.getAllChatList()
    }

    fun getUserFromId(userId:String) : LiveData<Users> {
        return usersRepo.getUserFromId(userId)
    }

}