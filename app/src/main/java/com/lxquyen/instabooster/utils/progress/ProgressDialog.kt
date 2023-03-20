package com.lxquyen.instabooster.utils.progress

import androidx.fragment.app.DialogFragment
import com.lxquyen.instabooster.R

/**
 * Created by Furuichi on 08/07/2022
 */
class ProgressDialog : DialogFragment(R.layout.dialog_progress) {
    override fun getTheme(): Int {
        return R.style.ProgressDialogStyle
    }
}