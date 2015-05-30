package fr.ekinci.corbawrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;


/**
 * Corba wrapper class for server
 * Note : Don't forget to launch the ORBD (or tnameserv) program before launching your Java program
 *     For Linux   : orbd -ORBInitialPort 1050 -ORBInitialHost localhost&
 *     For Windows : start orbd -ORBInitialPort 1050 -ORBInitialHost localhost
 * 
 * @author Gokan EKINCI
 */
public class CORBAServer implements AutoCloseable {
    private final ORB orb;
    private final POA rootpoa;
    private final NamingContextExt ncRef;

    public CORBAServer(String host, int port) 
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive, 
            org.omg.CORBA.ORBPackage.InvalidName
    {
        
        // Initialize the ORB
        this.orb = ORB.init(
            new String[]{"-ORBInitialPort", String.valueOf(port), "-ORBInitialHost", host}, 
            null
        );

        // Initialize the RootPOA
        org.omg.CORBA.Object objPoa = orb.resolve_initial_references("RootPOA");
        this.rootpoa = POAHelper.narrow(objPoa);
        this.rootpoa.the_POAManager().activate();

        // Initialize the NameService
        org.omg.CORBA.Object objRef = this.orb.resolve_initial_references("NameService");
        this.ncRef = NamingContextExtHelper.narrow(objRef);
    }

    /**
     * Add service
     * 
     * @param serviceName
     * @param implementation
     * @param helpClass
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws org.omg.CosNaming.NamingContextPackage.InvalidName
     * @throws org.omg.CosNaming.NamingContextPackage.NotFound
     * @throws org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @throws org.omg.PortableServer.POAPackage.ServantNotActive
     * @throws org.omg.PortableServer.POAPackage.WrongPolicy
     */
    public <IMPL extends Servant, HELP> void addService(
        String serviceName,
        IMPL implementation,
        Class<HELP> helpClass    
    ) 
        throws IllegalArgumentException, 
            SecurityException, 
            InstantiationException, 
            IllegalAccessException, 
            InvocationTargetException, 
            NoSuchMethodException, 
            org.omg.CosNaming.NamingContextPackage.InvalidName, 
            org.omg.CosNaming.NamingContextPackage.NotFound, 
            org.omg.CosNaming.NamingContextPackage.CannotProceed, 
            org.omg.PortableServer.POAPackage.ServantNotActive,
            org.omg.PortableServer.POAPackage.WrongPolicy
    {
        org.omg.CORBA.Object ref = this.rootpoa.servant_to_reference(implementation);
        Method m = helpClass.getMethod("narrow", org.omg.CORBA.Object.class);
        org.omg.CORBA.Object href = (org.omg.CORBA.Object) m.invoke(null, ref);
        NameComponent path[] = this.ncRef.to_name( serviceName );
        this.ncRef.rebind(path, href);
    }

    
    /**
     *  Start the ORB
     */
    public void run(){
        orb.run();
    }

    
    /**
     * Stop the ORB (shutdown)
     */
    @Override
    public void close() throws Exception {
        orb.shutdown(false);       
    }

}
