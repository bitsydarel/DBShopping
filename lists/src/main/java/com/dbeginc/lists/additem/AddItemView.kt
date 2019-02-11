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

package com.dbeginc.lists.additem

import com.dbeginc.common.view.MVPVView
import com.dbeginc.lists.viewmodels.ItemModel

/**
 * Created by darel on 01.03.18.
 *
 * Add item view
 */
interface AddItemView : MVPVView {
    fun onNameIsIncorrect()

    fun onPriceIsLower()

    fun onCountIsLowerOrEquals()

    fun onItemValid(item: ItemModel)
}