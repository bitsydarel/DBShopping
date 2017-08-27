package com.dbeginc.dbshopping.addlist

import com.dbeginc.dbshopping.base.IPresenter
import com.dbeginc.dbshopping.base.IView
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
 * Created by darel on 23.08.17.
 */
interface AddListContract {

    interface AddListView : IView {

        fun getListName(): String

        fun getList(): ListModel

        fun displayNameIncorrectMessage()

        fun hideNameIncorrectMessage()

        fun displayLoadingProgress()

        fun hideLoadingProgress()

        fun displayErrorMessage(error: String)

        fun goToHomePage()

    }

    interface AddListPresenter : IPresenter<AddListView> {

        fun createList()

    }
}