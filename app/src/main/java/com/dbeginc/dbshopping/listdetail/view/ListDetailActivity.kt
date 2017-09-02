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

package com.dbeginc.dbshopping.listdetail.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.databinding.ListDetailLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.Navigator
import com.dbeginc.dbshopping.helper.extensions.snack
import com.dbeginc.dbshopping.listdetail.ListDetailContract
import com.dbeginc.dbshopping.listitems.view.ListItemsFragment
import com.dbeginc.dbshopping.viewmodels.ListModel
import com.dbeginc.dbshopping.viewmodels.UserModel
import javax.inject.Inject

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
 * List detail activity
 */
class ListDetailActivity : BaseActivity(), ListDetailContract.ListDetailView, DialogInterface.OnDismissListener {
    @Inject lateinit var presenter: ListDetailContract.ListDetailPresenter
    @Inject lateinit var user: UserModel
    private lateinit var binding: ListDetailLayoutBinding
    private lateinit var dialogProgress: LoadingDialog
    private lateinit var list: ListModel
    private lateinit var listItemFragment: ListItemsFragment

    /********************************************************** Android Part Method **********************************************************/
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.list_detail_layout)

        if (savedState == null) {
            list = intent.getParcelableExtra(ConstantHolder.LIST_DATA_KEY)
            listItemFragment = ListItemsFragment.newInstance(listId = list.id, listOfShoppingUser = list.usersShopping)

        } else {
            list = savedState.getParcelable(ConstantHolder.LIST_DATA_KEY)
            listItemFragment = supportFragmentManager.getFragment(savedState, ListItemsFragment::class.java.simpleName)
                    as ListItemsFragment
        }

        dialogProgress = LoadingDialog()
        dialogProgress.setMessage(getString(R.string.updatingDataMessageList))
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
        outState?.putParcelable(ConstantHolder.LIST_DATA_KEY, list)
        supportFragmentManager.putFragment(outState, ListItemsFragment::class.java.simpleName, listItemFragment )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /* Inflate the menu; this adds items to the action bar if it's present. */
        menuInflater.inflate(R.menu.list_detail_menu, menu)

        /**
         * Get menu items
         */
        presenter.isUserOwner(user.email).let {
            isOwner -> menu?.findItem(R.id.action_edit_list_name)?.isVisible = isOwner
            menu?.findItem(R.id.action_remove_list)?.isVisible = isOwner
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_edit_list_name -> {
                presenter.changeName()
                return true
            }

            R.id.action_remove_list -> {
                AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_warning)
                        .setTitle(R.string.action_remove_list)
                        .setMessage(R.string.action_remove_message_list)
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(android.R.string.yes) { _, _ -> presenter.deleteList() }
                        .show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDismiss(p0: DialogInterface?) = presenter.updateList()

    /********************************************************** List Detail View Part Method **********************************************************/
    override fun setupView() {
        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.title = list.name
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.detailToolbar.setTitleTextColor(Color.WHITE)

        binding.detailToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_white, theme)
        binding.detailToolbar.setNavigationOnClickListener { goToHomePage() }

        presenter.loadItems()
    }

    override fun cleanState() = presenter.unBind()

    override fun getListId(): String = list.id

    override fun getListName(): String = list.name

    override fun getOwner(): String = list.owner

    override fun displayItems() {
        Navigator.startFragment(supportFragmentManager, R.id.listDetailContainer, listItemFragment)
    }

    override fun requestNewName() {
        EditListNameDialog.newInstance(listModel = list)
                .show(supportFragmentManager, EditListNameDialog::class.java.simpleName)
    }

    override fun changeName(value: String) = presenter.updateName(value)

    override fun displayNewName(name: String) {
        list.name = name
        binding.ListDetailLayout.post { supportActionBar?.title = name }
    }

    override fun displayLoadingStatus() = dialogProgress.show(supportFragmentManager, LoadingDialog::class.java.simpleName)

    override fun hideLoadingStatus() = dialogProgress.dismiss()

    override fun goToHomePage() = finish()

    override fun displayErrorMessage(error: String) = binding.ListDetailLayout.snack(error)

}