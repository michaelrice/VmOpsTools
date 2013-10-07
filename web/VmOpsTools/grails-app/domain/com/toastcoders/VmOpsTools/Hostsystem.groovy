package com.toastcoders.VmOpsTools

class Hostsystem extends Device {


    static hasMany = [virtualmachine:Virtualmachine]
    int ram
    int cpu
    int storageSpace
    String os
    static belongsTo = [vcenter:Vcenter]
    static constraints = {
    }
}
