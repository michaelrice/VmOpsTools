package com.toastcoders.VmOpsTools

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
import static org.grails.jaxrs.response.Responses.*
import com.wordnik.swagger.annotations.*


@Path("/api/vcenter")
@Api(value="/vcenter",description="Stuff about vcenter")
@Produces('application/json')
class VcenterResource {
    def vcenterService

    @GET
    @ApiOperation(value = "Get All Vcenters", notes = "This will return a list of all vcenters.")
    Response getAllVcenters() {
        ok vcenterService.getAllVcenters()
    }

    @GET
    @ApiOperation(value = "Get vCenter for a given device", notes = "This will return a vcenter.")
    @Path('/device/{deviceId}')
    Response getVcenterByDevice(@ApiParam(value = "DeviceID of a Hostsystem or Virtualmachine to get the vCenter its in.", required = true)
                                @PathParam("deviceId") String deviceId) {
        ok vcenterService.getVcenterByDeviceNumber(deviceId)
    }
}