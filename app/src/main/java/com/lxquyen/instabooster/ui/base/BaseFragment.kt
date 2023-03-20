package com.lxquyen.instabooster.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lxquyen.instabooster.utils.progress.ProgressDialog
import com.ohayo.core.ui.base.CoreFragment
import com.ohayo.core.ui.extensions.showToast
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Created by Furuichi on 4/7/2022
 */
abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding>(_binding: (LayoutInflater, ViewGroup?, Boolean) -> VB) : CoreFragment<VM, VB>(_binding) {
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
            ?.also {
                it.fitsSystemWindows = true
                bindIndicatorEvent()
            }
    }

    private fun bindIndicatorEvent() {
        viewModel
            .indicatorEvent
            .filterIsInstance<IndicatorState.Error>()
            .onEach(this::handleError)
            .launchIn(uiScope)

        viewModel
            .indicatorEvent
            .filterIsInstance<IndicatorState.Loading>()
            .onEach(this::showLoading)
            .launchIn(uiScope)

        viewModel
            .indicatorEvent
            .filterIsInstance<IndicatorState.Idle>()
            .onEach(this::hideLoading)
            .launchIn(uiScope)
    }

    open fun handleError(error: IndicatorState.Error) {
        hideLoading()
        context?.showToast(error.message)
    }

    open fun showLoading(loading: IndicatorState.Loading) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
                .apply {
                    isCancelable = false
                }
        }
        progressDialog?.show(childFragmentManager, null)
    }

    open fun hideLoading(idle: IndicatorState.Idle? = null) {
        if (progressDialog?.isAdded == true) {
            progressDialog?.dismiss()
        }
        progressDialog = null
    }
}