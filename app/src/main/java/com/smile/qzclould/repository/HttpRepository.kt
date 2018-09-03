package com.smile.qzclould.repository

import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.repository.requestbody.*
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean
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
                    val original = chain?.request()
                    val request = original?.newBuilder()
                            ?.header("User-Agent", "QZCloud")
                            ?.header("token", UserInfoManager.get().getUserToken())
                            ?.build()

                    return chain!!.proceed(request)
                }
            })
            val retrofit = Retrofit.Builder()
                    .baseUrl(HOST)
                    .addConverterFactory(GsonConverterFactory.create())
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

    fun changePasswordByMessage(phoneInfo: String, code: String, newPassword: String): Observable<Respone<Boolean>> {
        val body = ChangePwdBody(phoneInfo, code, newPassword)
        return service.changePasswordByMessage(body).doRequestAsync()
    }
}