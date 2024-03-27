package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.Utils
import com.example.chatapp.modal.Users
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {

    private var firestore = FirebaseFirestore.getInstance()

    fun getUsers() : LiveData<List<Users>> {

        val users = MutableLiveData<List<Users>>()

        firestore.collection("User").addSnapshotListener{
            snapshot, exception->
            if ( exception != null ){
                return@addSnapshotListener
            }

            val usersList = mutableListOf<Users>()
            snapshot?.documents?.forEach{ documentSnapshot ->
                val user = documentSnapshot.toObject(Users::class.java)
                if ( user!!.userid != Utils.getUiLoggedIn() ){
                    user.let{
                        usersList.add(it)
                    }
                }
                users.value=usersList
            }

        }

        return users
    }

    fun getUserFromId(userId:String):LiveData<Users>{
        val user = MutableLiveData<Users>()

        val userRef = firestore.collection("User").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val currentUser = document.toObject(Users::class.java)
                    user.value = currentUser!!
                }
            }
            .addOnFailureListener { exception ->
                // Xử lý lỗi nếu có
                println("Error getting document: $exception")
            }
        return user
    }


}