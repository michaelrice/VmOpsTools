package com.toastcoders.VmOpsTools

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
import static org.grails.jaxrs.response.Responses.*
import com.wordnik.swagger.annotations.*


@Path("/api/vcenter")
@Api(value="/vcenter",description="vCenter Object Resource")
@Produces('application/json')
@Consumes('application/json')
class VcenterResource {
    def vcenterService

    @GET
    @ApiOperation(value="Get All Vcenters",notes="This will return a list of all vCenters.")
    Response getAllVcenters() {
        ok vcenterService.getAllVcenters()
    }

    @PUT
    @ApiOperation(value="Update vCenter",notes="This will update a vCenter")
    Response updateVcenter(@ApiParam(name="the name",value="this is the value") Vcenter vcenter) {
        ok vcenterService.update(vcenter)
    }

    @GET
    @ApiOperation(value="Get vCenter for a given device",notes="This will return a vcenter.")
    @Path('/device/{deviceId}')
    Response getVcenterByDevice(@ApiParam(value="DeviceID of a Hostsystem or Virtualmachine to get the vCenter its in.",required=true)
                                @PathParam("deviceId") String deviceId) {
        ok vcenterService.getVcenterByDeviceNumber(deviceId)
    }

    @POST
    @ApiOperation(value="Create New vCenter",notes="Adds a new vCenter",tags="these are tags")
    Response createVcenter(@ApiParam(name="Vcenter",value="this is the value") Vcenter vcenter) {
        created vcenterService.createVcenter(vcenter)
    }

}