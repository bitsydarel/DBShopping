package com.dbeginc.dbshopping.listitems.adapter.view

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.widget.CheckBox
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.ItemLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Navigator
import com.dbeginc.dbshopping.helper.extensions.hide
import com.dbeginc.dbshopping.helper.extensions.show
import com.dbeginc.dbshopping.itemdetail.view.ItemDetailActivity
import com.dbeginc.dbshopping.listitems.adapter.ItemContract
import com.dbeginc.dbshopping.viewmodels.ItemModel

/**

 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 24.08.17.
 */
class ItemViewHolder(private val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root), ItemContract.ItemView {

    override fun setupView() { /* Nothing to setup view */ }

    override fun cleanState() { /* Nothing to clear here*/ }

    override fun displayItem(item: ItemModel) {
        binding.item = item
    }

    override fun setUpOnClick(presenter: ItemContract.ItemPresenter) {
        binding.itemLayout.setOnClickListener { presenter.onItemClick() }
        binding.itemBrought.setOnClickListener { view -> presenter.onItemChecked((view as CheckBox).isChecked) }
    }

    override fun displayItemDetail(item: ItemModel) {
        val context = binding.root.context
        val intent = Intent(context, ItemDetailActivity::class.java)
        intent.putExtra(ConstantHolder.ITEM_DATA_KEY, binding.item)
        Navigator.startActivity(context, intent)
    }

    override fun displayItemBroughtBy(currentUserName: String) {
        binding.itemBroughtBy.show()
        binding.itemBroughtBy.text = binding.root.context.getString(R.string.itemBroughtBy, currentUserName)
    }

    override fun hideItemBroughtBy() = binding.itemBroughtBy.hide()

}