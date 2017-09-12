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

package com.dbeginc.dbshopping.settings.adapter.view

import android.support.v7.widget.RecyclerView
import com.dbeginc.dbshopping.databinding.SettingItemLayoutBinding
import com.dbeginc.dbshopping.helper.extensions.hide
import com.dbeginc.dbshopping.helper.extensions.show
import com.dbeginc.dbshopping.settings.adapter.SettingContract
import com.dbeginc.dbshopping.viewmodels.SettingModel

/**
 * Created by darel on 09.09.17.
 *
 * Setting View Holder
 */
class SettingViewHolder(private val binding: SettingItemLayoutBinding) : RecyclerView.ViewHolder(binding.root), SettingContract.SettingView {
    override fun setupView() {}

    override fun cleanState() {}

    override fun displaySetting(setting: SettingModel) {
        binding.setting = setting
        if (setting.hasAction) binding.settingAction.show() else binding.settingAction.hide()
    }
}