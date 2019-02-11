package com.dbeginc.domain.entities.request

/**
 * Created by darel on 21.08.17.
 *
 * Authentication UserProfile Request Model
 */
data class AuthRequestModel(val email: String, val password: String, val name: String)