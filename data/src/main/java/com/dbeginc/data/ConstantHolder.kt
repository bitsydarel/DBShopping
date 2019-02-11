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

package com.dbeginc.data

import android.support.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
object ConstantHolder {
    const val USER_PENDING_TABLE = "user_pending_table"
    const val LIST_PENDING_TABLE = "list_pending_table"
    const val LIST_MEMBER_PENDING_TABLE = "list_member_pending_table"
    /**************************** USERS ****************************/
    const val REMOTE_USERS_TABLE = "users"
    const val REMOTE_USERS_PROFILE = "profile"
    const val REMOTE_USERS_LISTS = "list_ids"
    const val REMOTE_USERS_FRIENDS = "friends"
    /**************************** LIST ****************************/
    const val REMOTE_LISTS_TABLE = "lists"
    const val REMOTE_LISTS_DATA = "data"
    const val REMOTE_LISTS_MEMBERS = "members"
    /*************************************************************/
    const val REMOTE_ITEMS_TABLE = "items"
    const val REMOTE_IMAGES_TABLE = "images"
    const val REMOTE_FRIENDS_REQUEST_TABLE = "friends_request"
    /********************* PENDING REQUEST TYPE **********************/
    const val INSERT_REQUEST : Short = 0
    const val UPDATE_REQUEST : Short = 1
    const val UPDATE_IMAGE_REQUEST : Short = 2
    const val DELETE_REQUEST : Short = 3
    /*************************************************************/
    const val LOCAL_USERS_TABLE = "users"
    const val LOCAL_LISTS_TABLE = "lists"
    const val LOCAL_ITEMS_TABLE = "items"
    const val LOCAL_FRIENDS_TABLE = "friends"
    const val LOCAL_ITEMS_COMMENTS = "items_comments"
    const val LOCAL_LISTS_MEMBERS = "lists_members"
}