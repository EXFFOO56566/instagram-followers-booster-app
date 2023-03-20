package com.lxquyen.instabooster.ui.main

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.databinding.FragmentMainBinding
import com.lxquyen.instabooster.ui.base.BaseFragment
import com.lxquyen.instabooster.ui.boot.BoostFragment
import com.lxquyen.instabooster.ui.explore.ExploreFragment
import com.lxquyen.instabooster.ui.profile.ProfileFragment
import com.lxquyen.instabooster.ui.shop.ShopFragment
import com.lxquyen.instabooster.utils.dialog.WelcomeDialogFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Furuichi on 07/07/2022
 */
enum class Tab {
    EXPLORE, SHOP, BOOST, PROFILE
}

interface MainDelegate {
    fun setTabIndex(tab: Tab)
}

@AndroidEntryPoint
class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>(FragmentMainBinding::inflate), MainDelegate {

    override val viewModel: MainViewModel by viewModels()

    private val args: MainFragmentArgs by navArgs()

    //Thông tin user để show màn Welcome
    private val user: User?
        get() = args.userArg

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        bindAction()
        bindState()
    }

    /**
     * SETUP VIEWS
     */
    private fun setupViews() {
        viewBinding.viewPager2.apply {
            adapter = PagerAdapter()
            isUserInputEnabled = false
            offscreenPageLimit = 4
        }

        viewBinding.tabs.apply {
            for (i in 0..this.tabCount) {
                val params = this.getTabAt(i)?.view?.getChildAt(0)?.layoutParams as LinearLayout.LayoutParams?
                params?.bottomMargin = 0
                this.getTabAt(i)?.view?.getChildAt(0)?.layoutParams = params
            }
        }

        TabLayoutMediator(
            viewBinding.tabs,
            viewBinding.viewPager2,
            false, false
        ) { tab, position ->
            val (icon, text) = when (position) {
                0 -> Pair(R.drawable.ic_tab_explore, "Explore")
                1 -> Pair(R.drawable.ic_tab_shop, "Buy Stars")
                2 -> Pair(R.drawable.ic_tab_boost, "Boost")
                else -> Pair(R.drawable.ic_tab_profile, "Profile")
            }
            tab.text = text
            tab.setIcon(icon)
            val params = tab.view.getChildAt(0)?.layoutParams as LinearLayout.LayoutParams?
            params?.bottomMargin = 0
            tab.view.getChildAt(0)?.layoutParams = params
        }.attach()

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
        user?.let { safeUser ->
            if (safeUser.isFirstLogin) {
                showWelcomeDialog(safeUser)
            }
        }
    }

    private fun showWelcomeDialog(safeUser: User) {
        val action = WelcomeDialogFragmentDirections.globalActionToWelcomeDialogFragment()
        action.setUserArg(safeUser)
        findNavController().navigate(action)
    }

    override fun setTabIndex(tab: Tab) {
        viewBinding.tabs.getTabAt(tab.ordinal)?.select()
    }

    inner class PagerAdapter : FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ExploreFragment()
                1 -> ShopFragment()
                2 -> BoostFragment()
                else -> ProfileFragment()
            }
        }

    }


}