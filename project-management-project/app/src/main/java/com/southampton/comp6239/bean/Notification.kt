package com.southampton.comp6239.bean

import java.io.Serializable
import java.util.*

class Notification (
    var content: String = "",
    var sender: String = "",
    var time: Date? = null,
    var notiId: String = "",
    var read: Boolean? = null
)