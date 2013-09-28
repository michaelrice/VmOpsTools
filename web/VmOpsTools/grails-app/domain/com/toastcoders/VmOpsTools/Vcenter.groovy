package com.toastcoders.VmOpsTools

class Vcenter {

    String name
    String uuid
    String hostName
    String ip
    Date dateCreated
    Date lastUpdated
    static hasMany = [virtualmachine:Virtualmachine,hostsystem:Hostsystem]
    static constraints = {
        uuid unique: true
    }
}