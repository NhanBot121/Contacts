package com.mck.contacts.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mck.contacts.model.ContactDao

class ContactsViewModelFactory(private val dao: ContactDao)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            return ContactsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}