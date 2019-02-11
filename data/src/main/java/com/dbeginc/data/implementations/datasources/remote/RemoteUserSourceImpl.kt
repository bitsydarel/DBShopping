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
import com.dbeginc.data.ConstantHolder.REMOTE_USERS_PROFILE
import com.dbeginc.data.proxies.remote.list.RemoteShoppingUser
import com.dbeginc.data.proxies.remote.mapper.toDomain
import com.dbeginc.data.proxies.remote.mapper.toRemoteProxy
import com.dbeginc.data.proxies.remote.mapper.toUser
import com.dbeginc.data.proxies.remote.user.RemoteUserProfile
import com.dbeginc.domain.entities.request.*
import com.dbeginc.domain.entities.user.AuthenticationType
import com.dbeginc.domain.entities.user.FriendProfile
import com.dbeginc.domain.entities.user.FriendRequest
import com.dbeginc.domain.entities.user.UserProfile
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import com.google.firebase.storage.StorageReference
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@RestrictTo(RestrictTo.Scope.LIBRARY)
class RemoteUserSourceImpl(private val userTable: DatabaseReference,
                           private val cloudStorage: StorageReference,
                           private val listsTable: DatabaseReference,
                           private val friendRequestTable: DatabaseReference,
                           private val firebaseAuth: FirebaseAuth) : RemoteUserSource {

    override fun registerUser(requestModel: AuthRequestModel): Single<UserProfile> {
        val createUser = Single.create<AuthResult> { emitter ->  firebaseAuth
                .createUserWithEmailAndPassword(requestModel.email, requestModel.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(task.result)
                    else emitter.onError(task.exception!!)
                }
        }

        return createUser
                .flatMap { result ->
                    val user = requestModel.toRemoteUser(result)
                    createUserInDatabase(user)
                            .andThen(Single.fromCallable(user::toDomain))
        }
    }

    override fun registerUserWithGoogle(requestModel: GoogleRequestModel): Single<UserProfile> {
        val credential = GoogleAuthProvider.getCredential(requestModel.token, null)

        val createUser = Single.create<AuthResult> { emitter -> firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(task.result)
                    else emitter.onError(task.exception!!)
                }
        }

        return createUser.flatMap { result ->
            val user = result.toRemoteUser(requestModel.nickname)
            createUserInDatabase(user)
                    .andThen(Single.fromCallable(user::toDomain))
        }
    }

    override fun registerUserWithFacebook(requestModel: FacebookRequestModel): Single<UserProfile> {
        val credential = FacebookAuthProvider.getCredential(requestModel.token)

        val createUser = Single.create<AuthResult> { emitter -> firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(task.result)
                    else emitter.onError(task.exception!!)
                }
        }

        return createUser.flatMap { result ->
            val user = result.toRemoteUser(requestModel.nickname)
            createUserInDatabase(user)
                    .andThen(Single.fromCallable(user::toDomain))
        }
    }

    override fun loginUser(requestModel: AuthRequestModel): Single<UserProfile> {
        val loginRequest = Single.create<String> { emitter -> firebaseAuth
                .signInWithEmailAndPassword(requestModel.email, requestModel.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(task.result.user.uid)
                    else emitter.onError(task.exception!!)
                }
        }

        return loginRequest.flatMap { userId -> getUser(UserRequestModel(userId, Unit)) }
    }

    override fun loginWithGoogle(requestModel: GoogleRequestModel): Single<UserProfile> {
        val credential = GoogleAuthProvider.getCredential(requestModel.token, null)

        val loginRequest = Single.create<String> { emitter ->  firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(task.result.user.uid)
                    else emitter.onError(task.exception!!)
                }
        }

        return loginRequest.flatMap { userId -> getUser(UserRequestModel(userId, Unit)) }
    }

    override fun loginWithFacebook(requestModel: FacebookRequestModel): Single<UserProfile> {
        val credential = FacebookAuthProvider.getCredential(requestModel.token)

        val loginRequest = Single.create<String> { emitter ->  firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(task.result.user.uid)
                    else emitter.onError(task.exception!!)
                }
        }

        return loginRequest.flatMap { userId -> getUser(UserRequestModel(userId, Unit)) }
    }

    override fun canUserLoginWithAccountProvider(requestModel: AccountRequestModel): Single<Boolean> {
        return Single.create { emitter -> firebaseAuth
                .fetchProvidersForEmail(requestModel.email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(task.result.findProvider(requestModel.provider))
                    else emitter.onError(task.exception!!)
                }
        }
    }

    override fun canUserRegisterWithAccountProvider(requestModel: AccountRequestModel): Single<Boolean> {
        return Single.create { emitter -> firebaseAuth
                .fetchProvidersForEmail(requestModel.email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onSuccess(!task.result.findProvider(requestModel.provider))
                    else emitter.onError(task.exception!!)
                }
        }
    }

    override fun sendResetPasswordInstructions(requestModel: AccountRequestModel): Completable {
        return Completable.create { emitter -> firebaseAuth
                .sendPasswordResetEmail(requestModel.email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    override fun linkAccountWithGoogle(requestModel: GoogleRequestModel): Completable {
        val currentUser = firebaseAuth.currentUser ?: return Completable.error(FirebaseNoSignedInUserException("User not Signed in"))

        val credential = GoogleAuthProvider.getCredential(requestModel.token, null)

        return Completable.create { emitter -> currentUser.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    override fun linkAccountWithFacebook(requestModel: FacebookRequestModel): Completable {
        val currentUser = firebaseAuth.currentUser ?: return Completable.error(FirebaseNoSignedInUserException("User not Signed in"))

        val credential = FacebookAuthProvider.getCredential(requestModel.token)

        return Completable.create { emitter -> currentUser.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    override fun logoutUser(requestModel: UserRequestModel<Unit>): Completable =
            Completable.fromCallable { firebaseAuth.signOut() }

    override fun verifyIfUserStillIn(): Single<Boolean> = Single.fromCallable { firebaseAuth.currentUser != null }

    override fun getUser(requestModel: UserRequestModel<Unit>): Single<UserProfile> {
        return Single.create({ emitter -> userTable
                .child(requestModel.userId)
                .child(ConstantHolder.REMOTE_USERS_PROFILE)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) = emitter.onSuccess(data.toUser())
                })
        })
    }

    override fun getFriend(requestModel: UserRequestModel<String>): Single<FriendProfile> = getFriendById(requestModel.arg)

    override fun getUserHisFriends(requestModel: UserRequestModel<Unit>): Single<List<FriendProfile>> {
        val listOfFriendIds = Single.create<List<String>> { emitter -> userTable
                .child(requestModel.userId)
                .child(ConstantHolder.REMOTE_USERS_FRIENDS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) = emitter.onSuccess(data.children.map { it.key })
                })
        }

        return listOfFriendIds.flatMap { friendIds ->
            if (friendIds.isEmpty()) Single.just<List<FriendProfile>>(emptyList())
            else Single.zip(
                    friendIds.map { id -> getFriendById(id) },
                    { friends -> friends.map<Any?, FriendProfile> { it as FriendProfile } }
            )
        }
    }

    override fun findFriends(requestModel: UserRequestModel<String>): Single<List<FriendProfile>> {
        //TODO think about combining elastic search or bigQuery with firebase

        val findFriendByNickname = Single.create<List<FriendProfile>> { emitter -> userTable
                .orderByChild(ConstantHolder.REMOTE_USERS_PROFILE)
                .startAt(requestModel.arg)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) {
                        val friends = mutableListOf<FriendProfile>()

                        data.children.filter {
                            it.child(ConstantHolder.REMOTE_USERS_PROFILE).toUser().nickname.contains(requestModel.arg)
                        }.mapTo(friends) {
                            remoteFriend -> remoteFriend.child(ConstantHolder.REMOTE_USERS_PROFILE).toFriend(false)
                        }

                        emitter.onSuccess(friends)
                    }
                })
        }

        return findFriendByNickname
                .flatMapPublisher { friends -> Flowable.fromIterable(friends) }
                .flatMapSingle { friend -> Single.create<FriendProfile> { emitter ->
                    userTable.child(requestModel.userId)
                            .child(ConstantHolder.REMOTE_USERS_FRIENDS)
                            .orderByKey()
                            .equalTo(friend.uniqueId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                                override fun onDataChange(data: DataSnapshot) = emitter.onSuccess(friend.apply {
                                    isFriendWithCurrentUser = data.exists()
                                })
                            })
                } }.collect(
                        { mutableListOf<FriendProfile>() },
                        { lists, list -> lists.add(list) }
                ).map {
                    friends -> friends.toList()
                }
    }

    override fun getFriendRequests(requestModel: UserRequestModel<Unit>): Single<List<FriendRequest>> {
        return Single.create { emitter -> friendRequestTable
                .child(requestModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(requests: DataSnapshot) {
                        val friendRequests = mutableListOf<FriendRequest>()

                        if (requests.value != null) requests.children.mapTo(friendRequests) {
                            request -> request.getValue(FriendRequest::class.java) as FriendRequest
                        }

                        emitter.onSuccess(friendRequests)
                    }
                })
        }
    }

    override fun sendFriendRequest(requestModel: FriendRequestModel): Completable {
        return Completable.create { emitter -> friendRequestTable
                .child(requestModel.friendUserId)
                .child(requestModel.currentUserId)
                .setValue(FriendRequest(
                        requestModel.currentUserId,
                        requestModel.currentUserNickname,
                        requestModel.currentUserAvatar,
                        requestModel.idOfListToShare,
                        requestModel.nameOfListToShare
                ))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    override fun acceptFriendRequest(requestModel: FriendRequestModel): Completable {
        val addAcceptedUserToCurrentUserFriendList = Completable.create { emitter -> userTable
                .child(requestModel.currentUserId)
                .child(ConstantHolder.REMOTE_USERS_FRIENDS)
                .child(requestModel.friendUserId)
                .setValue(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val addCurrentUserToAcceptedUserFriendList = Completable.create { emitter -> userTable
                .child(requestModel.friendUserId)
                .child(ConstantHolder.REMOTE_USERS_FRIENDS)
                .child(requestModel.currentUserId)
                .setValue(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val addSharedListToCurrentUserLists = Completable.create { emitter -> userTable
                .child(requestModel.currentUserId)
                .child(ConstantHolder.REMOTE_USERS_LISTS)
                .child(requestModel.idOfListToShare)
                .setValue(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val addCurrentUserToListMembers = Completable.create { emitter -> listsTable
                .child(requestModel.idOfListToShare)
                .child(ConstantHolder.REMOTE_LISTS_MEMBERS)
                .child(requestModel.currentUserId)
                .setValue(RemoteShoppingUser(
                        requestModel.currentUserId,
                        requestModel.currentUserEmail,
                        requestModel.currentUserNickname,
                        requestModel.currentUserAvatar,
                        false
                ))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }

        val acceptFriendRequest = Completable.create { emitter -> friendRequestTable
                .child(requestModel.currentUserId)
                .child(requestModel.friendUserId)
                .removeValue()
                .addOnCompleteListener {
                    task -> if (task.isSuccessful) emitter.onComplete() else emitter.onError(task.exception!!)
                }
        }

        return addAcceptedUserToCurrentUserFriendList
                .andThen(addCurrentUserToAcceptedUserFriendList)
                .andThen(addSharedListToCurrentUserLists)
                .andThen(addCurrentUserToListMembers)
                .andThen(acceptFriendRequest)
    }

    override fun declineFriendRequest(requestModel: FriendRequestModel): Completable {
        return Completable.create { emitter -> friendRequestTable
                .child(requestModel.currentUserId)
                .child(requestModel.friendUserId)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    override fun updateUserAvatar(requestModel: UserRequestModel<UserProfile>): Completable {
        val pushImageToCloud = Single.create<String> { emitter -> cloudStorage
                .child(requestModel.userId)
                .putFile(Uri.parse(requestModel.arg.avatar))
                .addOnSuccessListener { result -> emitter.onSuccess(result.downloadUrl.toString()) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }

        return pushImageToCloud
                .flatMapCompletable { avatarUrl ->
                    publishPendingUserChanges(requestModel.arg.apply { avatar = avatarUrl })
                }
    }

    override fun publishPendingUserChanges(user: UserProfile): Completable {
        return Completable.create { emitter -> userTable
                .child(user.uniqueId)
                .child(REMOTE_USERS_PROFILE)
                .setValue(user.toRemoteProxy())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    private fun ProviderQueryResult.findProvider(provider: AuthenticationType) : Boolean = providers?.contains(provider.value()) ?: false

    private fun AuthenticationType.value() : String {
        return when(this) {
            AuthenticationType.DEFAULT -> EmailAuthProvider.PROVIDER_ID
            AuthenticationType.GOOGLE -> GoogleAuthProvider.PROVIDER_ID
            AuthenticationType.FACEBOOK -> FacebookAuthProvider.PROVIDER_ID
        }
    }

    private fun createUserInDatabase(user: RemoteUserProfile) : Completable {
        return Completable.create { emitter -> userTable
                .child(user.uniqueId)
                .child(ConstantHolder.REMOTE_USERS_PROFILE)
                .setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) emitter.onComplete()
                    else emitter.onError(task.exception!!)
                }
        }
    }

    private fun getFriendById(id: String) : Single<FriendProfile> {
        return Single.create<FriendProfile> { emitter -> userTable
                .child(id)
                .child(ConstantHolder.REMOTE_USERS_PROFILE)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())
                    override fun onDataChange(data: DataSnapshot) {
                        val friend = data.toFriend(true)

                        emitter.onSuccess(friend)
                    }
                })
        }
    }

    private fun AuthRequestModel.toRemoteUser(result: AuthResult): RemoteUserProfile {
        return RemoteUserProfile(
                uniqueId = result.user.uid,
                nickname = name,
                email = result.user.email ?: email,
                avatar = "",
                creationDate = result.user.metadata?.creationTimestamp ?: ServerValue.TIMESTAMP,
                birthday = ""
        )
    }

    private fun AuthResult.toRemoteUser(name: String) : RemoteUserProfile {
        return RemoteUserProfile(
                email = user.email!!,
                nickname = name,
                uniqueId = user.uid,
                avatar = user.photoUrl?.toString() ?: "",
                creationDate = user.metadata?.creationTimestamp ?: ServerValue.TIMESTAMP,
                birthday = null
        )
    }

    private fun DataSnapshot.toFriend(isFriendWithCurrentUser: Boolean): FriendProfile {
        val proxy : RemoteUserProfile = getValue(RemoteUserProfile::class.java) as RemoteUserProfile

        return FriendProfile(
                uniqueId = proxy.uniqueId,
                isFriendWithCurrentUser = isFriendWithCurrentUser,
                nickname = proxy.nickname,
                email = proxy.email,
                avatar = proxy.avatar,
                birthday= proxy.birthday
        )
    }
}
