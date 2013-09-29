package com.toastcoders.VmOpsTools

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
import static org.grails.jaxrs.response.Responses.*

@Path("/api/vcenter")
@Produces('application/json')
class VcenterResource {
    def vcenterService

    @GET
    Response getAllVcenters() {
        ok vcenterService.getAllVcenters()
    }

    @GET
    @Path('/device/{deviceId}')
    Response getVcenterByDevice(@PathParam("deviceId") String deviceId) {
        ok vcenterService.getVcenterByDeviceNumber(deviceId)
    }
}