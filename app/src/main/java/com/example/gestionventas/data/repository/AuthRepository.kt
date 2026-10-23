package com.example.gestionventas.data.repository

import com.example.gestionventas.model.Credential

class AuthRepository(
    private val validCredential: Credential = Credential.Admin
) {
    fun login(username: String, password: String): Boolean {
        return username == validCredential.username && password == validCredential.password
    }
}