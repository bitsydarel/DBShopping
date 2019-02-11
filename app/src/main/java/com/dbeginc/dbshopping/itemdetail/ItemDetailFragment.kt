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

package com.dbeginc.dbshopping.itemdetail


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentItemDetailBinding
import com.dbeginc.dbshopping.utils.extensions.calculateTotalPrice
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.ITEM_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LIST_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.dbshopping.utils.helper.DBShoppingEngine
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.dbshopping.utils.helper.WithParentRequiredArguments
import com.dbeginc.lists.itemdetail.ItemDetailView
import com.dbeginc.lists.itemdetail.ItemDetailViewModel
import com.dbeginc.lists.viewmodels.ItemModel
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy


/**
 * Created by bitsy darel
 * A simple [Fragment] subclass.
 *
 * Item detail fragment
 */
class ItemDetailFragment : BaseFragment(), ItemDetailView, WithParentRequiredArguments {
    private lateinit var viewModel: ItemDetailViewModel
    private lateinit var binding : FragmentItemDetailBinding
    private var itemDetailMenu: Menu? = null
    private lateinit var listId: String
    private lateinit var itemId: String
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }
    private val itemObserver = Observer<ItemModel> { setItem(item = it!!) }

    companion object {
        @JvmStatic fun newInstance(args: Bundle) : ItemDetailFragment {
            return ItemDetailFragment().apply {
                arguments = args
            }
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
            itemId = arguments!!.getString(ITEM_DATA_KEY)

        } else {
            listId = savedState.getString(LIST_DATA_KEY)
            itemId = savedState.getString(ITEM_DATA_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LIST_DATA_KEY, binding.item?.itemOf ?: listId)
        outState.putString(ITEM_DATA_KEY, binding.item?.uniqueId ?: itemId)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[ItemDetailViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

        viewModel.getItem()
                .observe(this, itemObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_item_detail,
                container,
                false
        )

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.item_detail_menu, menu)

        itemDetailMenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_open_item_comment -> {
                MainNavigator.goToItemComment(activity as MainActivity, Bundle().apply {
                    putString(LIST_DATA_KEY, listId)
                    putString(ITEM_DATA_KEY, itemId)
                })
                return true
            }
            R.id.action_item_done -> {
                viewModel.uploadItem(listId, binding.item!!)
                return true
            }

            R.id.action_delete_item -> {
                AlertDialog.Builder(context!!)
                        .setIcon(R.drawable.ic_warning)
                        .setTitle(R.string.action_remove_item)
                        .setMessage(R.string.action_remove_message_item)
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(android.R.string.yes) { _, _ -> viewModel.deleteItem(listId, itemId) }
                        .show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ConstantHolder.WRITE_AND_READ_EXTERNAL_STORAGE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            binding.itemDetailImage.callOnClick()

        } else onUserRefuseToGrantPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ConstantHolder.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            binding.item?.imageUrl = Matisse.obtainResult(data).first().toString()
            viewModel.changeItemImage(listId, binding.item!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.itemDetailToolbar)

        viewModel.presenter.bind(this)

    }

    override fun setupView() {
        binding.itemDetailToolbar.setNavigationOnClickListener {
            goToListDetail()
        }

        binding.itemDetailRemoveCount.setOnClickListener {
            if (viewModel.presenter.canRemoveCount(binding.item!!)) {
                binding.item!!.count--

                binding.itemDetailCount.text = binding.item?.count.toString()

                calculateTotalPrice(binding.itemDetailTotalPrice, binding.item!!.price, binding.item!!.count)
            }
        }

        binding.itemDetailAddCount.setOnClickListener {
            binding.item!!.count++

            binding.itemDetailCount.text = binding.item?.count.toString()

            calculateTotalPrice(binding.itemDetailTotalPrice, binding.item!!.price, binding.item!!.count)
        }

        binding.itemDetailHasBeenBought.setOnClickListener {
            currentUser?.let {
                viewModel.uploadItem(
                        listId,
                        binding.item!!.apply {
                            bought = true
                            boughtBy = it.nickname
                        }
                )
            }
        }

        binding.itemDetailImage.setOnClickListener {
            onItemImageChange()
        }

        binding.itemDetailBoughtProgressBar.hideWithAnimation()

        binding.itemDetailProgressBar.hideWithAnimation()

        viewModel.loadItem(listId, itemId)

    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> onItemRequestStarted()
            RequestState.COMPLETED -> binding.root.postDelayed(this::onItemRequestCompleted, LOADING_PERIOD)
            RequestState.ERROR -> binding.root.postDelayed(this::onItemRequestFailed, LOADING_PERIOD)
        }
    }

    private fun onItemRequestStarted() {
        binding.itemDetailProgressBar.showWithAnimation()
        binding.itemDetailBoughtProgressBar.showWithAnimation()
        binding.itemDetailHasBeenBought.hideWithAnimation()
    }

    private fun onItemRequestCompleted() {
        binding.itemDetailProgressBar.hideWithAnimation()
        binding.itemDetailBoughtProgressBar.hideWithAnimation()
        binding.itemDetailHasBeenBought.showWithAnimation()

        when(viewModel.getLastRequest()) {
            RequestType.GET -> return
            RequestType.UPDATE -> goToListDetail()
            RequestType.IMAGE -> return
            RequestType.DELETE -> goToListDetail()
            RequestType.ADD -> return
        }
    }

    private fun onItemRequestFailed() {
        binding.itemDetailProgressBar.hideWithAnimation()
        binding.itemDetailBoughtProgressBar.hideWithAnimation()
        binding.itemDetailHasBeenBought.showWithAnimation()

        val notifier = Snackbar.make(binding.itemDetailLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)

        when(viewModel.getLastRequest()) {
            RequestType.GET -> notifier.setText(R.string.couldNotLoadItemDetail).setAction(R.string.action_retry) { viewModel.loadItem(listId, itemId) }
            RequestType.UPDATE -> notifier.setText(R.string.couldNotProcessYourRequest).setAction(R.string.action_retry) { viewModel.uploadItem(listId, binding.item!!) }
            RequestType.IMAGE -> notifier.setText(R.string.couldNotChangeItemPicture).setAction(R.string.action_retry) { viewModel.changeItemImage(listId, binding.item!!) }
            RequestType.DELETE -> notifier.setText(R.string.couldNotProcessYourRequest).setAction(R.string.action_retry) { viewModel.deleteItem(listId, itemId) }
            RequestType.ADD -> {
                notifier.show()
                return
            }
        }

        notifier.show()
    }

    private fun goToListDetail() {
        (activity as? MainActivity)?.let {
            MainNavigator.goToListDetail(
                    it,
                    retrieveParentArguments()
            )
        }
    }

    private fun onUserRefuseToGrantPermission() {
        Snackbar.make(binding.itemDetailLayout, R.string.user_cant_use_feature_unless_accept_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { onItemImageChange() })
                .show()
    }

    private fun onItemImageChange() {
        if (hasWritePermission()) {
            Matisse.from(this)
                    .choose(MimeType.ofImage())
                    .maxSelectable(1)
                    .thumbnailScale(0.85f)
                    .showSingleMediaType(true)
                    .theme(R.style.Matisse_Zhihu)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)
                    .imageEngine(DBShoppingEngine())
                    .capture(true)
                    .captureStrategy(CaptureStrategy(true, ConstantHolder.FILE_PROVIDER))
                    .forResult(ConstantHolder.REQUEST_IMAGE_CAPTURE)

        } else requestWriteAndReadPermission()
    }

    private fun setItem(item: ItemModel) {
        binding.item = item
        // Hide the delete icon if the current user
        // is not the creator of the item
        itemDetailMenu?.findItem(R.id.action_delete_item)?.isVisible = binding.item?.itemOwner == currentUser?.email

        binding.executePendingBindings()
    }

}