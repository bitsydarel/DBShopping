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

package com.dbeginc.dbshopping.addlist.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.addlist.AddListContract
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.databinding.ActivityAddListBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.Navigator
import com.dbeginc.dbshopping.helper.extensions.snack
import com.dbeginc.dbshopping.home.view.HomeActivity
import com.dbeginc.dbshopping.viewmodels.ListModel
import com.dbeginc.dbshopping.viewmodels.UserModel
import java.util.*
import javax.inject.Inject

class AddListActivity : BaseActivity(), AddListContract.AddListView {
    @Inject lateinit var presenter: AddListContract.AddListPresenter
    @Inject lateinit var user: UserModel
    private lateinit var binding: ActivityAddListBinding
    private lateinit var progressDialog : LoadingDialog

    /********************************************************** Android Part Method **********************************************************/

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_list)
        progressDialog = LoadingDialog()
        progressDialog.setMessage(getString(R.string.creatingListMessage))

        if (savedState == null) {
            binding.list = ListModel(id = UUID.randomUUID().toString(), name = "", usersShopping = listOf(), owner = user.name, lastChange = "")
        } else {
            binding.list = savedState.getParcelable(ConstantHolder.LIST_DATA_KEY)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(ConstantHolder.LIST_DATA_KEY, binding.list)
    }

    /********************************************************** Add List View Part Method **********************************************************/

    override fun setupView() {
        binding.close.setOnClickListener { goToHomePage() }
        binding.createButton.setOnClickListener { presenter.createList() }
    }

    override fun cleanState() = presenter.unBind()

    override fun getListName(): String = binding.list.name

    override fun getList(): ListModel = binding.list

    override fun displayNameIncorrectMessage() {
        binding.ListNameContainer.error = getString(R.string.nameIncorrect)
    }

    override fun hideNameIncorrectMessage() {
        binding.ListNameContainer.isErrorEnabled = false
    }

    override fun displayLoadingProgress() {
        progressDialog.show(supportFragmentManager, LoadingDialog::class.java.simpleName)
    }

    override fun hideLoadingProgress() = progressDialog.dismiss()

    override fun displayErrorMessage(error: String) = binding.addListLayout.snack(error)

    override fun goToHomePage() = Navigator.startActivity(this, Intent(this, HomeActivity::class.java))

}
