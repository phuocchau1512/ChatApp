package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel: ViewModel() {

    private val _isHold = MutableLiveData<Boolean>()
    val isHold: LiveData<Boolean> get() = _isHold

    fun isHoldIcon(isHoldIcon:Boolean){
        this._isHold.value = isHoldIcon
    }

}