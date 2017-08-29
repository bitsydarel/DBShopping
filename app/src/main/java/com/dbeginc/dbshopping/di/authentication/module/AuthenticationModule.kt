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

package com.dbeginc.dbshopping.di.authentication.module

import android.content.res.Resources
import com.dbeginc.dbshopping.di.qualifiers.AuthenticationErrorManager
import com.dbeginc.dbshopping.di.qualifiers.AuthenticationFieldValidator
import com.dbeginc.dbshopping.di.scopes.AuthenticationScope
import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.exception.implementation.AuthErrorHandler
import com.dbeginc.dbshopping.fieldvalidator.IFormValidator
import com.dbeginc.dbshopping.fieldvalidator.implementation.AuthFormValidator
import dagger.Module
import dagger.Provides

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
@Module
class AuthenticationModule {

    @Provides
    @AuthenticationScope
    @AuthenticationErrorManager
    internal fun provideAuthenticationErrorHandler(resources: Resources) : IErrorManager = AuthErrorHandler(resources)

    @Provides
    @AuthenticationScope
    @AuthenticationFieldValidator
    internal fun provideAuthenticationFieldValidator() : IFormValidator = AuthFormValidator()
}