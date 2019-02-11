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

package com.dbeginc.users.authentication.validator

import android.support.v4.util.PatternsCompat

/**
 * Created by darel on 22.02.18.
 *
 * Authentication field validator
 */
class AuthFormValidator {
    fun verifyEmailValid(email: String) : Boolean =
            if (email.isEmpty()) false else PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()

    fun verifyPasswordValid(password: String) : Boolean {
        return when {
            password.isEmpty() -> false
            password.length <= 6 -> return false
            else -> true
        }
    }

    fun verifyNickname(nickname: String) : Boolean {
        return when {
            nickname.isEmpty() -> return false
            nickname.length <= 2 -> return false
            else -> nickname.last() != ' '
        }
    }
}