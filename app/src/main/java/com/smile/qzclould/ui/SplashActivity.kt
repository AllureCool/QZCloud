package com.smile.qzclould.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.smile.qzclould.R
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.user.loign.activity.LoginActivity
import hei.permission.PermissionActivity

class SplashActivity : PermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission(PermissionActivity.CheckPermListener {
                if (UserInfoManager.get().hasLogin()) {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                finish()
        }, R.string.need_storage_permission,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE)

    }
}