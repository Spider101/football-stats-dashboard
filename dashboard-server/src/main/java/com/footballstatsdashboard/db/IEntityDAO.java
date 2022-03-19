package com.footballstatsdashboard.db;

import java.util.UUID;

public interface IEntityDAO<E> {
    void insertEntity(E entity);
    E getEntity(UUID entityId);
    void updateEntity(E updatedEntity);
    void deleteEntity(UUID entityId);
}