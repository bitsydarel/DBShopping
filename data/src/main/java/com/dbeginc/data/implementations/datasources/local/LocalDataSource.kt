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

package com.dbeginc.data.implementations.datasources.local

import android.support.annotation.RestrictTo
import com.dbeginc.data.proxies.local.list.LocalPendingDataRequest
import com.dbeginc.data.proxies.local.list.LocalPendingListMemberRequest
import com.dbeginc.domain.entities.data.ItemComment
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.data.ShoppingUser
import com.dbeginc.domain.entities.request.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**

 * Created by darel on 20.08.17.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface LocalDataSource {
    /************************************* LIST ACTIONS ************************************/

    /**
     * Get shopping list from the database
     * @param requestModel [ListRequestModel] containing required information
     * to get requested list
     * @return {@Link [Flowable] } of ShoppingList object
     */
    fun getList(requestModel: ListRequestModel<Unit>) : Flowable<ShoppingList>

    fun getListMember(requestModel: ListRequestModel<String>): Flowable<ShoppingUser>

    fun getListMembers(requestModel: ListRequestModel<Unit>) : Flowable<List<ShoppingUser>>

    fun addListMembers(requestModel: ListRequestModel<List<ShoppingUser>>) : Completable

    fun updateListMember(requestModel: ListRequestModel<ShoppingUser>) : Completable

    /**
     * Get all shopping list
     * @return [Flowable] of all available shopping lists
     */
    fun getAllListsFromUser(requestModel: UserRequestModel<Unit>): Flowable<List<ShoppingList>>

    /**
     * Add Shopping list to the local database
     * @param requestModel [ListRequestModel] containing required information
     * to add a list to the local database
     * @return [Completable] that notify if the task is done
     */
    fun addList(requestModel: AddListRequest) : Completable

    /**
     * Change shopping list requesterNickname
     * @param requestModel [ListRequestModel] containing required information
     * to change list requesterNickname
     * @return [Completable] that notify if the list requesterNickname has been modified
     */
    fun changeListName(requestModel: ListRequestModel<String>): Completable

    /**
     * Update list information
     * @param requestModel [ListRequestModel] containing required information
     * to update list
     * @return [Completable] that notify if the list nickname has been modified
     */
    fun updateList(requestModel: ListRequestModel<ShoppingList>) : Completable

    /**
     * Add all Shopping list to the local database
     * @param lists of shopping lists to add to the local database
     * @return [Completable] that notify if the task is done
     */
    fun addAllList(lists: List<ShoppingList>) : Completable

    /**
     * Delete a list from database and all his items
     * @param requestModel [ListRequestModel] containing required information
     * to delete a list
     * @return [Completable] that notify about task completion
     */
    fun deleteList(requestModel: ListRequestModel<Unit>) : Completable

    /***************************************** LIST ACTIONS ****************************************/

    /**
     * Get list items
     * @param requestModel [ListRequestModel] containing required information
     * to get the list his items
     * @return {@Link [Flowable] } of all the items of a list
     */
    fun getItems(requestModel: ListRequestModel<Unit>) : Flowable<List<ShoppingItem>>

    /**
     * Get Shopping Item
     * @param requestModel [ItemRequestModel] containing required information
     * to get the requested item
     * @return {@Link [Flowable] } of shopping item
     */
    fun getItem(requestModel: ItemRequestModel<String>) : Flowable<ShoppingItem>

    fun updateOrAddItem(item: ShoppingItem): Completable

    /**
     * Add Shopping item to the local database
     * @param requestModel [ItemRequestModel] containing required information
     * to add a item to the local database
     * @return [Completable] that notify if the task is done
     */
    fun addItem(requestModel: ItemRequestModel<ShoppingItem>) : Completable

    /**
     * Update item information
     * @param requestModel [ItemRequestModel] containing required information
     * to update an item
     * @return [Completable] that notify if the task completed
     */
    fun updateItem(requestModel: ItemRequestModel<ShoppingItem>) : Completable

    /**
     * Delete a list from database and all his items
     * @param requestModel [ItemRequestModel] containing required information
     * to delete an item
     * @return [Completable] that notify about task completion
     */
    fun deleteItem(requestModel: ItemRequestModel<String>) : Completable

    fun getAllItemComments(requestModel: ItemRequestModel<String>) : Flowable<List<ItemComment>>

    fun addItemComment(comment: ItemComment): Completable

    /**
     * Delete all the data in the repository
     * @return [Completable] that notify about task completion
     */
    fun deleteAll() : Completable

    fun addPendingListMemberRequest(request: LocalPendingListMemberRequest) : Completable

    fun deletePendingListMemberRequest(request: LocalPendingListMemberRequest) : Completable

    fun getAllListMemberRequest(): Maybe<List<LocalPendingListMemberRequest>>

    fun addPendingDataRequest(request: LocalPendingDataRequest) : Completable

    fun getAllPendingDataRequest() : Maybe<List<LocalPendingDataRequest>>

    fun deletePendingDataRequest(request: LocalPendingDataRequest) : Completable
}