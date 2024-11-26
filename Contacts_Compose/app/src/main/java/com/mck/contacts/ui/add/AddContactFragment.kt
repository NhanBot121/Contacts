package com.mck.contacts.ui.add

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.model.ContactDatabase
import com.mck.contacts.ui.contacts.ContactsFragmentContent
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.runtime.livedata.observeAsState


class AddContactFragment : Fragment() {

    private val viewModel: AddContactViewModel by viewModels {
        AddContactViewModelFactory(ContactDatabase.getInstance(requireActivity().application).contactDao)
    }

    // Request permission launcher for external storage access
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ActivityResultLauncher for selecting an image
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.viewModelScope.launch {
                val savedImagePath = viewModel.saveImageToInternalStorage(requireContext(), uri)
                viewModel.newContact.value?.picture = savedImagePath
            }
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupObservers()

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface {
                        AddFragmentContent(viewModel)
                    }
                }
            }
        }    }

    private fun setupObservers() {
        // Observe the LiveData to trigger image picker
        viewModel.openImagePickerEvent.observe(viewLifecycleOwner) { shouldOpen ->
            if (shouldOpen) {
                handleImageSelection()
                viewModel.resetImagePickerEvent()
            }
        }

        // Observe the LiveData for back event (save or back)
        viewModel.navigateToContacts.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_addContactFragment_to_contactFragment)
                viewModel.onNavigatedToContacts()
            }
        }

    }

    private fun handleImageSelection() {
        // Check for required permission
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is granted, open the image picker
            pickImageLauncher.launch("image/*")
        } else {
            // Request permission
            requestPermissionLauncher.launch(permission)
        }
    }
}

@Composable
fun AddFragmentContent(viewModel: AddContactViewModel) {
//    val newContact by viewModel.newContact.observeAsState("")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Save and Back Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.onBackClick() }) {
                Text("Back")
            }
            Button(onClick = { viewModel.onSaveClick() }) {
                Text("Save")
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            ProfilePicture(imageUri = "imageUri", onAddPictureClick = {
                viewModel.onAddPictureClick()
            })

            Spacer(modifier = Modifier.height(16.dp))

            // Fields for name, phone, and email
            ContactFields(
                name = "",
                onNameChange = { viewModel.newContact.value?.name = it },
                phone = "",
                onPhoneChange = { viewModel.newContact.value?.number = it },
                email = "",
                onEmailChange = { viewModel.newContact.value?.email = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
fun ProfilePicture(imageUri: String?, onAddPictureClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Display the image using Coil
        Image(
            painter = rememberAsyncImagePainter(model = imageUri),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        // Add Picture Button
        Button(onClick = onAddPictureClick) {
            Text("Add Picture")
        }
    }
}

@Composable
fun ContactFields(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Field
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )
    }
}

