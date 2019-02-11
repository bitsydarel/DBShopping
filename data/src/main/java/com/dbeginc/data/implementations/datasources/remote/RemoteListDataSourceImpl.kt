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

package com.dbeginc.data.implementations.datasources.remote

import android.net.Uri
import android.support.annotation.RestrictTo
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.proxies.remote.list.RemoteItemComment
import com.dbeginc.data.proxies.remote.list.RemoteShoppingItem
import com.dbeginc.data.proxies.remote.list.RemoteShoppingList
import com.dbeginc.data.proxies.remote.mapper.toDomain
import com.dbeginc.data.proxies.remote.mapper.toItemComment
import com.dbeginc.data.proxies.remote.mapper.toRemoteProxy
import com.dbeginc.data.proxies.remote.mapper.toShoppingUser
import com.dbeginc.domain.entities.data.*
import com.dbeginc.domain.entities.request.*
import com.google.firebase.database.*
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@RestrictTo(RestrictTo.Scope.LIBRARY)
class RemoteListDataSourceImpl(private val listTable: DatabaseReference,
                               private val itemsTable: DatabaseReference,
                               private val itemsCommentsTable: DatabaseReference,
                               private val userTable: DatabaseReference,
                               private val remoteStorage: StorageReference) : RemoteListDataSource {

    /***************************************** LIST RELATED STUFF *****************************************/
    override fun addList(requestModel: AddListRequest): Completable {
        // We first need to create the list
        val addTheList = Completable.create { emitter -> listTable
                .child(requestModel.list.uniqueId)
                .child(ConstantHolder.REMOTE_LISTS_DATA)
                .setValue(requestModel.list.toRemoteProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        // Next add user to the list members
        val addUserToListMembers = Completable.create { emitter -> listTable
                .child(requestModel.list.uniqueId)
                .child(ConstantHolder.REMOTE_LISTS_MEMBERS)
                .child(requestModel.member.uniqueId)
                .setValue(requestModel.member.toRemoteProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        // Add the list id in user his lists
        val addListToUserHisList = Completable.create { emitter -> userTable
                .child(requestModel.member.uniqueId)
                .child(ConstantHolder.REMOTE_USERS_LISTS)
                .updateChildren(mapOf(requestModel.list.uniqueId to true))
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        return addTheList.andThen(addUserToListMembers).andThen(addListToUserHisList)
    }

    override fun changeListName(requestModel: ListRequestModel<String>): Completable {
        return Completable.create { emitter -> listTable
                .child(requestModel.listId)
                .child(ConstantHolder.REMOTE_LISTS_DATA)
                .updateChildren(mapOf<String, Any>(
                        RemoteShoppingList::name.name to requestModel.arg,
                        RemoteShoppingList::lastChange.name to ServerValue.TIMESTAMP
                ))
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun updateList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return Completable.create { emitter -> listTable
                .child(requestModel.listId)
                .child(ConstantHolder.REMOTE_LISTS_DATA)
                .setValue(requestModel.arg.toRemoteProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun getList(requestModel: ListRequestModel<Unit>): Single<ShoppingList> {
        return Single.create { emitter -> listTable
                .child(requestModel.listId)
                .child(ConstantHolder.REMOTE_LISTS_DATA)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) = emitter.onSuccess(data.toShoppingList())
                })

        }
    }

    override fun getListMembers(requestModel: ListRequestModel<Unit>): Single<List<ShoppingUser>> {
        return Single.create { emitter -> listTable
                .child(requestModel.listId)
                .child(ConstantHolder.REMOTE_LISTS_MEMBERS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) {
                        val members = mutableListOf<ShoppingUser>()
                        data.children.mapTo(members, { it.toShoppingUser() })
                        emitter.onSuccess(members)
                    }
                })
        }
    }

    override fun getAllListsFromUser(requestModel: UserRequestModel<Unit>): Single<List<ShoppingList>> {
        val getListIds = Single.create<List<String>> { emitter -> userTable
                .child(requestModel.userId)
                .child(ConstantHolder.REMOTE_USERS_LISTS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) {
                        val ids = mutableListOf<String>()
                        data.children.forEach { remoteIds -> ids.add(remoteIds.key) }
                        emitter.onSuccess(ids)
                    }
                })
        }

        return getListIds
                .flatMapPublisher { listIds -> Flowable.fromIterable(listIds) }
                .flatMapSingle { listId -> getList(ListRequestModel(listId, Unit)) }
                .collect(
                        { mutableListOf<ShoppingList>() },
                        { lists, list -> lists.add(list) }
                )
                .map { lists -> lists.toList() }
    }

    override fun updateListMember(requestModel: ListRequestModel<ShoppingUser>): Completable {
        return Completable.create { emitter -> listTable
                .child(requestModel.listId)
                .child(ConstantHolder.REMOTE_LISTS_MEMBERS)
                .updateChildren(mapOf(requestModel.arg.uniqueId to requestModel.arg.toRemoteProxy()))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }.andThen(updateListLastChange(requestModel.listId))
    }

    override fun deleteList(requestModel: ListRequestModel<Unit>): Completable {
        val getMembers = Single.create<List<ShoppingUser>> { emitter -> listTable
                .child(requestModel.listId)
                .child(ConstantHolder.REMOTE_LISTS_MEMBERS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) {
                        val users = mutableListOf<ShoppingUser>()
                        data.children.mapTo(users, { it.toShoppingUser() })
                        emitter.onSuccess(users)
                    }
                })
        }

        val removeListFromMember : (ShoppingUser) -> Completable = { user -> Completable.create { emitter -> userTable
                .child(user.uniqueId)
                .child(ConstantHolder.REMOTE_USERS_LISTS)
                .child(requestModel.listId)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        } }

        val removeListItems = Completable.create { emitter -> itemsTable
                .child(requestModel.listId)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val removeList = Completable.create { emitter -> listTable
                .child(requestModel.listId)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val removeListFiles = Completable.create { emitter -> remoteStorage
                .child(requestModel.listId)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else {
                        val error = task.exception!!

                        if (error is StorageException && error.httpResultCode == 404) emitter.onComplete()
                        else emitter.onError(error)

                    }
                }
        }

        return getMembers
                .flatMapPublisher { members -> Flowable.fromIterable(members) }
                .flatMapCompletable(removeListFromMember)
                .andThen(removeListItems)
                .andThen(removeList)
                .andThen(removeListFiles)
    }

    /******************************************** Item RELATED STUFF ***************************************************/
    override fun addItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        val addItemRequest = Completable.create { emitter -> itemsTable
                .child(requestModel.listId)
                .child(requestModel.arg.uniqueId)
                .setValue(requestModel.arg.toRemoteProxy())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        return addItemRequest.andThen(
                if (requestModel.arg.imageUrl.isBlank()) Completable.complete()
                else  updateItemImage(requestModel)
        ).andThen(updateListLastChange(requestModel.listId))
    }

    override fun updateItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        val updateTheItem = if (requestModel.arg.imageUrl.contains("content://")) updateItemImage(requestModel)
        else uploadShoppingItem(requestModel.listId, requestModel.arg.toRemoteProxy())

        return updateTheItem.andThen(updateListLastChange(requestModel.listId))
    }

    override fun updateItemImage(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return uploadImage(requestModel.listId, requestModel.arg.uniqueId, Uri.parse(requestModel.arg.imageUrl))
                .flatMapCompletable { newUrl ->
                    uploadShoppingItem(requestModel.listId, requestModel.arg.apply { imageUrl = newUrl }.toRemoteProxy())
                }
    }

    override fun getItems(requestModel: ListRequestModel<Unit>): Flowable<Pair<ShoppingItem, Boolean>> {
        return Flowable.create({ emitter -> itemsTable
                .child(requestModel.listId)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(data: DataSnapshot, p1: String?) =
                            emitter.onNext(data.toShoppingItem() to false)

                    override fun onChildRemoved(data: DataSnapshot) =
                            emitter.onNext(data.toShoppingItem() to true)

                    override fun onChildChanged(data: DataSnapshot, p1: String?) =
                            emitter.onNext(data.toShoppingItem() to false)

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

                    override fun onCancelled(p0: DatabaseError) = emitter.onError(p0.toException())

                })
        }, BackpressureStrategy.LATEST)
    }

    override fun getItem(requestModel: ItemRequestModel<String>): Single<ShoppingItem> {
        return Single.create<ShoppingItem> { emitter ->
            itemsTable.child(requestModel.listId)
                    .child(requestModel.arg)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                        override fun onDataChange(data: DataSnapshot) = emitter.onSuccess(data.toShoppingItem())
                    })
        }
    }

    override fun getAllItemComments(requestModel: ItemRequestModel<String>): Flowable<ItemComment> {
        return Flowable.create({ emitter -> itemsCommentsTable
                .child(requestModel.listId)
                .child(requestModel.arg)
                .orderByChild(RemoteItemComment::publishTime.name)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(data: DataSnapshot, p1: String?) =
                            emitter.onNext(data.toItemComment())

                    override fun onChildRemoved(p0: DataSnapshot?) {}

                    override fun onChildChanged(data: DataSnapshot, p1: String?) =
                            emitter.onNext(data.toItemComment())

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

                    override fun onCancelled(p0: DatabaseError) = emitter.onError(p0.toException())

                })
        }, BackpressureStrategy.BUFFER)
    }

    override fun postComment(requestModel: ItemRequestModel<ItemComment>) : Completable {
        val addComment : (RemoteItemComment) -> Completable = { comment -> Completable.create { emitter ->
            itemsCommentsTable.child(requestModel.listId)
                    .child(comment.itemId)
                    .child(comment.uniqueId)
                    .setValue(comment)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) emitter.onComplete()
                        else emitter.onError(task.exception!!)
                    }
        }.andThen(updateListLastChange(requestModel.listId)) }

        val uploadCommentExtrasAndAdd  = Single.create<RemoteItemComment> { emitter ->
            remoteStorage
                    .child(requestModel.listId)
                    .child(requestModel.arg.itemId)
                    .child(requestModel.arg.uniqueId)
                    .putFile(Uri.parse(requestModel.arg.commentArg))
                    .addOnSuccessListener { emitter
                            .onSuccess(requestModel.arg.toRemoteProxy().apply {
                                commentArg = it.downloadUrl?.toString()
                            })
                    }.addOnFailureListener { error -> emitter.onError(error) }
        }.flatMapCompletable { comment -> addComment(comment) }.andThen(updateListLastChange(requestModel.listId))

        return when(requestModel.arg.commentType) {
            CommentType.TEXT -> addComment(requestModel.arg.toRemoteProxy())
            CommentType.IMAGE -> uploadCommentExtrasAndAdd
            CommentType.VOICE -> uploadCommentExtrasAndAdd
            CommentType.LOCATION -> addComment(requestModel.arg.toRemoteProxy())
        }
    }

    override fun deleteItem(requestModel: ItemRequestModel<String>): Completable {
        val deleteItem = Completable.create { emitter -> itemsTable
                .child(requestModel.listId)
                .child(requestModel.arg)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val deleteFile = Completable.create { emitter -> remoteStorage
                .child(requestModel.listId)
                .child(requestModel.arg)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val deleteComments = Completable.create { emitter -> itemsCommentsTable
                .child(requestModel.listId)
                .child(requestModel.arg)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        return deleteItem.andThen(deleteComments).andThen(deleteFile).andThen(updateListLastChange(requestModel.listId))
    }

    private fun uploadImage(listId: String, itemId: String, imageUri: Uri) : Single<String> {
        return Single.create { emitter -> remoteStorage
                .child(listId)
                .child(itemId)
                .child(imageUri.lastPathSegment)
                .putFile(imageUri)
                .addOnSuccessListener { emitter.onSuccess(it.downloadUrl?.toString()!!) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

    private fun uploadShoppingItem(listId: String, item: RemoteShoppingItem) : Completable {
        return Completable.create { emitter -> itemsTable.child(listId)
                .child(item.uniqueId)
                .setValue(item)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    private fun updateListLastChange(listId: String) : Completable {
        return Completable.create { emitter -> listTable
                .child(listId)
                .child(ConstantHolder.REMOTE_LISTS_DATA)
                .updateChildren(mapOf<String, Any>(RemoteShoppingList::lastChange.name to ServerValue.TIMESTAMP))
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    /******************************** DELETE ALL USER STUFF ********************************/
    override fun deleteAllUserData(requestModel: UserRequestModel<Unit>): Completable {
        val deleteListInfo = Completable.create { emitter ->
            userTable.child(requestModel.userId)
                    .child(ConstantHolder.REMOTE_USERS_LISTS)
                    .removeValue()
                    .addOnCompleteListener {
                        task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                    }
        }

        return getAllListsFromUser(requestModel)
                .flatMapPublisher { lists -> Flowable.fromIterable(lists) }
                .filter { list -> list.ownerId == requestModel.userId }
                .flatMapCompletable { list -> deleteList(ListRequestModel(listId = list.uniqueId, arg = Unit)) }
                .andThen(deleteListInfo)
    }

    private fun DataSnapshot.toShoppingList() : ShoppingList {
        val proxy : RemoteShoppingList = getValue(RemoteShoppingList::class.java) as RemoteShoppingList
        return proxy.toDomain()
    }

    private fun DataSnapshot.toShoppingItem(): ShoppingItem {
        val proxy : RemoteShoppingItem = getValue(RemoteShoppingItem::class.java) as RemoteShoppingItem
        return proxy.toDomain()
    }
}
