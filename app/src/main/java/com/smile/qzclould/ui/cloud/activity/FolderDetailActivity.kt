package com.smile.qzclould.ui.cloud.activity

import androidx.navigation.Navigation
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R

class FolderDetailActivity: BaseActivity() {
    private val navController by lazy {  Navigation.findNavController(this, R.id.folderDetailFragment) }

    override fun setLayoutId(): Int {
        return R.layout.act_folder_detail
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}