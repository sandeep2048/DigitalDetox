package com.sanson.digitaldetox.ui.screens.messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanson.digitaldetox.data.db.AppDatabase
import com.sanson.digitaldetox.data.db.entity.CustomMessageEntity
import com.sanson.digitaldetox.data.repository.MessageRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MessageEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val messageRepo = MessageRepository(db.customMessageDao())

    val messages = messageRepo.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addMessage(text: String, timeStart: String? = null, timeEnd: String? = null) {
        viewModelScope.launch {
            messageRepo.addMessage(text, timeStart, timeEnd)
        }
    }

    fun updateMessage(entity: CustomMessageEntity) {
        viewModelScope.launch {
            messageRepo.updateMessage(entity)
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            messageRepo.deleteMessage(id)
        }
    }

    fun toggleActive(entity: CustomMessageEntity) {
        viewModelScope.launch {
            messageRepo.updateMessage(entity.copy(isActive = !entity.isActive))
        }
    }
}
