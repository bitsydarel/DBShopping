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

package com.dbeginc.dbshopping.itemdetail.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.databinding.ItemDetailLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.DBShoppingEngine
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.extensions.hide
import com.dbeginc.dbshopping.helper.extensions.setImageUrl
import com.dbeginc.dbshopping.helper.extensions.show
import com.dbeginc.dbshopping.helper.extensions.snack
import com.dbeginc.dbshopping.itemdetail.ItemDetailContract
import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.dbshopping.viewmodels.UserModel
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import javax.inject.Inject

class ItemDetailActivity : BaseActivity(), ItemDetailContract.ItemDetailView {
    @Inject lateinit var presenter: ItemDetailContract.ItemDetailPresenter
    @Inject lateinit var user: UserModel
    private lateinit var binding: ItemDetailLayoutBinding
    private lateinit var loadingDialog: LoadingDialog
    private var isInShoppingMode: Boolean = false

    /********************************************************** Android Part Method **********************************************************/
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.item_detail_layout)

        if (savedState == null) {
            binding.item = intent.getParcelableExtra(ConstantHolder.ITEM_DATA_KEY)
            isInShoppingMode = intent.getBooleanExtra(ConstantHolder.IS_IN_SHOPPING_MODE, false)

        } else {
            binding.item = savedState.getParcelable(ConstantHolder.ITEM_DATA_KEY)
            isInShoppingMode = savedState.getBoolean(ConstantHolder.IS_IN_SHOPPING_MODE)
        }

        loadingDialog = LoadingDialog()
        loadingDialog.setMessage(getString(R.string.updatingDataMessageItem))
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(ConstantHolder.ITEM_DATA_KEY, binding.item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ConstantHolder.WRITE_AND_READ_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            presenter.changeItemImage()

        } else displayErrorMessage("Please Accept permission to add picture to item")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantHolder.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            presenter.onImageSelected(Matisse.obtainResult(data).first().toString())
            presenter.saveItemImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item_details, menu)
        // Hide the delete icon if the current user
        // is not the creator of the item
        menu?.findItem(R.id.action_delete_item)?.isVisible = binding.item.itemOwner == user.email
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_open_item_comment -> return true
            R.id.action_delete_item -> {
                AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_warning)
                        .setTitle(R.string.action_remove_item)
                        .setMessage(R.string.action_remove_message_item)
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(android.R.string.yes) { _, _ -> presenter.deleteItem() }
                        .show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /********************************************************** Item Detail View Part Method **********************************************************/
    override fun setupView() {
        val toolbar = binding.itemDetailToolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_done, theme)
        toolbar.setNavigationOnClickListener { presenter.updateItem() }

        binding.itemImageDetail.setOnClickListener { presenter.changeItemImage() }
        binding.addItemCountDetail.setOnClickListener { presenter.addQuantity() }
        binding.removeItemCountDetail.setOnClickListener { presenter.removeQuantity() }

        if (isInShoppingMode) {
            binding.itemBrought.show()
            if (binding.item.bought) {
                binding.itemBrought.setImageResource(R.drawable.ic_item_was_not_bought_black)
                binding.itemBrought.backgroundTintList = ResourcesCompat.getColorStateList(resources, android.R.color.holo_red_light, theme)
            }

        } else binding.itemBrought.hide()

        binding.itemBrought.setOnClickListener { _ -> presenter.onItemBought(!binding.item.bought) }

        presenter.setupRestrictions()
    }

    override fun cleanState() = presenter.unBind()

    override fun getItemId(): String = binding.item.id

    override fun getItem(): ItemModel = binding.item

    override fun getCurrentUser(): UserModel = user

    override fun itemNotBought() {
        binding.item.bought = false
        binding.item.boughtBy = ""

        binding.itemBrought.setImageResource(R.drawable.ic_item_was_bought_black)

        binding.itemBrought.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.ItemDetailColorSecondary, theme)
    }

    override fun itemBought() {
        binding.item.bought = true
        binding.item.boughtBy = user.name

        binding.itemBrought.setImageResource(R.drawable.ic_item_was_not_bought_black)

        binding.itemBrought.backgroundTintList = ResourcesCompat.getColorStateList(resources, android.R.color.holo_red_light, theme)
    }

    override fun requestImage() {
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

        } else { requestWriteAndReadPermission() }
    }

    override fun displayItem(itemModel: ItemModel) {
        binding.item = itemModel
    }

    override fun displayImage(imageUrl: String) {
        binding.item.image = imageUrl
        setImageUrl(binding.itemImageDetail, binding.item.image)
    }

    override fun addQuantity(value: Int) {
        binding.item.count += value
        binding.itemCountDetail.text = binding.item.count.toString()
    }

    override fun removeQuantity(value: Int) {
        binding.item.count -= value
        binding.itemCountDetail.text = binding.item.count.toString()
    }

    override fun displayLoadingStatus() = loadingDialog.show(supportFragmentManager, LoadingDialog::class.java.simpleName)

    override fun hideLoadingStatus() = loadingDialog.dismiss()

    override fun displayUpdateStatus() = binding.itemDetailUpdateStatusPB.show()

    override fun hideUpdateStatus() = binding.itemDetailUpdateStatusPB.hide()

    override fun restrictUserToEditItemName() {
        binding.itemNameDetailContainer.apply {
            isFocusable = false
            isEnabled = false
        }
    }

    override fun allowUserToEditItemName() {
        binding.itemNameDetailContainer.apply {
            isFocusable = true
            isEnabled = true
        }
    }

    override fun displayImageUploadDoneMessage() = binding.itemDetailLayout.snack("Image Upload Completed")

    override fun goToList() = finish()

    override fun displayErrorMessage(error: String) = binding.itemDetailLayout.snack(error)
}