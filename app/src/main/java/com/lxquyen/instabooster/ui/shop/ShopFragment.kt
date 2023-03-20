package com.lxquyen.instabooster.ui.shop

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.anjlab.android.iab.v3.SkuDetails
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.data.model.Product
import com.lxquyen.instabooster.databinding.FragmentShopBinding
import com.lxquyen.instabooster.databinding.ItemNativeAdBinding
import com.lxquyen.instabooster.ui.base.BaseFragment
import com.lxquyen.instabooster.ui.shop.ShopViewModel.UiAction.*
import com.lxquyen.instabooster.ui.shop.ShopViewModel.UiDestination.PurchaseList
import com.lxquyen.instabooster.utils.Constants
import com.lxquyen.instabooster.utils.NativeAdHelper
import com.ohayo.core.ui.extensions.customViewInflate
import com.ohayo.core.ui.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

/**
 * Created by Furuichi on 07/07/2022
 */
@AndroidEntryPoint
class ShopFragment : BaseFragment<ShopViewModel, FragmentShopBinding>(FragmentShopBinding::inflate), BillingProcessor.IBillingHandler {

    override val viewModel: ShopViewModel by viewModels()

    private lateinit var bp: BillingProcessor

    private val shopAdapter: ShopAdapter
        get() = viewBinding.rvPack.adapter as ShopAdapter

    private var currentNativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBillingProcessor()
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        bindState()
        bindDestination()
    }

    /**
     * SETUP BILLING PROCESSOR
     */
    private fun setupBillingProcessor() {
        bp = BillingProcessor(
            requireContext(),
            null,
            this
        )
        bp.initialize()
    }

    /**
     * SETUP VIEWS
     */
    private fun setupViews() {
        viewBinding.rvPack.adapter = ShopAdapter(this::handlePurchase)

        setupNativeAd()
    }

    /**
     * STATE
     */
    private fun bindState() {
        viewModel.uiState
            .map { it.packs }
            .onEach(shopAdapter::submitList)
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
            .filterIsInstance<PurchaseList>()
            .map { it.packageIds }
            .onEach(this::handleGetPurchaseList)
            .launchIn(uiScope)
    }


    /**
     * SETUP NATIVE AD
     */
    private fun setupNativeAd() {
        if (Constants.FEATURE_FLAG_SHOW_NATIVE_AD.not()) {
            return
        }

        val adLoader = AdLoader.Builder(requireContext(), getString(R.string.ad_native_unit_shop))
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

    //region#BillingProcessor.IBillingHandler

    private fun handlePurchase(product: Product) {
        bp.purchase(requireActivity(), product.productId)
    }

    private fun handleGetPurchaseList(packageIds: List<String>) {
        val productIdList = arrayListOf(*packageIds.toTypedArray())
        bp.getPurchaseListingDetailsAsync(
            productIdList,
            object : BillingProcessor.ISkuDetailsResponseListener {
                override fun onSkuDetailsResponse(products: MutableList<SkuDetails>?) {
                    Timber.d("products: $products")
                    val action = OnSkuDetailsResponse(
                        products?.sortedBy { it.priceLong } ?: emptyList()
                    )
                    viewModel.accept(action)
                }

                override fun onSkuDetailsError(error: String?) {
                    Timber.d(error)
                }
            })
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        Timber.d("productId: $productId")
        Timber.d("details: ${details?.responseData}")
        bp.consumePurchaseAsync(productId, object : BillingProcessor.IPurchasesResponseListener {
            override fun onPurchasesSuccess() {
                viewModel.accept(Purchased(details))
                context?.showToast("Successfully consumed");
            }

            override fun onPurchasesError() {}
        })
    }

    override fun onBillingInitialized() {
        viewModel.accept(GetPackages)
    }

    override fun onPurchaseHistoryRestored() {}
    override fun onBillingError(errorCode: Int, error: Throwable?) {}
    //endregion

    override fun onDestroy() {
        if (::bp.isInitialized) {
            bp.release()
        }
        currentNativeAd?.destroy()
        super.onDestroy()
    }

}