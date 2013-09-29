import grails.util.Environment
import com.toastcoders.VmOpsTools.User
import com.toastcoders.VmOpsTools.Role
import com.toastcoders.VmOpsTools.UserRole
import com.toastcoders.VmOpsTools.Vcenter
import com.toastcoders.VmOpsTools.Customer
import com.toastcoders.VmOpsTools.Account
import com.toastcoders.VmOpsTools.Hostsystem
import com.toastcoders.VmOpsTools.Virtualmachine
import java.util.UUID


class BootStrap {

    def env = Environment.getCurrent().name

    def init = { servletContext ->
        /**
         * Setup user and admin roles if they arent there
         * Also we create the user 'hero' if its not there
         * then make the user an admin
         */
        def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(flush: true)
        def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(flush: true)
        def userId = 1
        if (!User.findById(userId)) {
           def adminUser = new User(username: 'hero', enabled: true, password: 'secure')
           adminUser.save(flush: true)
           UserRole.create adminUser, adminRole, true
        }
        println env
        if(env == 'development') {
            println "running development mode"
            def vcenter = Vcenter.findAll() ?: new Vcenter(name: "vs00.mrice.me",uuid: generateUuid(),hostName: "vs00.mrice.me",ip: "10.12.254.10").save(flush: true)
            println vcenter
            def customer = Customer.findAll() ?: new Customer(name: "Mr. Errr LLC").save(flush: true)
            def account = Account.findAll() ?: new Account(name: "DFW Account", customer: customer).save(flush: true)
            def host = Hostsystem.findAll() ?: new Hostsystem(ram: 512,cpu: 12, storageSpace: 120,
                    vcenter: vcenter,
                    account: account,
                    name: "host1.mrice.me",
                    uuid: generateUuid(),
                    ip: "10.10.10.10",
                    hostName: "host1.mrice.me").save(flush: true)
            def vm1 = Virtualmachine.findAll() ?: new Virtualmachine(ram:2,cpu: 3,storageSpace: 30,
                    vcenter:vcenter,
                    account:account,
                    name: "web1.mrice.me",
                    uuid: generateUuid(),
                    hostsystem: host,
                    ip: "192.168.1.10",
                    hostName: "web1.mrice.me",
                    storageType: "SAN",
                    os: "CentOS",
                    osVersion: "6.4").save(flush: true)
            def vm2 = new Virtualmachine(ram:2,cpu: 3,storageSpace: 30,
                    vcenter:vcenter,
                    account:account,
                    name: "web2.mrice.me",
                    uuid: generateUuid(),
                    hostsystem: host,
                    ip: "192.168.1.11",
                    hostName: "web2.mrice.me",
                    storageType: "SAN",
                    os: "CentOS",
                    osVersion: "6.4").save(flush: true)
            def vm3 = new Virtualmachine(ram:2,cpu: 3,storageSpace: 30,
                    vcenter:vcenter,
                    account:account,
                    name: "web3.mrice.me",
                    uuid: generateUuid(),
                    hostsystem: host,
                    ip: "192.168.1.12",
                    hostName: "web3.mrice.me",
                    storageType: "SAN",
                    os: "CentOS",
                    osVersion: "6.4").save(flush: true)
        }
    }

    def destroy = {

    }

    private String generateUuid() {
        return UUID.randomUUID().toString()
    }
}
