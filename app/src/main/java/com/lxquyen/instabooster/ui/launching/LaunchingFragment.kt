package com.lxquyen.instabooster.ui.launching

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lxquyen.instabooster.BuildConfig
import com.lxquyen.instabooster.databinding.FragmentLaunchingBinding
import com.lxquyen.instabooster.ui.base.BaseFragment
import com.lxquyen.instabooster.ui.launching.LaunchingViewModel.UiAction.InputChanged
import com.lxquyen.instabooster.ui.launching.LaunchingViewModel.UiAction.Submit
import com.lxquyen.instabooster.ui.launching.LaunchingViewModel.UiDestination.Main
import com.ohayo.core.ui.extensions.hideKeyboardWhenTouchOutside
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * Created by Furuichi on 07/07/2022
 */
@AndroidEntryPoint
class LaunchingFragment : BaseFragment<LaunchingViewModel, FragmentLaunchingBinding>(FragmentLaunchingBinding::inflate) {

    override val viewModel: LaunchingViewModel by viewModels()

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        bindAction()
        bindState()
        bindDestination()
    }

    /**
     * SETUP VIEWS
     */
    private fun setupViews() {
        activity?.hideKeyboardWhenTouchOutside(view)

        viewBinding.inputUsername
            .apply {
                showKeyboard()

                doOnTextChanged = { textChanged ->
                    val action = InputChanged(textChanged)
                    viewModel.accept(action)
                }
            }
        if (BuildConfig.DEBUG) {
            viewBinding.inputUsername.text = "lx.quyen"
        }
    }

    /**
     * ACTION
     */
    private fun bindAction() {
        viewBinding.btnSignIn.setOnClickListener {
            viewModel.accept(Submit)
        }
    }

    /**
     * STATE
     */
    private fun bindState() {
        viewModel.uiState
            .map { it.enable }
            .onEach(viewBinding.btnSignIn::setEnabled)
            .launchIn(uiScope)
    }

    /**
     * DESTINATION
     */
    private fun bindDestination() {
        viewModel.uiDestination
            .filterIsInstance<Main>()
            .onEach(this::navigateToMainScreen)
            .launchIn(uiScope)
    }


    private fun navigateToMainScreen(trigger: Main) {
        val action = LaunchingFragmentDirections.actionLaunchingFragmentToMainFragment()
        action.setUserArg(trigger.user)
        findNavController().navigate(action)
    }

}