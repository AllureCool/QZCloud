package com.smile.qzclould.ui.cloud.fragment

import android.os.Bundle
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import kotlinx.android.synthetic.main.act_view_folder.*

class FolderFragment: BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.act_view_folder
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBtnBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
    }
}