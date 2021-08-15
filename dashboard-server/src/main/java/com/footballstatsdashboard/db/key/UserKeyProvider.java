package com.footballstatsdashboard.db.key;

/**
 * Key Provider for User resource
 * @author Abhimanyu Banerjee
 */
public class UserKeyProvider extends BaseKeyProvider {

    @Override
    protected String getResourceName() {
        return "user";
    }
}