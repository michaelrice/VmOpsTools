package com.toastcoders.VmOpsTools

class Device {

    String name
    String uuid
    static belongsTo = [account:Account]
    static constraints = {
        uuid unique: true
    }
}