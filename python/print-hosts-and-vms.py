#!/usr/bin/env python

from psphere.client import Client
from psphere.managedobjects import VirtualMachine
from psphere.managedobjects import HostSystem
from getpass import getpass

usr = raw_input("User name: ")
passwd = getpass(prompt="Password: ")
server = raw_input("Server: ")
client = Client(server,usr,passwd)

hosts = HostSystem.all(client)
print "--------------------"
for host in hosts:
    print "Host %s" % (host.name)
    print "Virtual machines on %s" % (host.name)
    for vms in host.vm:
        print vms.name
    print "--------------------"
