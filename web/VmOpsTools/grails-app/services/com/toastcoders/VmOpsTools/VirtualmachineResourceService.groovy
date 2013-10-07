package com.toastcoders.VmOpsTools

import com.toastcoders.VmOpsTools.exceptions.MissingUuid
import com.toastcoders.VmOpsTools.vmware.Virtualmachine.Migrate
import org.grails.jaxrs.provider.DomainObjectNotFoundException

class VirtualmachineResourceService {

    def migrate(Long virtualMachineId, Long newHostsystemId) {
        vmuuid = Virtualmachine.findById(virtualMachineId)?.uuid
        hostuuid = Hostsystem.findById(newHostsystemId)?.uuid
        if(!vmuuid) {
            throw new MissingUuid(virtualMachineId)
        }
        if(!hostuuid) {
            throw new MissingUuid(newHostsystemId)
        }
        Migrate client = new Migrate(virtualMachineId)
        Boolean status = client.migrate()
        if(!status) {
            return ["migration":"failed"]
        }
        ["migration":"success"]
    }

    def create(Virtualmachine dto) {
        dto.save()
    }

    def read(def id) {
        def obj = Virtualmachine.get(id)
        if (!obj) {
            throw new DomainObjectNotFoundException(Virtualmachine.class, id)
        }
        obj
    }

    def readAll() {
        Virtualmachine.findAll()
    }

    def update(Virtualmachine dto) {
        def obj = Virtualmachine.get(dto.id)
        if (!obj) {
            throw new DomainObjectNotFoundException(Virtualmachine.class, dto.id)
        }
        obj.properties = dto.properties
        obj
    }

    void delete(def id) {
        def obj = Virtualmachine.get(id)
        if (obj) {
            obj.delete()
        }
    }

}

