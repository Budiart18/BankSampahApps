package com.aeryz.banksampah.data.repository

import android.net.Uri
import com.aeryz.banksampah.data.network.firebase.auth.FirebaseAuthDataSource
import com.aeryz.banksampah.model.User
import com.aeryz.banksampah.model.toUser
import com.aeryz.banksampah.utils.ResultWrapper
import com.aeryz.banksampah.utils.proceedFlow
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    fun doLogout(): Boolean
    fun sendChangePasswordRequestByEmail(): Boolean
    suspend fun updateEmail(newEmail: String): Flow<ResultWrapper<Boolean>>
    suspend fun updatePassword(newPassword: String): Flow<ResultWrapper<Boolean>>
    suspend fun updateProfile(
        fullName: String? = null,
        photoUri: Uri? = null
    ): Flow<ResultWrapper<Boolean>>
    suspend fun doRegister(fullName: String, email: String, password: String): Flow<ResultWrapper<Boolean>>
    suspend fun doLogin(email: String, password: String): Flow<ResultWrapper<Boolean>>
}

class UserRepositoryImpl(private val dataSource: FirebaseAuthDataSource) : UserRepository {

    override fun getCurrentUser(): User? {
        return dataSource.getCurrentUser().toUser()
    }

    override fun isLoggedIn(): Boolean {
        return dataSource.isLoggedIn()
    }

    override fun doLogout(): Boolean {
        return dataSource.doLogout()
    }

    override fun sendChangePasswordRequestByEmail(): Boolean {
        return dataSource.sendChangePasswordRequestByEmail()
    }

    override suspend fun updateEmail(newEmail: String): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.updateEmail(newEmail) }
    }

    override suspend fun updatePassword(newPassword: String): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.updatePassword(newPassword) }
    }

    override suspend fun updateProfile(
        fullName: String?,
        photoUri: Uri?
    ): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.updateProfile(fullName, photoUri) }
    }

    override suspend fun doRegister(
        fullName: String,
        email: String,
        password: String
    ): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.doRegister(fullName, email, password) }
    }

    override suspend fun doLogin(email: String, password: String): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.doLogin(email, password) }
    }
}
