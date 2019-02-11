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

package com.dbeginc.lists.addlist

import com.dbeginc.common.presenter.MVPVPresenter

/**
 * Created by darel on 27.02.18.
 *
 * Add list presenter
 */
class AddListPresenter : MVPVPresenter<AddListView> {
    override fun bind(view: AddListView) = view.setupView()

    fun validateListName(name: String, view: AddListView) {
        if (name.isNotBlank()) view.onListNameValid(name)
        else view.onListNameInvalid()
    }
}