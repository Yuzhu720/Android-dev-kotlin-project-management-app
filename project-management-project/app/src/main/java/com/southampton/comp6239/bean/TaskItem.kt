package com.southampton.comp6239.bean

import java.util.*

class TaskItem (
    var taskId: String = "",
    var taskName: String = "",
    var projectId: String = "",
    var status: String = "0",
    var createTime: Date? = null,
    var deadline: Date? = null,
    var description: String = "",
    var projectName: String= "",
    var managerName: String=""
)