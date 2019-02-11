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

import android.support.annotation.RestrictTo
import com.dbeginc.domain.entities.request.*
import com.dbeginc.domain.entities.user.FriendProfile
import com.dbeginc.domain.entities.user.FriendRequest
import com.dbeginc.domain.entities.user.UserProfile
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by darel on 21.02.18.
 *
 * Remote UserProfile Source
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface RemoteUserSource {
    /**
     * Create New UserProfile
     * @param requestModel [AuthRequestModel] containing information required
     * to register a new user using email address
     * @return [Completable] that notify about the task completion
     */
    fun registerUser(requestModel: AuthRequestModel) : Single<UserProfile>

    /**
     * Register New UserProfile With a Google Account
     * @param requestModel [GoogleRequestModel] containing information required
     * to register a new user using google account
     * @return [Completable] that notify about the task completion
     */
    fun registerUserWithGoogle(requestModel: GoogleRequestModel) : Single<UserProfile>

    /**
     * Register New UserProfile With a Facebook Account
     * @param requestModel [FacebookRequestModel] containing information required
     * to register a new user using facebook account
     * @return [Completable] that notify about the task completion
     */
    fun registerUserWithFacebook(requestModel: FacebookRequestModel): Single<UserProfile>

    /**
     * Login UserProfile
     * @param requestModel [AuthRequestModel] containing information required
     * to login an user
     * @return [Single] that provide the logged user or an error
     */
    fun loginUser(requestModel: AuthRequestModel) : Single<UserProfile>

    /**
     * Login UserProfile With Google
     * @param requestModel [GoogleRequestModel] containing information required
     * to login an user with google account
     * @return [Completable] that provide the logged user or an error
     */
    fun loginWithGoogle(requestModel: GoogleRequestModel) : Single<UserProfile>

    /**
     * Login UserProfile With Facebook
     * @param requestModel [FacebookRequestModel] containing information required
     * to login an user with facebook account
     * @return [Completable] that provide the logged user or an error
     */
    fun loginWithFacebook(requestModel: FacebookRequestModel): Single<UserProfile>

    /**
     * Verify if user already exist
     * @param requestModel [UserRequestModel] containing information required
     * to check if specified user id exist
     * @return [Completable] that notify about the task completion
     */
    fun canUserLoginWithAccountProvider(requestModel: AccountRequestModel) : Single<Boolean>

    /**
     * Logout user
     * @param requestModel [UserRequestModel] containing information required
     * to check if specified user id exist
     * @return [Completable] that notify about the task completion
     */
    fun logoutUser(requestModel: UserRequestModel<Unit>): Completable

    /**
     * Verify if user can login with the specified authentication type
     * @param requestModel [UserRequestModel] that the user is trying to login with
     * @return [Completable] that notify about the task completion
     */
    fun canUserRegisterWithAccountProvider(requestModel: AccountRequestModel) : Single<Boolean>

    fun sendResetPasswordInstructions(requestModel: AccountRequestModel): Completable

    fun linkAccountWithFacebook(requestModel: FacebookRequestModel): Completable

    fun linkAccountWithGoogle(requestModel: GoogleRequestModel): Completable

    fun verifyIfUserStillIn(): Single<Boolean>

    /**
     * Get UserProfile
     * @param requestModel [UserRequestModel] containing information required
     * to retrieve an user
     * @return [Completable] that notify about the task completion
     */
    fun getUser(requestModel: UserRequestModel<Unit>) : Single<UserProfile>

    /**
     * Get FriendProfile
     * @param requestModel [UserRequestModel] containing information required
     * to retrieve an user's friend
     * @return [Completable] that notify about the task completion
     */
    fun getFriend(requestModel: UserRequestModel<String>) : Single<FriendProfile>

    fun getUserHisFriends(requestModel: UserRequestModel<Unit>) : Single<List<FriendProfile>>

    fun findFriends(requestModel: UserRequestModel<String>) : Single<List<FriendProfile>>

    fun getFriendRequests(requestModel: UserRequestModel<Unit>) : Single<List<FriendRequest>>

    fun sendFriendRequest(requestModel: FriendRequestModel): Completable

    fun acceptFriendRequest(requestModel: FriendRequestModel): Completable

    fun declineFriendRequest(requestModel: FriendRequestModel): Completable

    fun updateUserAvatar(requestModel: UserRequestModel<UserProfile>) : Completable

    fun publishPendingUserChanges(user: UserProfile) : Completable
}