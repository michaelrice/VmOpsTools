package com.toastcoders.VmOpsTools

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam

import static org.grails.jaxrs.response.Responses.*

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.PUT
import javax.ws.rs.core.Response

import org.grails.jaxrs.provider.DomainObjectNotFoundException

@Path("/api/virtualmachine")
@Api(value="/virtualmachine",description="Virtualmachine Object Resource")
@Consumes(['application/json'])
@Produces(['application/json'])
class VirtualmachineResource {

    def virtualmachineResourceService

    @POST
    @ApiOperation(value="Create",notes="Create new Virtualmachine object")
    Response create(Virtualmachine dto) {
        created virtualmachineResourceService.create(dto)
    }

    @GET
    @ApiOperation(value="List All",notes="List all Virtualmachine objects")
    @ApiResponses(value=[@ApiResponse(code=404, message="Virtualmachines not found")])
    Response readAll() {
        ok virtualmachineResourceService.readAll()
    }

    @GET
    @Path('/{id}')
    @ApiOperation(value="Get",notes="Get a Virtualmachine object")
    @ApiResponses(value=[@ApiResponse(code=404, message="Virtualmachine not found")])
    Response getResource(@ApiParam(name="id",value="Device id of a Virtualmachine to fetch.",required=true) @PathParam('id') Long id) {
        ok virtualmachineResourceService.read(id)
    }

    @PUT
    @Path('/{id}')
    @ApiOperation(value="Update Virtualmachine",notes="This will update a Virtualmachine")
    Response update(@ApiParam(name="id",value="Device id of a Virtualmachine to update.",required=true) @PathParam('id') Long id, Virtualmachine dto) {
        ok virtualmachineResourceService.update(dto)
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value="Delete Virtualmachine",notes="This will delete a Virtualmachine")
    void delete(@ApiParam(name="id",value="Device id of a Virtualmachine to delete.",required=true) @PathParam('id') Long id) {
        virtualmachineResourceService.delete(id)
    }

    @POST
    @Path("/{id}/migrate/{hostId}")
    @ApiOperation(value="Migrate",notes="Migrate Virtualmachine to a new HostSystem. Uses vMotion. vMotion rules apply.")
    Response migrate(@ApiParam() @PathParam('id') Long id, @PathParam('hostId') Long hostId) {
        ok virtualmachineResourceService.migrate(id,hostId)
    }
}

