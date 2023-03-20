package com.ohayo.core.ui.extensions

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.createSelectImageLauncher(callback: (Uri) -> Unit): ActivityResultLauncher<String> {
    return activityResultRegistry.register("SelectImage", this, ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            callback.invoke(uri)
        }
    }
}

fun FragmentActivity.createSelectMediaLauncher(callback: (Uri) -> Unit): ActivityResultLauncher<Array<String>> {
    return activityResultRegistry.register("SelectMedia", ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            callback.invoke(uri)
        }
    }
}

fun FragmentActivity.createSelectMediasLauncher(callback: (List<Uri>) -> Unit): ActivityResultLauncher<Array<String>> {
    return activityResultRegistry.register("SelectMedia", ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNullOrEmpty().not()) {
            callback.invoke(uris)
        }
    }
}