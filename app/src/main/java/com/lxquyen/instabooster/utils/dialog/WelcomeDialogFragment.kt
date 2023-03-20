package com.lxquyen.instabooster.utils.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.databinding.DialogWelcomeBinding
import com.ohayo.core.ui.extensions.setAvatarImage

/**
 * Created by Furuichi on 26/07/2022
 */
class WelcomeDialogFragment : DialogFragment(R.layout.dialog_welcome) {

    private lateinit var binding: DialogWelcomeBinding

    private val args: WelcomeDialogFragmentArgs by navArgs()
    private val user: User?
        get() = args.userArg

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWelcomeBinding.bind(view)
        setupView()
        bindState()
    }

    private fun bindState() {
        binding.viewInfo.apply {
            imgAvatar.setAvatarImage(user?.profilePicUrl)
            tvName.text = user?.displayName
            tvFollower.text = user?.followersText
            tvFollowing.text = user?.followingsText
            tvLike.text = "0"
        }

        binding.btnPositive.setOnClickListener {
            dismiss()
        }
    }

    private fun setupView() {
        binding.viewProfile.clipToOutline = true
    }

    override fun getTheme(): Int {
        return R.style.ConfirmDialogStyle
    }

}