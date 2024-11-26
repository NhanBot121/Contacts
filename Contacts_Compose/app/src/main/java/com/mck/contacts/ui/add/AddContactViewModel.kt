package com.mck.contacts.ui.add

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.launch
import com.mck.contacts.model.Contact
import com.mck.contacts.ui.shared.BaseContactViewModel

class AddContactViewModel(dao: ContactDao) : BaseContactViewModel(dao) {

    private val _newContact = MutableLiveData<Contact>(Contact())
    val newContact: LiveData<Contact> = _newContact

    val update = updateContact(_newContact)

    // Handle the button onClick
    fun onBackClick() {
        _navigateToContacts.value = true
    }
    fun onSaveClick() {
        // Insert new contact
        viewModelScope.launch {
            dao.insert(_newContact.value!!)
        }
        // Navigate back to contact list
        _navigateToContacts.value = true
    }
    fun onAddPictureClick() {
        _openImagePickerEvent.value = true
    }

}
