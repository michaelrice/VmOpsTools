import com.toastcoders.VmOpsTools.User
import com.toastcoders.VmOpsTools.Role
import com.toastcoders.VmOpsTools.UserRole

class BootStrap {

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
    }

    def destroy = {

    }
}
