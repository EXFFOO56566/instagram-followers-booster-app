package com.lxquyen.instabooster.ui.boot

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.databinding.FragmentBoostBinding
import com.lxquyen.instabooster.databinding.ItemNativeAdBinding
import com.lxquyen.instabooster.ui.base.BaseFragment
import com.lxquyen.instabooster.ui.boot.BoostViewModel.UiAction.UseNow
import com.lxquyen.instabooster.ui.boot.BoostViewModel.UiDestination.Boost
import com.lxquyen.instabooster.ui.main.MainDelegate
import com.lxquyen.instabooster.ui.main.Tab
import com.lxquyen.instabooster.utils.Constants
import com.lxquyen.instabooster.utils.NativeAdHelper
import com.lxquyen.instabooster.utils.dialog.ConfirmDialogFragmentDirections
import com.lxquyen.instabooster.utils.dialog.DialogType
import com.ohayo.core.ui.extensions.customViewInflate
import com.ohayo.core.ui.helper.FragmentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * Created by Furuichi on 7/7/2022
 */
@AndroidEntryPoint
class BoostFragment : BaseFragment<BoostViewModel, FragmentBoostBinding>(FragmentBoostBinding::inflate) {

    override val viewModel: BoostViewModel by viewModels()

    private val mainDelegate: MainDelegate?
        get() = FragmentUtils.getParent(this, MainDelegate::class.java)

    private val boostAdapter: BoostAdapter
        get() = viewBinding.rvPack.adapter as BoostAdapter

    private var currentNativeAd: NativeAd? = null

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        bindAction()
        bindState()
        bindDestination()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentNativeAd?.destroy()
    }

    /**
     * SETUP VIEWS
     */
    private fun setupViews() {
        viewBinding.rvPack.adapter = BoostAdapter(
            onBoostClicked = {
                val action = UseNow(it.boostId)
                viewModel.accept(action)
            },
            onBuyClicked = {
                mainDelegate?.setTabIndex(Tab.SHOP)
            }
        )

        setupNativeAd()
    }

    /**
     * ACTION
     */
    private fun bindAction() {

    }

    /**
     * STATE
     */
    private fun bindState() {
        viewModel.uiState
            .map { it.packs }
            .onEach(boostAdapter::submitList)
            .launchIn(uiScope)

        viewModel.uiState
            .map { "${it.stars}⭐" }
            .onEach(viewBinding.tvStar::setText)
            .launchIn(uiScope)
    }

    /**
     * DESTINATION
     */
    private fun bindDestination() {
        viewModel.uiDestination
            .filterIsInstance<Boost>()
            .onEach {
                boostAdapter.notifyDataSetChanged()
                val action = ConfirmDialogFragmentDirections.globalActionToConfirmDialogFragment(DialogType.BOOST.ordinal)
                findNavController().navigate(action)
            }
            .launchIn(uiScope)
    }

    private fun setupNativeAd() {
        if (Constants.FEATURE_FLAG_SHOW_NATIVE_AD.not()) {
            return
        }
        val adLoader = AdLoader.Builder(requireContext(), getString(R.string.ad_native_unit_boost))
            .forNativeAd(this::preparePopulateNativeAd)
            .build()
        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }

    private fun preparePopulateNativeAd(nativeAd: NativeAd) {
        if (activity?.isDestroyed == true
            || activity?.isFinishing == true
            || activity?.isChangingConfigurations == true
        ) {
            nativeAd.destroy()
            return
        }
        this.currentNativeAd?.destroy()
        this.currentNativeAd = nativeAd
        viewBinding.adView.removeAllViews()
        val itemNativeAdBinding = viewBinding.adView.customViewInflate(ItemNativeAdBinding::inflate)
        NativeAdHelper.populateNativeAd(itemNativeAdBinding.root, nativeAd)
    }
}