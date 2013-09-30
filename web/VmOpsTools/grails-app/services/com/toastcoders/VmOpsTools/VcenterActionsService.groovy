package com.toastcoders.VmOpsTools

import com.toastcoders.VmOpsTools.Vcenter
import com.vmware.vim25.*
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
        ServiceInstance si = vsclient.getSi()
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
        LicenseManager lm = vsclient.getSi().getLicenseManager()
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
}