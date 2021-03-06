package com.toastcoders.VmOpsTools.vmware.Virtualmachine

import com.toastcoders.VmOpsTools.vmware.Client
import com.toastcoders.VmOpsTools.exceptions.MigrationNotPossible
import com.vmware.vim25.HostVMotionCompatibility
import com.vmware.vim25.VirtualMachineMovePriority
import com.vmware.vim25.mo.ComputeResource
import com.vmware.vim25.mo.HostSystem
import com.vmware.vim25.mo.Task
import com.vmware.vim25.mo.VirtualMachine
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: Michael Rice
 * Twitter: @errr_
 * Website: http://www.errr-online.com/
 * Github: https://github.com/michaelrice
 * Date: 10/5/13
 * Time: 7:30 PM
 * Licenses: MIT http://opensource.org/licenses/MIT
 */
class Migrate {

    private Logger log = Logger.getLogger(getClass().name)
    private Client client
    /**
     *
     * @param deviceId
     */
    Migrate(Client client) {
        this.client = client
    }

    /**
     * migrate virtualmachine from its current host to another host.
     * @param vmUuid UUID of Virtualmachine device you want to move
     * @param newHostUuid UUID of the new Hostsystem you want to move the virtualmachine to
     * @return Boolean Returns true if the migration was a success
     * @throws MigrationNotPossible Thrown if the task was anything other than SUCCESS
     */
    public Boolean migrate(String vmUuid, String newHostUuid) throws MigrationNotPossible {
        log.trace("Migration job request. Move ${vmUuid} to ${newHostUuid}")
        VirtualMachine vm = client.getVmByUuid(vmUuid) as VirtualMachine
        HostSystem newHost  = client.getHostSystemByUuid(newHostUuid) as HostSystem
        HostSystem[] newHostArray = [newHost]
        ComputeResource cr = newHost.getParent() as ComputeResource
        String[] checks = ["cpu", "software"]
        HostVMotionCompatibility[] vmcs = client.serviceInstance.queryVMotionCompatibility(vm,newHostArray,checks)
        String[] comps = vmcs[0].getCompatibility()
        if(checks.length != comps.length) {
            log.trace("CPU or Software not compatible. Unable to complete migration request.")
            //uh oh. System not Compatible
            throw new MigrationNotPossible("CPU or Software not compatible.")
        }
        Task task = vm.migrateVM_Task(cr.resourcePool, newHost,
                VirtualMachineMovePriority.highPriority,
                vm.getRuntime().powerState)
        if(task.waitForTask() != Task.SUCCESS) {
            log.trace("Migration task was unsuccessful. ${task.getTaskInfo().error.fault}")
            throw new MigrationNotPossible("Something failed. ${task.getTaskInfo().error.fault}")
        }
        log.trace("Migration task successful.")
        return true
    }
}