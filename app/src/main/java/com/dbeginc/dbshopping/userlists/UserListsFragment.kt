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


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.view.MVPVView
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentUserListsBinding
import com.dbeginc.dbshopping.utils.customviews.BadgeDrawable
import com.dbeginc.dbshopping.utils.extensions.*
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.SORTING_ORDER
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.domain.entities.user.FriendRequest
import com.dbeginc.lists.userlists.UserListsViewModel
import com.dbeginc.lists.userlists.UserListsViewModel.Companion.ORDER_BY_PUBLISH_TIME
import com.dbeginc.lists.userlists.userlist.UserListView
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel
import com.dbeginc.users.friends.friendrequests.FriendRequestViewModel


/**
 * A UserListsFragment [UserListsFragment] subclass.
 */
class UserListsFragment : BaseFragment(), MVPVView, UserListAdapterBridge {
    private var listsToolbarMenu: Menu? = null
    private lateinit var viewModel: UserListsViewModel
    private lateinit var friendViewModel: FriendRequestViewModel
    private lateinit var binding: FragmentUserListsBinding
    private val listsAdapter by lazy { UserListsAdapter(actionBridge = this) }
    private val swipeToRemove by lazy { ItemTouchHelper(SwipeToDeleteList(this)) }
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }
    private val membersObserver = Observer<Pair<String, List<ShoppingUserModel>>> { listsAdapter.findWhoIsShopping(it!!) }
    private val friendRequestObserver = Observer<List<FriendRequest>> { setAmountOfFriendRequest(it!!) }
    private val listsObserver = Observer<List<ListModel>> {
        setLists(lists = it!!)
        viewModel.findMembers(it)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[UserListsViewModel::class.java]

        friendViewModel = ViewModelProviders.of(this, viewModelFactory)[FriendRequestViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent().observe(this, stateObserver)

        viewModel.getLists().observe(this, listsObserver)

        viewModel.getMembers().observe(this, membersObserver)

        friendViewModel.getFriendRequests().observe(this, friendRequestObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.user_lists_menu, menu)

        listsToolbarMenu = menu

        val searchView = menu.findItem(R.id.action_search_list).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(possibleListName: String): Boolean {
                if (viewModel.presenter.isQueryValid(possibleListName))
                    viewModel.findList(
                            possibleListName,
                            preferences.getString(ConstantHolder.USER_ID, "")
                    )

                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_open_notifications -> {
                MainNavigator.goToFriendRequestsScreen(activity as MainActivity)
                return true
            }
            R.id.action_sort_lists -> {
                UserListsSortingPreferenceDialog()
                        .show(childFragmentManager, UserListsSortingPreferenceDialog::class.java.simpleName)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_user_lists,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as?MainActivity)?.setSupportActionBar(binding.userListsToolbar)

        setupView()
    }

    override fun setupView() {
        binding.userListsEmptyAnimation.hide()
        binding.userListsEmptyMessage.hide()

        binding.userListsToolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.openNavigationDrawer()
        }

        binding.userLists.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.userLists.adapter = listsAdapter

        binding.userLists.setRecyclerListener {
            listsAdapter.freeViewHolder(it as UserListViewHolder)
        }

        swipeToRemove.attachToRecyclerView(binding.userLists)

        binding.userListsRefresh.setOnRefreshListener {
            getUserLists(userId = preferences.getString(ConstantHolder.USER_ID, ""))
        }

        binding.userListsCreateListBtn.setOnClickListener { MainNavigator.goToAddListScreen(activity as MainActivity) }

        val userId = preferences.getString(ConstantHolder.USER_ID, "")

        getUserLists(userId = userId)

    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> binding.userListsRefresh.isRefreshing = true
            RequestState.COMPLETED -> binding.userListsRefresh.isRefreshing = false
            RequestState.ERROR -> onListsRequestFailed()
        }
    }

    override fun onListDeleted(list: ListModel, position: Int) {
        Snackbar.make(binding.userListsLayout, R.string.list_deleted, Snackbar.LENGTH_LONG)
                .setAction(android.R.string.cancel) { listsAdapter.cancelItemDeletion(position) }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT) {
                            listsAdapter.remoteItemAt(position)
                            viewModel.deleteList(list.uniqueId)
                        }
                    }
                })
                .show()
    }

    override fun isCurrentUserTheCreatorOfTheList(list: ListModel): Boolean = list.ownerId == preferences.getString(ConstantHolder.USER_ID, "")

    override fun onListSelected(list: ListModel) = goToListDetail(list)

    override fun findWhoIsShopping(members: List<ShoppingUserModel>, view: UserListView) {
        currentUser?.let {
            viewModel.presenter.findWhoIsShopping(it.nickname, members, view)
        }
    }

    private fun onListsRequestFailed() {
        binding.userListsRefresh.isRefreshing = false

        Snackbar.make(binding.userListsLayout, R.string.couldNotLoadLists, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { getUserLists(preferences.getString(ConstantHolder.USER_ID, "")) })
                .show()
    }

    private fun setLists(lists: List<ListModel>) {
        if (lists.isEmpty()) showEmptyDatasetMessage()
        else hideEmptyDatasetMessage()

        listsAdapter.updateData(lists)
    }

    private fun setAmountOfFriendRequest(requests: List<FriendRequest>) {
        val friendRequestMenuItem = listsToolbarMenu?.findItem(R.id.action_open_notifications)

        val friendRequestsIcon : LayerDrawable? = friendRequestMenuItem?.icon as? LayerDrawable

        val iconToReuse : Drawable? = friendRequestsIcon?.findDrawableByLayerId(R.id.friend_requests_count)

        val badge = iconToReuse as? BadgeDrawable ?: BadgeDrawable(binding.root.context)

        badge.setCount(requests.size)
        
        friendRequestsIcon?.mutate()

        friendRequestsIcon?.setDrawableByLayerId(R.id.friend_requests_count, badge)
    }

    private fun hideEmptyDatasetMessage() {
        binding.userListsEmptyAnimation.hideWithAnimation()

        binding.userListsEmptyMessage.hideWithAnimation()

        binding.userLists.showWithAnimation()
    }

    private fun showEmptyDatasetMessage() {
        binding.userLists.hideWithAnimation()

        if (!binding.userListsEmptyAnimation.isVisible()) {
            binding.userListsEmptyAnimation.show()
            binding.userListsEmptyMessage.show()
        }

        binding.userListsEmptyAnimation.showWithAnimation()

        binding.userListsEmptyMessage.showWithAnimation()
    }

    private fun getUserLists(userId : String = currentUser?.uniqueId ?: preferences.getString(ConstantHolder.USER_ID, "")) {
        val sortingOrder = preferences.getInt(SORTING_ORDER, ORDER_BY_PUBLISH_TIME)

        viewModel.loadLists(userId, sortingOrder)

        friendViewModel.getFriendRequests(userId)

    }

    private fun goToListDetail(list: ListModel) {
        MainNavigator.goToListDetail(activity as MainActivity, Bundle().apply {
            putString(ConstantHolder.LIST_DATA_KEY, list.uniqueId)
        })
    }

}
