_CORBA wrapper for easier development_

## Introduction

corbawrapper is a **Java project**.

CORBA is an architecture designed by OMG to facilitate the communication of systems that are deployed on diverse platforms, but there is 2 repulsives facts to choose this technology :
* The chosen port can be blocked by firewalls, so people prefer to use webservices like REST which use HTTP protocol.
* The syntax is hard to understand.

In spite of those disadvantages, CORBA use interoperable components, and its IIOP protocol is stream based. I cannot configure your firewalls for CORBA, but I can create a wrapper in order to use CORBA easily.

## How to use **corba-wrapper**

### 1. IDL

Generate your IDL with : _**>idlj -fall Hello.idl**_
```
module HelloApp {
  interface Hello {
      string sayHello();
      oneway void shutdown();
  };
};
```
Thanks to your IDL contract file and the _**ildj**_ command line you will generate several java files : _Hello.java_, _HelloHelper.java_, _HelloHolder.java_, _HelloOperations.java_, _HelloPOA.java_, _\_HelloStub.java_.

Please see Sun/Oracle documentation for more information about generated files.

### 2. Implementation Class

Now create your Hello implementation class
```
public class HelloImpl extends HelloPOA{

    @Override
    public String sayHello() {
        return "Hello";
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
    }
}
```

### 3. Create Corba Server
```
import fr.ekinci.corbawrapper.CorbaServer;
import HelloApp.HelloHelper;
import HelloApp.HelloImpl;

public class CorbaServerDemo {
    public static void main(String args[]) throws Exception {
        CorbaServer server = new CorbaServer("127.0.0.1", 1050);
        server.addService("Hello", new HelloImpl(), HelloHelper.class);
        server.run();
    }
}
```

### 4. Create Corba Client
```
import fr.ekinci.corbawrapper.CorbaClient;
import HelloApp.Hello;
import HelloApp.HelloHelper;

public class CorbaClientDemo {
    public static void main(String args[]) throws Exception {
        CorbaClient client = new CorbaClient("127.0.0.1", 1050);
        Hello hello = client.<Hello, HelloHelper>lookup("Hello", HelloHelper.class);
        System.out.println(hello.sayHello());
    }
}
```

### 5. Launch ORBD first, then server-side program, and finally client-side program

First launch your ORDB (server-side command):
* For Linux : orbd -ORBInitialPort 1050 -ORBInitialHost localhost&
* For Windows : start orbd -ORBInitialPort 1050 -ORBInitialHost localhost

Note about ORBD:
* If you are under Linux, you may encounter a problem when you are using RMI or CORBA protocols on the server side, in order to resolve this problem, change your /etc/hosts file localhost IP (127.0.0.1) with the real one.
* If you are under Linux and if your ORBD is already running, for stoping process you may see its PID with "ps aux" command, then kill its PID.


To learn more about CORBA :
* OMG : http://www.omg.org/
* Wikipedia : http://en.wikipedia.org/wiki/Common_Object_Request_Broker_Architecture