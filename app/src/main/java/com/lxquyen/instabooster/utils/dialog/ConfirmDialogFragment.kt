package com.lxquyen.instabooster.utils.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.databinding.DialogConfirmBinding
import com.lxquyen.instabooster.databinding.ItemNativeAdMediumBinding
import com.lxquyen.instabooster.utils.Constants
import com.lxquyen.instabooster.utils.NativeAdHelper
import com.ohayo.core.ui.extensions.customViewInflate
import com.ohayo.core.ui.extensions.hide
import kotlinx.coroutines.delay

enum class DialogType {
    REWARD,
    BOOST,
    WELCOME
}

class ConfirmDialogFragment : DialogFragment(R.layout.dialog_confirm) {

    companion object {
        private const val SKIP_AD_DURATION = 5
    }

    private lateinit var binding: DialogConfirmBinding

    private val args: ConfirmDialogFragmentArgs by navArgs()
    private val dialogType: DialogType
        get() = DialogType.values()[args.dialogTypeArg]
    private var currentNativeAd: NativeAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogConfirmBinding.bind(view)
        setupView()
        setupNativeAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentNativeAd?.destroy()
    }

    private fun setupView() {
        dialog?.setCancelable(false)

        val wrapper = when (dialogType) {
            DialogType.REWARD -> {
                Wrapper(
                    title = "Reward to you",
                    message = "Congratulation! You got 1 more ⭐️ for task completed. Keep moving \uD83E\uDD18\uD83E\uDD18\uD83E\uDD18 together",
                    banner = R.drawable.background_illustration_gifbox,
                    star = "+01 ⭐️",
                    positive = "Wallet",
                    negative = "Close"
                )
            }
            DialogType.BOOST -> {
                Wrapper(
                    title = "Followers are coming",
                    message = "Don’t take your’eyes off from your profile. Your number of followers will rocket soon \uD83D\uDE80\uD83D\uDE80\uD83D\uDE80 ",
                    banner = R.drawable.background_illustration_gifbox,
                    positive = "Profile",
                    negative = "Close"
                )
            }
            DialogType.WELCOME -> {
                Wrapper(
                    title = "Welcome to InstaBooster",
                    message = "Congratulation! You got 05⭐️ for the first access. Let explore awesome features",
                    banner = R.drawable.background_illustration_gifbox,
                    star = "+05 ⭐️",
                    positive = "Next"
                )
            }
        }

        binding.apply {
            tvTitle.text = wrapper.title
            tvMessage.text = wrapper.message
            tvStar.text = wrapper.star
            imgBanner.setImageResource(wrapper.banner)
            btnNegative.text = wrapper.negative
            btnPositive.text = wrapper.positive
        }

        binding.btnPositive.setOnClickListener {
            dismiss()
        }
    }

    private fun setupNativeAd() {
        if (Constants.FEATURE_FLAG_SHOW_NATIVE_AD.not()) {
            return
        }
        binding.imgBanner.hide()
        initCountDown()

        val adLoader = AdLoader.Builder(requireContext(), getString(R.string.ad_native_unit_popup))
            .forNativeAd(this::preparePopulateNativeAd)
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setVideoOptions(
                        VideoOptions.Builder()
                            .setStartMuted(true)
                            .build()
                    )
                    .build()
            )
            .build()
        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }

    private fun initCountDown() {
        binding.btnPositive.isEnabled = false
        lifecycleScope.launchWhenStarted {
            repeat(SKIP_AD_DURATION) {
                binding.btnPositive.text = "Skip Ad (${SKIP_AD_DURATION - it}s)"
                delay(1000)
            }
            binding.btnPositive.apply {
                text = "Skip Ad"
                isEnabled = true
            }
        }
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
        binding.adView.removeAllViews()
        val itemNativeAdBinding = binding.adView.customViewInflate(ItemNativeAdMediumBinding::inflate)
        NativeAdHelper.populateNativeAd(itemNativeAdBinding.root, nativeAd)
    }

    override fun getTheme(): Int {
        return R.style.ConfirmDialogStyle
    }

    data class Wrapper(
        val title: String,
        val message: String,
        val banner: Int,
        val star: String? = null,
        val positive: String,
        val negative: String? = null
    )
}