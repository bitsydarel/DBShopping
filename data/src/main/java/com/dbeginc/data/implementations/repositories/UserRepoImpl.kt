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

package com.dbeginc.data.implementations.repositories

import android.util.Base64
import android.util.Log
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.ThreadProvider
import com.dbeginc.data.datasource.DataSource
import com.dbeginc.data.datasource.UserSource
import com.dbeginc.domain.entities.requestmodel.AccountRequestModel
import com.dbeginc.domain.entities.requestmodel.AuthRequestModel
import com.dbeginc.domain.entities.requestmodel.GoogleRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.Account
import com.dbeginc.domain.entities.user.User
import com.dbeginc.domain.repositories.IUserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver

class UserRepoImpl(private val firebaseAuth: FirebaseAuth, private val local: UserSource, private val remote: UserSource,
                   private val schedulerProvider: ThreadProvider,
                   private val localData: DataSource) : IUserRepo {

    private val subscriptions = CompositeDisposable()
    /********************************** Create user methods **********************************/

    override fun createNewUser(requestModel: AuthRequestModel<String>): Completable {

        return Single.create<FirebaseUser> { emitter -> firebaseAuth.createUserWithEmailAndPassword(requestModel.email, requestModel.password)
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onSuccess(task.result.user) else emitter.onError(task.exception!!)
                }
        }.flatMapCompletable {
            user -> val encodedEmail = Base64.encodeToString(user.email?.toByteArray(), Base64.NO_WRAP)
            val userToAdd = UserRequestModel(encodedEmail, User(encodedEmail, requestModel.arg, email = user.email!!))
            val userAccount = UserRequestModel(encodedEmail, Account(encodedEmail, requestModel.arg, accountProviders = listOf(user.providers!![0])))

            remote.createUser(userToAdd, userAccount).concatWith(local.createUser(userToAdd, userAccount))

        }.subscribeOn(schedulerProvider.network).observeOn(schedulerProvider.ui)
    }

    override fun createNewUserWithGoogle(requestModel: GoogleRequestModel<String>): Completable {
        val credentials = GoogleAuthProvider.getCredential(requestModel.arg, null)

        val login = Single.create<FirebaseUser> { emitter -> firebaseAuth.signInWithCredential(credentials)
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onSuccess(task.result.user) else emitter.onError(task.exception!!)
                }
        }.subscribeOn(schedulerProvider.network)

        return login.flatMapCompletable {
            user -> val userToAdd = UserRequestModel(requestModel.userId, User(requestModel.userId, name = user.displayName ?: "", email = user.email ?: ""))
            val userAccount = UserRequestModel(requestModel.userId, requestModel.userAccount)
            remote.createUser(userToAdd, userAccount).concatWith(local.createUser(userToAdd, userAccount))
        }.subscribeOn(schedulerProvider.network).observeOn(schedulerProvider.ui)
    }

    /********************************** Login user methods **********************************/

    override fun loginUser(requestModel: AuthRequestModel<Unit>): Completable {
        return Completable.create { emitter -> firebaseAuth.signInWithEmailAndPassword(requestModel.email, requestModel.password)
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }.subscribeOn(schedulerProvider.network).observeOn(schedulerProvider.ui)
    }

    override fun loginWithGoogle(requestModel: UserRequestModel<String>): Completable {
        val credentials = GoogleAuthProvider.getCredential(requestModel.arg, null)

        return Completable.create { emitter -> firebaseAuth.signInWithCredential(credentials)
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }.subscribeOn(schedulerProvider.network).observeOn(schedulerProvider.ui)
    }

    /********************************** User checking methods **********************************/

    override fun doesUserExist(requestModel: UserRequestModel<Unit>): Single<Boolean> =
            remote.checkIfUserExist(requestModel)
                    .subscribeOn(schedulerProvider.network)
                    .observeOn(schedulerProvider.ui)

    override fun canUserLoginWithAccountProvider(requestModel: UserRequestModel<String>): Single<Boolean> =
            remote.checkIfUserHasAccountProvider(requestModel)
                    .subscribeOn(schedulerProvider.network)
                    .observeOn(schedulerProvider.ui)

    /********************************** User Interactions methods **********************************/

    override fun getUser(requestModel: UserRequestModel<Unit>): Flowable<User> {
        return remote.getUser(requestModel)
                .subscribeOn(schedulerProvider.network)
                .doOnNext { user -> subscriptions.addUser(user) }
                .publish {
                    remoteData -> Flowable.mergeDelayError(remoteData, local.getUser(requestModel).takeUntil(remoteData).subscribeOn(schedulerProvider.computation))
                }.observeOn(schedulerProvider.ui)
    }

    override fun getUsers(requestModel: UserRequestModel<List<String>>): Flowable<List<User>> {
        return remote.getUsers(requestModel)
                .subscribeOn(schedulerProvider.network)
                .doOnNext { users -> users.forEach { user -> subscriptions.addUser(user) } }
                .publish {
                    remoteData -> Flowable.mergeDelayError(remoteData, local.getUsers(requestModel).takeUntil(remoteData).subscribeOn(schedulerProvider.computation))
                }.observeOn(schedulerProvider.ui)
    }

    override fun getAccount(requestModel: AccountRequestModel<Unit>): Flowable<Account> {
        return remote.getAccount(requestModel)
                .subscribeOn(schedulerProvider.network)
                .doOnNext { account -> subscriptions.addAccount(account) }
                .publish {
                    remoteData -> Flowable.mergeDelayError(remoteData, local.getAccount(requestModel).takeUntil(remoteData).subscribeOn(schedulerProvider.computation))
                }.observeOn(schedulerProvider.ui)
    }

    override fun updateAccount(requestModel: AccountRequestModel<Account>): Completable {
        return local.updateAccount(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.updateAccount(requestModel).subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun updateUser(requestModel: UserRequestModel<User>): Completable {
        return local.updateUser(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.updateUser(requestModel).subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun deleteUser(requestModel: UserRequestModel<Unit>): Completable {
        return remote.deleteUser(requestModel)
                .subscribeOn(schedulerProvider.network)
                .andThen(local.deleteUser(requestModel).subscribeOn(schedulerProvider.computation))
                .observeOn(schedulerProvider.ui)
    }

    override fun logoutUser(requestModel: UserRequestModel<Unit>): Completable {
        val logout = Completable.fromAction { firebaseAuth.signOut() }
                .subscribeOn(schedulerProvider.network)

        return localData.deleteAll(UserRequestModel(requestModel.userId, Unit))
                .subscribeOn(schedulerProvider.computation)
                .andThen(logout)
                .observeOn(schedulerProvider.ui)
    }

    private fun CompositeDisposable.addUser(user: User) {
        val requestModel = UserRequestModel(userId = user.uuid, arg = user)
        add(local.updateUser(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .subscribeWith(UpdateObserver())
        )
    }

    private fun CompositeDisposable.addAccount(account: Account) {
        val requestModel = AccountRequestModel(userId = account.userId, arg = account)
        add(local.updateAccount(requestModel).subscribeOn(schedulerProvider.computation)
                .subscribeWith(UpdateObserver())
        )
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            Log.i(ConstantHolder.TAG, "Update of data done in ${UserRepoImpl::class.java.simpleName}")
            dispose()
        }
        override fun onError(e: Throwable) {
            Log.e(ConstantHolder.TAG, "Error in ${UserRepoImpl::class.java.simpleName}: ", e)
        }
    }

    override fun clean() = subscriptions.clear()
}