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

package com.dbeginc.data.proxies.remote.list

import android.support.annotation.Keep

/**
 * Created by darel on 27.02.18.
 *
 * Remote Shopping UserProfile Object
 *
 * All properties are mutable
 * and with default value because firebase need no arguments constructor
 */
@Keep
data class RemoteShoppingUser(var uniqueId: String = "",
                              var email: String = "",
                              var nickname: String = "",
                              var avatar: String = "",
                              var isShopping: Boolean = false
)