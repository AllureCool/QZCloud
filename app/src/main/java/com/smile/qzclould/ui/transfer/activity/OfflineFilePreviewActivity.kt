package com.smile.qzclould.ui.transfer.activity

import androidx.navigation.Navigation.findNavController
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R

class OfflineFilePreviewActivity: BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.activity_offline_file_preview
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.fragment).navigateUp()
    }
}