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

package com.dbeginc.users.viewmodels

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by darel on 22.02.18.
 *
 * UserProfile Model
 */
data class UserProfileModel(val uniqueId: String,
                            var nickname: String,
                            var email: String,
                            var avatar: String,
                            val creationDate: Long,
                            val birthday: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uniqueId)
        parcel.writeString(nickname)
        parcel.writeString(email)
        parcel.writeString(avatar)
        parcel.writeLong(creationDate)
        parcel.writeString(birthday)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<UserProfileModel> {
        override fun createFromParcel(parcel: Parcel): UserProfileModel = UserProfileModel(parcel)

        override fun newArray(size: Int): Array<UserProfileModel?> = arrayOfNulls(size)
    }
}