package com.toastcoders.VmOpsTools

import org.grails.jaxrs.provider.DomainObjectNotFoundException

class VcenterService {

    def getAllVcenters() {

        def vcenters = Vcenter.findAll()
        if(!vcenters) {
            // Strange.. how are there no vcenters?
            throw new DomainObjectNotFoundException(Vcenter,0)
        }
        def retMap = [:]
        vcenters.each { vcenter ->
            retMap.put("name",vcenter.name)
            retMap.put("ip",vcenter.ip)
            retMap.put("hostname",vcenter.hostName)
            retMap.put("uuid",vcenter.uuid)
            retMap.put("dateCreated",vcenter.dateCreated)
        }
        retMap
    }

    def getVcenterByDeviceNumber(String deviceId) {
        deviceId = deviceId as int
        def device = Device.findById(deviceId)
        if(!device) {
            throw new DomainObjectNotFoundException(Device, deviceId)
        }
        def retMap = [:]
        def vcenter
        // check if this is a hostsystem, if so no need to look further
        if(device instanceof com.toastcoders.VmOpsTools.Hostsystem) {
            vcenter = device.vcenter
            retMap.put("name",vcenter.name)
            retMap.put("ip",vcenter.ip)
            retMap.put("hostname",vcenter.hostName)
            retMap.put("uuid",vcenter.uuid)
            retMap.put("dateCreated",vcenter.dateCreated)
            return retMap
        }
        // check if a vm, if so grab the host then the vcenter
        if(device instanceof com.toastcoders.VmOpsTools.Virtualmachine) {
            vcenter = device.hostsystem.vcenter
            retMap.put("name",vcenter.name)
            retMap.put("ip",vcenter.ip)
            retMap.put("hostname",vcenter.hostName)
            retMap.put("uuid",vcenter.uuid)
            retMap.put("dateCreated",vcenter.dateCreated)
            return retMap
        }
        // need a Hostsystem or a Virtualmachine to find a vcenter
        throw new DomainObjectNotFoundException(Vcenter,0)
    }
}