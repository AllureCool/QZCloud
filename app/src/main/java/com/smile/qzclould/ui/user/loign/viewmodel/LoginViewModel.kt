package com.smile.qzclould.ui.user.loign.viewmodel

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean

class LoginViewModel : BaseViewModel() {
    val repo by lazy { HttpRepository() }

    val verifyCodeResult by lazy { MediatorLiveData<String>() }

    val loginResult by lazy { MediatorLiveData<UserInfoBean>() }

    val logoutResult by lazy { MediatorLiveData<Boolean>() }

    val modifyPwdResult by lazy { MediatorLiveData<Boolean>() }

    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }


    fun login(name: String, pwd: String) {
        repo.login(name, pwd)
                .subscribe({
                    if (it.success) {
                        UserInfoManager.get().saveUserToken(it.token)
                        loginResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun sendRegisterMessage(countryCode: String, phoneNum: String) {
        repo.sendRegisterMessage(countryCode, phoneNum)
                .subscribe({
                    if (it.success) {
                        UserInfoManager.get().saveUserToken(it.token)
                        verifyCodeResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun register(phoneInfo: String, code: String, name: String, password: String) {
        repo.register(phoneInfo, code, name, password)
                .subscribe({
                    if (it.success) {
                        UserInfoManager.get().saveUserToken(it.token)
                        loginResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun logout() {
        repo.logout()
                .subscribe({
                    if(it.success) {
                        logoutResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun sendChangePasswordMessage() {
        repo.sendChangePasswordMessage()
                .subscribe({
                    if (it.success) {
                        UserInfoManager.get().saveUserToken(it.token)
                        verifyCodeResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun changePasswordByMessage(phoneInfo: String, code: String, newPassword: String) {
        repo.changePasswordByMessage(phoneInfo, code, newPassword)
                .subscribe({
                    if(it.success) {
                        modifyPwdResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }
}
