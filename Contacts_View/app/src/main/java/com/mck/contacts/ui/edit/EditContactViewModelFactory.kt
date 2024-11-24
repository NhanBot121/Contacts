package com.mck.contacts.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mck.contacts.model.ContactDao

class EditContactViewModelFactory(private val contactId: Long,
                                  private val dao: ContactDao)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditContactViewModel::class.java)) {
            return EditContactViewModel(contactId, dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}