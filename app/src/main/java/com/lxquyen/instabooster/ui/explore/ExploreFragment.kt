package com.lxquyen.instabooster.ui.explore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.databinding.FragmentExploreBinding
import com.lxquyen.instabooster.ui.base.BaseFragment
import com.lxquyen.instabooster.ui.explore.ExploreViewModel.UiAction.*
import com.lxquyen.instabooster.ui.explore.ExploreViewModel.UiDestination.Reward
import com.lxquyen.instabooster.ui.explore.ExploreViewModel.UiDestination.Web
import com.lxquyen.instabooster.utils.dialog.ConfirmDialogFragmentDirections
import com.lxquyen.instabooster.utils.dialog.DialogType
import com.lxquyen.instabooster.widget.CurveDrawable
import com.lxquyen.instabooster.widget.swipestack.SwipeStack
import com.ohayo.core.ui.extensions.dp2Px
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

/**
 * Created by Furuichi on 07/07/2022
 */
@AndroidEntryPoint
class ExploreFragment : BaseFragment<ExploreViewModel, FragmentExploreBinding>(FragmentExploreBinding::inflate) {

    override val viewModel: ExploreViewModel by viewModels()

    private val swipeStackAdapter: SwipeStackAdapter
        get() = viewBinding.swipeStack.adapter as SwipeStackAdapter

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        bindAction()
        bindState()
        bindDestination()
    }

    override fun onResume() {
        super.onResume()
        viewModel.accept(CheckFollow)
    }

    /**
     * SETUP VIEWS
     */
    private fun setupViews() {
        viewBinding.container.apply {
            val curveDrawable = CurveDrawable(
                180f.dp2Px(),
                ContextCompat.getColor(context, R.color._f5f5f5)
            )
            background = curveDrawable
        }

        viewBinding.swipeStack.apply {

            val listener = object : SwipeStack.SwipeStackListener {

                override fun onViewSwipedToLeft(position: Int) {
                    val action = Swiped(position)
                    viewModel.accept(action)
                }

                override fun onViewSwipedToRight(position: Int) {}
                override fun onStackEmpty() {}
            }

            adapter = SwipeStackAdapter {
                viewModel.accept(Refresh)
            }
            setListener(listener)
        }

    }

    /**
     * ACTION
     */
    private fun bindAction() {

        viewBinding.btnClear.setOnClickListener {
            viewBinding.swipeStack.swipeTopViewToLeft()
        }

        viewBinding.btnFollow.setOnClickListener {
            viewModel.accept(Follow)
        }
    }

    /**
     * STATE
     */
    private fun bindState() {
        viewModel.uiState
            .map { it.users }
            .distinctUntilChanged()
            .onEach {
                swipeStackAdapter.submitList(it)
                viewBinding.swipeStack.resetStack()
            }
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
            .filterIsInstance<Web>()
            .onEach {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/${it.user.username}/")
                )
                this.startActivity(browserIntent)
            }
            .launchIn(uiScope)

        viewModel.uiDestination
            .filterIsInstance<Reward>()
            .onEach {
                viewBinding.swipeStack.swipeTopViewToLeft()
                val action = ConfirmDialogFragmentDirections.globalActionToConfirmDialogFragment(DialogType.REWARD.ordinal)
                findNavController().navigate(action)
            }
            .launchIn(uiScope)
    }
}