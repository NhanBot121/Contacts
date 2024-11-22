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
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.databinding.FragmentEditContactBinding
import com.mck.contacts.model.ContactDatabase
import com.mck.contacts.ui.add.AddContactViewModel
import com.mck.contacts.ui.add.AddContactViewModelFactory
import kotlin.getValue

class EditContactFragment : Fragment() {

    var _binding: FragmentEditContactBinding? = null
    val binding get() = _binding!!

    private lateinit var viewModel: EditContactViewModel

    // ActivityResultLauncher for selecting an image
    val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.contact.value?.picture = viewModel.saveImageToInternalStorage(requireContext(), uri)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditContactBinding.inflate(inflater, container, false)

        // Safely retrieve arguments
        val contactId = EditContactFragmentArgs.fromBundle(requireArguments()).contactId

        // Initialize ViewModel after arguments are available
        val dao = ContactDatabase.getInstance(requireActivity().application).contactDao
        val viewModelFactory = EditContactViewModelFactory(contactId, dao)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditContactViewModel::class.java]

        // get ref
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner //for data binding

        // Save button
        viewModel.navigateToContact.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val action = EditContactFragmentDirections.actionEditContactFragmentToContactInfoFragment(contactId)
                findNavController().navigate(action)

                viewModel.onNavigatedToContact()
            }
        }

        // Delete
        viewModel.navigateToList.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_editContactFragment_to_contactFragment)
                viewModel.onNavigatedToList()
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
        viewModel.validateInputs()

        viewModel.isInputValid.observe(viewLifecycleOwner) { isValid ->
            binding.saveButtonEdit.isEnabled = isValid
        }


        return binding.root
    }
}