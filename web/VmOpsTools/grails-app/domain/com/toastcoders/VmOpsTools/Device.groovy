package com.toastcoders.VmOpsTools

class Device {

    String name
    String uuid
    String hostName
    String ip
    Date dateCreated
    Date lastUpdated
    static belongsTo = [account:Account]
    static constraints = {
        uuid unique: true
    }
}