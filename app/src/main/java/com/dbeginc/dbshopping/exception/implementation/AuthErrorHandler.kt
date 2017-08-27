package com.dbeginc.dbshopping.exception.implementation

import android.content.res.Resources
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.exception.IErrorManager
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

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
 */
class AuthErrorHandler(private val resource: Resources) : IErrorManager {

    override fun translateError(error: Throwable): String = when(error) {
        is FirebaseAuthUserCollisionException -> resource.getString(R.string.emailAlreadyTaken)
        is FirebaseAuthWeakPasswordException -> resource.getString(R.string.passwordWeak)
        is FirebaseAuthInvalidUserException -> resource.getString(R.string.userDoesNotExist)
        is FirebaseAuthInvalidCredentialsException -> resource.getString(R.string.passwordInvalid)
        else -> error.localizedMessage
    }
}