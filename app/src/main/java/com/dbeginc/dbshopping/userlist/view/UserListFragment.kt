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

package com.dbeginc.dbshopping.userlist.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.UserListLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.extensions.*
import com.dbeginc.dbshopping.userlist.UserListContract
import com.dbeginc.dbshopping.userlist.adapter.UserListAdapter
import com.dbeginc.dbshopping.viewmodels.ListModel
import javax.inject.Inject

/**
 * Created by darel on 22.08.17.
 *
 * User List View
 */
class UserListFragment : BaseFragment(), UserListContract.UserListView {

    @Inject lateinit var presenter: UserListContract.UserListPresenter
    private lateinit var binding: UserListLayoutBinding
    private lateinit var adapter: UserListAdapter
    private lateinit var currentUser: String

    /********************************************************** Android Part Method **********************************************************/
    companion object {
        @JvmStatic fun newInstance(currentUserName: String): UserListFragment {
            val fragment = UserListFragment()
            val arguments = Bundle()

            arguments.putString(ConstantHolder.CURRENT_USERNAME, currentUserName)

            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)

        if (savedState == null) {
            currentUser = arguments.getString(ConstantHolder.CURRENT_USERNAME)
            adapter = UserListAdapter(emptyList(), currentUser)
        } else {
            val lists : List<ListModel> = savedState.getList(ConstantHolder.LIST_DATA_KEY) ?: emptyList()
            currentUser = savedState.getString(ConstantHolder.CURRENT_USERNAME)
            adapter = UserListAdapter(lists, currentUser)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putList(ConstantHolder.LIST_DATA_KEY, adapter.getData())
        outState?.putString(ConstantHolder.CURRENT_USERNAME, currentUser)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_list_layout, container, false)
        return binding.root
    }

    /********************************************************** User List View Part Method **********************************************************/

    override fun setupView() {
        binding.userListRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.userListRecyclerView.adapter = adapter
        binding.userListRefresh.setOnRefreshListener { presenter.loadList() }

        presenter.loadList()
    }

    override fun cleanState() = presenter.unBind()

    override fun displayUserList(lists: List<ListModel>) = adapter.updateData(lists)

    override fun displayNoListAvailableMessage() = binding.emptyContent.show()

    override fun hideNoListAvailableMessage() = binding.emptyContent.hide()

    override fun showUserLists() = binding.userListRecyclerView.show()

    override fun hideUserLists() = binding.userListRecyclerView.hide()

    override fun displayLoadingStatus() {
        binding.userListRefresh.isRefreshing = true
    }

    override fun hideLoadingStatus() {
        binding.userListRefresh.isRefreshing = false
    }

    override fun displayErrorMessage(error: String) = binding.userListLayout.snack(error)

}