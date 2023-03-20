package com.lxquyen.instabooster.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.databinding.FragmentProfileBinding
import com.lxquyen.instabooster.ui.base.BaseFragment
import com.lxquyen.instabooster.ui.profile.ProfileViewModel.UiAction.FollowOurInsta
import com.lxquyen.instabooster.utils.PrefKeys
import com.lxquyen.instabooster.widget.CurveDrawable
import com.ohayo.core.helper.preferences.DataStoreManager
import com.ohayo.core.ui.extensions.composeEmail
import com.ohayo.core.ui.extensions.dp2Px
import com.ohayo.core.ui.extensions.openUrl
import com.ohayo.core.ui.extensions.setAvatarImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Furuichi on 07/07/2022
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var dataStore: DataStoreManager

    override val viewModel: ProfileViewModel by viewModels()

    private lateinit var rewardedAd: RewardedAd
    private var isFollowing: Boolean = false

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupRewardedAd()
        bindAction()
        bindState()
    }

    override fun onResume() {
        super.onResume()
        if (isFollowing) {
            viewModel.accept(FollowOurInsta)
            isFollowing = false
        }
    }

    /**
     * SETUP VIEWS
     */
    private fun setupViews() {
        viewBinding.container.apply {
            val curveDrawable = CurveDrawable(
                320f.dp2Px(),
                ContextCompat.getColor(context, R.color._f5f5f5)
            )
            background = curveDrawable
        }

        viewBinding.viewFooter.apply {
            viewWatchVideo.setOnClickListener {
                showVideoAd()
            }

            viewInstagram.setOnClickListener {
                isFollowing = true
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/witworkapp/")
                )
                context?.startActivity(browserIntent)
            }
        }

        viewBinding.viewFooter.apply {
            tvEmail.setOnClickListener {
                context?.composeEmail(arrayOf("hello@witwork.app"), "")
            }
            tvLink.setOnClickListener {
                context?.openUrl("https://www.behance.net/witworkapp")
            }
        }
    }

    /**
     * SETUP REWARDED AD
     */
    private fun setupRewardedAd() {
        val fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Timber.d("onAdShowedFullScreenContent #called()")
            }

            override fun onAdDismissedFullScreenContent() {
                viewModel.accept(ProfileViewModel.UiAction.Watch)
                Timber.d("onAdDismissedFullScreenContent #called()")
            }
        }
        RewardedAd.load(
            requireContext(),
            getString(R.string.ad_reward_unit_profile),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    rewardedAd.fullScreenContentCallback = fullScreenContentCallback
                }
            }
        )
    }

    /**
     * ACTION
     */
    private fun bindAction() {
        viewBinding.btnLogout.setOnClickListener {
            uiScope.launch {
                dataStore.remove(PrefKeys.USER_ID)
                findNavController().navigate(R.id.action_mainFragment_to_launchingFragment)
            }
        }
    }

    /**
     * STATE
     */
    private fun bindState() {
        viewModel.uiState
            .map { it.user }
            .filterNotNull()
            .onEach(this::bindUser)
            .launchIn(uiScope)
    }

    private fun bindUser(user: User) {
        viewBinding.viewHeader.apply {
            imgAvatar.setAvatarImage(user.profilePicUrl)
            tvName.text = user.displayName
            tvFollower.text = user.followersText
            tvFollowing.text = user.followingsText
            tvLike.text = "0"
        }

        viewBinding.viewWallet.apply {
            val star = user.stars ?: 0
            tvWallet.text = String.format("%s⭐", star.toString())
        }
    }

    private fun showVideoAd() {
        if (!::rewardedAd.isInitialized) {
            return
        }
        viewBinding.viewFooter.viewWatchVideo.isEnabled = false
        rewardedAd.show(
            requireActivity()
        ) { }
    }
}