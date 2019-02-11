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

package com.dbeginc.dbshopping.userlists

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseAdapter
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by darel on 04.03.18.
 *
 * UserProfile Lists Adapter
 */
class UserListsAdapter (override var data: LinkedList<ListModel> = LinkedList(), private val actionBridge: UserListAdapterBridge) : BaseAdapter<ListModel, UserListViewHolder>() {
    private val _holders = HashMap<String, UserListViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return UserListViewHolder(DataBindingUtil.inflate(
                inflater,
                R.layout.user_list_item_layout,
                parent,
                false
        ), actionBridge)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val listModel = data[position]

        _holders[listModel.uniqueId] = holder

        holder.bind(listModel)
    }

    @Synchronized
    override fun updateData(newData: List<ListModel>) {
        val diffResult = DiffUtil.calculateDiff(UserListDiff(data, newData))

        data = LinkedList(newData)

        diffResult.dispatchUpdatesTo(this)
    }

    fun findWhoIsShopping(listWithMembers: Pair<String, List<ShoppingUserModel>>) {
        _holders[listWithMembers.first]?.let {
            actionBridge.findWhoIsShopping(listWithMembers.second, it)
        }
    }

    fun freeViewHolder(listHolder: UserListViewHolder) = _holders.remove(listHolder.binding.list!!.uniqueId)

}