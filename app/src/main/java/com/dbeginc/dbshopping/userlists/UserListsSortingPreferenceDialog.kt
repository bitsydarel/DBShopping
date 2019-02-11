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

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.UserlistsSortingPreferencesLayoutBinding
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.SORTING_ORDER
import com.dbeginc.lists.userlists.UserListsViewModel.Companion.ORDER_BY_LIST_NAME
import com.dbeginc.lists.userlists.UserListsViewModel.Companion.ORDER_BY_OWNER_NAME
import com.dbeginc.lists.userlists.UserListsViewModel.Companion.ORDER_BY_PUBLISH_TIME

/**
 * Created by darel on 16.03.18.
 *
 * Sorting preference dialog
 */
class UserListsSortingPreferenceDialog : DialogFragment(), View.OnClickListener {
    private lateinit var binding: UserlistsSortingPreferencesLayoutBinding
    private val preferences by lazy { context!!.getSharedPreferences(ConstantHolder.SHARED_PREFS, Context.MODE_PRIVATE) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.userlists_sorting_preferences_layout,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentSorting = preferences.getInt(SORTING_ORDER, ORDER_BY_PUBLISH_TIME)

        when(currentSorting) {
            ORDER_BY_PUBLISH_TIME -> binding.sortingByStatus.check(R.id.sortingByPublishTimeChecked)
            ORDER_BY_LIST_NAME -> binding.sortingByStatus.check(R.id.sortingByListNameChecked)
            ORDER_BY_OWNER_NAME -> binding.sortingByStatus.check(R.id.sortingByOwnerNameChecked)
        }

        binding.sortingByStatus.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.sortingByPublishTimeChecked -> preferences.edit().putInt(SORTING_ORDER, 1).apply()
                R.id.sortingByListNameChecked -> preferences.edit().putInt(SORTING_ORDER, 2).apply()
                R.id.sortingByOwnerNameChecked -> preferences.edit().putInt(SORTING_ORDER, 3).apply()
            }
        }

        binding.cancelAction.setOnClickListener(this)

        binding.confirmAction.setOnClickListener(this)
    }

    override fun onClick(v: View?) = dismiss()

}