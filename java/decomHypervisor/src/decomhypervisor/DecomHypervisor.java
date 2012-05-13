package decomhypervisor;

import com.vmware.vim25.InvalidState;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.Timedout;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author errr
 */
public class DecomHypervisor
{

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception
    {
        String server = args[0];
        String viuser  = args[1];
        String passwd = args[2];
        String hostName = args[3];

        ServiceInstance si = new ServiceInstance(new URL("https://" + server + "/sdk"),viuser,passwd,true);
        Folder rootFolder = si.getRootFolder();
        HostSystem host = ( HostSystem ) new InventoryNavigator(rootFolder).searchManagedEntity( "HostSystem", hostName);
        VirtualMachine[] vms = host.getVms();
        if(vms.length == 0) {
            remove(host);
        }
        else {
            // have to move vms to another host.
            System.out.println("Host still has virtual machines on it. Do something about that..");
        }
    }

    private static void remove(HostSystem hs) {
        try {
            String st = hs.enterMaintenanceMode( 0, true).waitForTask();
            if(!st.equalsIgnoreCase( "success") ) {
                System.out.println("Unable to enter maint mode. Please investigate.");
                System.exit( 1);
            }
            //System.out.println(st);
            //System.exit( 0);
            st = "";
            st = hs.shutdownHost_Task( true ).waitForTask();
            if(!st.equalsIgnoreCase( "success") ) {
                System.out.println("Unable to power off host. Please investigate.");
                System.exit(1);
            }
            st = "";
            st = hs.destroy_Task().waitForTask();
            if(!st.equalsIgnoreCase( "success") ) {
                System.out.println("Unable to remove host from vcenter. Please investigate");
                System.exit( 1);
            }
            System.out.println("Successfully removed host from vcenter and powered it off.");
        }
        catch ( InterruptedException ex ) {
            Logger.getLogger( DecomHypervisor.class.getName() ).log( Level.SEVERE , null , ex );
        }        catch ( Timedout ex ) {
            Logger.getLogger( DecomHypervisor.class.getName() ).log( Level.SEVERE , null , ex );
        }
        catch ( InvalidState ex ) {
            Logger.getLogger( DecomHypervisor.class.getName() ).log( Level.SEVERE , null , ex );
        }
        catch ( RuntimeFault ex ) {
            Logger.getLogger( DecomHypervisor.class.getName() ).log( Level.SEVERE , null , ex );
        }
        catch ( RemoteException ex ) {
            Logger.getLogger( DecomHypervisor.class.getName() ).log( Level.SEVERE , null , ex );
        }
    }
}