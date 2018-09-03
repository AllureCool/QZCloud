package com.smile.qzclould.utils

import android.support.annotation.IdRes
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by wangzhg on 2018/7/26
 * Describe:
 */

inline fun <T> Observable<T>.doRequestAsync(): Observable<T> {
    return this.let { it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()) }
}

inline fun NavController?.navgateById(@IdRes resId: Int) {
    return this.let{
        it?.navigateUp()
        it?.navigate(resId)
    }
}

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
