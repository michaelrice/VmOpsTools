package com.toastcoders.VmOpsTools.vmware

import com.vmware.vim25.mo.*

import com.toastcoders.VmOpsTools.Device
import org.grails.jaxrs.provider.DomainObjectNotFoundException

import org.apache.log4j.Logger

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA

/**
 * Created with IntelliJ IDEA.
 * User: Michael Rice
 * Twitter: @errr_
 * Website: http://www.errr-online.com/
 * Github: https://github.com/michaelrice
 * Date: 9/29/13
 * Time: 1:58 AM
 * Licenses: MIT http://opensource.org/licenses/MIT
 */
class Client {

    def grailsApplication
    // defines a logger
    private Logger log = Logger.getLogger(getClass().name)
    // ServiceInstance
    private ServiceInstance si
    // RootFolder
    private Folder rootFolder

    public Client(String deviceId) {
        log.trace("Building ${this.class} for ${deviceId}")
        // find the vcenter by deviceid and return a service instance
        deviceId = deviceId as int
        def device = Device.findById(deviceId)
        if(!device) {
            log.trace("Failed to build ${this.class}. Could not locate ${deviceId}")
            throw new DomainObjectNotFoundException(Device, deviceId)
        }
        def vcenter
        // check if this is a hostsystem, if so no need to look further
        if(device instanceof com.toastcoders.VmOpsTools.Hostsystem) {
            vcenter = device?.vcenter
            log.trace("Found Hostsystem and set vCenter for ${deviceId}")
        }
        // check if a vm, if so grab the host then the vcenter
        else if(device instanceof com.toastcoders.VmOpsTools.Virtualmachine) {
            vcenter = device?.hostsystem?.vcenter
            log.trace("Found Virtualmachine and set vCenter for ${deviceId}")
        }
        else {
            // not really right should make some other kind of exception here
            log.trace("Failed to find Hostsystem or Virtualmachine and set vCenter for ${deviceId}")
            log.trace("Device of type: ${device.class}")
            throw new DomainObjectNotFoundException(Device,deviceId)
        }
        def context = SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
        grailsApplication = context.grailsApplication
        // gives us access to the stanza in Config.groovy for the vcenter admin user and password
        String vcuser = grailsApplication.config.vcenter.admin_user
        String vcpass = grailsApplication.config.vcenter.admin_pass
        setSi(new ServiceInstance(new URL("https://${vcenter.ip.toString()}/sdk"), vcuser, vcpass, true))
        log.trace("Created ServiceInstance to vCenter ${vcenter.ip.toString()} for ${deviceId}")
        setRootFolder(getServiceInstance().getRootFolder())
        log.trace("Set RootFolder for ${deviceId}")
    }

    public Client(String username, String password, String vcip) {
        // connect to the vcenter using provided username and password and return service instance
        setSi(new ServiceInstance(new URL("https://${vcip}/sdk"), username, password, true))
        log.trace("Created ServiceInstance to vCenter ${vcip}")
        setRootFolder(getServiceInstance().getRootFolder())
        log.trace("Set RootFolder")
    }

    ServiceInstance getServiceInstance() {
        return si
    }

    private void setSi(ServiceInstance si) {
        this.si = si
    }

    Folder getRootFolder() {
        return rootFolder
    }

    private void setRootFolder(Folder rootFolder) {
        this.rootFolder = rootFolder
    }

    public VirtualMachine getVmByUuid(String uuid) {
        return si.getSearchIndex().findByUuid(null,uuid,true) as VirtualMachine
    }

    public HostSystem getHostSystemByUuid(String uuid) {
        return si.getSearchIndex().findByUuid(null,uuid,false) as HostSystem
    }
}