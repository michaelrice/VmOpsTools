#!/usr/bin/env perl
#
# Simple script to run from icinga/nagios to monitor the API
# of a vshpere host.
#
use warnings;
use strict;

BEGIN { $ENV{PERL_LWP_SSL_VERIFY_HOSTNAME} = 0 }

use VMware::VIRuntime;

Opts::parse();
Opts::validate();
eval {
    Util::connect();
    my $service_instance = Vim::get_service_instance();
    &set_time($service_instance->CurrentTime());
    Util::disconnect();
};

if($@) {
    print "API FAIL - error: / $!;\n";
    exit 2;
}

my $time;
sub set_time() {
    $time = shift;
}

print "API OK - time: / $time;\n";
