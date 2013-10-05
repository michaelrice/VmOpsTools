package com.toastcoders.VmOpsTools

import org.grails.jaxrs.provider.DomainObjectNotFoundException

class VirtualmachineResourceService {
    
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

