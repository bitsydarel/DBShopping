package com.dbeginc.dbshopping.fieldvalidator.implementation

import android.support.v4.util.PatternsCompat
import com.dbeginc.dbshopping.fieldvalidator.IFormValidator

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 21.08.17.
 *
 * Authentication form validator
 */
class AuthFormValidator : IFormValidator {

    override fun validateNickname(nickname: String): Boolean {
        if (nickname.isEmpty()) return false
        if (nickname.length <= 2) return false
        return nickname.last() != ' '
    }

    override fun validateEmailForm(email: String) : Boolean {
        if (email.isEmpty()) return false
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun validatePasswordForm(password: String) : Boolean {
        if (password.isEmpty()) return false
        if (password.length <= 6) return false
        return true
    }

    override fun validateEmailAndPassword(emailForm: String, passwordForm: String) : Boolean =
            validateEmailForm(emailForm) && validatePasswordForm(passwordForm)

    override fun validateEmailPasswordAndNickname(email: String, password: String, nickname: String) : Boolean =
            validateEmailForm(email) && validatePasswordForm(password) && validateNickname(nickname)

}