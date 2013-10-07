package com.toastcoders.VmOpsTools

class Device {

    String name
    String uuid
    String hostName
    String ip
    Date dateCreated
    Date lastUpdated
    static belongsTo = [account:Account]
    static constraints = {
        uuid unique: true
    }
    static mapping = {
        id generator: "org.hibernate.id.enhanced.SequenceStyleGenerator", params: [initial_value:1001, increment_size:1]
    }

}