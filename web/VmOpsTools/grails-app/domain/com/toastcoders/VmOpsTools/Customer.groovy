package com.toastcoders.VmOpsTools

class Customer {

    String name
    static hasMany = [contacts: Contact, account:Account]
    static constraints = {

    }
}