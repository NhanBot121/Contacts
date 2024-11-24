package com.mck.contacts.ui.edit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
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
import com.mck.contacts.databinding.FragmentEditContactBinding
import com.mck.contacts.model.ContactDatabase
import com.mck.contacts.ui.add.AddContactViewModel
import com.mck.contacts.ui.add.AddContactViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class EditContactFragment : Fragment() {

    var _binding: FragmentEditContactBinding? = null
    val binding get() = _binding!!

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
        // Initialize ViewModel after arguments are available
        val dao = ContactDatabase.getInstance(requireActivity().application).contactDao
        val viewModelFactory = EditContactViewModelFactory(contactId, dao)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditContactViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentEditContactBinding.inflate(inflater, container, false).apply {
            viewModel = this@EditContactFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setUpObservers(contactId)

        return binding.root

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

        // Observe input validity
        viewModel.isInputValid.observe(viewLifecycleOwner) { isValid ->
            binding.saveButtonEdit.isEnabled = isValid
        }
    }
}