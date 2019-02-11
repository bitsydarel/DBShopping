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

import android.support.v7.widget.RecyclerView
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.UserListItemLayoutBinding
import com.dbeginc.dbshopping.utils.extensions.getFormattedTime
import com.dbeginc.dbshopping.utils.extensions.getString
import com.dbeginc.lists.userlists.userlist.UserListView
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel

/**
 * Created by darel on 04.03.18.
 *
 * UserProfile list view holder
 */
class UserListViewHolder (val binding: UserListItemLayoutBinding, private val actionBridge: UserListAdapterBridge) : RecyclerView.ViewHolder(binding.root), UserListView {
    init {
        binding.listItemLayout.setOnClickListener {
            actionBridge.onListSelected(binding.list!!)
        }
    }

    fun bind(list: ListModel) {
        binding.list = list

        binding.listLastChange.text = list.getFormattedTime(binding.root.context)
    }

    override fun noOneIsShopping() {
        binding.listUsersShopping.text = binding.getString(R.string.nobodyIsShopping)
    }

    override fun onlyCurrentUserShopping() {
        binding.listUsersShopping.text = binding.getString(R.string.youAreShopping)
    }

    override fun onePersonShopping(userShopping: String) {
        binding.listUsersShopping.text = binding.getString(R.string.userIsShopping).format(userShopping)
    }

    override fun currentUserAndSomeoneElseAreShopping(otherOne: ShoppingUserModel) {
        binding.listUsersShopping.text = binding.getString(R.string.youAndAnotherUserAreShopping).format(otherOne.nickname)
    }

    override fun twoUsersAreShopping(firstUser: String, secondUser: String) {
        binding.listUsersShopping.text = binding.getString(R.string.twoUsersAreShopping).format(firstUser, secondUser)
    }

    override fun currentUserAndOtherUsersAreShopping(numberOfOtherUserShopping: Int) {
        binding.listUsersShopping.text = binding.getString(R.string.youAndOtherUsersAreShopping).format(numberOfOtherUserShopping)
    }

    override fun manyPeopleAreShopping(numberOfPeopleShopping: Int) {
        binding.listUsersShopping.text = binding.getString(R.string.usersAreShopping).format(numberOfPeopleShopping)
    }
}