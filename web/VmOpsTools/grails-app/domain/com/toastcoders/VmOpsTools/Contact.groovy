package com.toastcoders.VmOpsTools

class Contact {

    String firstName
    String lastName
    String email
    String phoneNumber
    String secondaryNumber
    String title
    String note
    boolean isPrimary = false
    static belongsTo = [customer:Customer]
    static constraints = {
        note type: 'text'
    }
}