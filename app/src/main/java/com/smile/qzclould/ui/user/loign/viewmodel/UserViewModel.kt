package com.smile.qzclould.ui.user.loign.viewmodel

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean

class UserViewModel : BaseViewModel() {
    val repo by lazy { HttpRepository() }

    val verifyCodeResult by lazy { MediatorLiveData<String>() }

    val loginResult by lazy { MediatorLiveData<UserInfoBean>() }

    val logoutResult by lazy { MediatorLiveData<Boolean>() }

    val modifyPwdResult by lazy { MediatorLiveData<Boolean>() }

    val modifyNameResult by lazy { MediatorLiveData<String>() }

    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    val logoutError by lazy { MediatorLiveData<ErrorStatus>() }

    val loginMsgRsp by lazy { MediatorLiveData<String>() }


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

    fun loginByMessage(phoneInfo: String, code: String) {
        repo.loginByMessage(phoneInfo, code)
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
                        logoutError.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    logoutError.value = ErrorStatus(100, it.message)
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

    fun sendForgetPwdMessage(phone: String) {
        repo.sendForgetPwdMessage(phone)
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

    fun resetPwdByMessage(phoneInfo: String, code: String, newPassword: String) {
        repo.resetPwdByMessage(phoneInfo, code, newPassword)
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

    fun modifyName(nickName: String) {
        repo.changeUserName(nickName)
                .subscribe({
                    if(it.success) {
                        modifyNameResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    //----------------------v2接口----------------------------
    fun sendLoginMessage(countryCode: String, phone: String) {
        repo.sendLoginMessage(countryCode, phone)
                .subscribe({
                    if(it.success) {
                        loginMsgRsp.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }
}
