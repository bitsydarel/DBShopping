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

package com.dbeginc.dbshopping.itemdetail.presenter

import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.helper.extensions.addTo
import com.dbeginc.dbshopping.itemdetail.ItemDetailContract
import com.dbeginc.dbshopping.mapper.data.toItem
import com.dbeginc.dbshopping.mapper.data.toItemModel
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.item.DeleteItem
import com.dbeginc.domain.usecases.data.item.GetItem
import com.dbeginc.domain.usecases.data.item.UpdateImage
import com.dbeginc.domain.usecases.data.item.UpdateItem
import com.dbeginc.domain.usecases.data.list.GetList
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 24.08.17.
 *
 * Item Detail Presenter
 */
class ItemDetailPresenterImpl(dataRepo: IDataRepo, private val errorManager: IErrorManager) : ItemDetailContract.ItemDetailPresenter {
    private lateinit var view: ItemDetailContract.ItemDetailView
    private val getItem = GetItem(dataRepo)
    private val getRestrictionForItem = GetList(dataRepo)
    private val updateItem = UpdateItem(dataRepo)
    private val uploadImage = UpdateImage(dataRepo)
    private val deleteItem = DeleteItem(dataRepo)
    private val subscriptions = CompositeDisposable()

    override fun bind(view: ItemDetailContract.ItemDetailView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        getItem.clean()
        getRestrictionForItem.clean()
        updateItem.clean()
        uploadImage.clean()
        deleteItem.clean()
        subscriptions.clear()
    }

    override fun changeItemImage() = view.requestImage()

    override fun onImageSelected(imageUrl: String) = view.displayImage(imageUrl)

    override fun addQuantity() = view.addQuantity(1)

    override fun removeQuantity() {
        if (view.getItem().count > 0) view.removeQuantity(1)
    }

    override fun onItemBought(boolean: Boolean) {
        if (boolean) view.itemBought() else {
            if (view.getItem().boughtBy == view.getCurrentUser().name) {
                view.itemNotBought()
            }
        }
    }

    override fun setupRestrictions() {
        getRestrictionForItem.execute(ListRequestModel(view.getItem().itemOf, Unit))
                .doOnSubscribe { view.displayUpdateStatus() }
                .doOnTerminate { view.hideUpdateStatus() }
                .subscribe(
                        { list ->
                            if (!list.usersShopping.contains(view.getCurrentUser().id) || view.getItem().bought) view.restrictUserToEditItemName()
                            else view.allowUserToEditItemName()
                        },
                        { error -> view.displayErrorMessage(error.localizedMessage) }
                ).addTo(subscriptions)
    }

    override fun saveItemImage() {
        uploadImage.execute(ItemRequestModel(view.getItem().itemOf, view.getItem().toItem()))
                .doOnSubscribe { view.displayLoadingStatus() }
                .doOnError { view.hideLoadingStatus() }
                .subscribe(
                        { getUpdatedItem() },
                        { error -> view.displayErrorMessage(errorManager.translateError(error)) }
                ).addTo(subscriptions)
    }

    override fun updateItem() {
        updateItem.execute(ItemRequestModel(view.getItem().itemOf, view.getItem().toItem()))
                .doOnSubscribe { view.displayLoadingStatus() }
                .doOnTerminate { view.hideLoadingStatus() }
                .subscribe({ view.goToList() }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    override fun deleteItem() {
        deleteItem.execute(ItemRequestModel(view.getItem().itemOf, view.getItem().id))
                .doOnSubscribe { view.displayLoadingStatus() }
                .doOnTerminate { view.hideLoadingStatus() }
                .subscribe({ view.goToList() }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    private fun getUpdatedItem() {
        getItem.execute(ItemRequestModel(view.getItem().itemOf, view.getItemId()))
                .doOnTerminate { view.hideLoadingStatus() }
                .subscribe(
                        { item -> view.displayItem(item.toItemModel()); view.displayImageUploadDoneMessage() },
                        { error -> view.displayErrorMessage(error.localizedMessage) }

                ).addTo(subscriptions)
    }
}