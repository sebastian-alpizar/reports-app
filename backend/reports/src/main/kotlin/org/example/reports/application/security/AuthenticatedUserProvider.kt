package org.example.reports.application.security

interface AuthenticatedUserProvider {
    fun getCurrentUserEmail(): String
}