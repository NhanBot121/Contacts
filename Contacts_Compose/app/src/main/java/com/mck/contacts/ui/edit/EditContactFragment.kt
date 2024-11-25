package com.mck.contacts.ui.edit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.model.ContactDatabase
import com.mck.contacts.ui.add.AddContactViewModel
import com.mck.contacts.ui.add.AddContactViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.draw.clip


class EditContactFragment : Fragment() {
    private lateinit var viewModel : EditContactViewModel

    // ActivityResultLauncher for selecting an image
    val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.viewModelScope.launch {
                val savedImagePath = viewModel.saveImageToInternalStorage(requireContext(), uri)
                viewModel.updateContactPicture(savedImagePath)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Safely retrieve arguments
        val contactId = EditContactFragmentArgs.fromBundle(requireArguments()).contactId
        setUpViewModel(contactId)

        setUpObservers(contactId)


        return ComposeView(requireContext()).apply {
            setContent{
                MaterialTheme {
                    Surface {
                        EditFragmentContent(viewModel)
                    }
                }
            }
        }
    }

    private fun setUpViewModel(contactId: Long) {
        // Initialize ViewModel after arguments are available
        val dao = ContactDatabase.getInstance(requireActivity().application).contactDao
        val viewModelFactory = EditContactViewModelFactory(contactId, dao)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditContactViewModel::class.java]
    }

    private fun setUpObservers(contactId: Long) {
        // Save button
        viewModel.navigateToInfo.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val action = EditContactFragmentDirections.actionEditContactFragmentToContactInfoFragment(contactId)
                findNavController().navigate(action)
                viewModel.onNavigatedToInfo()
            }
        }

        // Delete
        viewModel.navigateToContacts.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_editContactFragment_to_contactFragment)
                viewModel.onNavigatedToContacts()
            }
        }

        // Add Picture button click listener
        viewModel.openImagePickerEvent.observe(viewLifecycleOwner) { open ->
            if (open) {
                pickImageLauncher.launch("image/*")
                viewModel.resetImagePickerEvent()
            }
        }

    }
}

@Composable
fun EditFragmentContent(viewModel: EditContactViewModel) {
    val contact by viewModel.contact.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Save and Cancel Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { viewModel.onCancelClick() }) {
                Text("Cancel")
            }
            Button(onClick = { viewModel.onSaveClick() }) {
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            ProfilePictureEdit(
                imageUri = contact?.picture,
                onUpdatePictureClick = { viewModel.onUpdatePictureClick() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Contact Fields
            ContactEditFields(
                name = contact?.name.orEmpty(),
                onNameChange = { viewModel.contact.value?.name = it },
                phone = contact?.number.orEmpty(),
                onPhoneChange = { viewModel.contact.value?.number = it },
                email = contact?.email.orEmpty(),
                onEmailChange = { viewModel.contact.value?.email = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Delete Button
            IconButton(
                onClick = { viewModel.onDeleteClick() },
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = rememberAsyncImagePainter(model = android.R.drawable.ic_menu_delete),
                    contentDescription = "Delete Contact",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}


@Composable
fun ProfilePictureEdit(imageUri: String?, onUpdatePictureClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Display the image using Coil
        Image(
            painter = rememberAsyncImagePainter(model = imageUri),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onUpdatePictureClick) {
            Text("Update Picture")
        }
    }
}

@Composable
fun ContactEditFields(
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Field
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
    }
}

