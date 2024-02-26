package com.acmebannk;

import com.acmebank.AccountManagerUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountManagerUtilTest {
    @Test
    public void testValidateUUID() {
        assertEquals(true, AccountManagerUtil.validateUUID("5cb2786a-b2c8-4a24-8334-898c1874be8a"));
        assertEquals(false, AccountManagerUtil.validateUUID("5cb2786a-b2c8-4a24-8334-898c1874be8"));
    }
}
