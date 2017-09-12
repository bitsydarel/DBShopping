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

import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.datasource.UserSource
import com.dbeginc.data.proxies.remote.RemoteAccount
import com.dbeginc.data.proxies.remote.RemoteUser
import com.dbeginc.data.proxies.remote.mapper.toProxy
import com.dbeginc.domain.entities.requestmodel.AccountRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.Account
import com.dbeginc.domain.entities.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class RemoteUserSourceImpl(private val userTable: DatabaseReference, private val accountTable: DatabaseReference) : UserSource {

    override fun createUser(userToAdd: UserRequestModel<User>, userAccount: UserRequestModel<Account>): Completable {
        val createUser = Completable.create { emitter -> userTable.child(userToAdd.userId)
                .setValue(userToAdd.arg.toProxy())
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        val createAccount = Completable.create { emitter -> accountTable.child(userAccount.userId)
                .setValue(userAccount.arg.toProxy())
                .addOnCompleteListener {
                    task ->  if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        return createUser.andThen(createAccount)
    }

    override fun checkIfUserExist(requestModel: UserRequestModel<Unit>): Single<Boolean> {
        return Single.create<Boolean> { emitter -> userTable.child(requestModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(user: DataSnapshot) = emitter.onSuccess(user.value != null)
                })
        }
    }

    override fun checkIfUserHasAccountProvider(requestModel: UserRequestModel<String>): Single<Boolean> {
        return Single.create { emitter -> accountTable.child(requestModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

                    override fun onDataChange(data: DataSnapshot) {
                        if (data.value == null) emitter.onSuccess(false)
                        else {
                            val account : RemoteAccount = data.getValue(RemoteAccount::class.java)!!
                            emitter.onSuccess(account.accountProviders.keys.contains(requestModel.arg))
                        }
                    }
                })
        }
    }

    override fun getUser(requestModel: UserRequestModel<Unit>): Flowable<User> {
        return Flowable.create({ emitter -> userTable.child(requestModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

                    override fun onDataChange(data: DataSnapshot) {
                        emitter.onNext(data.toUser())
                        emitter.onComplete()
                    }
                })
        }, BackpressureStrategy.LATEST)
    }

    override fun getUsers(requestModel: UserRequestModel<List<String>>): Flowable<List<User>> {
        val listOfUsers = mutableListOf<User>()

        return Flowable.fromIterable(requestModel.arg)
                .flatMap { userId -> getUser(UserRequestModel(userId, Unit)) }
                .map {  user -> listOfUsers.add(user); listOfUsers }
                .map { users -> users.toList() }
                .take(requestModel.arg.size.toLong())
                .takeLast(1)
    }

    override fun getAccount(requestModel: AccountRequestModel<Unit>): Flowable<Account> {
        return Flowable.create({ emitter -> accountTable.child(requestModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) {
                        emitter.onNext(data.toAccount())
                        emitter.onComplete()
                    }
                })
        }, BackpressureStrategy.LATEST)
    }

    override fun updateAccount(requestModel: AccountRequestModel<Account>): Completable {
        return Completable.create { emitter -> val updates = mutableMapOf<String, Any>()
            updates.put(ConstantHolder.NAME, requestModel.arg.name)
            updates.put(ConstantHolder.PROFILE_IMAGE, requestModel.arg.profileImage)

            accountTable.child(requestModel.userId)
                    .updateChildren(updates)
                    .addOnCompleteListener {
                        task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                    }
        }
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

    private fun DataSnapshot.toUser() : User {
        val proxy : RemoteUser = getValue(RemoteUser::class.java) as RemoteUser
        return proxy.toDomainUser()
    }

    private fun DataSnapshot.toAccount(): Account {
        val proxy : RemoteAccount = getValue(RemoteAccount::class.java) as RemoteAccount
        return proxy.toDomainAccount()
    }
}
