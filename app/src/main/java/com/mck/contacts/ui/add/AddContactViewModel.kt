package com.mck.contacts.ui.add

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.launch
import com.mck.contacts.model.Contact
import java.io.File

class AddContactViewModel(val dao: ContactDao) : ViewModel() {

    // Fields as LiveData for two-way binding
    val newContactName = MutableLiveData<String>()
    val newContactNumber = MutableLiveData<String>()
    val newContactEmail = MutableLiveData<String>()
    val newContactPicture = MutableLiveData<String>()

    init {
        // Observe changes and validate whenever any field changes
        newContactName.observeForever { validateInputs() }
        newContactNumber.observeForever { validateInputs() }
        newContactEmail.observeForever { validateInputs() }
    }

    // LiveData to handle to back event
    private var _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList : LiveData<Boolean> get() = _navigateToList

    // LiveData to signal when to open the image picker
    private val _openImagePickerEvent = MutableLiveData<Boolean>()
    val openImagePickerEvent: LiveData<Boolean> get() = _openImagePickerEvent

    fun onAddPictureClick() {
        _openImagePickerEvent.value = true
    }

    // Reset the event to prevent multiple triggers
    fun resetImagePickerEvent() {
        _openImagePickerEvent.value = false
    }



    private val _isInputValid = MutableLiveData<Boolean>(false)
    val isInputValid: LiveData<Boolean> get() = _isInputValid

    // Validation logic
    fun validateInputs() {
        val name = newContactName.value.orEmpty()
        val number = newContactNumber.value.orEmpty()
        val email = newContactEmail.value.orEmpty()

        // Check for empty fields or invalid formats
        _isInputValid.value = name.isNotBlank() &&
                number.isNotBlank() &&
                android.util.Patterns.PHONE.matcher(number).matches() &&
                (email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val contact = Contact().apply {
                name = newContactName.value.orEmpty()
                number = newContactNumber.value.orEmpty()
                email = newContactEmail.value.orEmpty()
                picture = newContactPicture.value.orEmpty()
            }
            dao.insert(contact)
        }
        _navigateToList.value = true
    }

    fun onBackClick() {
        _navigateToList.value = true
    }

    fun onNavigatedToList() {
        _navigateToList.value = false
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

}