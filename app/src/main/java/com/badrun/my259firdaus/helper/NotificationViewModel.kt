package com.badrun.my259firdaus.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel : ViewModel() {

    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> get() = _unreadCount

    fun setUnreadCount(count: Int) {
        _unreadCount.value = count
    }

}