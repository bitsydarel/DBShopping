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

package com.dbeginc.dbshopping.listdetail.listmembers


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.view.MVPVView
import com.dbeginc.dbshopping.MainActivity

import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseAdapter
import com.dbeginc.dbshopping.base.BaseDataDiff
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentListMemberBinding
import com.dbeginc.dbshopping.databinding.ListMemberLayoutBinding
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LIST_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.dbshopping.utils.helper.WithParentRequiredArguments
import com.dbeginc.lists.listdetail.ListDetailViewModel
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel
import java.util.*


/**
 * Created by darel bitsy
 *
 * List Member Fragment.
 */
class ListMemberFragment : BaseFragment(), MVPVView, WithParentRequiredArguments {
    private lateinit var listId: String
    private lateinit var viewModel: ListDetailViewModel
    private lateinit var binding: FragmentListMemberBinding
    private val listMembersAdapter by lazy { MembersAdapter() }
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }
    private val membersObserver = Observer<Pair<ListModel, List<ShoppingUserModel>>> {
        binding.listName = it!!.first.name

        binding.listMemberToolbar.subtitle = getString(R.string.listMemberCount, it.second.size)

        listMembersAdapter.updateData(it.second)
    }

    companion object {
        @JvmStatic
        fun newInstance(args: Bundle) : ListMemberFragment {
            val fragment = ListMemberFragment()

            fragment.arguments = args

            return fragment
        }
    }

    override fun retrieveParentArguments(): Bundle {
        return Bundle().apply {
            putString(ConstantHolder.LIST_DATA_KEY, listId)
        }
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
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        listId = if (savedState == null) arguments!!.getString(ConstantHolder.LIST_DATA_KEY) else savedState.getString(ConstantHolder.LIST_DATA_KEY)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(LIST_DATA_KEY, listId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_list_member,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        super.onViewCreated(view, state)

        (activity as AppCompatActivity).setSupportActionBar(binding.listMemberToolbar)

        setupView()

    }

    override fun setupView() {
        binding.listMemberProgressBar.hideWithAnimation()

        binding.listMemberToolbar.setNavigationOnClickListener { MainNavigator.goToListDetail(
                activity as MainActivity,
                retrieveParentArguments()
        ) }

        binding.addMember.setOnClickListener {
            (activity as? MainActivity)?.let {
                binding.listName?.let { validName ->
                    MainNavigator.goToShareListScreen(
                            it,
                            listId,
                            validName
                    )
                }
            }
        }

        binding.listMembers.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.listMembers.adapter = listMembersAdapter

        binding.listMembers.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        viewModel.loadList(listId)
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> binding.listMemberProgressBar.showWithAnimation()
            RequestState.COMPLETED -> binding.listMemberLayout.postDelayed(binding.listMemberProgressBar::hideWithAnimation, ConstantHolder.LOADING_PERIOD)
            RequestState.ERROR -> binding.listMemberLayout.postDelayed(this::onRequestFailed, ConstantHolder.LOADING_PERIOD)
        }
    }

    private fun onRequestFailed() {
        Snackbar.make(binding.listMemberLayout, R.string.couldNotLoadListMembers, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { viewModel.loadList(listId) })
                .setActionTextColor(Color.RED)
                .show()
    }

    inner class MembersAdapter(override var data: LinkedList<ShoppingUserModel> = LinkedList()) : BaseAdapter<ShoppingUserModel, MembersAdapter.MemberViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
            return MemberViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_member_layout,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
            val member = data[position]

            holder.bindMember(member)
        }

        @Synchronized
        override fun updateData(newData: List<ShoppingUserModel>) {
            val diffResult = DiffUtil.calculateDiff(ListMemberDiff(data, newData))

            data = LinkedList(newData)

            diffResult.dispatchUpdatesTo(this)
        }

        inner class MemberViewHolder(private val binding: ListMemberLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bindMember(member: ShoppingUserModel) {
                binding.member = member

                binding.executePendingBindings()
            }
        }

        inner class ListMemberDiff(oldMembers: List<ShoppingUserModel>, newMembers: List<ShoppingUserModel>) : BaseDataDiff<ShoppingUserModel>(oldMembers, newMembers) {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    oldData[oldItemPosition].uniqueId == newData[newItemPosition].uniqueId
        }
    }

}
