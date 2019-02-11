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

package com.dbeginc.users.authentication.register

import com.dbeginc.common.view.MVPVView

/**
 * Created by darel on 23.02.18.
 *
 * Register View
 */
interface RegisterView : MVPVView {
    fun nicknameDoesNotMatchTheRequirement()

    fun emailDoesNotMatchTheRequirement()

    fun passwordDoesNotMatchTheRequirement()

    fun onCredentialsValidated(nickname: String, email: String, password: String)
}