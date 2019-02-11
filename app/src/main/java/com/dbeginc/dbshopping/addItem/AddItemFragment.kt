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

package com.dbeginc.dbshopping.addItem


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
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.dbeginc.common.utils.RequestState
import com.dbeginc.dbshopping.DBShopping
import com.dbeginc.dbshopping.DBShopping.Companion.memoryCache
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentAddItemBinding
import com.dbeginc.dbshopping.utils.extensions.getUser
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.setCircleItemImage
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LIST_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.REQUEST_IMAGE_CAPTURE
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.USER_ID
import com.dbeginc.dbshopping.utils.helper.DBShoppingEngine
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.dbshopping.utils.helper.WithParentRequiredArguments
import com.dbeginc.lists.additem.AddItemView
import com.dbeginc.lists.additem.AddItemViewModel
import com.dbeginc.lists.viewmodels.ItemModel
import com.dbeginc.users.viewmodels.UserProfileModel
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import java.util.*


/**
 * Add item fragment [AddItemFragment]
 *
 * Add item to an list
 */
class AddItemFragment : BaseFragment(), AddItemView, WithParentRequiredArguments{
    private lateinit var binding : FragmentAddItemBinding
    private lateinit var viewModel: AddItemViewModel
    private lateinit var listId: String
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }
    private val barcodeDetector: BarcodeDetector by lazy {
        BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()
    }

    companion object {
        @JvmStatic
        fun newInstance(listId: String) : AddItemFragment {
            val fragment = AddItemFragment()

            val args = Bundle().apply {
                putString(LIST_DATA_KEY, listId)
            }

            fragment.arguments = args

            return fragment
        }
    }

    override fun retrieveParentArguments(): Bundle {
        return Bundle().apply {
            putString(LIST_DATA_KEY, listId)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[AddItemViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent().observe(this, stateObserver)
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        listId = (if (state == null) arguments!!.getString(LIST_DATA_KEY) else state.getString(LIST_DATA_KEY))

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.generic_create_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_create -> {
                viewModel.presenter.verifyItem(binding.item!!, this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LIST_DATA_KEY, listId)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ConstantHolder.WRITE_AND_READ_EXTERNAL_STORAGE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            binding.addItemImage.callOnClick()

        } else onUserRefuseToGrantPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ConstantHolder.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            binding.item?.imageUrl = Matisse.obtainResult(data).first().toString()
            setCircleItemImage(binding.addItemImage, binding.item?.imageUrl)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_add_item,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.addItemToolbar)

        viewModel.presenter.bind(this)

    }

    override fun setupView() {
        binding.addItemToolbar.setNavigationOnClickListener {
            MainNavigator.goToListDetail(activity as MainActivity, retrieveParentArguments())
        }

        binding.addItemName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.addItemNameContainer.error = null
            }
        })

        binding.addItemPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.addItemPriceContainer.error = null
            }
        })

        binding.addItemCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.addItemCountContainer.error = null
            }
        })

        binding.addItemImage.setOnClickListener {
            onItemImageChange()
        }

        binding.addItemProgressBar.hideWithAnimation()

        val userId = preferences.getString(USER_ID, "")

         DBShopping.memoryCache.getUser(userId)?.let {
             binding.item = binding.item ?: ItemModel(uniqueId = UUID.randomUUID().toString(),
                     name = "",
                     itemOf = listId,
                     itemOwner = it.email,
                     count = 1,
                     price = 0.0,
                     bought = false,
                     boughtBy = "",
                     imageUrl = ""
             )
         }
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> onCreateItemRequestStarted()
            RequestState.COMPLETED -> onCreateItemRequestCompleted()
            RequestState.ERROR -> onCreateItemRequestFailed()
        }
    }

    override fun onNameIsIncorrect() {
        binding.addItemNameContainer.error = getString(R.string.invalid_name)
    }

    override fun onPriceIsLower() {
        binding.addItemPriceContainer.error = getString(R.string.invalid_price)
    }

    override fun onCountIsLowerOrEquals() {
        binding.addItemCountContainer.error = getString(R.string.invalid_count)
    }

    override fun onItemValid(item: ItemModel) = viewModel.addItem(listId, item)

    private fun onCreateItemRequestStarted() {
        binding.addItemProgressBar.showWithAnimation()
        binding.addItemCV.hideWithAnimation()
    }

    private fun onCreateItemRequestFailed() {
        binding.addItemProgressBar.hideWithAnimation()

        binding.addItemCV.showWithAnimation()

        Snackbar.make(binding.addItemLayout, R.string.error_while_creating_item, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { viewModel.presenter.verifyItem(binding.item!!, this) })
                .show()
    }

    private fun onCreateItemRequestCompleted() {
        binding.addItemProgressBar.hideWithAnimation()

        binding.addItemCV.showWithAnimation()

        MainNavigator.goToListDetail(activity as MainActivity, retrieveParentArguments())

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
                    .forResult(REQUEST_IMAGE_CAPTURE)

        } else requestWriteAndReadPermission()
    }

    private fun onUserRefuseToGrantPermission() {
        Snackbar.make(binding.addItemLayout, R.string.user_cant_use_feature_unless_accept_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { onItemImageChange() })
                .show()
    }

}
