package com.example.facechat.Class

class Contacts {

    private var name: String = ""
    private var status: String = ""
    private var image: String = ""
    private var uid: String = ""
    constructor()
    constructor(name: String, status: String, uid: String,image:String) {
        this.name = name
        this.status = status
        this.uid = uid
        this.image=image
    }
    fun getName(): String
    {
        return  name
    }

    fun setName(name: String)
    {
        this.name = name
    }
    fun getStatus(): String
    {
        return  status
    }

    fun setStatus(status: String)
    {
        this.status = status
    }

    fun getImage(): String
    {
        return  image
    }

    fun setImage(image: String)
    {
        this.image = image
    }

    fun getUID(): String
    {
        return  uid
    }

    fun setUID(uid: String)
    {
        this.uid = uid
    }

}