package com.smile.qzclould.repository

import com.smile.qzclould.repository.requestbody.*
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.ui.cloud.bean.*
import com.smile.qzclould.ui.preview.pdf.PdfDetailBean
import com.smile.qzclould.ui.preview.picture.PictureBean
import com.smile.qzclould.ui.preview.picture.PictureBeanV2
import com.smile.qzclould.ui.preview.player.bean.VideoDetailBean
import com.smile.qzclould.ui.preview.video.VideoBeanV2
import com.smile.qzclould.ui.transfer.bean.*
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean
import com.smile.qzclould.ui.user.loign.bean.UserOnlineBean
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by wangzhg on 2018/8/25
 * Describe:
 */
interface ApiService {

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
     * 获取pdf信息
     */
    @POST("/v1/preview/pdf")
    fun getPdfInfo(@Body requestBody: PathBody): Observable<Respone<PdfDetailBean>>

    //------------------------------v2接口------------------------------------------
    /**
     * 登录发送验证码v2
     */
    @POST("/v2/user/sendLoginMessage")
    fun sendLoginMessage(@Body requestBody: SendLoginMsgBody): Observable<Respone<String>>

    /**
     * 发送验证码v2
     */
    @POST("/v2/user/sendRegisterMessage")
    fun sendRegisterMessageV2(@Body requestBody: SendVerifyCodeBody): Observable<Respone<String>>

    /**
     * 用户注册接口v2
     */
    @POST("v2/user/register")
    fun registerV2(@Body requestBody: RegisterBodyV2): Observable<Respone<UserInfoBean>>

    /**
     * 用户手机验证码登录接口v2
     */
    @POST("v2/user/loginWithMessage")
    fun loginByMessageV2(@Body loginBody: LoginByMessageBody): Observable<Respone<UserInfoBean>>

    /**
     * 登录v2
     */
    @POST("/v2/user/login")
    fun loginV2(@Body loginBody: LoginBody): Observable<Respone<UserInfoBean>>

    /**
     * 注销登录v2
     */
    @POST("/v2/user/logout")
    fun logoutV2(@Body logoutBody: LogoutBody): Observable<Respone<Boolean>>

    /**
     * 根据path列出文件v2
     */
    @POST("/v2/files/page")
    fun listFileByPathV2(@Body requestBody: GetDataByPathBody): Observable<Respone<FileBean>>

    /**
     * 新建文件夹v2
     */
    @POST("/v2/files/createDirectory")
    fun createDirectoryV2(@Body requestBody: CreateDirectoryBody): Observable<Respone<Direcotory>>

    /**
     * 文件上传v2
     */
    @POST("/v2/upload/token")
    fun uploadFileV2(@Body requestBody: UploadFileBodyV2): Observable<Respone<UploadFileResponeBeanV2>>

    /**
     * 获取图片预览信息v2
     */
    @POST("/v2/preview/image")
    fun getPictureInfoV2(@Body requestBody: PathBody): Observable<Respone<PictureBeanV2>>

    /**
     * 预览视频文件v2
     */
    @POST("/v2/preview/video")
    fun getVideoInfoV2(@Body requestBody: PathBody): Observable<Respone<VideoBeanV2>>

    /**
     * 预览音频文件v2
     */
    @POST("/v2/preview/audio")
    fun getAudioInfoV2(@Body requestBody: PathBody): Observable<Respone<VideoBeanV2>>

    /**
     * 获取文件详细信息V2
     */
    @POST("/v2/files/get")
    fun getFileDetailV2(@Body requestBody: PathBody): Observable<Respone<FileDetailBean>>

    /**
     * 查询用户多端登录接口v2
     */
    @POST("/v2/user/online")
    fun getOnlinInfoV2(): Observable<Respone<UserOnlineBean>>

    /**
     * 踢掉用户v2
     */
    @POST("/v2/user/logoutOther")
    fun logoutOther(@Body requestBody: LogoutOtherBody): Observable<Respone<Boolean>>

    /**
     * 移动文件v2
     */
    @POST("/v2/files/move")
    fun moveFileV2(@Body requestBody: MoveFileBodyV2): Observable<Respone<String>>

    /**
     * 复制文件V2
     */
    @POST("/v2/files/copy")
    fun copyFileV2(@Body requestBody: MoveFileBodyV2): Observable<Respone<String>>

    /**
     * 删除文件夹或文件v2
     */
    @POST("/v2/files/delete")
    fun removeFileV2(@Body requestBody: PathArrayBodyV2): Observable<Respone<String>>

    /**
     * 修改用户名V2
     */
    @POST("/v2/user/changeName")
    fun changeUserNameV2(@Body requestBody: ModifyNameBody): Observable<Respone<String>>

    /**
     * 预解析文件V2
     */
    @POST("/v2/offline/parseUrl")
    fun parseurlV2(@Body requestBody: ParseUrlBody): Observable<Respone<List<OfflineParseBean>>>

    /**
     * 离线下载任务添加V2
     */
    @POST("/v2/offline/add")
    fun startOfflineDownloadV2(@Body requestBody: OfflineAddBody): Observable<Respone<OfflineAddResultBean>>

    /**
     * 获取离线下载列表
     */
    @POST("/v2/offline/page")
    fun offlineDownloadListV2(@Body requestBody: OfflineDownloadListBodyV2): Observable<Respone<OfflineListBean>>
}

