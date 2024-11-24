package com.mck.contacts.ui.base

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


open class BaseContactViewModel(val dao: ContactDao) : ViewModel() {

    protected val _openImagePickerEvent = MutableLiveData<Boolean>()
    val openImagePickerEvent: LiveData<Boolean> get() = _openImagePickerEvent

    protected val _navigateToContacts = MutableLiveData<Boolean>()
    val navigateToContacts: LiveData<Boolean> get() = _navigateToContacts

    protected val _navigateToInfo = MutableLiveData<Boolean>()
    val navigateToInfo: LiveData<Boolean> get() = _navigateToInfo
    
    protected val _isInputValid = MutableLiveData<Boolean>(false)
    val isInputValid: LiveData<Boolean> get() = _isInputValid

    fun onUpdatePictureClick() {
        _openImagePickerEvent.value = true
    }

    fun resetImagePickerEvent() {
        _openImagePickerEvent.value = false
    }

    fun onNavigatedToContacts() {
        _navigateToContacts.value = false
    }

    fun onNavigatedToInfo() {
        _navigateToInfo.value = false
    }

    fun validateInputs(name: String, number: String, email: String) {
        _isInputValid.value = name.isNotBlank() &&
                number.isNotBlank() &&
                android.util.Patterns.PHONE.matcher(number).matches() &&
                (email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            file.absolutePath
        }
    }
}
