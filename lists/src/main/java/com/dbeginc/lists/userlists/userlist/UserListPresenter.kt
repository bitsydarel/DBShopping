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
 * UserProfile List Presenter
 */
class UserListPresenter {

    fun findWhoIsShopping(currentUsername: String, members: List<ShoppingUserModel>, view: UserListView) {
        val numberOfPeopleShopping = members.filter { it.isShopping }.size

        val isCurrentUserShopping = members.find { it.nickname == currentUsername && it.isShopping } != null

        when(numberOfPeopleShopping) {
            0 -> view.noOneIsShopping()
            1 -> {
                if (isCurrentUserShopping) view.onlyCurrentUserShopping()
                else view.onePersonShopping(members.first { it.isShopping }.nickname)
            }
            2 -> {
                if (isCurrentUserShopping) view.currentUserAndSomeoneElseAreShopping(
                        members.first {
                            it.nickname != currentUsername && it.isShopping
                        }
                )
                else {
                    val firstShopper = members.first { it.isShopping }.nickname

                    view.twoUsersAreShopping(
                            firstShopper,
                            members.first { firstShopper != it.nickname && it.isShopping }.nickname
                    )
                }
            }
            else -> if (isCurrentUserShopping) view.currentUserAndOtherUsersAreShopping(numberOfPeopleShopping - 1) else view.manyPeopleAreShopping(numberOfPeopleShopping)
        }
    }

    fun isQueryValid(query: String) : Boolean = query.isNotBlank()

}