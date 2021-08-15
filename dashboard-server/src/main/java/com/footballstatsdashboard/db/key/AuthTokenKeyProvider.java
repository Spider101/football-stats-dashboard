package com.footballstatsdashboard.db.key;

/**
 * Key Provider for Auth Token resource
 * @author abhimanyu.banerjee
 */
public class AuthTokenKeyProvider extends BaseKeyProvider {

    @Override
    String getResourceName() {
        return "authToken";
    }
}