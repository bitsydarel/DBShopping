package com.dbeginc.dbshopping.userlist.adapter.presenter

import com.dbeginc.dbshopping.userlist.adapter.ListContract
import com.dbeginc.dbshopping.viewmodels.ListModel

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
 * Created by darel on 22.08.17.
 *
 * List Presenter Implementation
 */
class ListPresenterImpl(val list: ListModel) : ListContract.ListPresenter {

    private lateinit var view: ListContract.ListView

    override fun bind(view: ListContract.ListView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() { /* Empty because not needed for this presenter */ }

    override fun getData(): ListModel = list

    override fun loadList() = view.displayList(list)

    override fun onListClick() = view.displayListDetail(list)

}