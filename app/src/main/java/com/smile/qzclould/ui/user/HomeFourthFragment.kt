package com.smile.qzclould.ui.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.navigation.Navigation
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.user.loign.activity.LoginActivity
import com.smile.qzclould.ui.user.loign.activity.ModifyPwdActivity
import com.smile.qzclould.ui.user.loign.fragment.PwdInputFragment
import com.smile.qzclould.ui.user.loign.viewmodel.LoginViewModel
import com.smile.qzclould.utils.FileUtils
import kotlinx.android.synthetic.main.frag_home_fourth.*
import org.jetbrains.anko.doAsync
import java.io.File
import android.net.Uri
import com.smile.qzclould.BuildConfig
import com.smile.qzclould.R


class HomeFourthFragment: BaseFragment() {
    private val mModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }
    private val mUserInfo by lazy { UserInfoManager.get().getUserInfo() }

    override fun getLayoutId(): Int {
        return R.layout.frag_home_fourth
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvNick.text = mUserInfo?.nickName
        mTvPhone.text = mUserInfo?.phone
        mTvVersion.text = "v${BuildConfig.VERSION_NAME}"
    }

    override fun initListener() {
        mBtnLogout.setOnClickListener {
            showLoading()
            mModel.logout()
        }

        mBtnModifyPwd.setOnClickListener {
            showLoading()
            mModel.sendChangePasswordMessage()
        }

        mBtnPrivacyAgreement.setOnClickListener {
            jumpActivity(PrivacyAgreementActvity::class.java)
        }

        mBtnFeedback.setOnClickListener {
            joinQQGroup("5tLjB6LJfsZXB6bKdOH5ytFmPnLChR-q")
        }
    }

    override fun initViewModel() {
        mModel.logoutResult.observe(this, Observer {
            FileDownloader.getImpl().clearAllTaskData()
            val dao = App.getCloudDatabase()?.DirecotoryDao()
            doAsync {
                val downloadList = dao?.loadDirecotory()
                for (item in downloadList!!) {
                    val tempPath = FileDownloadUtils.getTempPath(FileUtils.createDir() + File.separator + item.name)
                    val file = File(tempPath)
                    file.deleteRecursively()
                }
                dao.deleteDirecotory(dao.loadDirecotory())
            }

            UserInfoManager.get().logout()
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.logout_success))
            jumpActivity(LoginActivity::class.java)
            mActivity?.finish()
        })

        mModel.verifyCodeResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, mActivity?.getString(R.string.send_success)!!)
            val bundle = Bundle()
            bundle.putString("phone_info", it)
            bundle.putString("toolbar_title", App.instance.getString(R.string.modify_pwd))
            bundle.putInt("jump_type", PwdInputFragment.TYPE_MODIFY_PWD)
            jumpActivity(ModifyPwdActivity::class.java, bundle)
        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_NORMAL, it?.errorMessage!!)
        })
    }

    /****************
     *
     * 发起添加群流程。群号：6pan安卓客户端反馈(132665926) 的 key 为： 5tLjB6LJfsZXB6bKdOH5ytFmPnLChR-q
     * 调用 joinQQGroup(5tLjB6LJfsZXB6bKdOH5ytFmPnLChR-q) 即可发起手Q客户端申请加群 6pan安卓客户端反馈(132665926)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
            return true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            return false
        }

    }

}