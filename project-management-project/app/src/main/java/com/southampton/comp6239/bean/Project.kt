package com.southampton.comp6239.bean

import java.io.Serializable
import java.util.*

class Project: Serializable {
    var projectId: String = ""
    var projectName: String = ""
    var mangerName: String = ""
    var userId: String = ""
    var status: String = ""
    var deadline: Date? = null
    var createTime: Date? = null
    var description: String = ""

}