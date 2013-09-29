package com.toastcoders.VmOpsTools

import org.grails.jaxrs.provider.DomainObjectNotFoundException

class AccountService {

    /**
     * List of all devices by account number.
     *
     * @param accountNumber
     * @return
     */
    def devicesByAccount(def accountNumber) {
        Account account = Account.findById(accountNumber)
        if(!account) {
            throw new DomainObjectNotFoundException(Account, accountNumber)
        }
        def devices = Device.findAllByAccount(account)
        if(!devices) {
            throw new DomainObjectNotFoundException(Device,accountNumber)
        }
        return devices
    }

    /**
     * All the info you need to connect to a vcenter based off an account number
     *
     * @param accountNumber
     * @return HashMap
     */
    def getVcenterByAccount(def accountNumber) {
        def devices = devicesByAccount(accountNumber)
        def vcenter
        def retMap  = [:]
        devices.each{ device ->
            if(device instanceof com.toastcoders.VmOpsTools.Hostsystem) {
                vcenter = device.vcenter
                retMap.put("name",vcenter.name)
                retMap.put("ip",vcenter.ip)
                retMap.put("hostname",vcenter.hostName)
                retMap.put("uuid",vcenter.uuid)
                retMap.put("dateCreated",vcenter.dateCreated)
            }
        }
        return retMap
    }
}