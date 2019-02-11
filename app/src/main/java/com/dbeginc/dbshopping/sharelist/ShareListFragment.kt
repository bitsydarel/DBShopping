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

package com.dbeginc.dbshopping.sharelist


import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentShareListBinding
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LIST_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.dbshopping.utils.helper.WithParentRequiredArguments
import com.dbeginc.users.friends.sharelist.ShareListView
import com.dbeginc.users.friends.sharelist.ShareListViewModel
import com.dbeginc.users.viewmodels.FriendModel


/**
 * A Share List Fragment [ShareListFragment].
 */
class ShareListFragment : BaseFragment(), ShareListView, WithParentRequiredArguments {
    private lateinit var listId: String
    private lateinit var listName: String
    private lateinit var viewModel: ShareListViewModel
    private lateinit var binding: FragmentShareListBinding
    private lateinit var listMembers: List<String>
    private val shareAdapter by lazy { ShareListAdapter() }
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }
    private val membersObserver = Observer<List<String>> {
        listMembers = it!!

        shareAdapter.updateData(newData = viewModel
                .presenter
                .findUserWhoAreNotMemberOfThisList(
                        shareAdapter.getDataSet(),
                        listMembers
                )
        )
    }
    private val friendObserver = Observer<List<FriendModel>> {
        shareAdapter.updateData(newData = viewModel
                .presenter
                .findUserWhoAreNotMemberOfThisList(
                        it!!,
                        listMembers
                )
        )
    }

    companion object {
        private const val LIST_NAME = "list_name"
        private const val LIST_OF_MEMBERS = "list_of_members"

        @JvmStatic
        fun newInstance(listId: String, listName: String) : ShareListFragment {
            val args = Bundle().apply {
                putString(LIST_DATA_KEY, listId)
                putString(LIST_NAME, listName)
            }

            val fragment = ShareListFragment()

            fragment.arguments = args

            return fragment
        }
    }

    override fun retrieveParentArguments(): Bundle {
        return Bundle().apply {
            putString(LIST_DATA_KEY, listId)
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        if (savedState == null) {
            listId = arguments!!.getString(LIST_DATA_KEY)
            listName = arguments!!.getString(LIST_NAME)
            listMembers = emptyList()

        } else {
            listId = savedState.getString(LIST_DATA_KEY)
            listName = savedState.getString(LIST_NAME)
            listMembers = savedState.getStringArray(LIST_OF_MEMBERS).toList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LIST_DATA_KEY, listId)
        outState.putString(LIST_NAME, listName)
        outState.putStringArray(LIST_OF_MEMBERS, listMembers.toTypedArray())
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[ShareListViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

        viewModel.getMembers()
                .observe(this, membersObserver)

        viewModel.getFriends()
                .observe(this, friendObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.share_list_menu, menu)

        val activity = activity as? MainActivity

        activity?.let {
            val searchManager = it.getSystemService(Context.SEARCH_SERVICE) as SearchManager

            val searchView = menu.findItem(R.id.action_search_friend).actionView as SearchView

            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))

            searchView.isSubmitButtonEnabled = false

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.presenter.isQueryValid(newText, this@ShareListFragment)
                    return true
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_share_list,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setSupportActionBar(binding.shareListToolbar)

        binding.shareListToolbar.setNavigationOnClickListener {
            MainNavigator.goToListDetail(
                activity as MainActivity,
                retrieveParentArguments()
            )
        }

        viewModel.presenter.bind(this)

    }

    override fun setupView() {
        binding.shareListFriends.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        )

        binding.shareListFriends.adapter = shareAdapter

        binding.shareListProgressBar.hideWithAnimation()

        binding.shareListBtn.setOnClickListener {
            val newMembers = shareAdapter.guysToShareWith.toList()

            if (listMembers.isEmpty()) MainNavigator.goToListDetail(
                    activity as MainActivity,
                    retrieveParentArguments()
            )
            else currentUser?.let {
                viewModel.shareListWithFriends(
                        listId = listId,
                        listName = listName,
                        currentUser = it,
                        newMembers = newMembers
                )
            }
        }

        viewModel.loadMembers(listId)

        viewModel.loadFriends(preferences.getString(ConstantHolder.USER_ID, ""))

    }

    override fun findUserWithName(username: String) {
        viewModel.findFriends(preferences.getString(ConstantHolder.USER_ID, ""), username)
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> binding.shareListProgressBar.showWithAnimation()
            RequestState.COMPLETED -> binding.root.postDelayed(this::onRequestCompleted, LOADING_PERIOD)
            RequestState.ERROR -> binding.root.postDelayed(this::onRequestFailed, LOADING_PERIOD)
        }
    }

    private fun onRequestCompleted() {
        binding.shareListProgressBar.hideWithAnimation()

        when(viewModel.getLastRequest()) {
            RequestType.ADD -> return
            RequestType.GET -> return
            RequestType.UPDATE -> Snackbar.make(binding.shareListLayout, R.string.sent_friend_request, Snackbar.LENGTH_LONG)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            (activity as? MainActivity)?.let {
                                MainNavigator.goToListDetail(it, retrieveParentArguments())
                            }
                        }
                    })
                    .show()
            RequestType.IMAGE -> return
            RequestType.DELETE -> return
        }
    }

    private fun onRequestFailed() {
        binding.shareListProgressBar.hideWithAnimation()

        val notifier = Snackbar.make(binding.shareListLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)

        when(viewModel.getLastRequest()) {
            RequestType.ADD -> return
            RequestType.GET -> return
            RequestType.UPDATE -> notifier.setText(R.string.error_while_sharing_your_share_lists)
            RequestType.IMAGE -> return
            RequestType.DELETE -> return
        }

        notifier.show()
    }

}