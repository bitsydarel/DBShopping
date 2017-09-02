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

import android.util.Log
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.datasource.UserSource
import com.dbeginc.data.proxies.local.LocalShoppingList
import com.dbeginc.data.proxies.remote.RemoteUser
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import com.google.firebase.database.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
class RemoteUserSourceImpl(private val userTable: DatabaseReference) : UserSource {

    override fun createUser(requestModel: UserRequestModel<User>): Completable {
        return Completable.create { emitter -> userTable.child(requestModel.userId)
                .setValue(requestModel.arg.toProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    override fun checkIfUserExist(requestModel: UserRequestModel<Unit>): Single<Boolean> {

        return Single.create<Boolean> { emitter -> userTable.child(requestModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(user: DataSnapshot) = emitter.onSuccess(user.value != null)
                })
        }
    }

    override fun getUser(requestModel: UserRequestModel<Unit>): Flowable<User> {
        return Flowable.create({ emitter -> userTable.child(requestModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

                    override fun onDataChange(data: DataSnapshot) = emitter.onNext(data.toUser())
                })
        }, BackpressureStrategy.LATEST)
    }

    override fun getUsers(requestModel: UserRequestModel<List<String>>): Flowable<List<User>> {
        return Flowable.fromIterable(requestModel.arg)
                .flatMap { userId -> getUser(UserRequestModel(userId, Unit)) }
                .onBackpressureBuffer()
                .toList()
                .toFlowable()
    }

    override fun updateUser(requestModel: UserRequestModel<User>): Completable {
        return Completable.create { emitter -> val updates = mutableMapOf<String, Any>()
            updates.put(ConstantHolder.NAME, requestModel.arg.name)
            updates.put(ConstantHolder.EMAIL, requestModel.arg.email)

            userTable.child(requestModel.userId)
                    .updateChildren(updates)
                    .addOnCompleteListener {
                        task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                    }
        }
    }

    override fun deleteUser(requestModel: UserRequestModel<Unit>): Completable {
        return Completable.create { emitter -> userTable.child(requestModel.userId)
                .removeValue()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }
    }

    private fun User.toProxy() : RemoteUser {

        var creationTime : Any = ServerValue.TIMESTAMP

        if (joinedAt.isNotEmpty()) {
            try {
                creationTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        .parse(joinedAt)
                        .time

            } catch (pe: ParseException) {
                Log.e(ConstantHolder.TAG, "Error in ${LocalShoppingList::class.java.simpleName}: ${pe.localizedMessage}")
            }
        }

        return RemoteUser(uuid = uuid, name = name, email = email, joinedAt = creationTime)
    }

    private fun DataSnapshot.toUser() : User {
        val proxy : RemoteUser = getValue(RemoteUser::class.java) as RemoteUser

        return proxy.toDomainUser()
    }
}