package com.lxquyen.instabooster.ui

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.databinding.ActivityMainBinding
import com.lxquyen.instabooster.ui.base.BaseActivity
import com.lxquyen.instabooster.utils.dialog.NetworkDialogFragment
import com.ohayo.core.ui.helper.connectivity.ConnectivityProvider
import com.ohayo.core.ui.helper.connectivity.ConnectivityProvider.NetworkState.NotConnectedState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    @Inject
    lateinit var userRepository: UserRepository

    private var networkDialog: NetworkDialogFragment? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setupAds()
        checkLogin()
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (state is NotConnectedState) {
            showNetworkDisconnectedDialog()
            return
        } else {
            networkDialog?.dismiss()
            networkDialog = null
        }
    }

    private fun checkLogin() {
        userRepository.checkLogin()
            .onEach(this::initMain)
            .catch {
                initLaunching()
            }
            .launchIn(scope)
    }

    private fun initMain(trigger: Unit) {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment ?: return
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.mainFragment)
        navHostFragment.navController.graph = navGraph
    }

    private fun initLaunching() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment ?: return
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.launchingFragment)
        navHostFragment.navController.graph = navGraph
    }

    private fun setupAds() {
        MobileAds.initialize(this) { }

        val testDeviceIds = listOf(
            "9073243007061EE364CFBDEEE916853C"
        )

        val configuration = RequestConfiguration
            .Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)
    }

    open fun showNetworkDisconnectedDialog() {
        if (networkDialog == null) {
            networkDialog = NetworkDialogFragment()
                .apply {
                    isCancelable = false
                }
        }
        networkDialog?.show(supportFragmentManager, null)
    }

}