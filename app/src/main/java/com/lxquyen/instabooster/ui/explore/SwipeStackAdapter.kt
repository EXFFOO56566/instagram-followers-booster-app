package com.lxquyen.instabooster.ui.explore

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.databinding.ItemCardEmptyBinding
import com.lxquyen.instabooster.databinding.ItemCardViewBinding
import com.ohayo.core.ui.extensions.inflate
import com.ohayo.core.ui.extensions.setImage

/**
 * Created by Furuichi on 08/07/2022
 */
class SwipeStackAdapter(
    private val onSwipeRefresh: () -> Unit
) : BaseAdapter() {

    private var data = mutableListOf<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun submitList(data: List<User>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return data.count() + 1
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.size) TYPE_EMPTY else TYPE_CARD
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val type = getItemViewType(position)

        if (type == TYPE_EMPTY) {
            val (view, holder) = if (convertView == null) {
                val binding = parent.inflate(ItemCardEmptyBinding::inflate)
                val holder = EmptyViewHolder(binding)
                Pair(binding.root, holder)
            } else {
                val binding = ItemCardEmptyBinding.bind(convertView)
                val holder = binding.root.tag as EmptyViewHolder
                Pair(binding.root, holder)
            }
            return view
        }

        val (view, holder) = if (convertView == null) {
            val binding = parent.inflate(ItemCardViewBinding::inflate)
            val holder = CardViewHolder(binding)
            Pair(binding.root, holder)
        } else {
            val binding = ItemCardViewBinding.bind(convertView)
            val holder = binding.root.tag as CardViewHolder
            Pair(binding.root, holder)
        }
        holder.bindData(data[position])

        return view
    }

    inner class CardViewHolder(private val binding: ItemCardViewBinding) {

        fun bindData(user: User) {
            binding.tvName.text = user.displayName
            binding.tvUsername.text = "@${user.username}"
            binding.tvDescription.text = user.bio
            binding.imgAvatar.setImage(user.profilePicUrl)
        }
    }

    inner class EmptyViewHolder(private val binding: ItemCardEmptyBinding) {

        init {
            binding.swipeRefresh.setOnRefreshListener {
                binding.swipeRefresh.isRefreshing = false
                onSwipeRefresh.invoke()
            }
        }

    }

    companion object {
        private const val TYPE_EMPTY = 999
        private const val TYPE_CARD = 998
    }
}