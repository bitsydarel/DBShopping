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

import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.datasource.UserSource
import com.dbeginc.data.proxies.local.LocalAccount
import com.dbeginc.data.proxies.local.LocalUser
import com.dbeginc.data.proxies.local.RealmString
import com.dbeginc.data.proxies.local.mapper.toProxy
import com.dbeginc.domain.entities.requestmodel.AccountRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.Account
import com.dbeginc.domain.entities.user.User
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.Realm

class LocalUserSourceImpl : UserSource {

    override fun createUser(userToAdd: UserRequestModel<User>, userAccount: UserRequestModel<Account>): Completable {
        return Completable.fromCallable{ Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {
            db -> db.insertOrUpdate(userToAdd.arg.toProxy())
            db.insertOrUpdate(userAccount.arg.toProxy())
        } } }
    }

    override fun checkIfUserHasAccountProvider(requestModel: UserRequestModel<String>): Single<Boolean> {
        return Single.fromCallable {
            var hasAccount = false

            Realm.getDefaultInstance().use {
                realm -> realm.executeTransaction {
                db -> val account = db.where(LocalAccount::class.java)
                    .equalTo(ConstantHolder.UUID, requestModel.userId)
                    .findFirst()
                hasAccount = account?.accountProviders?.contains(RealmString()) ?: false
            } }

            return@fromCallable hasAccount
        }
    }

    override fun checkIfUserExist(requestModel: UserRequestModel<Unit>): Single<Boolean> {
        return Single.fromCallable {
            var userFound = false

            Realm.getDefaultInstance().use {
                realm -> realm.executeTransaction {

                db -> userFound = db.where(LocalUser::class.java)
                    .equalTo(ConstantHolder.UUID, requestModel.userId)
                    .findFirst() != null
            } }

            return@fromCallable userFound
        }
    }

    override fun getUser(requestModel: UserRequestModel<Unit>): Flowable<User> {
        return Flowable.create({ emitter -> Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {

            db -> val user: LocalUser? = db.where(LocalUser::class.java)
                        .equalTo(ConstantHolder.UUID, requestModel.userId)
                        .findFirst()

            if (user != null) emitter.onNext(user.toDomainUser())
            emitter.onComplete()

        } } }, BackpressureStrategy.LATEST)
    }

    override fun getUsers(requestModel: UserRequestModel<List<String>>): Flowable<List<User>> {
        val listOfUsers = mutableListOf<User>()

        return Flowable.fromIterable(requestModel.arg) // Emit all userIds
                .flatMap { userId -> getUser(UserRequestModel(userId, Unit)) } // For each userIds get the user
                .map { user -> listOfUsers.add(user); listOfUsers } // add the user to the list
                .map { users -> users.toList() } // since our result need a unmodifiable list
                .take(requestModel.arg.size.toLong()) // keep taking response until we match the number of user requiered
                .takeLast(1) // take only the last value, which would be the full list of users required
    }

    override fun getAccount(requestModel: AccountRequestModel<Unit>): Flowable<Account> {
        return Flowable.create({ emitter -> Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {

            db -> val account: LocalAccount? = db.where(LocalAccount::class.java)
                .equalTo(ConstantHolder.UUID, requestModel.userId)
                .findFirst()

            if (account != null) emitter.onNext(account.toDomainAccount())
            emitter.onComplete()

        } } }, BackpressureStrategy.LATEST)
    }

    override fun updateAccount(requestModel: AccountRequestModel<Account>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxy()) }
        } }
    }

    override fun updateUser(requestModel: UserRequestModel<User>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxy()) }
        } }
    }

    override fun deleteUser(requestModel: UserRequestModel<Unit>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {

            db -> db.where(LocalUser::class.java)
                .equalTo(ConstantHolder.UUID, requestModel.userId)
                .findAll()
                .deleteAllFromRealm()
        } } }
    }
}