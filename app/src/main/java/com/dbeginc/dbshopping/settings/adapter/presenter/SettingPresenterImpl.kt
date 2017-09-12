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

package com.dbeginc.dbshopping.settings.adapter.presenter

import com.dbeginc.dbshopping.settings.adapter.SettingContract
import com.dbeginc.dbshopping.viewmodels.SettingModel

/**
 * Created by darel on 09.09.17.
 *
 * Setting Presenter Implementation
 */
class SettingPresenterImpl(private val setting: SettingModel) : SettingContract.SettingPresenter {
    private lateinit var view: SettingContract.SettingView

    override fun bind(view: SettingContract.SettingView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {}

    override fun loadSetting() = view.displaySetting(setting)
}