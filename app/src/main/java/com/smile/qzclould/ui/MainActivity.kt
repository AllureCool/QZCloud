package com.smile.qzclould.ui

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.widget.Toast
import com.imnjh.imagepicker.activity.PhotoPickerActivity
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.event.BackPressEvent
import com.smile.qzclould.event.UploadFileEvent
import com.smile.qzclould.ui.cloud.fragment.CloudBoardFragment
import com.smile.qzclould.ui.task.HomeThirdFragment
import com.smile.qzclould.ui.transfer.fragment.HomeTransferFragment
import com.smile.qzclould.ui.user.HomeFourthFragment
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.act_main.*

class MainActivity : BaseActivity() {

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
            if (!Constants.pathList.isEmpty()) {
                RxBus.post(BackPressEvent())
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
        } else {
            System.exit(0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === Activity.RESULT_OK && requestCode === 100) {
            val pathList = data?.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT_SELECTION)
            val original = data?.getBooleanExtra(PhotoPickerActivity.EXTRA_RESULT_ORIGINAL, false)
            if( pathList!= null) {
                RxBus.post(UploadFileEvent(pathList))
            }
        }
    }

}
