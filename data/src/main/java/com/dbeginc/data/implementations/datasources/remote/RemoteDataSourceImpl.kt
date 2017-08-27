package com.dbeginc.data.implementations.datasources.remote

import android.net.Uri
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.datasource.DataSource
import com.dbeginc.data.proxies.remote.RemoteItemImage
import com.dbeginc.data.proxies.remote.RemoteShoppingItem
import com.dbeginc.data.proxies.remote.RemoteShoppingList
import com.dbeginc.domain.entities.data.ItemImage
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

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
 * Created by darel on 21.08.17.
 */
class RemoteDataSourceImpl(private val remoteListTable: DatabaseReference, private val remoteItemsTable: DatabaseReference,
                           private val remoteStorage: StorageReference) : DataSource {

    override fun getList(requestModel: ListRequestModel<Unit>): Flowable<ShoppingList> {
        return Flowable.create<ShoppingList>({ emitter -> remoteListTable.child(requestModel.listId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

                    override fun onDataChange(data: DataSnapshot) = emitter.onNext(data.toList())
                })

        }, BackpressureStrategy.LATEST)
    }

    override fun getAllList(): Flowable<List<ShoppingList>> {
        return Flowable.create({ emitter -> remoteListTable.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

            override fun onDataChange(allData: DataSnapshot) {
                val lists = mutableListOf<ShoppingList>()

                allData.children.forEach { data -> lists.add(data.toList()) }

                emitter.onNext(lists)
            }
        }) }, BackpressureStrategy.LATEST)
    }

    override fun getItems(requestModel: ItemRequestModel<Unit>): Flowable<List<ShoppingItem>> {
        return Flowable.create({ emitter -> remoteItemsTable.child(requestModel.listId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

            override fun onDataChange(allData: DataSnapshot) {
                val lists = mutableListOf<ShoppingItem>()

                allData.children.forEach { data -> lists.add(data.toItem()) }

                emitter.onNext(lists)
            }
        }) }, BackpressureStrategy.LATEST)
    }

    override fun getItem(requestModel: ItemRequestModel<String>): Flowable<ShoppingItem> {
        return Flowable.create<ShoppingItem>({
            emitter -> remoteItemsTable.child(requestModel.listId).child(requestModel.arg)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

                    override fun onDataChange(data: DataSnapshot) = emitter.onNext(data.toItem())

                })
        }, BackpressureStrategy.LATEST)
    }

    override fun addList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return Completable.create { emitter -> remoteListTable.child(requestModel.listId)
                .setValue(requestModel.arg.toProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun addItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return Completable.create { emitter -> remoteItemsTable.child(requestModel.listId)
                .child(requestModel.arg.uuid)
                .setValue(requestModel.arg.toProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun changeListName(requestModel: ListRequestModel<String>): Completable {
        return Completable.create { emitter -> val updates = mutableMapOf<String, Any>()
            updates.put(ConstantHolder.NAME, requestModel.arg)
            updates.put(ConstantHolder.LAST_CHANGE, ServerValue.TIMESTAMP)

            remoteListTable.child(requestModel.listId)
                    .updateChildren(updates)
                    .addOnCompleteListener {
                        task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                    }
        }
    }

    override fun updateList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return Completable.create { emitter -> remoteListTable.child(requestModel.listId)
                .setValue(requestModel.arg.toProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun updateItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return Completable.create { emitter -> remoteItemsTable.child(requestModel.listId)
                .child(requestModel.arg.uuid)
                .setValue(requestModel.arg.toProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun uploadItemImage(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        val image = Uri.parse(requestModel.arg.image.uri)

        return Single.create<ShoppingItem> { emitter -> remoteStorage.child(requestModel.listId)
                .child(requestModel.arg.uuid)
                .child(image.lastPathSegment)
                .putFile(image)
                .addOnSuccessListener {
                    requestModel.arg.image.uri = it.downloadUrl.toString()
                    emitter.onSuccess(requestModel.arg)
                }.addOnFailureListener { error -> emitter.onError(error) }

        }.flatMapCompletable { item -> val update = mutableMapOf<String, Any>()
            update.put(ConstantHolder.IMAGE, item.image.toProxy())

            Completable.create { emitter -> remoteItemsTable.child(requestModel.listId)
                    .child(item.uuid)
                    .updateChildren(update)
                    .addOnCompleteListener {
                        task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                    } }
        }
    }

    override fun deleteList(requestModel: ListRequestModel<Unit>): Completable {
        val removeList = Completable.create { emitter -> remoteListTable.child(requestModel.listId)
                .removeValue()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        val removeItem = Completable.create { emitter -> remoteItemsTable.child(requestModel.listId)
                .removeValue()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        return removeList.andThen(removeItem)
    }

    override fun deleteItem(requestModel: ItemRequestModel<String>): Completable {
        return Completable.create { emitter -> remoteItemsTable.child(requestModel.listId)
                .child(requestModel.arg)
                .removeValue()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun deleteAll(requestModel: UserRequestModel<Unit>): Completable {
        val deleteLists = Completable.create { emitter -> remoteListTable.removeValue()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        val deleteItems = Completable.create { emitter -> remoteItemsTable.removeValue()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        val removeFiles = Completable.create { emitter -> remoteStorage.delete()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        return deleteLists.andThen(deleteItems).andThen(removeFiles)
    }

    override fun clean() { /* Not necessary here  */ }

    private fun ShoppingList.toProxy(): RemoteShoppingList =
            RemoteShoppingList(uuid = uuid, name = name, ownerName = ownerName)

    private fun ShoppingItem.toProxy() : RemoteShoppingItem {
        return RemoteShoppingItem(uuid = uuid, name = name, itemOf = itemOf, count = count,
                price = price, brought = brought, image = image.toProxy()
        )
    }

    private fun ItemImage.toProxy() : RemoteItemImage = RemoteItemImage(uri = uri)

    private fun DataSnapshot.toList() : ShoppingList {
        val proxy : RemoteShoppingList = getValue(RemoteShoppingList::class.java) as RemoteShoppingList
        return proxy.toDomainList()
    }

    private fun DataSnapshot.toItem(): ShoppingItem {
        val proxy : RemoteShoppingItem = getValue(RemoteShoppingItem::class.java) as RemoteShoppingItem
        return proxy.toDomainItem()
    }
}
