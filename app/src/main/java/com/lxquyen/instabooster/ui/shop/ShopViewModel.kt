package com.lxquyen.instabooster.ui.shop

import androidx.lifecycle.viewModelScope
import com.anjlab.android.iab.v3.PurchaseInfo
import com.anjlab.android.iab.v3.SkuDetails
import com.lxquyen.instabooster.data.model.Product
import com.lxquyen.instabooster.data.model.response.PackageResponse
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by Furuichi on 07/07/2022
 */
@HiltViewModel
class ShopViewModel @Inject constructor(
    private val userRepos: UserRepository
) : BaseViewModel() {

    sealed class UiAction {
        object GetPackages : UiAction()
        data class OnSkuDetailsResponse(val products: List<SkuDetails>) : UiAction()
        data class Purchased(val purchaseInfo: PurchaseInfo?) : UiAction()
    }

    data class UiState(
        val packs: List<Product> = emptyList(),
        val stars: Int = 0
    )

    sealed class UiDestination {
        data class PurchaseList(val packageIds: List<String>) : UiDestination()
    }

    /**
     * Stream of immutable states representative of the UI.
     */
    val uiState: StateFlow<UiState>
        get() = _uiStateEvent

    val uiDestination: SharedFlow<UiDestination>
        get() = _uiDestination

    /**
     * Processor of side effects from the UI which in turn feedback into [accept]
     */
    fun accept(action: UiAction) {
        _actionEvent.tryEmit(action)
    }

    private val _actionEvent = MutableSharedFlow<UiAction>(extraBufferCapacity = 1)
    private val _uiDestination = MutableSharedFlow<UiDestination>(extraBufferCapacity = 1)
    private val _uiStateEvent = MutableStateFlow(value = UiState())

    private val _packages = MutableStateFlow<List<PackageResponse.Data>>(value = emptyList())

    init {

        //#UiAction.GetPackages
        handleGetPackagesAction()

        //#UiAction.OnSkuDetailsResponse
        handleOnSkuDetailsResponseAction()

        //#UiAction.Purchased
        handlePurchaseAction()
    }

    private fun handleGetPackagesAction() {
        _actionEvent.filterIsInstance<UiAction.GetPackages>()
            .flatMapLatest(this::getPackagesTask)
            .onEach(_packages::emit)
            .map { it.mapNotNull { `package` -> `package`.packageId } }
            .map(UiDestination::PurchaseList)
            .onEach(_uiDestination::emit)
            .launchIn(viewModelScope)
    }

    private fun handleOnSkuDetailsResponseAction() {

        combine(
            _actionEvent.filterIsInstance<UiAction.OnSkuDetailsResponse>(),
            _packages,
            userRepos.getCurrentUser(),
            ::Triple
        )
            .map { (skuDetails, packages, user) ->
                val products = skuDetails.products.map(::Product)

                products.forEach { product ->
                    product.updateStar(packages)
                }

                val stars = user.stars ?: 0
                return@map UiState(packs = products, stars = stars)
            }
            .onEach(_uiStateEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun handlePurchaseAction() {
        _actionEvent.filterIsInstance<UiAction.Purchased>()
            .map { it.purchaseInfo }
            .filterNotNull()
            .flatMapLatest(this::purchaseTask)
            .launchIn(viewModelScope)
    }

    private fun getPackagesTask(trigger: UiAction.GetPackages): Flow<List<PackageResponse.Data>> {
        return userRepos.getPackages()
            .transformLoading()
    }

    private fun purchaseTask(purchaseInfo: PurchaseInfo): Flow<Unit> {
        return userRepos.purchase(purchaseInfo.purchaseData)
            .transformLoading()
    }
}