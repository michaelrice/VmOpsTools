package com.toastcoders.VmOpsTools.vmware.exceptions

/**
 * Created with IntelliJ IDEA.
 * User: Michael Rice
 * Twitter: @errr_
 * Website: http://www.errr-online.com/
 * Github: https://github.com/michaelrice
 * Date: 10/5/13
 * Time: 9:36 PM
 * Licenses: MIT http://opensource.org/licenses/MIT
 */
class VirtualmachineNotFound extends Exception {

    VirtualmachineNotFound(String vmName) {
        super("Virtualmachine ${vmName} unable to be located in vCenter.")
    }

    VirtualmachineNotFound(Long vmid) {
        super("Virtualmachine ${vmid.toString()} unable to be located in vCenter.")
    }
}