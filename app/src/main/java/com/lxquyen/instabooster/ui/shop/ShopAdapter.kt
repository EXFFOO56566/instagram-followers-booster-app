package com.lxquyen.instabooster.ui.shop

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lxquyen.instabooster.data.model.Product
import com.lxquyen.instabooster.databinding.ItemStarPackBinding
import com.lxquyen.instabooster.ui.shop.ShopAdapter.ShopViewHolder
import com.ohayo.core.ui.extensions.inflate

/**
 * Created by Furuichi on 08/07/2022
 */
class ShopAdapter(
    private val onBuyClicked: (Product) -> Unit
) : ListAdapter<Product, ShopViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = parent.inflate(ItemStarPackBinding::inflate)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.product = getItem(position)
    }

    inner class ShopViewHolder(private val binding: ItemStarPackBinding) : RecyclerView.ViewHolder(binding.root) {

        var product: Product? = null
            set(value) {
                field = value
                value?.let {
                    bindState(value)
                }
            }

        private fun bindState(product: Product) {
            binding.tvAmount.text = product.amount
            binding.tvStar.text = product.star
            binding.btnBuy.setOnClickListener {
                onBuyClicked.invoke(product)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.skuDetails.productId == newItem.skuDetails.productId
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

}