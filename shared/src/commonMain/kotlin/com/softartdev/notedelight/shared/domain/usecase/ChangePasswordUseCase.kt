package com.softartdev.notedelight.shared.domain.usecase

import com.softartdev.notedelight.shared.domain.repository.SafeRepository

class ChangePasswordUseCase(private val safeRepository: SafeRepository) {

    operator fun invoke(oldPassword: CharSequence?, newPassword: CharSequence) {
        if (oldPassword == null) {
            safeRepository.encrypt(newPassword)
        } else {
            safeRepository.rekey(oldPassword, newPassword)
        }
    }
}
