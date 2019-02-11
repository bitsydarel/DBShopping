/*
 *
 *  * Copyright (C) 2017 Darel Bitsy
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License
 *
 */

package com.dbeginc.dbshopping.listdetail.items

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseAdapter
import com.dbeginc.dbshopping.databinding.ItemLayoutBinding
import com.dbeginc.dbshopping.utils.extensions.hide
import com.dbeginc.dbshopping.utils.extensions.setBoughtBy
import com.dbeginc.dbshopping.utils.extensions.show
import com.dbeginc.lists.viewmodels.ItemModel
import java.util.*

/**
 * Created by darel on 27.02.18.
 *
 * Items Adapter
 */
class ItemsAdapter (override var data: LinkedList<ItemModel> = LinkedList(), private val actionBridge: ItemsAdapterBridge) : BaseAdapter<ItemModel, ItemsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ItemViewHolder(
                DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_layout,
                        parent,
                        false
                ),
                actionBridge
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]

        holder.bind(item)
    }

    override fun updateData(newData: List<ItemModel>) {
        val diffResult = DiffUtil.calculateDiff(
                ItemDiffCallback(data, newData)
        )

        data = LinkedList(newData)

        diffResult.dispatchUpdatesTo(this)
    }

    inner class ItemViewHolder(val itemBinding: ItemLayoutBinding, private val actionBridge: ItemsAdapterBridge) : RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemBinding.root.setOnClickListener {
                actionBridge.onItemSelected(itemBinding.item!!)
            }

            itemBinding.itemBrought.setOnClickListener { _ ->
                itemBinding.item?.bought = itemBinding.itemBrought.isChecked
                actionBridge.onItemBoughtStatusChanged(itemBinding.item!!)
                setBoughtBy(itemBinding.itemBoughtBy, itemBinding.item?.boughtBy)
            }
        }

        fun bind(item: ItemModel) {
            itemBinding.item = item

            if (actionBridge.canUserEditItem(item)) itemBinding.itemBrought.show()
            else itemBinding.itemBrought.hide()

            itemBinding.executePendingBindings()
        }
    }
}