package account;

import astric.HashUtil;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class HashUtilTest {
    @Test
    void testHash() {

        String result = HashUtil.hashPassword("mypass");
        System.out.println(result);
        assert result != null;
    }
}
