package com.toastcoders.VmOpsTools

class Account {

    String name
    static hasMany = [device:Device]
    static belongsTo = [customer:Customer]
    static constraints = {
    }
}