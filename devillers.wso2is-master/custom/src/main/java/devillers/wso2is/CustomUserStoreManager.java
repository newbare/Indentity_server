package devillers.wso2is;

import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.jdbc.JDBCUserStoreManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;

import javax.sql.DataSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


/**
 * Created by Martin Devillers on 27-2-2015.
 */
public class CustomUserStoreManager extends JDBCUserStoreManager {

    private static Log log = LogFactory.getLog(CustomUserStoreManager.class);

    public CustomUserStoreManager() {
    }

    public CustomUserStoreManager(RealmConfiguration realmConfig, int tenantId) throws UserStoreException {
        super(realmConfig, tenantId);
    }

    public CustomUserStoreManager(DataSource ds, RealmConfiguration realmConfig, int tenantId, boolean addInitData) throws UserStoreException {
        super(ds, realmConfig, tenantId, addInitData);
    }

    public CustomUserStoreManager(DataSource ds, RealmConfiguration realmConfig) throws UserStoreException {
        super(ds, realmConfig);
    }

    public CustomUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties, ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId) throws UserStoreException {
        super(realmConfig, properties, claimManager, profileManager, realm, tenantId);
    }

    public CustomUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties, ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId, boolean skipInitData) throws UserStoreException {
        super(realmConfig, properties, claimManager, profileManager, realm, tenantId, skipInitData);
    }

    @Override
    protected String preparePassword(String password, String saltValue) throws UserStoreException {
        try {
            String e = password;
            if(saltValue != null) {
                e = saltValue + password;
            }

            String digestFunction = (String)this.realmConfig.getUserStoreProperties().get("PasswordDigest");
            if(digestFunction != null) {
                if(digestFunction.equals("PLAIN_TEXT")) {
                    return password;
                }

                MessageDigest dgst = MessageDigest.getInstance(digestFunction);
                byte[] byteValue = dgst.digest(e.getBytes());

                password = bytesToHex(byteValue);
            }

            return password;
        } catch (NoSuchAlgorithmException var7) {
            throw new UserStoreException(var7.getMessage(), var7);
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
