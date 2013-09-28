package com.toastcoders.VmOpsTools

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces


@Path('/api/account')
class AccountResource {

    @GET
    @Produces('text/plain')
    String getAccountRepresentation() {
        'Account'
    }
    
}
