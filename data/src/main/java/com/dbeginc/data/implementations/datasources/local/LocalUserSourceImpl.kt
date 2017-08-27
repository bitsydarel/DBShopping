package com.dbeginc.data.implementations.datasources.local

import android.util.Log
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.datasource.UserSource
import com.dbeginc.data.proxies.local.LocalUser
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.Realm
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
class LocalUserSourceImpl : UserSource {

    override fun createUser(requestModel: UserRequestModel<User>): Completable {
        return Completable.fromCallable{ Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxy()) }
        } }
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

            db -> val user : LocalUser? = db.where(LocalUser::class.java)
                .equalTo(ConstantHolder.UUID, requestModel.userId)
                .findFirst()

            if (user != null) emitter.onNext(user.toDomainUser())
            else emitter.onComplete()

        } } }, BackpressureStrategy.LATEST)
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

    private fun User.toProxy() : LocalUser {
        var creationDate = 0L

        if (joinedAt.isNotEmpty()) {
            try {
                creationDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        .parse(joinedAt)
                        .time

            } catch (pe: ParseException) {
                Log.e(ConstantHolder.TAG, "Error in ${LocalUserSourceImpl::class.java.simpleName}: ${pe.localizedMessage}")
            }
        }

        return LocalUser(uuid = uuid, name = name, email = email, joinedAt = creationDate)
    }
}