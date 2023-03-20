package com.lxquyen.instabooster.ui.boot

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.data.model.Boost
import com.lxquyen.instabooster.databinding.ItemBoostPackBinding
import com.lxquyen.instabooster.ui.boot.BoostAdapter.BoostViewHolder
import com.ohayo.core.ui.extensions.inflate
import com.ohayo.core.ui.R as coreR

/**
 * Created by Furuichi on 08/07/2022
 */
class BoostAdapter(
    private val onBoostClicked: (Boost) -> Unit,
    private val onBuyClicked: () -> Unit
) : ListAdapter<Boost, BoostViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoostViewHolder {
        val binding = parent.inflate(ItemBoostPackBinding::inflate)
        return BoostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoostViewHolder, position: Int) {
        holder.boost = getItem(position)
    }

    inner class BoostViewHolder(private val binding: ItemBoostPackBinding) : RecyclerView.ViewHolder(binding.root) {

        var boost: Boost? = null
            set(value) {
                field = value
                value?.let {
                    bindState(value)
                }
            }

        private fun bindState(boost: Boost) {
            binding.tvStar.text = "+${boost.stars}⭐"
            binding.tvFollowers.text = "+${boost.followers}"

            val isUseNow = boost.isUseNow.invoke(boost.stars)
            if (isUseNow) {
                binding.tvPack.text = "Ready to boost"
                binding.btnBoost.apply {
                    text = "Use now"
                    background.setTint(ContextCompat.getColor(context, coreR.color.white))
                    setOnClickListener {
                        onBoostClicked.invoke(boost)
                    }
                    setTextColor(ContextCompat.getColor(context, coreR.color.black))
                }
                binding.imgBackground.setBackgroundResource(R.drawable.background_pack)
            } else {
                binding.tvPack.text = "You need"
                binding.btnBoost.apply {
                    text = "Buy⭐"
                    background.setTint(ContextCompat.getColor(context, R.color._7759f0))
                    setOnClickListener {
                        onBuyClicked.invoke()
                    }
                    setTextColor(ContextCompat.getColor(context, coreR.color.white))
                }
                binding.imgBackground.setBackgroundResource(R.drawable.background_pack_green)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Boost>() {
            override fun areItemsTheSame(oldItem: Boost, newItem: Boost): Boolean {
                return oldItem.stars == newItem.stars
            }

            override fun areContentsTheSame(oldItem: Boost, newItem: Boost): Boolean {
                return oldItem == newItem
            }
        }
    }

}