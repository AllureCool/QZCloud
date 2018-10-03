package com.smile.qzclould.ui

import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.widget.Toast
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.ui.cloud.fragment.CloudBoardFragment
import com.smile.qzclould.ui.cloud.fragment.HomeFirstFragment
import com.smile.qzclould.ui.task.HomeThirdFragment
import com.smile.qzclould.ui.transfer.fragment.HomeTransferFragment
import com.smile.qzclould.ui.user.HomeFourthFragment
import kotlinx.android.synthetic.main.act_main.*
import me.jessyan.autosize.internal.CancelAdapt

class MainActivity : BaseActivity(), CancelAdapt {

    private var isQuit = false
    private val mFragments by lazy { mutableListOf<Fragment>() }
    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            isQuit = false
        }
    }


    override fun setLayoutId(): Int {
        return R.layout.act_main
    }


    override fun initView() {
        mFragments.add(CloudBoardFragment())
        mFragments.add(HomeTransferFragment())
        mFragments.add(HomeThirdFragment())
        mFragments.add(HomeFourthFragment())

        mVpContainer.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }
        }

        mVpContainer.offscreenPageLimit = 4
        mBottomBar.setOnTabSelectListener {
            when {
                it == R.id.tab_cloud -> mVpContainer.setCurrentItem(0, false)
                it == R.id.tab_chuanshu -> mVpContainer.setCurrentItem(1, false)
                it == R.id.tab_clock -> mVpContainer.setCurrentItem(2, false)
                it == R.id.tab_user -> mVpContainer.setCurrentItem(3, false)
            }
        }
    }

    override fun onBackPressed() {
        if (mFragments[mVpContainer.currentItem] is CloudBoardFragment) {
            val fragment = mFragments[mVpContainer.currentItem] as CloudBoardFragment

            if (fragment.onBackPressed()) {
                return
            }
        }
        if (!isQuit) {
            isQuit = true
            Toast.makeText(applicationContext, "再按一次退出程序",
                    Toast.LENGTH_SHORT).show()
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000)
            return
        }
        super.onBackPressed()
    }

}
