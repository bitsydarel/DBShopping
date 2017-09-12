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

package com.dbeginc.dbshopping.settings.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.SettingItemLayoutBinding
import com.dbeginc.dbshopping.settings.adapter.presenter.SettingPresenterImpl
import com.dbeginc.dbshopping.settings.adapter.view.SettingViewHolder
import com.dbeginc.dbshopping.viewmodels.SettingModel

/**
 * Created by darel on 09.09.17.
 *
 * Settings Adapter
 */
class SettingsAdapter(listOfSettings: List<SettingModel>) : RecyclerView.Adapter<SettingViewHolder>() {
    private val presenters: MutableList<SettingContract.SettingPresenter> = mutableListOf()
    private var container: RecyclerView? = null

    init {
        listOfSettings.mapTo(presenters) { setting -> SettingPresenterImpl(setting) }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SettingViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val item = DataBindingUtil.inflate<SettingItemLayoutBinding>(layoutInflater, R.layout.setting_item_layout, parent, false)

        return SettingViewHolder(item)
    }

    override fun onBindViewHolder(holder: SettingViewHolder?, position: Int) {

    }

    override fun getItemCount(): Int = presenters.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    fun updateSettings(newSettings: List<SettingModel>) {
        if (presenters.isEmpty()) {
            //Fix for view holder invalid when view is empty
            container?.recycledViewPool?.clear()

            fillMe(newSettings)
            // Posting update on RecyclerView Thread
            container?.post { notifyDataSetChanged() }

        } else {

//            val diffResult = DiffUtil.calculateDiff()

            fillMe(newSettings)
            // Posting diffUtils result on the recyclerView queue
            // Which fix the Illegal statement Layout computing
            // And updating the layout changes on the container queue

//            container?.post { diffResult.dispatchUpdatesTo(this) }
        }
    }

    private fun fillMe(newSettings: List<SettingModel>) {
        presenters.clear()
        newSettings.mapTo(presenters) { setting -> SettingPresenterImpl(setting) }
    }
}