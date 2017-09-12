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

package com.dbeginc.dbshopping.home.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.addlist.view.AddListActivity
import com.dbeginc.dbshopping.authentication.login.view.LoginActivity
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.databinding.ActivityHomeBinding
import com.dbeginc.dbshopping.databinding.DrawerHeaderBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.Navigator
import com.dbeginc.dbshopping.helper.extensions.snack
import com.dbeginc.dbshopping.home.HomeContract
import com.dbeginc.dbshopping.userlist.view.UserListFragment
import com.dbeginc.dbshopping.viewmodels.AccountModel
import com.dbeginc.dbshopping.viewmodels.UserModel
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeContract.HomeView {

    @Inject lateinit var presenter: HomeContract.HomePresenter
    @Inject lateinit var user: UserModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var toolbarDrawerToggle: ActionBarDrawerToggle
    private lateinit var userListView: UserListFragment

    /********************************************************** Android Part Method **********************************************************/
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        userListView = UserListFragment.newInstance(user.name)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /* Inflate the menu; this adds items to the action bar if it's present. */
        menuInflater.inflate(R.menu.home_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_sort_lists -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        supportFragmentManager.fragments.forEach { it.onRequestPermissionsResult(requestCode, permissions, grantResults) }
    }

    /********************************************************** Home View Part Method **********************************************************/

    override fun setupView() {
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.title = user.name.plus(" " + ConstantHolder.LISTS)

        binding.homeTab.setDefaultTab(R.id.list)
        binding.homeTab.setOnTabSelectListener { tabId ->
            when(tabId) {
                R.id.list -> presenter.onUserListClick()
                R.id.timeline -> presenter.onTimeLine()
            }
        }

        binding.addListBtn.setOnClickListener { presenter.onAddListClick() }

        presenter.loadAccount()

        binding.homeNavigationView.setNavigationItemSelectedListener {
            menuItem -> presenter.onNavigationClick(menuItem.itemId)
            return@setNavigationItemSelectedListener true
        }

        /*********************************** Navigation drawer setup ***********************************/
        toolbarDrawerToggle = ActionBarDrawerToggle(this, binding.homeLayout, binding.homeToolbar, R.string.action_open_drawer, R.string.action_close_drawer)
        binding.homeLayout.addDrawerListener(toolbarDrawerToggle)
        toolbarDrawerToggle.syncState()
    }

    override fun showAccount(accountModel: AccountModel) {
        DataBindingUtil.bind<DrawerHeaderBinding>(binding.homeNavigationView.getHeaderView(0)).account = accountModel
    }

    override fun displayAddListPage() = Navigator.startActivity(this, Intent(this, AddListActivity::class.java))

    override fun displayUserListPage() = Navigator.startFragment(supportFragmentManager, R.id.homeFragmentContainer, userListView)

    override fun displayTimeLine() = binding.homeLayout.snack("Going to timeline page")

    override fun getAppUser(): UserModel = user

    override fun displayUserProfile() {
        binding.homeLayout.closeDrawers()
        binding.homeLayout.snack("opened user profile")
    }

    override fun displayHelp() {
        binding.homeLayout.closeDrawers()
        binding.homeLayout.snack("opened help")
    }

    override fun sendComments() {
        binding.homeLayout.closeDrawers()
        binding.homeLayout.snack("send comments")
    }

    override fun recommendUs() {
        binding.homeLayout.closeDrawers()
        binding.homeLayout.snack("recommend use")
    }

    override fun confirmLogout() {
        binding.homeLayout.closeDrawers()
        AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_error)
                .setTitle(R.string.action_confirm_action)
                .setMessage(R.string.action_logout)
                .setPositiveButton(R.string.action_yes, { _, _ -> presenter.logout() })
                .setNegativeButton(android.R.string.no, { _, _ -> })
                .create()
                .show()
    }

    override fun goToLogin() = Navigator.startActivity(this, Intent(this, LoginActivity::class.java))

    override fun displayErrorMessage(error: String) = binding.homeLayout.snack(error)

    override fun cleanState() = presenter.unBind()
}