package account;

import astric.server.lambda.account.HashUtil;
import org.junit.jupiter.api.Test;

public class HashUtilTest {
    @Test
    void testHash() {

        String result = HashUtil.hashPassword("mypass");
        System.out.println(result);
        assert result != null;
    }
}
