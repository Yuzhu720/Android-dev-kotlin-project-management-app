package com.southampton.comp6239.bean

import java.io.Serializable
import java.util.*

class Task (
    var taskId: String = "",
    var taskName: String = "",
    var projectId: String = "",
    var userName: String = "",
    var userId: String = "",
    var status: String = "Assigned",
    var createTime: Date? = null,
    var deadline: Date? = null,
    var description: String = "",
    var projectName: String= ""
) : Serializable