package com.southampton.comp6239.bean

import java.io.Serializable

class User : Serializable{
    var uid: String
    var lastname: String
    var firstname: String
    var userName: String
    var password: String
    var email: String
    var phone: String
    var skill: String
    var role: Int
    var pictureUrl: String

    constructor(
        uid : String,
        firstname:String,
        lastname:String,
        userName: String,
        password: String,
        skill: String,
        role: Int,
        email: String,
        phone: String,
        pictureUrl: String
    ) {
        this.uid = uid
        this.firstname = firstname
        this.lastname = lastname
        this.userName = userName
        this.password = password
        this.email = email
        this.role = 0
        this.pictureUrl = pictureUrl
        this.phone = phone
        this.skill = skill
    }

    constructor(){
        this.uid = ""
        this.firstname = ""
        this.lastname = ""
        this.userName = ""
        this.password = ""
        this.email = ""
        this.role = 0
        this.pictureUrl = ""
        this.phone = ""
        this.skill = ""
    }

    constructor(password: String, email: String) {
        this.uid = ""
        this.password = password
        this.email = email
        this.firstname = ""
        this.lastname = ""
        this.userName = ""
        this.role = 0
        this.pictureUrl = ""
        this.phone = ""
        this.skill = ""
    }

    override fun equals(other: Any?): Boolean {
        if(other === this) return true
        if(other !is User) return false
        return other.uid.equals(uid)
    }
}