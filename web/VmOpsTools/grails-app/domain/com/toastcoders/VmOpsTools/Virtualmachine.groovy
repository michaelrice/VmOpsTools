package com.toastcoders.VmOpsTools

class Virtualmachine extends Device {

    String os
    String osVersion
    String storageType
    int ram
    int cpu
    int storageSpace
    static belongsTo = [vcenter:Vcenter, hostsystem:Hostsystem]

    static constraints = {
    }
}