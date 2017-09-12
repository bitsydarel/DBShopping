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

package com.dbeginc.dbshopping.settings.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.databinding.SettingsLayoutBinding
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.extensions.hide
import com.dbeginc.dbshopping.settings.SettingsContract
import com.dbeginc.dbshopping.settings.adapter.SettingsAdapter
import com.dbeginc.dbshopping.viewmodels.SettingModel
import javax.inject.Inject

/**
 * Created by darel on 08.09.17.
 *
 * Application Setting Activity
 */
class SettingsActivity : BaseActivity(), SettingsContract.SettingsView {
    @Inject lateinit var presenter: SettingsContract.SettingsPresenter
    private lateinit var binding: SettingsLayoutBinding
    private lateinit var adapter: SettingsAdapter

    /********************************************************** Android Part Method **********************************************************/
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.settings_layout)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onBackPressed() = presenter.onBack()

    /********************************************************** Setting View Part Method **********************************************************/
    override fun setupView() {
        presenter.loadSettings()
    }

    override fun cleanState() = presenter.unBind()

    override fun displaySettings(listOfSettings: List<SettingModel>) {
        adapter.updateSettings(listOfSettings)
    }

    override fun isSettingVisible(): Boolean = supportFragmentManager.backStackEntryCount > 0

    override fun settings(): List<SettingModel> {
        val sortingSetting = SettingModel(0, "Sorting", false)

        return listOf(sortingSetting)
    }

    override fun closeSetting() {
        while(supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        binding.settingPlaceHolder.hide()
    }

    override fun goToHomepage() = finish()
}