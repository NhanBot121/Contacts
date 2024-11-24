package com.mck.contacts.utils

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.mck.contacts.R
import java.io.File

// Set the image URI on the ImageView
@BindingAdapter("imageUri")
fun setImageUri(view: ImageView, imageUri: String?) {
    val context = view.context

    if (!imageUri.isNullOrEmpty()) {
        try {
            val file = File(imageUri)
            if (file.exists()) {
                // Load the image from internal storage
                val uri = Uri.fromFile(file)
                view.setImageURI(uri)
            } else {
                // If the file doesn't exist, show a placeholder image
                view.setImageResource(R.drawable.avatar_placeholder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to placeholder in case of an error
            view.setImageResource(R.drawable.avatar_placeholder)
        }
    } else {
        // Show a placeholder image if the URI is null or empty
        view.setImageResource(R.drawable.avatar_placeholder)
    }
}

// Retrieve the image URI from the ImageView
@InverseBindingAdapter(attribute = "imageUri")
fun getImageUri(view: ImageView): String? {
    val uri = view.tag as? String // Use the tag to store the URI for simplicity
    return uri
}

// Listen for changes in the ImageView and notify the binding system
@BindingAdapter("imageUriAttrChanged")
fun setImageUriListener(view: ImageView, listener: InverseBindingListener?) {
    // Here, simulate a way to detect changes. Real implementation depends on your UI logic.
    view.setOnClickListener {
        // Simulate change detection for testing
        listener?.onChange()
    }
}

