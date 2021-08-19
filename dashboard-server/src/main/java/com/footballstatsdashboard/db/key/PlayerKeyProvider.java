package com.footballstatsdashboard.db.key;

/**
 * Key Provider for Player resource
 * @author Abhimanyu Banerjee
 */
public class PlayerKeyProvider extends BaseKeyProvider {

    @Override
    protected String getResourceName() {
        return "player";
    }
}