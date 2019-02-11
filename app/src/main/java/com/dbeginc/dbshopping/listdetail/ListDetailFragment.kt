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

package com.dbeginc.dbshopping.listdetail


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentListDetailBinding
import com.dbeginc.dbshopping.listdetail.items.ItemsAdapter
import com.dbeginc.dbshopping.listdetail.items.ItemsAdapterBridge
import com.dbeginc.dbshopping.listdetail.items.SwipeToDeleteItem
import com.dbeginc.dbshopping.utils.extensions.*
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.ITEM_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LIST_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.lists.listdetail.ListDetailView
import com.dbeginc.lists.listdetail.ListDetailViewModel
import com.dbeginc.lists.viewmodels.ItemModel
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ListDetailFragment : BaseFragment(), ListDetailView, ItemsAdapterBridge, EditListNameDialog.ListNameChangeListener {
    private lateinit var listId: String
    private var toolbarMenu: Menu? = null
    private lateinit var viewModel: ListDetailViewModel
    private lateinit var binding: FragmentListDetailBinding
    private val uiHelper by lazy { Handler() }
    private val itemsAdapter by lazy { ItemsAdapter(actionBridge = this) }
    private val swipeToRemove by lazy { ItemTouchHelper(SwipeToDeleteItem(this)) }
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }
    private val itemsObserver = Observer<List<ItemModel>> {
        setItems(items = it!!)
        uiHelper.post {
            calculateItemsTotalPrice(it)
        }
    }
    private val membersObserver = Observer<Pair<ListModel, List<ShoppingUserModel>>> {
        setListWithMembers(
                list = it!!.first,
                members = it.second
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(args: Bundle) : ListDetailFragment {
            val fragment = ListDetailFragment()

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        listId = if (savedState == null) arguments!!.getString(LIST_DATA_KEY) else savedState.getString(LIST_DATA_KEY)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[ListDetailViewModel::class.java]
    }

    override fun onActivityCreated(state: Bundle?) {
        super.onActivityCreated(state)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

        viewModel.getListWithMembers()
                .observe(this, membersObserver)

        viewModel.getItems()
                .observe(this, itemsObserver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(LIST_DATA_KEY, listId)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        /* Inflate the toolbarMenu; this adds items to the action bar if it's present. */
        inflater.inflate(R.menu.list_detail_menu, menu)

        toolbarMenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_edit_list_name -> {
                val editListNameDialog = EditListNameDialog()

                editListNameDialog.show(childFragmentManager, EditListNameDialog::class.java.simpleName)
                return true
            }

            R.id.action_share_list -> {
                MainNavigator.goToShareListScreen(activity as MainActivity, listId, binding.list!!.name)
                return true
            }
            R.id.action_list_members -> {
                MainNavigator.goToListMembersScreen(activity as MainActivity, listId)
                return true
            }
            R.id.action_remove_list -> {
                AlertDialog.Builder(context!!)
                        .setIcon(R.drawable.ic_warning)
                        .setTitle(R.string.action_remove_list)
                        .setMessage(R.string.action_remove_message_list)
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(android.R.string.yes) { _, _ -> viewModel.deleteList(listId) }
                        .show()
                return true
            }
        }
        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_list_detail,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        super.onViewCreated(view, state)

        if (listId != viewModel.presenter.listId) {
            viewModel.presenter.listId = listId

            viewModel.resetState()
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.listDetailToolbar)

        viewModel.presenter.bind(this)
    }

    override fun setupView() {
        binding.listDetailProgressBar.hideWithAnimation()

        binding.listDetailToolbar.setNavigationOnClickListener { MainNavigator.goToListsScreen(activity as MainActivity) }

        binding.addListItem.setOnClickListener { MainNavigator.goToAddItemScreen(activity as MainActivity, listId) }

        binding.listInShoppingModeStatus.apply {
            setOnClickListener {
                currentUser?.let {
                    viewModel.updateListMember(listId, it.toShoppingUser(isChecked))
                }
            }
        }

        binding.listItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.listItems.adapter = itemsAdapter

        swipeToRemove.attachToRecyclerView(binding.listItems)

        binding.ListDetailLayout.snack(resId = if (binding.listInShoppingModeStatus.isChecked) R.string.turnOffToggleShopping else R.string.turnOnToggleShopping)

        viewModel.loadList(listId)

        viewModel.loadItems(listId)

    }

    override fun onListNameChanged(newName: String) {
        viewModel.changeListName(listId, newName)
    }

    override fun showAdminActions() {
        toolbarMenu?.findItem(R.id.action_edit_list_name)?.isVisible = true
        toolbarMenu?.findItem(R.id.action_remove_list)?.isVisible = true
    }

    override fun hideAdminActions() {
        toolbarMenu?.findItem(R.id.action_edit_list_name)?.isVisible = false
        toolbarMenu?.findItem(R.id.action_remove_list)?.isVisible = false
    }

    override fun canUserEditItem(item: ItemModel): Boolean = !item.bought || item.boughtBy == currentUser?.nickname

    override fun isCurrentUserTheCreatorOfTheItem(item: ItemModel): Boolean = item.itemOwner == currentUser?.email

    override fun onItemDeleted(item: ItemModel, position: Int) {
        Snackbar.make(binding.ListDetailLayout, R.string.item_deleted, Snackbar.LENGTH_LONG)
                .setAction(android.R.string.cancel) { itemsAdapter.cancelItemDeletion(position) }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT) {
                            itemsAdapter.remoteItemAt(position)
                            viewModel.deleteItem(item)
                        }
                    }
                })
                .show()
    }

    override fun onItemSelected(item: ItemModel) {
        currentUser?.let {
            if (viewModel.presenter.canUserEditItem(nickname = it.nickname, email = it.email, item =  item)) MainNavigator.goToItemDetailScreen(activity as MainActivity, Bundle().apply {
                putString(LIST_DATA_KEY, listId)
                putString(ITEM_DATA_KEY, item.uniqueId)
            })
            else binding.ListDetailLayout.snack(resId = R.string.cant_edit_already_bought_item)
        }
    }

    override fun onItemBoughtStatusChanged(item: ItemModel) {
        currentUser?.let {
            viewModel.updateItem(
                    item.apply { boughtBy = if (bought) it.nickname else "" }
            )
        }
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> binding.listDetailProgressBar.showWithAnimation()
            RequestState.COMPLETED -> uiHelper.postDelayed(this::onRequestCompleted, LOADING_PERIOD)
            RequestState.ERROR -> uiHelper.postDelayed(this::onRequestFailed, LOADING_PERIOD)
        }
    }

    private fun onRequestCompleted() {
        binding.listDetailProgressBar.hideWithAnimation()

        when(viewModel.getLastRequest()) {
            RequestType.GET -> return
            RequestType.UPDATE -> return
            RequestType.IMAGE -> return
            RequestType.DELETE -> MainNavigator.goToListsScreen(activity as MainActivity)
            RequestType.ADD -> return
        }
    }

    private fun onRequestFailed() {
        binding.listDetailProgressBar.hideWithAnimation()

        val notifier = Snackbar.make(binding.ListDetailLayout, R.string.couldNotProcessYourRequest, Snackbar.LENGTH_LONG)

        when(viewModel.getLastRequest()) {
            RequestType.GET -> notifier.setText(R.string.couldNotLoadListDetail).setAction(R.string.action_retry) { viewModel.loadItems(listId) }
            RequestType.UPDATE -> notifier.setAction(R.string.action_retry) { viewModel.updateList(binding.list!!) }
            RequestType.IMAGE -> return
            RequestType.DELETE -> notifier.setAction(R.string.action_retry) { viewModel.deleteList(listId) }
            RequestType.ADD -> return
        }

        notifier.show()
    }

    private fun calculateItemsTotalPrice(lists: List<ItemModel>) {
        var totalPrice = 0.0

        lists.forEach { totalPrice += it.price * it.count }

        val formatter = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())

        formatter.maximumFractionDigits = 2

        binding.listItemsTotalAmount.text = getString(R.string.listTotalAmount, formatter.format(totalPrice))
    }

    private fun setListWithMembers(list: ListModel, members: List<ShoppingUserModel>) {
        binding.list = list

        viewModel.presenter.checkIfUserHasAdminRightOnList(
                preferences.getString(ConstantHolder.USER_ID, ""),
                list,
                this
        )

        binding.listDetailToolbar.subtitle = list.getFormattedTime(binding.root.context)

        currentUser?.let {
            val currentUserShopping = viewModel.presenter.isCurrentUserShopping(
                    it.nickname,
                    members
            )

            binding.listInShoppingModeStatus.isChecked = currentUserShopping

        }

        if (binding.listInShoppingModeStatus.isChecked) binding.listInShoppingModeLabel.setText(R.string.youAreCurrentlyShoppingList)
        else binding.listInShoppingModeLabel.setText(R.string.youAreNotCurrentlyShoppingList)

        binding.executePendingBindings()
    }

    private fun setItems(items: List<ItemModel>) = itemsAdapter.updateData(items)

}
