package com.smile.qzclould.ui.user.loign.dialog

import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smile.qzclould.R
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.event.SelectCountryCodeEvent
import com.smile.qzclould.ui.user.loign.adapter.CountryCodeAdapter
import com.smile.qzclould.ui.user.loign.bean.CountryCodeBean
import com.smile.qzclould.utils.ConverUtils
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.dialog_select_country_code.*

class SelectCountryCodeDialog: BaseDialogFragment() {

    private var mCountryCodes: List<CountryCodeBean>? = null
    private val mAdapter by lazy { CountryCodeAdapter() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private var mSelectCountryCode: String? = null
    private var mSelectIndex = 0

    override fun setLayoutId(): Int {
        return R.layout.dialog_select_country_code
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.BOTTOM)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(mWidth, mHeight / 2)
    }

    override fun initData() {
        val turnsType = object : TypeToken<List<CountryCodeBean>>() {}.type
        mCountryCodes = Gson().fromJson<List<CountryCodeBean>>(ConverUtils.toString(mActivity.assets.open("country_code.json")), turnsType)
        mSelectCountryCode = arguments?.getString("country_code")
        for(index in mCountryCodes!!.indices) {
            if(mSelectCountryCode!!.equals(mCountryCodes!![index].countryCode)) {
                mSelectIndex = index
                break
            }
        }
    }

    override fun initView() {
        mBtnClose.setOnClickListener { dismiss() }
        mRvCode.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvCode)
        mAdapter.mSelectPos = mSelectIndex
        mAdapter.setNewData(mCountryCodes)
        mAdapter.setOnItemClickListener { adapter, _, position ->
            RxBus.post(SelectCountryCodeEvent(adapter.getItem(position) as CountryCodeBean))
            dismiss()
        }
    }
}