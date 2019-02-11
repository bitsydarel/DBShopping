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

package com.dbeginc.dbshopping.addlist


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentAddListBinding
import com.dbeginc.dbshopping.utils.extensions.*
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.lists.addlist.AddListView
import com.dbeginc.lists.addlist.AddListViewModel
import com.dbeginc.lists.viewmodels.ListModel
import org.threeten.bp.Instant
import java.util.*


/**
 * Add List Fragment [AddListFragment]
 *
 * Add a list to user his lists
 */
class AddListFragment : BaseFragment(), AddListView {
    private lateinit var viewModel: AddListViewModel
    private lateinit var binding: FragmentAddListBinding
    private val stateObserver = Observer<RequestState> {
        onStateChanged(it!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[AddListViewModel::class.java]

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_add_list,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.addListToolbar)

        viewModel.presenter.bind(this)
    }

    override fun setupView() {

        binding.addListToolbar.setNavigationOnClickListener {
            MainNavigator.goToListsScreen(activity as MainActivity)
        }

        binding.addListCreateBtn.setOnClickListener {
            viewModel.presenter.validateListName(
                    binding.addListName.getValue(),
                    this
            )
        }

        binding.addListName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.addListCreateBtn.callOnClick()
            return@setOnEditorActionListener true
        }

        binding.addListName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(text: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.addListNameContainer.error = null
            }
        })

        binding.addListLoadingProgress.hideWithAnimation()
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> onStartCreatingList()
            RequestState.COMPLETED -> binding.root.postDelayed(this::onCompletedToCreateList, LOADING_PERIOD)
            RequestState.ERROR -> binding.root.postDelayed(this::onErrorWhileCreatingList, LOADING_PERIOD)
        }
    }

    override fun onListNameValid(name: String) {
        currentUser?.let {
            val list = ListModel(
                    uniqueId = UUID.randomUUID().toString(),
                    name = name,
                    ownerId = it.uniqueId,
                    ownerName = it.nickname,
                    lastChange = Instant.now().toEpochMilli()
            )

            viewModel.addList(list, it.toShoppingUser(false))
        }
    }

    override fun onListNameInvalid() {
        binding.addListNameContainer.error = getString(R.string.invalid_name)
    }

    private fun onStartCreatingList() {
        binding.addListCreateBtn.hideWithAnimation()

        binding.addListLoadingProgress.showWithAnimation()
    }

    private fun onCompletedToCreateList() {
        binding.addListLoadingProgress.hideWithAnimation()

        binding.addListCreateBtn.showWithAnimation()

        MainNavigator.goToListsScreen(activity as MainActivity)
    }

    private fun onErrorWhileCreatingList() {
        binding.addListLoadingProgress.hideWithAnimation()

        binding.addListCreateBtn.showWithAnimation()

        if (viewModel.getLastRequest() == RequestType.ADD) binding.addListLayout.snack(resId =  R.string.list_already_exist)
        else {
            Snackbar.make(binding.addListLayout, getString(R.string.error_while_creating_list), Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_retry) { binding.addListCreateBtn.callOnClick() }
                    .show()
        }
    }
}
