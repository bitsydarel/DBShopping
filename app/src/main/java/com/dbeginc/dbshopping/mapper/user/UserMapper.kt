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

package com.dbeginc.dbshopping.mapper.user

import com.dbeginc.dbshopping.viewmodels.UserModel
import com.dbeginc.domain.entities.user.User

/**
 * Created by darel on 29.08.17.
 *
 * User Mapper file
 */
fun UserModel.toUser() : User = User(id, name, email, joinedAt)

fun User.toUserModel() : UserModel = UserModel(uuid, name, email, joinedAt)