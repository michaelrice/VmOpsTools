package com.toastcoders.VmOpsTools

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation

import static org.grails.jaxrs.response.Responses.*

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.PUT
import javax.ws.rs.core.Response

import org.grails.jaxrs.provider.DomainObjectNotFoundException

@Api(value="",description="Virtualmachine Object Resource")
@Consumes(['application/json'])
@Produces(['application/json'])
class VirtualmachineResource {

    def virtualmachineResourceService
    def id

    @GET
    @ApiOperation(value="Get",notes="Get a Virtualmachine object")
    Response read() {
        ok virtualmachineResourceService.read(id)
    }

    @PUT
    @ApiOperation(value="Update Virtualmachine",notes="This will update a Virtualmachine")
    Response update(Virtualmachine dto) {
        dto.id = id
        ok virtualmachineResourceService.update(dto)
    }

    @DELETE
    @ApiOperation(value="Delete Virtualmachine",notes="This will delete a Virtualmachine")
    void delete() {
        virtualmachineResourceService.delete(id)
    }

}

