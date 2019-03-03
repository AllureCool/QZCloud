package com.smile.qzclould.repository

import com.smile.qzclould.repository.requestbody.*
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.ui.cloud.bean.FileBean
import com.smile.qzclould.ui.cloud.bean.OfflineDownloadResult
import com.smile.qzclould.ui.cloud.bean.ParseUrlResultBean
import com.smile.qzclould.ui.preview.pdf.PdfDetailBean
import com.smile.qzclould.ui.preview.picture.PictureBean
import com.smile.qzclould.ui.preview.player.bean.VideoDetailBean
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.ui.transfer.bean.FileDetailBean
import com.smile.qzclould.ui.transfer.bean.UploadFileResponeBean
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean
import io.reactivex.Observable
import retrofit2.http.*

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
     * 忘记密码发送验证码
     */
    @POST("/v1/user/sendChangePasswordMessage2")
    fun sendForgetPwdMessage(@Body requestBody: ForgetPwdMsgBody): Observable<Respone<String>>

    /**
     * 更改密码接口
     */
    @POST("/v1/user/changePasswordByMessage")
    fun changePasswordByMessage(@Body requestBody: ChangePwdBody): Observable<Respone<Boolean>>

    /**
     * 重置密码
     */
    @POST("/v1/user/changePasswordByMessage2")
    fun resetPwdByMessage(@Body requestBody: ChangePwdBody): Observable<Respone<Boolean>>

    /**
     * 修改用户名
     */
    @POST("/v1/user/changeName")
    fun changeUserName(@Body requestBody: ModifyNameBody): Observable<Respone<String>>

    /**
     * 新建文件夹
     */
    @POST("/v1/files/createDirectory")
    fun createDirectory(@Body requestBody: CreateDirectoryBody): Observable<Respone<Direcotory>>

    /**
     * 移动文件
     */
    @POST("/v1/files/move")
    fun moveFile(@Body requestBody: MoveFileBody): Observable<Respone<String>>

    /**
     * 复制文件
     */
    @POST("/v1/files/copy")
    fun copyFile(@Body requestBody: MoveFileBody): Observable<Respone<String>>

    /**
     * 列出文件夹
     */
    @POST("/v1/files/list")
    fun listDirectory(@Body requestBody: FileListBody): Observable<Respone<List<Direcotory>>>

    /**
     * 根据path列出文件
     */
    @POST("/v1/files/page")
    fun listFileByPath(@Body requestBody: GetDataByPathBody): Observable<Respone<FileBean>>

    /**
     * 预解析文件
     */
    @POST("/v1/offline/parseUrl")
    fun parseurl(@Body requestBody: ParseUrlBody): Observable<Respone<ParseUrlResultBean>>

    /**
     * 离线url下载
     */
    @POST("/v1/offline/start")
    fun offlineDownloadStart(@Body requestBody: OfflineDownloadBody): Observable<Respone<OfflineDownloadResult>>

    /**
     * 获取离线下载列表
     */
    @POST("/v1/offline/page")
    fun offlineDownloadList(@Body requestBody: OfflineDownloadListBody): Observable<Respone<DownloadTaskBean>>

    /**
     * 获取文件上传地址
     */
    @POST("/v1/store/token")
    fun uploadFile(@Body requestBody: UploadFileBody): Observable<Respone<UploadFileResponeBean>>

    /**
     * 获取
     */
    @POST("/v1/files/get")
    fun getFileDetail(@Body requestBody: PathBody): Observable<Respone<FileDetailBean>>

    /**
     * 删除文件夹或文件
     */
    @POST("/v1/files/remove")
    fun removeFile(@Body requestBody: PathArrayBody): Observable<Respone<String>>

    /**
     * 删除离线任务
     */
    @POST("/v1/offline/remove")
    fun removeOfflineFile(@Body requestBody: OfflinRemoveBody): Observable<Respone<String>>

    /**
     * 预览媒体文件
     */
    @POST("/v1/preview/media")
    fun getMediaInfo(@Body requestBody: PathBody): Observable<Respone<VideoDetailBean>>

    /**
     * 获取pdf信息
     */
    @POST("/v1/preview/pdf")
    fun getPdfInfo(@Body requestBody: PathBody): Observable<Respone<PdfDetailBean>>

    /**
     * 获取图片预览信息
     */
    @POST("/v1/preview/image")
    fun getPictureInfo(@Body requestBody: PathBody): Observable<Respone<PictureBean>>
}