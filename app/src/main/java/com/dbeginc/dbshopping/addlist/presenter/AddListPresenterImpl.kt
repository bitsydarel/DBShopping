package com.dbeginc.dbshopping.addlist.presenter

import android.util.Log
import com.dbeginc.dbshopping.addlist.AddListContract
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.mapper.data.toList
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.list.AddList
import io.reactivex.observers.DisposableCompletableObserver

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
 * Created by darel on 23.08.17.
 */
class AddListPresenterImpl(dataRepo: IDataRepo) : AddListContract.AddListPresenter {
    private lateinit var view: AddListContract.AddListView
    private val createList = AddList(dataRepo)

    override fun bind(view: AddListContract.AddListView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() = createList.dispose()

    override fun createList() {
        if (view.getListName().isEmpty()) {
            view.displayNameIncorrectMessage()
            return
        }

        val model = view.getList()
        createList.execute(TaskObserver(), ListRequestModel(model.id, model.toList()))
    }

    private inner class TaskObserver : DisposableCompletableObserver() {

        override fun onComplete() = view.goToHomePage()

        override fun onError(error: Throwable) {
            view.displayErrorMessage(error.localizedMessage)
            Log.e(ConstantHolder.TAG, error.localizedMessage, error)
        }
    }
}