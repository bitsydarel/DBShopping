package com.dbeginc.dbshopping.di.qualifiers

import javax.inject.Qualifier

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
 */

/**
 * Local User Data Source
 */
@Qualifier annotation class LocalUserSource

/**
 * Local User Data Source
 */
@Qualifier annotation class RemoteUserSource

/**
 * UserTable
 */
@Qualifier annotation class UserTable


/**
 * Network Thread
 */
@Qualifier annotation class IOThread

/**
 * User Interface Thread
 */
@Qualifier annotation class UIThread

/**
 * Computation Thread
 */
@Qualifier annotation class ComputationThread

/**
 * Authentication error manager
 */
@Qualifier annotation class AuthenticationErrorManager

/**
 * Authentication field validator
 */
@Qualifier annotation class AuthenticationFieldValidator

/**
 * Local Data Source
 */
@Qualifier annotation class LocalDataSource

/**
 * Remote Data Source
 */
@Qualifier annotation class RemoteDataSource

/**
 * Remote List Table
 */
@Qualifier annotation class RemoteListTable

/**
 * Remote Items Table
 */
@Qualifier annotation class RemoteItemsTable

/**
 * Storage Error Manager
 */
@Qualifier annotation class StorageErrorManager

/**
 * Authentication User repository
 */
@Qualifier annotation class AuthenticationUserRepo

/**
 * Application User repository
 */
@Qualifier annotation class AppUserRepo