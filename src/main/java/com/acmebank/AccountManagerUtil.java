package com.acmebank;

import java.util.regex.Pattern;

public class AccountManagerUtil {
    private static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean validateUUID(String uuid) {
        return UUID_REGEX.matcher(uuid).matches();
    }
}
