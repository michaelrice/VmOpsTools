#!/usr/bin/env python

from psphere.client import Client
from psphere.managedobjects import HostSystem
from getpass import getpass

usr = raw_input("User name: ")
passwd = getpass(prompt="Password: ")
server = raw_input("Server: ")
client = Client(server,usr,passwd)
hs = HostSystem.all(client)
for host in hs:
    print host.name
print "There were " + str(len(hs)) + " host systems found"
