/*
 *
 *  * Copyright (C) 2019 Darel Bitsy
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

package com.dbeginc.domain.repositories

import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import io.reactivex.Completable
import io.reactivex.Flowable

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
 * Created by darel on 20.08.17.
 *
 * Data Repository
 */
interface IDataRepo : Cleanable{

    /**
     * Get shopping list from the database
     * @param requestModel of the shopping list
     * @return {@Link [Flowable] } of ShoppingList object
     */
    fun getList(requestModel: ListRequestModel<Unit>) : Flowable<ShoppingList>

    /**
     * Get all shopping list
     * @return {@Link [Flowable] } of an list of shoppingList
     */
    fun getAllList(): Flowable<List<ShoppingList>>

    /**
     * Get list items
     * @param requestModel to request the shopping items
     * @return {@Link [Flowable] } of all the items of a list
     */
    fun getItems(requestModel: ItemRequestModel<Unit>) : Flowable<List<ShoppingItem>>

    /**
     * Get Shopping Item
     * @param requestModel to request a specific item
     * @return {@Link [Flowable] } of shopping item
     */
    fun getItem(requestModel: ItemRequestModel<String>) : Flowable<ShoppingItem>

    /**
     * Add Shopping list
     * @param requestModel [ListRequestModel] to be added
     * @return [Completable] that notify if the task is done
     */
    fun addList(requestModel: ListRequestModel<ShoppingList>) : Completable

    /**
     * Add Shopping item
     * @param requestModel [ItemRequestModel]
     * @return [Completable] that notify if the task is done
     */
    fun addItem(requestModel: ItemRequestModel<ShoppingItem>) : Completable


    /**
     * @param requestModel [ListRequestModel]
     * @return [Completable] that notify if the list name has been modified
     */
    fun changeListName(requestModel: ListRequestModel<String>): Completable

    /**
     * @param requestModel for list interaction
     * @return [Completable] that notify if the list name has been modified
     */
    fun updateList(requestModel: ListRequestModel<ShoppingList>) : Completable

    /**
     * @param requestModel for item interaction
     * @return [Completable] that notify if the task completed
     */
    fun updateItem(requestModel: ItemRequestModel<ShoppingItem>) : Completable

    /**
     * Upload shopping items images
     * @param requestModel [ItemRequestModel]
     * @return [Completable] that notify if the upload is done
     */
    fun uploadItemImage(requestModel: ItemRequestModel<ShoppingItem>) : Completable

    fun addUserShopping(requestModel: ListRequestModel<String>) : Completable

    fun removeUserShopping(requestModel: ListRequestModel<String>) : Completable

    /**
     * Delete a list from database and all his items
     * @param requestModel [ListRequestModel]
     * @return [Completable] that notify about task completion
     */
    fun deleteList(requestModel: ListRequestModel<Unit>) : Completable

    /**
     * Delete a list from database and all his items
     * @param requestModel [ItemRequestModel]
     * @return [Completable] that notify about task completion
     */
    fun deleteItem(requestModel: ItemRequestModel<String>) : Completable

    fun deleteAll(requestModel: UserRequestModel<Unit>) : Completable
}
