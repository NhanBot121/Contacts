package com.mck.contacts.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.ContactDao
import com.mck.contacts.ui.base.BaseContactViewModel
import kotlinx.coroutines.launch

class EditContactViewModel(contactId: Long, dao: ContactDao) : BaseContactViewModel(dao) {

    val contact = dao.get(contactId)


    fun onInfoUpdated() {
        validateInputs(
            contact.value?.name.orEmpty(),
            contact.value?.number.orEmpty(),
            contact.value?.email.orEmpty()
        )
    }

    fun updateContactPicture(savedImagePath: String) {
        contact.value?.let { currentContact ->
            currentContact.picture = savedImagePath
            viewModelScope.launch {
                dao.update(currentContact)
            }
        }
    }


    fun onSaveClick() {
        viewModelScope.launch {
            val updatedContact = contact.value?.copy (
                name = contact.value?.name.orEmpty(),
                number = contact.value?.number.orEmpty(),
                email = contact.value?.email.orEmpty(),
                picture = contact.value?.picture.orEmpty()
            )
            if (updatedContact != null) {
                dao.update(updatedContact)
            }
            _navigateToContacts.value = true
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            contact.value?.let { dao.delete(it) }
            _navigateToContacts.value = true
        }
    }

    fun onCancelClick() {
        _navigateToInfo.value = true
    }
}
