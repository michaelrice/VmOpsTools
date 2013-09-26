package com.toastcoders.VmOpsTools

class Customer {

    String name
    static hasMany = [contacts: Contact, accounts:Account]
    static constraints = {
        
    }
}