package com.dbeginc.dbshopping.userlist.adapter.view

import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.dbeginc.dbshopping.databinding.UserListItemLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Navigator
import com.dbeginc.dbshopping.listdetail.view.ListDetailActivity
import com.dbeginc.dbshopping.userlist.adapter.ListContract
import com.dbeginc.dbshopping.viewmodels.ListModel

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
 * Created by darel on 22.08.17.
 *
 * List view holder
 */
class ListViewHolder(val binding: UserListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root), ListContract.ListView {

    override fun setupView() { /* Not needed for the view holder with databinding*/ }

    override fun displayList(list: ListModel) {
        binding.list = list
        binding.executePendingBindings()
    }

    override fun setUpOnClick(presenter: ListContract.ListPresenter) {
        // We setup the onclick here
        // so that the view forward the event
        // to the presenter
        binding.listItemLayout.setOnClickListener {
            presenter.onListClick()
        }
    }

    override fun displayListDetail(list: ListModel) {
        val context = binding.root.context

        val intent = Intent(context, ListDetailActivity::class.java)

        intent.putExtra(ConstantHolder.LIST_DATA_KEY, list)

        Navigator.startActivity(context, intent)
    }

    override fun cleanState() { /* Empty because not needed for a view holder with DataBinding */ }

}