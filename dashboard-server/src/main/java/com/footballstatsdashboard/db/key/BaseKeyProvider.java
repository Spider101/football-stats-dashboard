package com.footballstatsdashboard.db.key;

import static com.footballstatsdashboard.core.utils.Constants.APPLICATION_NAME;

/**
 * Abstract key provider class for all resources
 * @author Abhimanyu Banerjee
 */
public abstract class BaseKeyProvider implements CouchbaseKeyProvider<ResourceKey> {

    @Override
    public String getCouchbaseKey(ResourceKey key) {
        return String.format("%s::%s::%s", APPLICATION_NAME, getResourceName(), key.getResourceId());
    }

    /**
     * The name of the resource the key is used for
     * @return resource name
     */
    abstract String getResourceName();
}
