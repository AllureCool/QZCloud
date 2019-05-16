package com.smile.qielive.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.NavHostFragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.common.base.Backable
import com.smile.qzclould.ui.MainActivity
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by wangzhg on 2018/7/13
 * Describe:
 */
abstract class BaseFragment: Fragment(), Backable {
    private var mCompositeDisposable = CompositeDisposable()
    private val mLoadingDialog by lazy { KProgressHUD.create(mActivity) }
    protected var mActivity: Activity? = null

    /**
     * 是否对用户可见
     */
    protected var mIsVisible: Boolean = false
    /**
     * 是否加载完成
     * 当执行完onViewCreated方法后即为true
     */
    protected var mIsPrepare: Boolean = false

    private var mRootView: View? = null
    private var mRootViewTemp: View? = null

    protected open fun initData() {

    }

    protected abstract fun initView(savedInstanceState: Bundle?)

    protected open fun initListener() {

    }

    protected open fun initViewModel() {

    }

    protected open fun initEvent() {

    }

    protected abstract fun getLayoutId(): Int

    //获取宿主Activity
    protected fun getHoldingActivity(): Activity? {
        return mActivity
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity) {
            mActivity = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(mRootView == null) {
            val view = inflater.inflate(getLayoutId(), container, false)
            mRootView = view
        } else {
            val parent = mRootView!!.parent
            if(parent != null) {
                (parent as ViewGroup).removeView(mRootView)
            }
        }
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(mRootView != mRootViewTemp) {
            initData()
            initView(savedInstanceState)
            initListener()
            initViewModel()
            initEvent()
            if(isLazyLoad()) {
                mIsPrepare = true
            }
            mRootViewTemp = mRootView
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsVisible = isVisibleToUser
        if(isVisibleToUser) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    /**
     * 是否懒加载
     *
     * @return the boolean
     */
    protected open fun isLazyLoad(): Boolean {
        return true
    }

    /**
     * 用户可见时执行的操作
     */
    protected open fun onVisible() {
        if (mIsVisible && mIsPrepare) {
            mIsPrepare = false
        }
    }

    /**
     * 用户不可见执行
     */
    protected open fun onInvisible() {

    }

    /**
     * 展示loading
     */
    protected fun showLoading() {
        mLoadingDialog.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.2f)
        mLoadingDialog.show()
    }

    /**
     * 隐藏loading
     */
    protected fun stopLoading() {
        mLoadingDialog.dismiss()
    }

    /**
     * toast提示
     */
    protected fun showToast(status: Int, msg: String = "") {
        when {
            status == Constants.TOAST_SUCCESS -> Toasty.success(App.instance, msg).show()
            status == Constants.TOAST_ERROR -> Toasty.error(App.instance, msg).show()
            status == Constants.TOAST_NORMAL -> Toasty.normal(App.instance, msg).show()
        }
    }

    protected fun jumpActivity(target: Class<*>) {
        startActivity(Intent(mActivity, target))
    }

    protected fun jumpActivity(target: Class<*>, bundle: Bundle) {
        val intent = Intent(mActivity, target)
        intent.putExtra("bundle_extra", bundle)
        startActivity(intent)
    }

    private fun onLazyLoad() {

    }

    open fun getTitle(): String {
        return ""
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    protected fun addDispose(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    protected fun dispose() {
        if(!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }


    fun Disposable?.autoDispose() {
        this?.let { addDispose(it) }
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }
}