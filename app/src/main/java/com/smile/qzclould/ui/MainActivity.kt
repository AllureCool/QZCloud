package com.smile.qzclould.ui

import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.widget.Toast
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.event.SwitchTabEvent
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.act_main.*

class MainActivity : BaseActivity() {

    private val navController by lazy {  Navigation.findNavController(this, R.id.my_nav_host_fragment) }
    private var isQuit = false
    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            isQuit = false
        }
    }


    override fun setLayoutId(): Int {
        return R.layout.act_main
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun initView() {
        mBottomBar.setOnTabSelectListener {
            when {
                it == R.id.tab_cloud -> RxBus.post(SwitchTabEvent(0))
                it == R.id.tab_chuanshu -> RxBus.post(SwitchTabEvent(1))
                it == R.id.tab_clock -> RxBus.post(SwitchTabEvent(2))
                it == R.id.tab_user -> RxBus.post(SwitchTabEvent(3))
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isQuit) {
                isQuit = true
                Toast.makeText(applicationContext, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show()
                // 利用handler延迟发送更改状态信息
                mHandler.sendEmptyMessageDelayed(0, 2000)
            } else {
                finish()
                System.exit(0)
            }
        }
        return false
    }

}
