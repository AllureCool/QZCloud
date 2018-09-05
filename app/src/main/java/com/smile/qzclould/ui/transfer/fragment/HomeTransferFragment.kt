package com.smile.qzclould.ui.transfer.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import kotlinx.android.synthetic.main.frag_home_transfer.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView

class HomeTransferFragment: BaseFragment() {

    private val mIdcTitle = listOf(App.instance.getString(R.string.download_list), App.instance.getString(R.string.offline_download))
    private val mFragments = mutableListOf<Fragment>()

    override fun getLayoutId(): Int {
        return R.layout.frag_home_transfer
    }

    override fun initData() {
        mFragments.add(TransferFragment())
        mFragments.add(TransferFragment())
    }

    override fun initView(savedInstanceState: Bundle?) {
        mVpTransfer.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }
        }

        mVpTransfer.offscreenPageLimit = 2
        initIndicator()
    }

    private fun initIndicator() {
        val commonNavigator = CommonNavigator(mActivity)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mFragments.size
            }

            override fun getTitleView(context: Context, i: Int): IPagerTitleView {
                val badgePagerTitleView = BadgePagerTitleView(context)
                val simplePagerTitleView = SimplePagerTitleView(context)
                simplePagerTitleView.normalColor = mActivity?.resources!!.getColor(R.color.color_black_5A5A5A)
                simplePagerTitleView.selectedColor = mActivity?.resources!!.getColor(R.color.color_green_2EC17C)
                simplePagerTitleView.text = mIdcTitle[i]
                simplePagerTitleView.textSize = 15f
                simplePagerTitleView.setOnClickListener {
                    mVpTransfer.setCurrentItem(i, false)
                }
                badgePagerTitleView.innerPagerTitleView = simplePagerTitleView
                return badgePagerTitleView
            }

            override fun getTitleWeight(context: Context?, index: Int): Float {
                return if (mIdcTitle.size <= 4) super.getTitleWeight(context, index) else if (mIdcTitle[index].length > 2) 1.6f else 1f
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val linePagerIndicator = LinePagerIndicator(context)
                linePagerIndicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                linePagerIndicator.startInterpolator = AccelerateInterpolator()
                linePagerIndicator.endInterpolator = DecelerateInterpolator(2.0f)
                linePagerIndicator.setColors(mActivity?.resources!!.getColor(R.color.color_green_2EC17C))
                return linePagerIndicator
            }
        }
        commonNavigator.isAdjustMode = true
        mIdcTransfer.navigator = commonNavigator
        ViewPagerHelper.bind(mIdcTransfer, mVpTransfer)
    }
}