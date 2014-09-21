package fr.ekinci.corbawrapper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/** 
 * @author Gugelhupf (eau-de-la-seine)
 */
public class CORBAClient{
    private ORB orb;
    private NamingContextExt ncRef;
    
    public CORBAClient(String[] args) throws org.omg.CORBA.ORBPackage.InvalidName {
        this.orb = ORB.init(args, null);
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        this.ncRef = NamingContextExtHelper.narrow(objRef);
    }

    /**
     * <p>Retrieve service</p>
     * 
     * @param serviceName
     * @param helpClass
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws org.omg.CosNaming.NamingContextPackage.NotFound
     * @throws org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @throws org.omg.CosNaming.NamingContextPackage.InvalidName
     */
    public <SERVICE, HELP> SERVICE lookup(String serviceName, Class<HELP> helpClass) 
        throws SecurityException, 
            NoSuchMethodException, 
            InvocationTargetException,
            IllegalAccessException,
            org.omg.CosNaming.NamingContextPackage.NotFound, 
            org.omg.CosNaming.NamingContextPackage.CannotProceed, 
            org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        Method m = helpClass.getMethod("narrow", org.omg.CORBA.Object.class);
        return (SERVICE) m.invoke(null, ncRef.resolve_str(serviceName));
    }
}
