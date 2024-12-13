package com.softartdev.notedelight.shared.domain.usecase

import com.softartdev.notedelight.shared.domain.repository.SafeRepository

class CheckPasswordUseCase(private val safeRepository: SafeRepository) {

    operator fun invoke(password: CharSequence): Boolean {
        return try {
            safeRepository.buildDbIfNeed(password)
            true
        } catch (e: Throwable) {
            false
        }
    }
}
