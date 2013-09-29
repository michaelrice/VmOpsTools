package com.toastcoders.VmOpsTools

class Account {

    String name
    static hasMany = [device:Device]
    static belongsTo = [customer:Customer]
    static constraints = {
    }
    static mapping = {
        id generator: "org.hibernate.id.enhanced.SequenceStyleGenerator", params: [initial_value:1000, increment_size:1]
    }
}