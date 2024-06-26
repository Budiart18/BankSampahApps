package com.aeryz.banksampah.data.network.firebase.auth

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface FirebaseAuthDataSource {

    fun getCurrentUser(): FirebaseUser?
    fun isLoggedIn(): Boolean
    fun doLogout(): Boolean
    fun sendChangePasswordRequestByEmail(): Boolean
    suspend fun updateEmail(newEmail: String): Boolean
    suspend fun updatePassword(newPassword: String): Boolean
    suspend fun updateProfile(
        fullName: String? = null,
        photoUri: Uri? = null
    ): Boolean

    @Throws(exceptionClasses = [Exception::class])
    suspend fun doRegister(fullName: String, email: String, password: String): Boolean

    @Throws(exceptionClasses = [Exception::class])
    suspend fun doLogin(email: String, password: String): Boolean
}

class FirebaseAuthDataSourceImpl(private val firebaseAuth: FirebaseAuth) : FirebaseAuthDataSource {
    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun doLogout(): Boolean {
        Firebase.auth.signOut()
        return true
    }

    override fun sendChangePasswordRequestByEmail(): Boolean {
        getCurrentUser()?.email?.let { firebaseAuth.sendPasswordResetEmail(it) }
        return true
    }

    override suspend fun updateEmail(newEmail: String): Boolean {
        getCurrentUser()?.updateEmail(newEmail)?.await()
        return true
    }

    override suspend fun updatePassword(newPassword: String): Boolean {
        getCurrentUser()?.updatePassword(newPassword)?.await()
        return true
    }

    override suspend fun updateProfile(fullName: String?, photoUri: Uri?): Boolean {
        getCurrentUser()?.updateProfile(
            userProfileChangeRequest {
                fullName?.let { displayName = fullName }
                photoUri?.let { this.photoUri = it }
            }
        )?.await()
        return true
    }

    @Throws(exceptionClasses = [Exception::class])
    override suspend fun doRegister(fullName: String, email: String, password: String): Boolean {
        val registerResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        registerResult.user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(fullName).build()
        )?.await()
        return registerResult.user != null
    }

    @Throws(exceptionClasses = [Exception::class])
    override suspend fun doLogin(email: String, password: String): Boolean {
        val loginResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return loginResult.user != null
    }
}
