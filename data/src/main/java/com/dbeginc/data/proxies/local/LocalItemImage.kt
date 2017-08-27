package com.dbeginc.data.proxies.local

import android.net.Uri
import com.dbeginc.domain.entities.data.ItemImage
import io.realm.RealmModel
import io.realm.annotations.RealmClass

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
 * Created by darel on 20.08.17.
 */
@RealmClass
open class LocalItemImage(var uri: String = Uri.EMPTY.toString()) : RealmModel {
    /**
     * Convert Proxy Image object
     * to application domain entity
     * @return {@link com.dbeginc.dbshopping.entities.data.Image }
     */
    fun toDomainImage() : ItemImage = ItemImage(uri = uri)
}