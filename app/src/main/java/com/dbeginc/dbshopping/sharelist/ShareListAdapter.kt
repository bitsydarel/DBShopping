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

package com.dbeginc.dbshopping.sharelist

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseAdapter
import com.dbeginc.dbshopping.databinding.FriendLayoutBinding
import com.dbeginc.users.viewmodels.FriendModel
import java.util.*

/**
 * Created by darel on 04.03.18.
 *
 * Share list adapter
 */
class ShareListAdapter (override var data: LinkedList<FriendModel> = LinkedList()) : BaseAdapter<FriendModel, ShareListAdapter.FriendViewHolder>() {
    val guysToShareWith: MutableSet<FriendModel> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return FriendViewHolder(
                DataBindingUtil.inflate(
                        inflater,
                        R.layout.friend_layout,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = data[position]

        holder.bind(friend)
    }

    override fun updateData(newData: List<FriendModel>) {
        val diffResult = DiffUtil.calculateDiff(
                FriendDiffCallback(
                        data,
                        newData
                )
        )

        data = LinkedList(newData)

        diffResult.dispatchUpdatesTo(this)
    }

    inner class FriendViewHolder(private val binding: FriendLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.selectFriend.setOnClickListener {
                if (binding.selectFriend.isChecked) guysToShareWith.add(binding.friend!!)
                else guysToShareWith.remove(binding.friend)
            }
        }

        fun bind(friend: FriendModel) {
            binding.friend = friend

            binding.executePendingBindings()
        }
    }
}