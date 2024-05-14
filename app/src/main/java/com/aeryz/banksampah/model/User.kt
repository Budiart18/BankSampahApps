package com.aeryz.banksampah.model

import com.google.firebase.auth.FirebaseUser

data class User(
    val fullName: String,
    val email: String,
    val photoUrl: String
)

fun FirebaseUser?.toUser(): User? = if (this != null) {
    User(
        fullName = this.displayName.orEmpty(),
        email = this.email.orEmpty(),
        photoUrl = this.photoUrl.toString()
    )
} else {
    null
}
