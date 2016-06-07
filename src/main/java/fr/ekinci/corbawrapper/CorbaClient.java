package fr.ekinci.corbawrapper;

import java.lang.reflect.Method;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/**
 * Corba wrapper class for client
 * 
 * @author Gokan EKINCI
 */
public class CorbaClient implements AutoCloseable {
    private final ORB orb;
    private final NamingContextExt ncRef;

    /**
     * CorbaClient constructor
     *
     * @param host
     * @param port
     * @throws CorbaException
     * which encapsulate: org.omg.CORBA.ORBPackage.InvalidName
     */
    public CorbaClient(String host, int port) throws CorbaException {
        try{
            // Initialize the ORB
            this.orb = ORB.init(
                new String[]{"-ORBInitialPort", String.valueOf(port), "-ORBInitialHost", host},
                null
            );

            // Initialize the NameService
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            this.ncRef = NamingContextExtHelper.narrow(objRef);
        } catch (org.omg.CORBA.ORBPackage.InvalidName e){
            throw new CorbaException(e);
        }
    }

    /**
     * Obtain service
     * 
     * @param serviceName
     * @param helpClass
     * @return remote object
     * @throws CorbaException
     * which encapsulate:
     *     java.lang.SecurityException
     *     java.lang.NoSuchMethodException
     *     java.lang.reflect.InvocationTargetException
     *     java.lang.IllegalAccessException
     *     org.omg.CosNaming.NamingContextPackage.NotFound
     *     org.omg.CosNaming.NamingContextPackage.CannotProceed
     *     org.omg.CosNaming.NamingContextPackage.InvalidName
     */
    public <SERVICE, HELP> SERVICE lookup(String serviceName, Class<HELP> helpClass) throws CorbaException {
        try {
            Method m = helpClass.getMethod("narrow", org.omg.CORBA.Object.class);
            return (SERVICE) m.invoke(null, ncRef.resolve_str(serviceName));
        } catch (java.lang.SecurityException
                | java.lang.NoSuchMethodException
                | java.lang.reflect.InvocationTargetException
                | java.lang.IllegalAccessException
                | org.omg.CosNaming.NamingContextPackage.NotFound
                | org.omg.CosNaming.NamingContextPackage.CannotProceed
                | org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            throw new CorbaException(e);
        }
    }
    

    /**
     * Stop the ORB (shutdown)
     */
    @Override
    public void close() throws Exception {
        orb.shutdown(false);       
    }
}

