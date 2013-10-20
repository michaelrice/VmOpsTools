package com.toastcoders.VmOpsTools

import com.vmware.vim25.mo.*
import com.vmware.vim25.*

import com.toastcoders.VmOpsTools.vmware.Client
import com.vmware.vim25.mo.util.PropertyCollectorUtil
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
        return ["current_time":vsclient.serviceInstance.currentTime().time.toString()]
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
     * uses property selector to get uuid and name of all VirtualMachines in a vcenter.
     *  returns a list of maps {name: "vmname", "uuid": vmuuid}
     * @param deviceId
     * @return
     */
    def getVirtualMachines(String deviceId) {
        String vcuser = grailsApplication.config.vcenter.admin_user
        String vcpass = grailsApplication.config.vcenter.admin_pass
        Vcenter vcenter = Vcenter.findById(Long.decode(deviceId))
        if(!vcenter) {
            throw new DomainObjectNotFoundException(Vcenter,deviceId)
        }
        String vcip = vcenter.ip
        Client vsclient = new Client(vcuser,vcpass,vcip)
        //set the properties of the vm we want to fetch
        String[] vmProperties = ["name", "config.uuid"]
        ManagedObjectReference rootFolderMOR = vsclient.rootFolder.getMOR()
        ManagedObjectReference pcMOR = vsclient.serviceInstance.getPropertyCollector().getMOR()
        PropertySpec vmSpec = new PropertySpec()
        vmSpec.setAll(false)
        vmSpec.setType("VirtualMachine")
        vmSpec.setPathSet(vmProperties)
        ObjectSpec oSpec = new ObjectSpec()
        oSpec.setObj(rootFolderMOR)
        oSpec.setSelectSet(PropertyCollectorUtil.buildFullTraversalV4())

        PropertyFilterSpec[] pfSpec = new PropertyFilterSpec[1];
        pfSpec[0] = new PropertyFilterSpec()

        ObjectSpec[] oo = [oSpec]
        pfSpec[0].setObjectSet(oo)
        PropertySpec[] pp = [vmSpec]
        pfSpec[0].setPropSet(pp)
        def newResult = []
        def tMap = [:]
        def result = (List) vsclient.serviceInstance.getServerConnection().getVimService().retrieveProperties(pcMOR, pfSpec)
        //clean up the result a bit to make it more reader friendly
        result.each { device ->
            device.propSet.each { property ->
                if(property.name == "config.uuid") {
                    property.name = "uuid"
                }
                tMap.putAt("${property.name}" , "${property.val}")
            }
            newResult.add(tMap)
            tMap = [:]
        }
        return newResult
    }
}