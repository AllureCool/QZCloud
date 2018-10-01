package com.smile.qzclould.manager

import android.util.SparseArray
import com.chad.library.adapter.base.BaseViewHolder
import com.liulishuo.filedownloader.BaseDownloadTask

class TasksManager {
    companion object {
        private var instance: TasksManager? = null
        get() {
            if (field == null) {
                field = TasksManager()
            }
            return field
        }

        fun getImpl(): TasksManager {
            return instance!!
        }
    }

    private val taskSparseArray = SparseArray<BaseDownloadTask>()

    fun addTaskForViewHolder(task: BaseDownloadTask) {
        taskSparseArray.put(task.id, task)
    }

    fun removeTaskForViewHolder(id: Int) {
        taskSparseArray.remove(id)
    }

    fun updateViewHolder(id: Int, holder: BaseViewHolder) {
        val task = taskSparseArray.get(id) ?: return
        task.tag = holder
    }

    fun releaseTask() {
        taskSparseArray.clear()
    }
}