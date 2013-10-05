package com.toastcoders.VmOpsTools

import org.codehaus.groovy.grails.exceptions.GrailsDomainException
import org.grails.jaxrs.provider.DomainObjectNotFoundException

class VcenterService {

    def getAllVcenters() {
        return Vcenter.findAll()
    }

    def create(Vcenter vc) {
        vc.save(flush: true)
    }

    def getVcenterByDeviceNumber(String deviceId) {
        deviceId = deviceId as int
        def device = Device.findById(deviceId)
        if(!device) {
            throw new DomainObjectNotFoundException(Device.class, deviceId)
        }
        def vcenter
        // check if this is a hostsystem, if so no need to look further
        if(device instanceof com.toastcoders.VmOpsTools.Hostsystem) {
            return device.vcenter
        }
        // check if a vm, if so grab the host then the vcenter
        if(device instanceof com.toastcoders.VmOpsTools.Virtualmachine) {
            return device.hostsystem.vcenter
        }
        // need a Hostsystem or a Virtualmachine to find a vcenter
        throw new DomainObjectNotFoundException(Vcenter.class,0)
    }

    def update(Vcenter vc) {
        def obj = Vcenter.get(vc.id)
        if (!obj) {
            throw new DomainObjectNotFoundException(Vcenter.class, vc.id)
        }
        obj.properties = dto.properties
        obj
    }

    void delete(def id) {
        def obj = Vcenter.get(id)
        if(obj){
            obj.delete()
        }
    }
}