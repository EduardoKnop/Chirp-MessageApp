package com.plcoding.auth.domain

object EmailValidator {
    
    private val EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    
    fun validate(email: String): Boolean {
        return EMAIL_PATTERN.matches(email)
    }
}