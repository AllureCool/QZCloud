package com.smile.qzclould.ui.cloud.bean

import com.smile.qzclould.db.Direcotory
import java.io.Serializable

data class FileBean(
      val page: Int,
      val pageSize: Int,
      val totalCount: Int,
      val totalPage: Int,
      val list: List<Direcotory>,
      val info: Direcotory
): Serializable
