package com.smile.qzclould.utils;

import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import static com.smile.qzclould.utils.FileUtils.getMIMEType;


public class CallOtherOpenFile {

    public static void openFile(Context context, File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性   
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型   
            String type = getMIMEType(file);
            //设置intent的data和Type属性。   
            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
            //跳转   
            context.startActivity(intent);
            //      Intent.createChooser(intent, "请选择对应的软件打开该附件！");  
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "sorry附件不能打开，请下载相关软件！", Toast.LENGTH_SHORT).show();
        }
    }

    public Intent getPdfFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        return Intent.createChooser(intent, "Open File");
    }
}
