package com.toastcoders.VmOpsTools

import static org.grails.jaxrs.response.Responses.*

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.core.Response
import com.wordnik.swagger.annotations.*

@Path('/api/virtualmachine')
@Api(value="/virtualmachine",description="Virtualmachine Object Resource")
@Consumes(['application/json'])
@Produces(['application/json'])
class VirtualmachineCollectionResource {

    def virtualmachineResourceService

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

    @Path('/{id}')
    @ApiOperation(value="Get",notes="Get a Virtualmachine object")
    VirtualmachineResource getResource(@PathParam('id') Long id) {
        new VirtualmachineResource(virtualmachineResourceService: virtualmachineResourceService, id:id)
    }

}
