package com.dbeginc.data.proxies.remote

import android.net.Uri
import com.dbeginc.domain.entities.data.ItemImage

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
data class RemoteItemImage(var uri: String = Uri.EMPTY.toString()) {
    /**
     * Convert Remote Image Proxy object
     * to application domain entity
     * @return {@link ItemImage }
     */
    fun toDomainImage() : ItemImage = ItemImage(uri = uri)
}