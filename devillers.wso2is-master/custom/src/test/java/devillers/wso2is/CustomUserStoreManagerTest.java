package devillers.wso2is;

import junit.framework.TestCase;
import org.wso2.carbon.user.api.RealmConfiguration;

import java.util.HashMap;
import java.util.Map;

public class CustomUserStoreManagerTest extends TestCase {

    public void testPreparePassword() throws Exception {
        RealmConfiguration realmConfig = new RealmConfiguration();
        Map<String, String> userStoreProperties = new HashMap<String, String>();
        userStoreProperties.put("PasswordDigest", "SHA-256");
        realmConfig.setUserStoreProperties(userStoreProperties);
        CustomUserStoreManager sut = new CustomUserStoreManager(realmConfig, 0);

        String password = "mijnmoc01";
        String expected = "0648DF0FA55E129F3BC6BF473C478BEF76446981B4A483A5D7572544D71214DB";
        String salt = "gf59";

        String actual = sut.preparePassword(password, salt);

        assertEquals(expected, actual);
    }
}