package com.smile.qzclould.repository

import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.repository.requestbody.*
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.ui.cloud.bean.FileBean
import com.smile.qzclould.ui.cloud.bean.OfflineDownloadResult
import com.smile.qzclould.ui.cloud.bean.ParseUrlResultBean
import com.smile.qzclould.ui.preview.picture.PictureBean
import com.smile.qzclould.ui.preview.pdf.PdfDetailBean
import com.smile.qzclould.ui.preview.player.bean.VideoDetailBean
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.ui.transfer.bean.FileDetailBean
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean
import com.smile.qzclould.utils.DLog
import com.smile.qzclould.utils.doRequestAsync
import io.reactivex.Observable
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*


/**
 * Created by wangzhg on 2018/8/25
 * Describe:
 */
class HttpRepository {
    companion object {
        const val HOST = "https://api.6pan.cn"

        val service: ApiService by lazy { buildServic() }

        private fun buildServic(): ApiService {
            val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()

            okHttpClient.hostnameVerifier(object : HostnameVerifier {
                override fun verify(hostname: String?, session: SSLSession?): Boolean {
                    return true
                }
            })

            val trustAllCerts: Array<TrustManager> = arrayOf(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                        }

                        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
            )

            try {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())

                okHttpClient.sslSocketFactory(sslContext.socketFactory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            okHttpClient.addInterceptor(httpLoggingInterceptor)
            okHttpClient.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): Response {
                    DLog.i(UserInfoManager.get().getUserToken() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
                    val original = chain?.request()
                    val request = original?.newBuilder()
                            ?.header("User-Agent", "QZCloud")
                            ?.header("token", UserInfoManager.get().getUserToken())
                            ?.header("user-timestamp", System.currentTimeMillis().toString())
                            ?.build()
                    val response = chain!!.proceed(request)
//                    response?.newBuilder()
//                            ?.header("User-Agent", "QZCloud")
//                            ?.header("token", UserInfoManager.get().getUserToken())
//                            ?.header("user-timestamp", System.currentTimeMillis().toString())
//                            ?.build()
                    DLog.i("Headers:" + request?.headers()!!.toString())
                    return response
                }
            })
            val retrofit = Retrofit.Builder()
                    .baseUrl(HOST)
                    .addConverterFactory(CustomGsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient.build())
                    .build()
            return retrofit.create(ApiService::class.java)
        }
    }

    //----------------------------------------------------------API request-----------------------------------------------------------

    fun login(name: String, pwd: String): Observable<Respone<UserInfoBean>> {
        val body = LoginBody(name, pwd)
        return service.login(body).doRequestAsync()
    }

    fun sendRegisterMessage(countryCode: String, phoneNum: String): Observable<Respone<String>> {
        val body = SendVerifyCodeBody(countryCode, phoneNum)
        return service.sendRegisterMessage(body).doRequestAsync()
    }

    fun register(phoneInfo: String, code: String, name: String, password: String): Observable<Respone<UserInfoBean>> {
        val body = RegisterBody(phoneInfo, code, name, password)
        return service.register(body).doRequestAsync()
    }

    fun logout(): Observable<Respone<Boolean>> {
        val body = LogoutBody(System.currentTimeMillis())
        return service.logout(body).doRequestAsync()
    }

    fun sendChangePasswordMessage(): Observable<Respone<String>> {
        val body = ChangePwdSendMsgBody(System.currentTimeMillis())
        return service.sendChangePasswordMessage(body).doRequestAsync()
    }

    fun sendForgetPwdMessage(phone: String): Observable<Respone<String>> {
        val body = ForgetPwdMsgBody(phone)
        return service.sendForgetPwdMessage(body).doRequestAsync()
    }

    fun changePasswordByMessage(phoneInfo: String, code: String, newPassword: String): Observable<Respone<Boolean>> {
        val body = ChangePwdBody(phoneInfo, code, newPassword)
        return service.changePasswordByMessage(body).doRequestAsync()
    }

    fun resetPwdByMessage(phoneInfo: String, code: String, newPassword: String): Observable<Respone<Boolean>> {
        val body = ChangePwdBody(phoneInfo, code, newPassword)
        return service.resetPwdByMessage(body).doRequestAsync()
    }

    fun changeUserName(name: String): Observable<Respone<String>> {
        val body = ModifyNameBody(name)
        return service.changeUserName(body).doRequestAsync()
    }

    fun createDirectory(directoryName: String, parentUUid: String = ""): Observable<Respone<Direcotory>> {
        val body = CreateDirectoryBody(directoryName, parentUUid)
        return service.createDirectory(body).doRequestAsync()
    }

    fun listFile( parent: String, path: String, start: Int, size: Int, recycle: Int, mime: String, orderBy: Int, type: Int): Observable<Respone<List<Direcotory>>> {
        val body = FileListBody(parent, path, start, size, recycle, mime, orderBy, type)
        return service.listDirectory(body).doRequestAsync()
    }

    fun listFileByPath(path: String, page: Int, pageSize: Int, orderBy: Int, type: Int): Observable<Respone<FileBean>> {
        val body = GetDataByPathBody(path, page, pageSize, orderBy, type)
        return service.listFileByPath(body).doRequestAsync()
    }

    fun parseUrlS(url: String): Observable<Respone<ParseUrlResultBean>> {
        val body = ParseUrlBody(url)
        return service.parseurl(body).doRequestAsync()
    }

    fun offlineDownloadStart(taskHash: String, savePath: String, copyFile: Array<Int> = arrayOf()): Observable<Respone<OfflineDownloadResult>> {
        val body = OfflineDownloadBody(taskHash, copyFile, savePath)
        return service.offlineDownloadStart(body).doRequestAsync()
    }

    fun offlineDownloadList(page: Int, pageSize: Int, order: Int): Observable<Respone<DownloadTaskBean>> {
        val body = OfflineDownloadListBody(page, pageSize, order)
        return service.offlineDownloadList(body).doRequestAsync()
    }

    fun getFileDetail(path: String): Observable<Respone<FileDetailBean>> {
        val body = PathBody(path)
        return service.getFileDetail(body).doRequestAsync()
    }

    fun removeFile(path: List<String>): Observable<Respone<String>> {
        val body = PathArrayBody(path)
        return service.removeFile(body).doRequestAsync()
    }

    fun removeOfflineFile(taskId: String): Observable<Respone<String>> {
        val body = OfflinRemoveBody(taskId)
        return service.removeOfflineFile(body).doRequestAsync()
    }

    fun getMediaInfo(path: String): Observable<Respone<VideoDetailBean>> {
        val body = PathBody(path)
        return service.getMediaInfo(body).doRequestAsync()
    }

    fun getPdfInfo(path: String): Observable<Respone<PdfDetailBean>> {
        val body = PathBody(path)
        return service.getPdfInfo(body).doRequestAsync()
    }

    fun getPictureInfo(path: String): Observable<Respone<PictureBean>> {
        val body = PathBody(path)
        return service.getPictureInfo(body).doRequestAsync()
    }

    fun moveFile(path: List<String>, destPath: String): Observable<Respone<String>> {
        val body = MoveFileBody(path, destPath)
        return service.moveFile(body).doRequestAsync()
    }

    fun copyFile(path: List<String>, destPath: String): Observable<Respone<String>> {
        val body = MoveFileBody(path, destPath)
        return service.copyFile(body).doRequestAsync()
    }
}