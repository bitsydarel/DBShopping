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

package com.dbeginc.data.implementations.datasources.local.list.room

import android.arch.persistence.room.*
import android.support.annotation.RestrictTo
import com.dbeginc.data.ConstantHolder.LIST_MEMBER_PENDING_TABLE
import com.dbeginc.data.ConstantHolder.LIST_PENDING_TABLE
import com.dbeginc.data.ConstantHolder.LOCAL_ITEMS_COMMENTS
import com.dbeginc.data.ConstantHolder.LOCAL_ITEMS_TABLE
import com.dbeginc.data.ConstantHolder.LOCAL_LISTS_MEMBERS
import com.dbeginc.data.ConstantHolder.LOCAL_LISTS_TABLE
import com.dbeginc.data.proxies.local.list.*
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by darel on 21.02.18.
 *
 * Local List Dao
 *
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Dao
abstract class LocalListDao {
    /************************* LIST ACTIONS ***************************/
    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun addShoppingList(shoppingList: LocalShoppingList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addShoppingLists(shoppingLists: List<LocalShoppingList>)

    @Query("SELECT * FROM $LOCAL_LISTS_TABLE WHERE unique_id LIKE :listId")
    abstract fun getShoppingList(listId: String) : Flowable<LocalShoppingList>

    @Query("SELECT * FROM $LOCAL_LISTS_TABLE")
    abstract fun getShoppingListsOfUser() : Flowable<List<LocalShoppingList>>

    @Update(onConflict = OnConflictStrategy.FAIL)
    abstract fun updateShoppingList(shoppingList: LocalShoppingList)

    @Query("UPDATE $LOCAL_LISTS_TABLE SET name = :newName WHERE unique_id LIKE :listId")
    abstract fun changeShoppingListName(listId: String, newName: String)

    @Query("DELETE FROM $LOCAL_LISTS_TABLE WHERE unique_id LIKE :listId")
    abstract fun deleteShoppingList(listId: String)

    @Query("DELETE FROM $LIST_PENDING_TABLE WHERE item_unique_id LIKE :listId")
    abstract fun deletePendingListRequest(listId: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addListMember(member: LocalShoppingUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addListMembers(members: List<LocalShoppingUser>)

    @Query("SELECT * FROM $LOCAL_LISTS_MEMBERS WHERE member_of LIKE :listId AND unique_id LIKE :userId")
    abstract fun getListMember(listId: String, userId: String): Flowable<LocalShoppingUser>

    @Query("SELECT * FROM $LOCAL_LISTS_MEMBERS WHERE member_of LIKE :listId")
    abstract fun getListMembers(listId: String): Flowable<List<LocalShoppingUser>>

    @Query("DELETE FROM $LOCAL_LISTS_MEMBERS WHERE member_of LIKE :listId")
    abstract fun deleteListMembers(listId: String)

    @Query("DELETE FROM $LIST_MEMBER_PENDING_TABLE")
    abstract fun deletePendingListMembersTable()

    @Transaction
    open fun deleteListWithChild(listId: String) {
        deleteShoppingList(listId)
        deleteListMembers(listId)
        deletePendingListRequest(listId)
        deletePendingListMemberRequestByListId(listId)

        getItemsFromList(listId).forEach {
            deleteShoppingItem(listId, itemId = it.uniqueId)
            deleteItemComments(itemId = it.uniqueId)
            deletePendingItemRequest(listId = listId, itemId = it.uniqueId)
        }
    }

    /************************* ITEM ACTIONS ***************************/
    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun addShoppingItem(shoppingItem: LocalShoppingItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateOrAddShoppingItem(shoppingItem: LocalShoppingItem)

    @Query("SELECT * FROM $LOCAL_ITEMS_TABLE WHERE unique_id LIKE :itemId AND item_of LIKE :listId")
    abstract fun getShoppingItem(listId: String, itemId: String) : Flowable<LocalShoppingItem>

    @Query("SELECT * FROM $LOCAL_ITEMS_TABLE WHERE item_of LIKE :listId")
    abstract fun getShoppingItemsFromList(listId: String) : Flowable<List<LocalShoppingItem>>

    @Query("SELECT * FROM $LOCAL_ITEMS_TABLE WHERE item_of LIKE :listId")
    abstract fun getItemsFromList(listId: String) : List<LocalShoppingItem>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateShoppingItem(shoppingItem: LocalShoppingItem)

    @Query("DELETE FROM $LOCAL_ITEMS_TABLE WHERE item_of=:listId AND unique_id=:itemId")
    abstract fun deleteShoppingItem(listId: String, itemId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addItemComment(comment: LocalItemComment)

    @Query("SELECT * FROM $LOCAL_ITEMS_COMMENTS WHERE item_id LIKE :itemId")
    abstract fun getAllItemComments(itemId: String) : Flowable<List<LocalItemComment>>

    @Query("SELECT * FROM $LOCAL_ITEMS_COMMENTS WHERE item_id LIKE :itemId AND publish_time > :timestamp ORDER BY publish_time ASC LIMIT :limit")
    abstract fun getMoreItemComments(itemId: String, timestamp: Long, limit: Int) : Flowable<List<LocalItemComment>>

    @Query("SELECT * FROM $LOCAL_ITEMS_COMMENTS WHERE item_id LIKE :itemId AND publish_time < :timestamp ORDER BY publish_time ASC LIMIT :limit")
    abstract fun getLessItemComments(itemId: String, timestamp: Long, limit: Int) : Flowable<List<LocalItemComment>>

    @Query("DELETE FROM $LOCAL_ITEMS_COMMENTS WHERE item_id LIKE :itemId")
    abstract fun deleteItemComments(itemId: String)

    @Query("DELETE FROM $LIST_PENDING_TABLE WHERE parent_id LIKE :listId AND item_unique_id LIKE :itemId")
    abstract fun deletePendingItemRequest(listId: String, itemId: String)

    @Transaction
    open fun deleteItem(listId: String, itemId: String) {
        deleteShoppingItem(listId, itemId)
        deleteItemComments(itemId)
        deletePendingItemRequest(listId, itemId)
    }

    /********************** PENDING REQUEST ACTION *****************************/

    @Transaction
    open fun deleteAll() {
        deleteListsTable()
        deleteItemsTable()
        deleteItemCommentsTable()
        deletePendingDataTable()
        deleteListMembersTable()
        deletePendingListMembersTable()
    }

    @Query("DELETE FROM $LOCAL_LISTS_TABLE")
    abstract fun deleteListsTable()

    @Query("DELETE FROM $LOCAL_ITEMS_TABLE")
    abstract fun deleteItemsTable()

    @Query("DELETE FROM $LIST_PENDING_TABLE")
    abstract fun deletePendingDataTable()

    @Query("DELETE FROM $LOCAL_LISTS_MEMBERS")
    abstract fun deleteListMembersTable()

    @Query("DELETE FROM $LOCAL_ITEMS_COMMENTS")
    abstract fun deleteItemCommentsTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addPendingListMemberRequest(request: LocalPendingListMemberRequest)

    @Query("SELECT * FROM $LIST_MEMBER_PENDING_TABLE")
    abstract fun getAllPendingListMemberRequest() : Maybe<List<LocalPendingListMemberRequest>>

    @Query("DELETE FROM $LIST_MEMBER_PENDING_TABLE WHERE list_id LIKE :listId")
    abstract fun deletePendingListMemberRequestByListId(listId: String)

    @Delete
    abstract fun deletePendingListMemberRequest(request: LocalPendingListMemberRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addPendingDataRequest(request: LocalPendingDataRequest)

    @Query("SELECT * FROM $LIST_PENDING_TABLE")
    abstract fun getAllPendingDataRequest() : Maybe<List<LocalPendingDataRequest>>

    @Delete
    abstract fun deletePendingDataRequest(request: LocalPendingDataRequest)
}