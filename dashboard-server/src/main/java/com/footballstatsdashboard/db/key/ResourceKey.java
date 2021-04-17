package com.footballstatsdashboard.db.key;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ResourceKey {
    private final UUID resourceId;

    public ResourceKey(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ResourceKey otherResourceKey = (ResourceKey) other;
        return new EqualsBuilder()
                .append(this.resourceId, otherResourceKey.resourceId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(resourceId)
                .toHashCode();
    }
}
