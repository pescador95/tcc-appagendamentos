package app.core.trace;

import app.core.utils.BasicFunctions;


public class RemoteHostKeyGenerator {


    public String generateKey(String userAgent) {


        try {
            String key = "";

            if (BasicFunctions.isNotEmpty(userAgent)) {
                key = userAgent + "_";
            }
            return key;

        } catch (Exception e) {
            return null;
        }
    }
}
