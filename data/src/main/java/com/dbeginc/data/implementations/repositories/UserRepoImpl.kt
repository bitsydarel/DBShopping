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

import android.content.Context
import com.dbeginc.data.ConstantHolder.REMOTE_FRIENDS_REQUEST_TABLE
import com.dbeginc.data.ConstantHolder.REMOTE_IMAGES_TABLE
import com.dbeginc.data.ConstantHolder.REMOTE_LISTS_TABLE
import com.dbeginc.data.ConstantHolder.REMOTE_USERS_TABLE
import com.dbeginc.data.CrashlyticsLogger
import com.dbeginc.data.RxThreadProvider
import com.dbeginc.data.implementations.datasources.local.LocalUserSource
import com.dbeginc.data.implementations.datasources.local.user.LocalUserSourceImpl
import com.dbeginc.data.implementations.datasources.local.user.room.LocalUserDatabase
import com.dbeginc.data.implementations.datasources.remote.RemoteUserSource
import com.dbeginc.data.implementations.datasources.remote.RemoteUserSourceImpl
import com.dbeginc.data.proxies.local.user.LocalUserPendingRequest
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.*
import com.dbeginc.domain.entities.user.FriendProfile
import com.dbeginc.domain.entities.user.FriendRequest
import com.dbeginc.domain.entities.user.UserProfile
import com.dbeginc.domain.repositories.IUserRepo
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver

class UserRepoImpl private constructor(
        private val local: LocalUserSource,
        private val remote: RemoteUserSource,
        private val threads: ThreadProvider,
        private val logger: Logger) : IUserRepo {

    private val subscriptions = CompositeDisposable()

    companion object {
        @JvmStatic
        fun create(appContext: Context) : IUserRepo {
            var firstTime = false

            val app : FirebaseApp = FirebaseApp.getInstance() ?: FirebaseApp.initializeApp(appContext)!!.also {
                firstTime = true
            }

            val firebaseDatabase = FirebaseDatabase.getInstance(app)

            if (firstTime) firebaseDatabase.setPersistenceEnabled(true)

            val firebaseStorage = FirebaseStorage.getInstance(app)

            firebaseStorage.maxUploadRetryTimeMillis = 300000

            val remoteUserSource = RemoteUserSourceImpl(
                    userTable = firebaseDatabase.reference.child(REMOTE_USERS_TABLE),
                    cloudStorage = firebaseStorage.reference.child(REMOTE_IMAGES_TABLE),
                    listsTable = firebaseDatabase.reference.child(REMOTE_LISTS_TABLE),
                    friendRequestTable = firebaseDatabase.reference.child(REMOTE_FRIENDS_REQUEST_TABLE),
                    firebaseAuth = FirebaseAuth.getInstance(app)
            )

            val localUserSource = LocalUserSourceImpl(LocalUserDatabase.create(appContext))

            return UserRepoImpl(localUserSource, remoteUserSource, RxThreadProvider, CrashlyticsLogger)
        }
    }

    override fun registerUser(requestModel: AuthRequestModel): Single<UserProfile> {
        return remote.registerUser(requestModel)
                .subscribeOn(threads.IO)
                .doAfterSuccess {
                    user -> local.defineCurrentUser(UserRequestModel(userId=user.uniqueId, arg=user))
                        .subscribeOn(threads.CP)
                        .subscribe(UpdateObserver())
                }
    }

    override fun registerUserWithGoogle(requestModel: GoogleRequestModel): Single<UserProfile> {
        return remote.registerUserWithGoogle(requestModel)
                .subscribeOn(threads.IO)
                .doAfterSuccess {
                    user -> local.defineCurrentUser(UserRequestModel(userId=user.uniqueId, arg=user))
                        .subscribeOn(threads.CP)
                        .subscribe(UpdateObserver())
                }
    }

    override fun registerUserWithFacebook(requestModel: FacebookRequestModel): Single<UserProfile> {
        return remote.registerUserWithFacebook(requestModel)
                .subscribeOn(threads.IO)
                .doAfterSuccess {
                    user -> local.defineCurrentUser(UserRequestModel(userId=user.uniqueId, arg=user))
                        .subscribeOn(threads.CP)
                        .subscribe(UpdateObserver())
                }
    }

    override fun loginUser(requestModel: AuthRequestModel): Single<UserProfile> {
        return remote.loginUser(requestModel)
                .subscribeOn(threads.IO)
                .doAfterSuccess {
                    user -> local.defineCurrentUser(UserRequestModel(userId=user.uniqueId, arg=user))
                        .subscribeOn(threads.CP)
                        .subscribe(UpdateObserver())
                }
    }

    override fun loginWithGoogle(requestModel: GoogleRequestModel): Single<UserProfile> {
        return remote.loginWithGoogle(requestModel)
                .subscribeOn(threads.IO)
                .doAfterSuccess {
                    user -> local.defineCurrentUser(UserRequestModel(userId=user.uniqueId, arg=user))
                        .subscribeOn(threads.CP)
                        .subscribe(UpdateObserver())
                }
    }

    override fun loginWithFacebook(requestModel: FacebookRequestModel): Single<UserProfile> {
        return remote.loginWithFacebook(requestModel)
                .subscribeOn(threads.IO)
                .doAfterSuccess {
                    user -> local.defineCurrentUser(UserRequestModel(userId=user.uniqueId, arg=user))
                        .subscribeOn(threads.CP)
                        .subscribe(UpdateObserver())
                }
    }

    override fun canUserLoginWithAccountProvider(requestModel: AccountRequestModel): Single<Boolean> = remote.canUserLoginWithAccountProvider(requestModel).subscribeOn(threads.IO)

    override fun canUserRegisterWithAccountProvider(requestModel: AccountRequestModel): Single<Boolean> = remote.canUserRegisterWithAccountProvider(requestModel).subscribeOn(threads.IO)

    override fun verifyIfUserStillIn(): Single<Boolean> = remote.verifyIfUserStillIn().subscribeOn(threads.IO)

    override fun logoutUser(requestModel: UserRequestModel<Unit>): Completable =
            remote.logoutUser(requestModel).subscribeOn(threads.IO).andThen(local.deleteAll().subscribeOn(threads.CP))

    override fun sendResetPasswordInstructions(requestModel: AccountRequestModel): Completable =
            remote.sendResetPasswordInstructions(requestModel).subscribeOn(threads.IO)

    override fun linkAccountWithFacebook(requestModel: FacebookRequestModel): Completable =
            remote.linkAccountWithFacebook(requestModel).subscribeOn(threads.IO)

    override fun linkAccountWithGoogle(requestModel: GoogleRequestModel): Completable =
            remote.linkAccountWithGoogle(requestModel).subscribeOn(threads.IO)

    override fun getUser(requestModel: UserRequestModel<Unit>): Flowable<UserProfile> {
        return local.getCurrentUser(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getUser(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribeWith(UpdateUserObserver())
                }
    }

    override fun getFriend(requestModel: UserRequestModel<String>): Flowable<FriendProfile> {
        return local.getFriend(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getFriend(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribeWith(UpdateFriendObserver())
                }
    }

    override fun getUserHisFriends(requestModel: UserRequestModel<Unit>): Flowable<List<FriendProfile>> {
        return local.getUserHisFriends(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getUserHisFriends(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribeWith(UpdateFriendsObserver())
                }
    }

    override fun findFriends(requestModel: UserRequestModel<String>): Single<List<FriendProfile>> =
            remote.findFriends(requestModel).subscribeOn(threads.IO)

    override fun getFriendRequests(requestModel: UserRequestModel<Unit>) : Single<List<FriendRequest>> =
            remote.getFriendRequests(requestModel).subscribeOn(threads.IO)

    override fun sendFriendRequests(requestModel: List<FriendRequestModel>): Completable {
        return Flowable.fromIterable(requestModel)
                .subscribeOn(threads.IO)
                .flatMapCompletable { friendRequest ->
                    remote.sendFriendRequest(friendRequest)
                            .subscribeOn(threads.IO)
                }
    }

    override fun acceptFriendRequest(requestModel: FriendRequestModel): Completable =
            remote.acceptFriendRequest(requestModel).subscribeOn(threads.IO)

    override fun declineFriendRequest(requestModel: FriendRequestModel): Completable =
            remote.declineFriendRequest(requestModel).subscribeOn(threads.IO)

    override fun updateUserInfo(requestModel: UserRequestModel<UserProfile>): Completable {
        val request = LocalUserPendingRequest(requestModel.userId)

        return local.updateUser(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.updateUserAvatar(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun publishPendingUserChanges(): Completable {
        return local.getAllPendingRequest()
                .subscribeOn(threads.IO)
                .flatMapPublisher { pendingRequests -> Flowable.fromIterable(pendingRequests) }
                .flatMapCompletable { pendingRequest ->
                    local.getCurrentUser(UserRequestModel(userId=pendingRequest.userUniqueId, arg=Unit))
                            .subscribeOn(threads.CP)
                            .flatMapCompletable { user ->
                                remote.updateUserAvatar(UserRequestModel(user.uniqueId, user))
                                        .subscribeOn(threads.IO)
                            }
                }
    }

    override fun clean() = subscriptions.clear()

    private fun CompositeDisposable.addUser(user: UserProfile) {
        val requestModel = UserRequestModel(userId = user.uniqueId, arg = user)
        add(local.updateUser(requestModel)
                .subscribeOn(threads.CP)
                .subscribeWith(UpdateObserver())
        )
    }

    private fun CompositeDisposable.addFriend(friend: FriendProfile) {
        val requestModel = UserRequestModel(userId = friend.uniqueId, arg = friend)
        add(local.addFriend(requestModel)
                .subscribeOn(threads.CP)
                .subscribeWith(UpdateObserver())
        )
    }

    private fun CompositeDisposable.addFriends(friends: List<FriendProfile>) {
        val requestModel = UserRequestModel(userId = friends.first().uniqueId, arg = friends)
        add(local.addFriends(requestModel)
                .subscribeOn(threads.CP)
                .subscribeWith(UpdateObserver())
        )
    }

    private inner class UpdateUserObserver : DisposableSingleObserver<UserProfile>() {
        override fun onSuccess(user: UserProfile) = subscriptions.addUser(user)
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateFriendsObserver : DisposableSingleObserver<List<FriendProfile>>() {
        override fun onSuccess(friends: List<FriendProfile>) {
            if (friends.isNotEmpty()) subscriptions.addFriends(friends)
        }
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateFriendObserver : DisposableSingleObserver<FriendProfile>() {
        override fun onSuccess(friend: FriendProfile) = subscriptions.addFriend(friend)
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() = logger.logEvent("Update of data done in ${UserRepoImpl::class.java.simpleName}")
        override fun onError(error: Throwable) = logger.logError(error)
    }
}