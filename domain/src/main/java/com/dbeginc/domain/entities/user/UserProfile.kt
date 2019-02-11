package com.dbeginc.domain.entities.user

/**
 * Created by darel on 20.08.17.
 *
 * Representation of a user
 */
data class UserProfile(val uniqueId: String,
                       var nickname: String,
                       var email: String,
                       var avatar: String,
                       val creationDate: Long,
                       val birthday: String?
)