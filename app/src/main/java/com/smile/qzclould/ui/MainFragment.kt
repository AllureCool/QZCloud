package com.smile.qzclould.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.event.SwitchTabEvent
import com.smile.qzclould.ui.cloud.fragment.CloudBoardFragment
import com.smile.qzclould.ui.user.HomeFourthFragment
import com.smile.qzclould.ui.transfer.HomeSecondFragment
import com.smile.qzclould.ui.task.HomeThirdFragment
import com.smile.qzclould.ui.user.UserBoardFragment
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_main.*

class MainFragment: BaseFragment() {

    private val mFragments by lazy { mutableListOf<Fragment>() }

    override fun getLayoutId(): Int {
        return R.layout.frag_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        mFragments.add(CloudBoardFragment())
        mFragments.add(HomeSecondFragment())
        mFragments.add(HomeThirdFragment())
        mFragments.add(HomeFourthFragment())

        mVpContainer.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }
        }

        mVpContainer.offscreenPageLimit = 4
    }

    override fun initEvent() {
        RxBus.toObservable(SwitchTabEvent::class.java)
                .subscribe {
                    mVpContainer.setCurrentItem(it.index, false)
                }
                .autoDispose()
    }
}