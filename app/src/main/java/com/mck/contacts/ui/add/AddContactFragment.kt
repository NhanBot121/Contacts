package com.mck.contacts.ui.add

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.databinding.FragmentAddContactBinding
import com.mck.contacts.model.ContactDatabase
import kotlinx.coroutines.launch

class AddContactFragment : Fragment() {

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

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
                viewModel.newContactPicture.value = savedImagePath
            }
//            viewModel.newContactPicture.value = viewModel.saveImageToInternalStorage(requireContext(), uri)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false).apply {
            viewModel = this@AddContactFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupObservers()

        return binding.root
    }

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

        // Observe for valid input
        viewModel.isInputValid.observe(viewLifecycleOwner) { isValid ->
            binding.saveButton.isEnabled = isValid
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
