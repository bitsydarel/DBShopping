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

package com.dbeginc.dbshopping.listitems.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.databinding.ListItemsLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.extensions.*
import com.dbeginc.dbshopping.listitems.ListItemsContract
import com.dbeginc.dbshopping.listitems.adapter.ItemsAdapter
import com.dbeginc.dbshopping.listitems.presenter.ListItemsPresenterImpl
import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.dbshopping.viewmodels.UserModel
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject

class ListItemsFragment : BaseFragment(), ListItemsContract.ListItemsView {
    @Inject lateinit var presenter: ListItemsContract.ListItemPresenter
    @Inject lateinit var user: UserModel
    private lateinit var binding: ListItemsLayoutBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var listId: String
    private lateinit var adapter: ItemsAdapter
    private lateinit var defaultName: String
    private lateinit var shoppingUsers: List<String>
    private val swipeToRemove = ItemTouchHelper(SwipeToDeleteItem())
    private val shoppingModeUpdate: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private var items = emptyList<ItemModel>()
    /************ User Shopping Messages *************/
    private val noUserShoppingMessage by lazy { getString(R.string.nobodyIsShopping) }
    private val currentUserShoppingMessage by lazy { getString(R.string.youAreShopping) }
    private val currentUserShoppingWithUsersMessage by lazy { getString(R.string.youAndUsersAreShopping) }
    private val currentUserShoppingWithMessage by lazy { getString(R.string.youAndUserAreShopping) }
    private val userIsShopping by lazy { getString(R.string.userIsShopping) }
    private val twoUserAreShoppingMessage by lazy { getString(R.string.twoUsersAreShopping) }
    private val usersAreShoppingMessage by lazy { getString(R.string.UsersAreShopping) }

    /********************************************************** Android Part Method **********************************************************/
    companion object {
        @JvmStatic fun newInstance(listId: String, listOfShoppingUser: List<String>) : ListItemsFragment {
            val fragment = ListItemsFragment()
            val args = Bundle()
            args.putString(ConstantHolder.LIST_ID, listId)
            args.putStringArrayList(ConstantHolder.SHOPPING_USERS, listOfShoppingUser as ArrayList<String>)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)

        if (savedState == null) {
            listId = arguments.getString(ConstantHolder.LIST_ID)
            // set the current shopping mode
            shoppingUsers = arguments.getStringArrayList(ConstantHolder.SHOPPING_USERS)
            shoppingModeUpdate.onNext(shoppingUsers.contains(user.id))

            adapter = ItemsAdapter(items, user.name, shoppingModeUpdate, (presenter as ListItemsPresenterImpl).itemUpdate)

        } else {
            listId = savedState.getString(ConstantHolder.LIST_ID)
            items = savedState.getList(ConstantHolder.ITEM_DATA_KEY) ?: emptyList()
            // Recover current shopping mode
            shoppingUsers = savedState.getStringArrayList(ConstantHolder.SHOPPING_USERS)
            shoppingModeUpdate.onNext(shoppingUsers.contains(user.id))

            adapter = ItemsAdapter(items, user.name, shoppingModeUpdate, (presenter as ListItemsPresenterImpl).itemUpdate)
        }

        defaultName = getString(R.string.default_item_name)
        loadingDialog = LoadingDialog()
        loadingDialog.setMessage(getString(R.string.savingItemChanges))
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.list_items_layout, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(ConstantHolder.LIST_ID, listId)
        outState?.putList(ConstantHolder.ITEM_DATA_KEY, adapter.getViewModels())
        outState?.putStringArrayList(ConstantHolder.SHOPPING_USERS, shoppingUsers as ArrayList<String>)
    }

    /********************************************************** List Items View Part Method **********************************************************/

    override fun setupView() {
        binding.listItemsRCV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.listItemsRCV.adapter = adapter
        swipeToRemove.attachToRecyclerView(binding.listItemsRCV)

        binding.addItemBtn.setOnClickListener { presenter.addItem() }
        binding.listItemsSRL.setOnRefreshListener { presenter.loadItems() }

        binding.listInShoppingModeStatus.setOnCheckedChangeListener { _, isOn -> presenter.onShoppingStatusChange(isOn) }
        binding.listInShoppingModeStatus.isChecked = shoppingModeUpdate.value as Boolean

        presenter.loadItems()
        presenter.defineUsersShopping()
    }

    override fun cleanState() {
        shoppingModeUpdate.onComplete()
        presenter.unBind()
    }

    /********************************************************** User Part Method **********************************************************/
    override fun getAppUser(): UserModel = user

    override fun getUsersShopping(): List<String> = shoppingUsers

    override fun displayNoUserShopping() {
        binding.listInShoppingModeLabel.text = noUserShoppingMessage
    }

    override fun displayCurrentUserShoppingAlone() {
        binding.listInShoppingModeLabel.text = currentUserShoppingMessage
    }

    override fun displayCurrentUserShoppingWith(numberOfUsers: Int) {
        binding.listInShoppingModeLabel.text = String.format(currentUserShoppingWithUsersMessage, numberOfUsers)
    }

    override fun displayCurrentUserShoppingWith(name: String) {
        binding.listInShoppingModeLabel.text = String.format(currentUserShoppingWithMessage, name)
    }

    override fun displayUserShopping(username: String) {
        binding.listInShoppingModeLabel.text = String.format(userIsShopping, username)
    }

    override fun displayTheTwoUsersShopping(firstUserName: String, secondUserName: String) {
        binding.listInShoppingModeLabel.text = String.format(twoUserAreShoppingMessage, firstUserName, secondUserName)
    }

    override fun displayUsersShopping(numberOfUsers: Int) {
        binding.listInShoppingModeLabel.text = String.format(usersAreShoppingMessage, numberOfUsers)
    }

    override fun showGettingUsersShoppingStatus() {
        binding.listInShoppingModeLabel.invisible()
        binding.loadingUsersShoppingPB.show()
    }

    override fun hideGettingUsersShoppingStatus() {
        binding.listInShoppingModeLabel.show()
        binding.loadingUsersShoppingPB.hide()
    }

    /********************************************************** Data Part Method **********************************************************/
    override fun getListId(): String = listId

    override fun displayItems(items: List<ItemModel>) = adapter.updateData(items)

    override fun hideItems() = binding.listItemsRCV.hide()

    override fun displayNoItemsMessage() = binding.emptyContent.show()

    override fun hideNoItemsMessage() = binding.emptyContent.hide()

    override fun addItem(item: ItemModel) = adapter.addItem(item)

    override fun removeItem(position: Int) = adapter.removeItem(position)

    override fun getItemAtPosition(position: Int): ItemModel = adapter.getViewModelAtPosition(position)

    override fun getDefaultItemName(): String = defaultName

    override fun getItemsSize(): Int = adapter.itemCount

    override fun displayUpdatingStatus() = binding.loadingBarPB.show()

    override fun hideUpdatingStatus() = binding.loadingBarPB.hide()

    override fun displayLoadingStatus() {
        binding.listItemsSRL.isRefreshing = true
    }

    override fun hideLoadingStatus() {
        binding.listItemsSRL.isRefreshing = false
    }

    override fun displayErrorMessage(error: String) = binding.listItemsLayout.snack(error)

    override fun enableShoppingMode() {
        shoppingModeUpdate.onNext(true)
        binding.listItemsLayout.snack(getString(R.string.shoppingModeEnable))
    }

    override fun disableShoppingMode() {
        shoppingModeUpdate.onNext(false)
        binding.listItemsLayout.snack(getString(R.string.shoppingModeDisable))
    }

    private inner class SwipeToDeleteItem : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //Todo: implement cancellation of task
            presenter.removeItem(viewHolder.adapterPosition)
        }
    }
}