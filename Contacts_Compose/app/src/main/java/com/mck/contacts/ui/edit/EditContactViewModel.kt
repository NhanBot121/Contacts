package com.mck.contacts.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.Contact
import com.mck.contacts.model.ContactDao
import com.mck.contacts.ui.shared.BaseContactViewModel
import kotlinx.coroutines.launch

class EditContactViewModel(contactId: Long, dao: ContactDao) : BaseContactViewModel(dao) {

    // Editable state for the contact
    private val _editableContact = MutableLiveData<Contact>()
    val editableContact : LiveData<Contact> = _editableContact

    init {
        // Load the contact into the editable state
        viewModelScope.launch {
            dao.get(contactId).observeForever { contact ->
                _editableContact.value = contact
            }
        }
    }

    // Update the editable state
    fun updateName(name: String) {
        _editableContact.value = _editableContact.value?.copy(name = name)
    }

    fun updateNumber(number: String) {
        _editableContact.value = _editableContact.value?.copy(number = number)
    }

    fun updateEmail(email: String) {
        _editableContact.value = _editableContact.value?.copy(email = email)
    }

    fun updatePicture(savedImagePath: String) {
        _editableContact.value = _editableContact.value?.copy(picture = savedImagePath)
    }

    fun onSaveClick() {
        viewModelScope.launch {
            _editableContact.value?.let { dao.update(it) }

            _navigateToContacts.value = true
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            _editableContact.value?.let { dao.delete(it) }
            _navigateToContacts.value = true
        }
    }

    fun onCancelClick() {
        _navigateToInfo.value = true
    }
}
