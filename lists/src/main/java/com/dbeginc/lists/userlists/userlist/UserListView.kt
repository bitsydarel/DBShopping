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

package com.dbeginc.lists.userlists.userlist

import com.dbeginc.lists.viewmodels.ShoppingUserModel

/**
 * Created by darel on 25.02.18.
 *
 * UserProfile List View
 */
interface UserListView {
    fun noOneIsShopping()

    fun onlyCurrentUserShopping()

    fun onePersonShopping(userShopping: String)

    fun currentUserAndSomeoneElseAreShopping(otherOne: ShoppingUserModel)

    fun twoUsersAreShopping(firstUser: String, secondUser: String)

    fun manyPeopleAreShopping(numberOfPeopleShopping: Int)

    fun currentUserAndOtherUsersAreShopping(numberOfOtherUserShopping: Int)
}