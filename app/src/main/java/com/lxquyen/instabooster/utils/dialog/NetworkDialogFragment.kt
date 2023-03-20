package com.lxquyen.instabooster.utils.dialog

import androidx.fragment.app.DialogFragment
import com.lxquyen.instabooster.R

/**
 * Created by Furuichi on 10/7/2022
 */
class NetworkDialogFragment : DialogFragment(R.layout.dialog_network) {

    override fun getTheme(): Int {
        return R.style.NetworkDialogStyle
    }
}