package com.smile.qzclould.repository

import com.smile.qzclould.repository.requestbody.*
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by wangzhg on 2018/8/25
 * Describe:
 */
interface ApiService {
    /**
     * 登录
     */
    @POST("/v1/user/login")
    fun login(@Body loginBody: LoginBody): Observable<Respone<UserInfoBean>>

    /**
     * 发送验证码
     */
    @POST("/v1/user/sendRegisterMessage")
    fun sendRegisterMessage(@Body requestBody: SendVerifyCodeBody): Observable<Respone<String>>

    /**
     * 用户注册接口
     */
    @POST("v1/user/register")
    fun register(@Body requestBody: RegisterBody): Observable<Respone<UserInfoBean>>

    /**
     * 注销登录
     */
    @POST("/v1/user/logout")
    fun logout(@Body logoutBody: LogoutBody): Observable<Respone<Boolean>>

    /**
     * 更改密码发送验证码
     */
    @POST("/v1/user/sendChangePasswordMessage")
    fun sendChangePasswordMessage(@Body requestBody: ChangePwdSendMsgBody): Observable<Respone<String>>

    /**
     * 更改密码接口
     */
    @POST("/v1/user/changePasswordByMessage")
    fun changePasswordByMessage(@Body requestBody: ChangePwdBody): Observable<Respone<Boolean>>
}