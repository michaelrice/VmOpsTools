package com.toastcoders.VmOpsTools

import com.vmware.vim25.mo.*

import com.toastcoders.VmOpsTools.vmware.Client
import org.grails.jaxrs.provider.DomainObjectNotFoundException

class VcenterActionsService {

    def grailsApplication

    /**
     * Get vCenter Time
     * @param deviceId
     * @return HashMap
     */
    def getVcenterTime(def deviceId) {
        Client vsclient = new Client(deviceId)
        ServiceInstance si = vsclient.getServiceInstance()
        return ["current_time":si.currentTime().time.toString()]
    }

    /**
     * Get License Report from a given vcenter
     * @param deviceId
     * @return ArrayList of HashMaps
     */
    def getLicenseReport(String deviceId) {
        String vcuser = grailsApplication.config.vcenter.admin_user
        String vcpass = grailsApplication.config.vcenter.admin_pass
        Vcenter vcenter = Vcenter.findById(Long.decode(deviceId))
        if(!vcenter) {
            throw new DomainObjectNotFoundException(Vcenter,deviceId)
        }
        String vcip = vcenter.ip
        Client vsclient = new Client(vcuser,vcpass,vcip)
        LicenseManager lm = vsclient.getServiceInstance().getLicenseManager()
        def licenses = lm.getLicenses()
        def retList = []
        def retMap = [:]
        licenses.each {license ->
            retMap.put("cost", license.costUnit)
            retMap.put("edition_key", license.editionKey)
            retMap.put("license_key", license.licenseKey)
            retMap.put("license_name", license.name)
            retMap.put("total", license.total)
            retMap.put("used", license.used)
            retList.add(retMap)
            retMap = [:]
        }
        return retList
    }

    /**
     * Get a list of all Datacenters from the root of a vcenter
     * @param deviceId
     * @return ArrayList of HashMaps
     */
    def getDataCenters(String deviceId) {
        String vcuser = grailsApplication.config.vcenter.admin_user
        String vcpass = grailsApplication.config.vcenter.admin_pass
        Vcenter vcenter = Vcenter.findById(Long.decode(deviceId))
        if(!vcenter) {
            throw new DomainObjectNotFoundException(Vcenter,deviceId)
        }
        String vcip = vcenter.ip
        Client vsclient = new Client(vcuser,vcpass,vcip)
        ManagedEntity[] datacenters = new InventoryNavigator(vsclient.rootFolder).searchManagedEntities("Datacenter")
        def retMap = [:]
        def retList = []
        datacenters.each { datacenter ->
            retMap.put("name",datacenter.name)
            retList.add(retMap)
            retMap = [:]
        }
        return retList
    }

    /**
     * Reboots a virtual machine given its device number
     * @param deviceid
     */
    def rebootVirtualMachine(def deviceid) {
        VirtualMachine vm = getVmByDeviceId(deviceid)
        vm.rebootGuest()
    }

    /**
     * PowerOn a virtual machine given its device number
     * @param deviceId
     * @return
     */
    def powerOnVirtualMachine(def deviceId) {
        VirtualMachine vm = getVmByDeviceId(deviceId)
        final task = vm.powerOnVM_Task(vm.getRuntime().getHost() as HostSystem)
        task.waitForTask()
    }

    /**
     * Method to return a VirtualMachine object given a device id
     * @param deviceId
     * @return
     */
    private VirtualMachine getVmByDeviceId(String deviceId) {
        Client vsclient = new Client(deviceid)
        Device device = Device.findById(Long.decode(deviceId))
        if(!device) {
            // need to bail here since no device was found
        }
        if(!(device instanceof com.toastcoders.VmOpsTools.Virtualmachine)) {
            // what ever it is, its not a vm. need to bail here.
        }
        VirtualMachine vm = vsclient.serviceInstance.getSearchIndex().findByUuid(null,device.uuid,true)
        return vm
    }
}