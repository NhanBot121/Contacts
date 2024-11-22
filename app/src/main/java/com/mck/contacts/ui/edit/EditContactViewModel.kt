package com.mck.contacts.ui.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.Contact
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.launch
import java.io.File

class EditContactViewModel(val contactId: Long, val dao: ContactDao) : ViewModel() {

    val contact = dao.get(contactId)

    // will navigate to contact by id
    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList: LiveData<Boolean>
        get() = _navigateToList

    private val _navigateToContact = MutableLiveData<Boolean>(false)
    val navigateToContact: LiveData<Boolean>
        get() = _navigateToContact

    // LiveData to signal when to open the image picker
    private val _openImagePickerEvent = MutableLiveData<Boolean>()
    val openImagePickerEvent: LiveData<Boolean>
        get() = _openImagePickerEvent

    // LiveData to hold the selected image URI
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?>
        get() = _selectedImageUri


    fun onNavigatedToList() {
        _navigateToList.value = false
    }

    fun onNavigatedToContact() {
        _navigateToContact.value = false
    }

    // to update the uri
    fun updateSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun onUpdatePictureClick() {
        _openImagePickerEvent.value = true
    }

    // Reset the event to prevent multiple triggers
    fun resetImagePickerEvent() {
        _openImagePickerEvent.value = false
    }

    fun onSaveClick() {
        viewModelScope.launch {
            dao.update(contact.value!!)
            _navigateToList.value = true
        }
    }

    fun onCancelClick() {
        _navigateToList.value = true
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            dao.delete(contact.value!!)
            _navigateToList.value = true
        }
    }

    fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file.absolutePath // Save this path in the database
    }

    private val _isInputValid = MutableLiveData<Boolean>(false)
    val isInputValid: LiveData<Boolean> get() = _isInputValid

    // Validation logic
    fun validateInputs() {
        val name = contact.value?.name.orEmpty()
        val number = contact.value?.number.orEmpty()
        val email = contact.value?.email.orEmpty()

        // Check for empty fields or invalid formats
        _isInputValid.value = name.isNotBlank() &&
                number.isNotBlank() &&
                android.util.Patterns.PHONE.matcher(number).matches() &&
                (email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

//    fun updateContactPicture(context: Context, uri: Uri) {
//        val updatedContact = contact.value
//        updatedContact?.picture = saveImageToInternalStorage(context, uri)
//        contact.value = updatedContact!! // Trigger LiveData update
//    }
}