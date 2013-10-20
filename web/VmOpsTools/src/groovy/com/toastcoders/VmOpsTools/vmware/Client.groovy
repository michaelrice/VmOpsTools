package com.toastcoders.VmOpsTools.vmware

import com.toastcoders.VmOpsTools.exceptions.ConnectionException
import com.toastcoders.VmOpsTools.exceptions.VirtualmachineNotFound
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

    /**
     *
     * @param deviceId
     */
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
        try {
            setSi(new ServiceInstance(new URL("https://${vcenter.ip.toString()}/sdk"), vcuser, vcpass, true))
            log.trace("Created ServiceInstance to vCenter ${vcenter.ip.toString()} for ${deviceId}")
            setRootFolder(getServiceInstance().getRootFolder())
            log.trace("Set RootFolder for ${deviceId}")
        }
        catch(java.net.ConnectException ce) {
            log.trace("Connection error to ${vcenter.ip.toString()}: ${ce.message}", ce)
            throw new Exception("Unable to connect to vcenter: ${ce.message}")
        }
    }

    /**
     *
     * @param username
     * @param password
     * @param vcip
     */
    public Client(String username, String password, String vcip) {
        // connect to the vcenter using provided username and password and return service instance
        try {
            setSi(new ServiceInstance(new URL("https://${vcip}/sdk"), username, password, true))
            log.trace("Created ServiceInstance to vCenter ${vcip}")
            setRootFolder(getServiceInstance().getRootFolder())
            log.trace("Set RootFolder")
        }
        catch(java.net.ConnectException ce) {
            log.trace("Connection error to ${vcenter.ip.toString()}: ${ce.message}", ce)
            throw new ConnectionException("Unable to connect to vcenter: ${ce.message}")
        }
    }

    /**
     *
     * @return
     */
    ServiceInstance getServiceInstance() {
        return si
    }

    /**
     *
     * @param si
     */
    private void setSi(ServiceInstance si) {
        this.si = si
    }

    Folder getRootFolder() {
        return rootFolder
    }

    /**
     *
     * @param rootFolder
     */
    private void setRootFolder(Folder rootFolder) {
        this.rootFolder = rootFolder
    }

    /**
     *
     * @param uuid
     * @return
     */
    public VirtualMachine getVmByUuid(String uuid) {
        return si.getSearchIndex().findByUuid(null,uuid,true) as VirtualMachine
    }

    /**
     * Method to return a VirtualMachine object given a device id
     * @param deviceId
     * @return
     */
    public VirtualMachine getVmByDeviceId(String deviceId) throws VirtualmachineNotFound {
        Device device = Device.findById(Long.decode(deviceId))
        if(!device) {
            // need to bail here since no device was found
            log.trace("Unable to locate Virtualmachine in database using device: ${deviceId}")
            throw new VirtualmachineNotFound("Unable to locate Virtualmachine in database.")
        }
        if(!(device instanceof com.toastcoders.VmOpsTools.Virtualmachine)) {
            //what ever it is, its not a vm. need to bail here.
        }
        return getVmByUuid(device.uuid)
    }

    /**
     *
     * @param uuid
     * @return
     */
    public HostSystem getHostSystemByUuid(String uuid) {
        return si.getSearchIndex().findByUuid(null,uuid,false) as HostSystem
    }
}