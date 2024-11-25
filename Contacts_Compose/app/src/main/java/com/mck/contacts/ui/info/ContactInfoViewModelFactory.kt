package com.mck.contacts.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mck.contacts.model.ContactDao

class ContactInfoViewModelFactory(private val contactId: Long,
                                  private val dao: ContactDao)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactInfoViewModel::class.java)) {
            return ContactInfoViewModel(contactId, dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}