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
 * @author Gugelhupf (eau-de-la-seine)
 */
public class CORBAServer{
    private ORB orb;
    private POA rootpoa;
    private NamingContextExt ncRef;

    public CORBAServer(String[] args) 
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive, 
            org.omg.CORBA.ORBPackage.InvalidName
    {
        this.orb = ORB.init(args, null);
        this.rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
        this.rootpoa.the_POAManager().activate();
        org.omg.CORBA.Object objRef = this.orb.resolve_initial_references("NameService");
        this.ncRef = NamingContextExtHelper.narrow(objRef);
    }

    /**
     * <p> Add a service </p>
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
        IMPL implementation, // L'intérêt ajouter ce paramètre est qu'il peut contenir des arguments
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
     * <p>Start the ORB</p>
     */
    public void run(){
        orb.run();
    }

    /**
     * <p>Shutdown ORB</p>
     */
    public void shutdown(){
        orb.shutdown(false);
    }

}
