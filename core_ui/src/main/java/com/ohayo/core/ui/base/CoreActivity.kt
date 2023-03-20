package com.ohayo.core.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.ohayo.core.ui.helper.connectivity.ConnectivityProvider
import com.ohayo.core.ui.helper.connectivity.hasInternet
import timber.log.Timber

/**
 * Created by Furuichi on 4/7/2022
 */
abstract class CoreActivity<VB : ViewBinding>(private val _binding: (LayoutInflater) -> VB) : FragmentActivity(), ConnectivityProvider.ConnectivityStateListener {

    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() {
            return _viewBinding ?: throw IllegalStateException(
                "should never call auto-cleared-value get when it might not be available"
            )
        }

    private val provider: ConnectivityProvider by lazy {
        ConnectivityProvider.createProvider(this)
    }

    abstract fun onActivityCreated(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = _binding(layoutInflater)
        setContentView(viewBinding.root)
        onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        provider.addListener(this)
    }

    override fun onStop() {
        super.onStop()
        provider.removeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        val hasInternet = state.hasInternet()
        Timber.d("hasInternet: $hasInternet -  $state")
    }
}