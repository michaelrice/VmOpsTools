package com.toastcoders.VmOpsTools.exceptions

/**
 * Created with IntelliJ IDEA.
 * User: Michael Rice
 * Twitter: @errr_
 * Website: http://www.errr-online.com/
 * Github: https://github.com/michaelrice
 * Date: 10/6/13
 * Time: 12:19 AM
 * Licenses: MIT http://opensource.org/licenses/MIT
 */
class MigrationNotPossible {

    MigrationNotPossible(String reason) {
        super("Virtualmachine Migration not possible: ${reason}")
    }
}