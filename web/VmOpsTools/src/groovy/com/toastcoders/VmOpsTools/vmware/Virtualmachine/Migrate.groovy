package com.toastcoders.VmOpsTools.vmware.Virtualmachine

import com.toastcoders.VmOpsTools.vmware.Client
import com.toastcoders.VmOpsTools.vmware.exceptions.MigrationNotPossibleException
import com.vmware.vim25.HostVMotionCompatibility
import com.vmware.vim25.VirtualMachineMovePriority
import com.vmware.vim25.mo.ComputeResource
import com.vmware.vim25.mo.HostSystem
import com.vmware.vim25.mo.Task
import com.vmware.vim25.mo.VirtualMachine

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
class Migrate extends Client {


    Migrate(String deviceId) {
        super(deviceId)
    }

    Migrate(String username, String password, String vcip) {
        super(username, password, vcip)
    }

    public Boolean migrate(String vmuuid, String newHostUuid) throws MigrationNotPossibleException {
        VirtualMachine vm = getVmByUuid(vmuuid) as VirtualMachine
        HostSystem newHost  = getHostSystemByUuid(newHostUuid) as HostSystem
        HostSystem[] newHostArray = [newHost]
        ComputeResource cr = newHost.getParent() as ComputeResource
        String[] checks = ["cpu", "software"]
        HostVMotionCompatibility[] vmcs = si.queryVMotionCompatibility(vm,newHostArray,checks)
        String[] comps = vmcs[0].getCompatibility()
        if(checks.length != comps.length) {
            //uh oh. System not Compatible
            throw new MigrationNotPossibleException("CPU or Software not compatible.")
        }
        Task task = vm.migrateVM_Task(cr.resourcePool, newHost,
                VirtualMachineMovePriority.highPriority,
                vm.getRuntime().powerState)
        if(task.waitForTask() != Task.SUCCESS) {
            throw new MigrationNotPossibleException("Something failed.. ${task.getTaskInfo().result} ")
        }
        return true
    }
}