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
import com.dbeginc.dbshopping.itemdetail.ItemDetailContract
import com.dbeginc.dbshopping.mapper.data.toItem
import com.dbeginc.dbshopping.mapper.data.toItemModel
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.item.DeleteItem
import com.dbeginc.domain.usecases.data.item.GetItem
import com.dbeginc.domain.usecases.data.item.UpdateImage
import com.dbeginc.domain.usecases.data.item.UpdateItem
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber

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
class ItemDetailPresenterImpl(dataRepo: IDataRepo, private val errorManager: IErrorManager) : ItemDetailContract.ItemDetailPresenter{
    private lateinit var view: ItemDetailContract.ItemDetailView
    private val getItem = GetItem(dataRepo)
    private val updateItem = UpdateItem(dataRepo)
    private val uploadImage = UpdateImage(dataRepo)
    private val deleteItem = DeleteItem(dataRepo)

    override fun bind(view: ItemDetailContract.ItemDetailView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        updateItem.dispose()
        uploadImage.dispose()
        deleteItem.dispose()
    }

    override fun changeItemImage() = view.requestImage()

    override fun onImageSelected(imageUrl: String) {
        view.displayImage(imageUrl)
    }

    override fun addQuantity() = view.addQuantity(1)

    override fun removeQuantity() {
        if (view.getItem().count > 0) view.removeQuantity(1)
    }

    override fun onItemBought(boolean: Boolean) = if (boolean) view.itemBought() else view.itemNotBought()

    private fun getUpdatedItem() {
        getItem.execute(ItemObserver(), ItemRequestModel(view.getItem().itemOf, view.getItemId()))
    }

    override fun saveItemImage() {
        view.displayUpdateStatus()
        uploadImage.execute(UploadTaskObserver(), ItemRequestModel(view.getItem().itemOf, view.getItem().toItem()))
    }

    override fun updateItem() {
        view.displayUpdateStatus()
        updateItem.execute(UpdateObserver(), ItemRequestModel(view.getItem().itemOf, view.getItem().toItem()))
    }

    override fun deleteItem() {
        view.displayUpdateStatus()
        deleteItem.execute(DeleteObserver(), ItemRequestModel(view.getItem().itemOf, view.getItem().id))
    }

    private inner class ItemObserver : DisposableSubscriber<ShoppingItem>() {
        override fun onNext(item: ShoppingItem) {
            view.displayItem(item.toItemModel())
            view.hideUpdateStatus()
            view.displayImageUploadDoneMessage()
            dispose()
        }

        override fun onComplete() = dispose()

        override fun onError(error: Throwable) {
            view.hideUpdateStatus()
            view.displayErrorMessage(error.localizedMessage)
        }
    }

    private inner class UploadTaskObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            getUpdatedItem()
            dispose()
        }

        override fun onError(error: Throwable) {
            view.hideUpdateStatus()
            view.displayErrorMessage(errorManager.translateError(error))
        }
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.goToList()
            dispose()
        }

        override fun onError(e: Throwable) {
            view.hideUpdateStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class DeleteObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.goToList()
            dispose()
        }

        override fun onError(e: Throwable) {
            view.hideUpdateStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }
}