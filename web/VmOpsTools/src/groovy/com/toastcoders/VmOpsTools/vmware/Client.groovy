package com.toastcoders.VmOpsTools.vmware

import com.toastcoders.VmOpsTools.exceptions.ConnectionException
import com.toastcoders.VmOpsTools.exceptions.VirtualMachineOperationFailed
import com.toastcoders.VmOpsTools.exceptions.VirtualmachineNotFound
import com.vmware.vim25.DisallowedOperationOnFailoverHost
import com.vmware.vim25.FileFault
import com.vmware.vim25.InsufficientResourcesFault
import com.vmware.vim25.InvalidPowerState
import com.vmware.vim25.InvalidState
import com.vmware.vim25.NotEnoughLicenses
import com.vmware.vim25.NotSupported
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.TaskInProgress
import com.vmware.vim25.VirtualMachinePowerState
import com.vmware.vim25.VmConfigFault
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

    /**
     * Reboots a virtual machine given its device number
     * @param deviceid
     */
    public void rebootVirtualMachine(String deviceId) {
        VirtualMachine vm = getVmByDeviceId(deviceId)
        vm.rebootGuest()
    }

    /**
     * PowerOn a virtual machine given its device number
     * @param deviceId
     * @return
     * @deprecated use {@link this.powerOnVirtualMachineMulti()} instead.
     */
    @Deprecated
    public boolean powerOnVirtualMachine(String deviceId) {
        VirtualMachine vm = getVmByDeviceId(deviceId)
        if(vm.getRuntime().powerState == VirtualMachinePowerState.poweredOn) {
            log.trace("powerOnVirtualMachine: VirtualMachine ${deviceId} already powered on.")
            return true
        }
        try {
            final task = vm.powerOnVM_Task()
            if(task.waitForTask() == Task.SUCCESS) {
                return true
            }
        }
        catch(DisallowedOperationOnFailoverHost e) {
            log.trace("powerOnVirtualMachine: Host specified is a failover host. Unable to preform requested operation.", e)
            throw new VirtualMachineOperationFailed("Host specified is a failover host. Unable to preform requested operation. ${e.message}")
        }
        catch(FileFault e) {
            //if there is a problem accessing the virtual machine on the filesystem.
            log.trace("powerOnVirtualMachine: There was a problem accessing the virtual machine on the filesystem.", e)
            throw new VirtualMachineOperationFailed("There was a problem accessing the virtual machine on the filesystem. ${e.message}")
        }
        catch(InsufficientResourcesFault e) {
            //if this operation would violate a resource usage policy.
            log.trace("powerOnVirtualMachine: operation would violate a resource usage policy.",e)
            throw new VirtualMachineOperationFailed("Operation would violate a resource usage policy. ${e.message}")
        }
        catch(InvalidPowerState e) {
            //if the power state is poweredOn
            // how the herp & derp did we end up here?
            log.trace("powerOnVirtualMachine: VirtualMachine ${deviceId} already powered on v2.",e)
            return true
        }
        catch(InvalidState e) {
            //if the host is in maintenance mode or if the virtual machine's configuration information is not available.
            log.trace("powerOnVirtualMachine: The host is in maintenance mode or the virtual machine's configuration information is not available. ${e.message}",e)
            throw new VirtualMachineOperationFailed("The host is in maintenance mode, or virtual machine config info not avaulable. ${e.message}")
        }
        catch(NotEnoughLicenses e) {
            //if there are not enough licenses to power on this virtual machine
            log.trace("powerOnVirtualMachine: there are not enough licenses to power on this virtual machine. ${e.message}",e)
            throw new VirtualMachineOperationFailed("There are not enough licenses to power on this virtual machine. ${e.message}")
        }
        catch(NotSupported e) {
            //if the virtual machine is marked as a template.
            log.trace("powerOnVirtualMachine: the virtual machine is marked as a template.",e)
            throw new VirtualMachineOperationFailed("This virtual machine is marked as a template.")
        }
        catch(RuntimeFault e) {
            //if any type of runtime fault is thrown that is not covered by the other faults; for example, a communication error.
            log.trace("powerOnVirtualMachine: runtime fault was thrown that is not covered by the other faults. ${e.message}",e)
            throw new VirtualMachineOperationFailed("runtime fault was thrown that is not covered by the other faults. ${e.message}")
        }
        catch(TaskInProgress e) {
            //if the virtual machine is busy.
            log.trace("powerOnVirtualMachine: virtual machine is busy. Unable to preform request task. ${e.message}",e)
            throw new VirtualMachineOperationFailed("virtual machine is busy. Unable to preform request task. ${e.message}")
        }
        catch(VmConfigFault e) {
            //if a configuration issue prevents the power-on. Typically, a more specific fault, such as UnsupportedVmxLocation, is thrown
            log.trace("powerOnVirtualMachine: A configuration issue is preventing the power-on operation. ${e.message}",e)
            throw new VirtualMachineOperationFailed("A configuration issue is preventing the power-on operation. ${e.message}")
        }
        false
    }

    /**
     *
     */
    public void powerOnVirtualMachineMulti() {

    }
}