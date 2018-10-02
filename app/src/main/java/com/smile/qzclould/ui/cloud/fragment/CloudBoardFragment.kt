package com.smile.qzclould.ui.cloud.fragment

import android.os.Bundle
import com.smile.qielive.common.BaseFragment

import com.smile.qzclould.R


class CloudBoardFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.board_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
