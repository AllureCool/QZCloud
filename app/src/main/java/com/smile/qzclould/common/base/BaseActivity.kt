package com.smile.qielive.common

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.gyf.barlibrary.ImmersionBar
import com.smile.qzclould.common.base.Backable
import hei.permission.PermissionActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by wangzhg on 2018/7/12
 * Describe:
 */
open abstract class BaseActivity: PermissionActivity() {
    private var mCompositeDisposable = CompositeDisposable()
    private var mImm: InputMethodManager? = null
    protected var mImmersionBar: ImmersionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setLayoutId())
        if(inImmersionBarEnabled()) {
            initImmersionBar()
        }
        //初始化数据
        initData()

        //view与数据绑定
        initView()

        //初始化viewModel
        initViewModel()

        //设置listener
        setListener()
    }

    protected abstract fun setLayoutId(): Int

    protected open fun initImmersionBar() {

    }

    protected open fun initData() {}

    protected open fun initView() {}

    protected open fun initViewModel() {}

    protected open fun setListener() {}

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected fun inImmersionBarEnabled(): Boolean {
        return true
    }

    protected fun addDispose(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    protected fun dispose() {
        if(!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }

    protected fun jumpActivity(target: Class<Any>) {
        startActivity(Intent(this, target))
    }

    fun Disposable?.autoDispose() {
        this?.let { addDispose(it) }
    }

    override fun finish() {
        super.finish()
        hideSoftKeyBoard()
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
        mImm = null
        mImmersionBar?.destroy()
    }

    fun hideSoftKeyBoard() {
        var localView: View? = currentFocus
        if(mImm == null) {
            mImm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        mImm?.hideSoftInputFromWindow(localView?.windowToken, 2)
    }

    override fun onBackPressed() {
        val fragment = getCurrentFragment()
        if (fragment != null && fragment is Backable) {
            if ((fragment as Backable).onBackPressed()) {
                // If the onBackPressed override in your fragment
                // did absorb the back event (returned true), return
                return
            }
        }
        super.onBackPressed()
    }

    fun getCurrentFragment(): Fragment? {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            val lastFragmentName = fragmentManager.getBackStackEntryAt(
                    fragmentManager.backStackEntryCount - 1).name
            return fragmentManager.findFragmentByTag(lastFragmentName)
        }
        return null
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        if(newConfig?.fontScale != 1f) {
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if(res.configuration.fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults()
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }
}