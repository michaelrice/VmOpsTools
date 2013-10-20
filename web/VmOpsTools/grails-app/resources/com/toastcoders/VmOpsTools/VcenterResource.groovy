package com.toastcoders.VmOpsTools

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
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
    def vcenterActionsService

    @GET
    @ApiOperation(value="Get All Vcenters",notes="This will return a list of all vCenters.")
    Response getAllVcenters() {
        ok vcenterService.getAllVcenters()
    }

    @GET
    @ApiOperation(value="Get All VirtualMachines From vCenter", notes="This will return a list of VirtualMachine names, and UUIDs directly from vCenter")
    @Path("/{id}/virtualmachines")
    Response getAllVirtualMachinesFromVcenter(@ApiParam(value="vCenter Id from DB", required=true)@PathParam("id") Long id) {
        ok vcenterActionsService.getVirtualMachines(id as String)
    }

    @PUT
    @ApiOperation(value="Update vCenter",notes="This will update a vCenter")
    Response updateVcenter(@ApiParam(name="the name",value="this is the value") Vcenter vcenter) {
        ok vcenterService.update(vcenter)
    }

    @GET
    @ApiOperation(value="Get vCenter for a given device",notes="This will return a vcenter.")
    @Path('/device/{deviceId}')
    Response getVcenterByDevice(@ApiParam(value="DeviceID of a Hostsystem or Virtualmachine to get the vCenter it is in.",required=true)
                                @PathParam("deviceId") String deviceId) {
        ok vcenterService.getVcenterByDeviceNumber(deviceId)
    }

    @POST
    @ApiOperation(value="Create New vCenter",notes="Adds a new vCenter",tags="these are tags")
    Response createVcenter(@ApiParam(name="Vcenter",value="this is the value") Vcenter vcenter) {
        created vcenterService.createVcenter(vcenter)
    }

    @DELETE
    @ApiOperation(value="Delete Vcenter",notes="This will delete a vcenter from the database. Use with caution.")
    @Path("/{deviceId}")
    Response deleteVcenter(@ApiParam(name="deviceId", value="Device Number of the vCenter", required=true) @PathParam("deviceId") Long deviceId) {
        ok vcenterService.delete(deviceId)
    }
}