import org.wso2.carbon.um.ws.service.AddUser;
import org.wso2.carbon.um.ws.service.RemoteUserStoreManagerServicePortType;
import org.wso2.carbon.um.ws.service.RemoteUserStoreManagerServiceUserStoreException_Exception;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.ConnectException;
import java.util.List;
import java.util.Map;

/**
 * USER : Dileepa
 * DATE : 5/7/15
 * TIME : 11:00 PM
 */
public class ISMain
{

    public static final String REMOTE_USER_STORE_MANAGER_SERVICE = "https://localhost:9443/services/RemoteUserStoreManagerService?wsdl";
    public static final String REMOTE_USER_STORE_MANAGER_NAMESPACE_URI = "http://service.ws.um.carbon.wso2.org";
    public static final String REMOTE_USER_STORE_MANAGER_SOAP11_PORT = "RemoteUserStoreManagerServiceHttpsSoap11Endpoint";


    public static void main(String[] args)
    {
        System.setProperty("javax.net.ssl.trustStore", "/usr/local/vega/dev/app/wso2is-5.0.0/repository/resources/security/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        URL urlWsdl = null;
        try
        {
            urlWsdl = new URL( REMOTE_USER_STORE_MANAGER_SERVICE );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }


        QName serviceQName = new QName( REMOTE_USER_STORE_MANAGER_NAMESPACE_URI, "RemoteUserStoreManagerService" );
        ServiceDelegate delegate = Provider.provider().createServiceDelegate( urlWsdl, serviceQName, RemoteUserStoreManagerServicePortType.class );
//        Class provideClass = Class.forName( "com.sun.xml.internal.ws.spi.ProviderImpl" );
//        Provider provider = (Provider) provideClass.newInstance();
//        ServiceDelegate delegate = provider.createServiceDelegate( urlWsdl, serviceQName, RemoteUserStoreManagerServicePortType.class );

        QName portQName = new QName( REMOTE_USER_STORE_MANAGER_NAMESPACE_URI, REMOTE_USER_STORE_MANAGER_SOAP11_PORT );
        RemoteUserStoreManagerServicePortType remoteUserStoreManagerService = delegate.getPort( portQName, RemoteUserStoreManagerServicePortType.class );
        Map<String, Object> ctxt = ( (BindingProvider) remoteUserStoreManagerService ).getRequestContext();
        ctxt.put( BindingProvider.USERNAME_PROPERTY, "admin" );
        ctxt.put( BindingProvider.PASSWORD_PROPERTY, "admin" );

        AddUser newUser = new AddUser();
        newUser.setUserName( "dileepa" );
        newUser.setRequirePasswordChange(false);
        newUser.setCredential("123");
        newUser.getRoleLists().add("USER_ROLE_TEST"); //it should existing role in DB, unless use addRole method for add a role.
        try
        {
            remoteUserStoreManagerService.addUser(newUser);
        }
        catch (RemoteUserStoreManagerServiceUserStoreException_Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            List<String> users = remoteUserStoreManagerService.listUsers( "*", 100 );
            for( String user : users )
            {
                System.out.println( "User : " + user );
            }
        }
        catch( RemoteUserStoreManagerServiceUserStoreException_Exception e )
        {
            e.printStackTrace();
        }

    }
}
