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

package com.dbeginc.dbshopping.listitems.adapter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.listitems.adapter.presenter.ItemPresenterImpl
import com.dbeginc.dbshopping.listitems.adapter.view.ItemViewHolder
import com.dbeginc.dbshopping.viewmodels.ItemModel
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

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
class ItemsAdapter(items: List<ItemModel>, private val currentUserName: String, private val shoppingModeState: BehaviorSubject<Boolean>,
                   private val itemUpdateEvent: PublishSubject<ItemModel>) : RecyclerView.Adapter<ItemViewHolder>() {

    private val presenters : MutableList<ItemContract.ItemPresenter> = mutableListOf()
    private var container: RecyclerView? = null

    init { items.mapTo(presenters) { item -> ItemPresenterImpl(item, currentUserName, shoppingModeState, itemUpdateEvent) } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ItemViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // We get the presenter for the view at this position
        val presenter = presenters[position]

        // Bind the presenter to it's view
        presenter.bind(holder)

        // configure the view to forward the click event
        // to it's presenter
        holder.setUpOnClick(presenter)

        presenter.loadItem()
    }

    override fun getItemCount(): Int = presenters.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    fun addItem(item: ItemModel) {
        presenters.add(ItemPresenterImpl(item, currentUserName, shoppingModeState, itemUpdateEvent))
        notifyItemChanged(presenters.size)
    }

    fun removeItem(position: Int) {
        presenters.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getViewModels() : List<ItemModel> = presenters.mapTo(mutableListOf()) { presenter -> presenter.getData() }

    fun updateData(newData: List<ItemModel>) {
        if (presenters.isEmpty()) {
            //Fix for view holder invalid when view is empty
            container?.recycledViewPool?.clear()

            fillMe(newData)
            // Posting update on RecyclerView Thread
            container?.post { notifyDataSetChanged() }

        } else {
            val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(presenters, newData))

            fillMe(newData)
            // Posting diffUtils result on the recyclerView queue
            // Which fix the Illegal statement Layout computing
            // And updating the layout changes on the container queue
            container?.post { diffResult.dispatchUpdatesTo(this) }
        }
    }

    fun getViewModelAtPosition(position: Int) : ItemModel = presenters[position].getData()

    /**
     * Fill the presenter container with new data
     * this method was named fill me
     * because tired
     *
     */
    private fun fillMe(newData: List<ItemModel>) {
        presenters.clear()
        newData.mapTo(presenters) { item -> ItemPresenterImpl(item, currentUserName, shoppingModeState, itemUpdateEvent) }
    }
}