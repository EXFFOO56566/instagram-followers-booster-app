package com.ohayo.core.ui.extensions

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

fun Fragment.findNavController(@IdRes id: Int): NavController? {
    val navHostFragment = childFragmentManager.findFragmentById(id) as? NavHostFragment ?: return null
    return navHostFragment.navController
}

private fun makeFragmentName(viewId: Int, id: Long): String {
    return "android:switcher:$viewId:$id"
}

fun Fragment.createTakePickerLauncher(callback: (Boolean) -> Unit): ActivityResultLauncher<Uri> {
    return registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        callback.invoke(isSuccess)
    }
}

fun Fragment.createSelectImageLauncher(callback: (Uri) -> Unit): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            callback.invoke(uri)
        }
    }
}

fun Fragment.createSelectMediaLauncher(callback: (Uri) -> Unit): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            callback.invoke(uri)
        }
    }
}

fun Fragment.createSelectMediasLauncher(callback: (List<Uri>) -> Unit): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNullOrEmpty().not()) {
            callback.invoke(uris)
        }
    }
}