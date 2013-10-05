package com.toastcoders.VmOpsTools

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation

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

@Api(value="/virtualmachine",description="Virtualmachine Object Resource")
@Consumes(['application/json'])
@Produces(['application/json'])
class VirtualmachineResource {

    def virtualmachineResourceService

    @PUT
    @ApiOperation(value="Update Virtualmachine",notes="This will update a Virtualmachine")
    Response update(Virtualmachine dto) {
        ok virtualmachineResourceService.update(dto)
    }

    @DELETE
    @ApiOperation(value="Delete Virtualmachine",notes="This will delete a Virtualmachine")
    void delete() {
        virtualmachineResourceService.delete(id)
    }

    @POST
    @ApiOperation(value="Create",notes="Create new Virtualmachine object")
    Response create(Virtualmachine dto) {
        created virtualmachineResourceService.create(dto)
    }

    @GET
    @ApiOperation(value="List All",notes="List all Virtualmachine objects")
    Response readAll() {
        ok virtualmachineResourceService.readAll()
    }

    @GET
    @Path('/{id}')
    @ApiOperation(value="Get",notes="Get a Virtualmachine object")
    VirtualmachineResource getResource(@PathParam('id') Long id) {
        ok virtualmachineResourceService.read(id)
    }
}

