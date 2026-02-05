package com.softartdev.notedelight.usecase.crypt

import com.softartdev.notedelight.repository.SafeRepo


class ChangePasswordUseCase(
    private val safeRepo: SafeRepo
): suspend (CharSequence?, CharSequence?) -> Unit {

    override suspend fun invoke(oldPass: CharSequence?, newPass: CharSequence?) {
        if (!oldPass.isNullOrEmpty()) {
            if (newPass.isNullOrEmpty()) {
                safeRepo.decrypt(oldPass)
            } else {
                safeRepo.rekey(oldPass, newPass)
            }
        } else {
            requireNotNull(newPass)
            safeRepo.encrypt(newPass)
        }
        safeRepo.relaunchListFlowCallback?.invoke()
    }
}
