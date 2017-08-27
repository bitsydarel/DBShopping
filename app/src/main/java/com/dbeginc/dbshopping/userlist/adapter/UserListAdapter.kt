package com.dbeginc.dbshopping.userlist.adapter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.userlist.adapter.presenter.ListPresenterImpl
import com.dbeginc.dbshopping.userlist.adapter.view.ListViewHolder
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
 * Created by darel on 23.08.17.
 *
 * User List Adapter
 *
 * Here it's used has a container with inner controllers or presenters
 */
class UserListAdapter(lists: List<ListModel>) : RecyclerView.Adapter<ListViewHolder>() {
    private val presenters : MutableList<ListContract.ListPresenter> = mutableListOf()
    private var container: RecyclerView? = null

    init { lists.mapTo(presenters) { list -> ListPresenterImpl(list) } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ListViewHolder(DataBindingUtil.inflate(inflater, R.layout.user_list_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        // We get the presenter for the view at this position
        val presenter = presenters[position]

        // Bind the presenter to it's view
        presenter.bind(holder)

        // configure the view to forward the click event
        // to it's presenter
        holder.setUpOnClick(presenter)

        presenter.loadList()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun getItemCount(): Int = presenters.size

    fun updateData(newData: List<ListModel>) {
        if (presenters.isEmpty()) {
            //Fix for view holder invalid when view is empty
            container?.recycledViewPool?.clear()

            fillMe(newData)
            // Posting update on RecyclerView Thread
            container?.post { notifyDataSetChanged() }

        } else {
            val diffResult = DiffUtil.calculateDiff(UserListDiff(presenters, newData))

            fillMe(newData)
            // Posting diffUtils result on the recyclerView queue
            // Which fix the Illegal statement Layout computing
            // And updating the layout changes on the container queue
            container?.post { diffResult.dispatchUpdatesTo(this) }
        }
    }

    /**
     * Fill the presenter container with new data
     * this method was named fill me
     * because tired
     *
     */
    private fun fillMe(newData: List<ListModel>) {
        presenters.clear()
        newData.mapTo(presenters) { item -> ListPresenterImpl(item) }
    }

}