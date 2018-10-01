package com.smile.qzclould.ui.transfer.callback

import android.support.v7.util.DiffUtil
import com.smile.qzclould.db.Direcotory

class AdapterDiffCallback: DiffUtil.Callback {

    private lateinit var mOldList: List<Direcotory>
    private lateinit var mNewList: List<Direcotory>

    constructor(oldList: List<Direcotory>, newList: List<Direcotory>) {
        mOldList = oldList
        mNewList = newList
    }

    override fun getOldListSize(): Int {
        return mOldList.size
    }

    override fun getNewListSize(): Int {
        return mNewList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].javaClass == mNewList[newItemPosition].javaClass
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].downProgress == mNewList[newItemPosition].downProgress
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}