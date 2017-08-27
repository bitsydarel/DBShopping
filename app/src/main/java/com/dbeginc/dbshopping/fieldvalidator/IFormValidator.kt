package com.dbeginc.dbshopping.fieldvalidator

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
 * field validator
 */
interface IFormValidator {

    /**
     * Validate the nickname
     * @return true if the nickname match the requirements
     */
    fun validateNickname(nickname: String) : Boolean

    /**
     * Validate the email
     * @param email to be checked
     * @return true if the email match the requirements
     */
    fun validateEmailForm(email: String) : Boolean

    /**
     * Validate the password
     * @param password to be checked
     * @return true if the password match the requirements
     */
    fun validatePasswordForm(password: String) : Boolean

    /**
     * Validate the email and password form
     * @param emailForm to be checked
     * @param passwordForm to be checked
     * @return true if the email match the requirements
     */
    fun validateEmailAndPassword(emailForm: String, passwordForm: String) : Boolean

    /**
     * Validate the email, password and nickname
     * @param email to be checked
     * @param password to be checked
     * @param nickname to be checked
     * @return true if the email match the requirements
     */
    fun validateEmailPasswordAndNickname(email: String, password: String, nickname: String) : Boolean
}