package com.toastcoders.VmOpsTools

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
import static org.grails.jaxrs.response.Responses.*


@Path('/api/account')
@Produces('application/json')
class AccountResource {

    def accountService

    @GET
    @Path('{accountNumber}/devices')
    Response getAccountRepresentation(@PathParam("accountNumber") String accountNumber) {
        ok accountService.devicesByAccount(accountNumber)
    }

    @GET
    @Path('{accountNumber}/vcenter')
    Response getVcenterByAccount(@PathParam("accountNumber") String accountNumber) {
        ok accountService.getVcenterByAccount(accountNumber)
    }

}
