package com.dbeginc.dbshopping.itemdetail.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.databinding.ItemDetailLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.DBShoppingEngine
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.extensions.setImageUrl
import com.dbeginc.dbshopping.helper.extensions.snack
import com.dbeginc.dbshopping.itemdetail.ItemDetailContract
import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import javax.inject.Inject

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 24.08.17.
 */
class ItemDetailActivity : BaseActivity(), ItemDetailContract.ItemDetailView {

    @Inject lateinit var presenter: ItemDetailContract.ItemDetailPresenter
    private lateinit var binding: ItemDetailLayoutBinding
    private lateinit var loadingDialog: LoadingDialog

    /********************************************************** Android Part Method **********************************************************/
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.item_detail_layout)

        binding.item = if (savedState == null) intent.getParcelableExtra(ConstantHolder.ITEM_DATA_KEY)
        else savedState.getParcelable(ConstantHolder.ITEM_DATA_KEY)

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
    }

    override fun cleanState() = presenter.unBind()

    override fun getItemId(): String = binding.item.id

    override fun getItem(): ItemModel = binding.item

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

    override fun displayUpdateStatus() = loadingDialog.show(supportFragmentManager, LoadingDialog::class.java.simpleName)

    override fun hideUpdateStatus() = loadingDialog.dismiss()

    override fun displayImageUploadDoneMessage() = binding.itemDetailLayout.snack("Image Upload Completed")

    override fun goToList() = finish()

    override fun displayErrorMessage(error: String) = binding.itemDetailLayout.snack(error)
}