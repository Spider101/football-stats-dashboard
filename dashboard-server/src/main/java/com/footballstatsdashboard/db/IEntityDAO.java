package com.footballstatsdashboard.db;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface IEntityDAO<E> {
    void insertEntity(E entity);
    E getEntity(UUID entityId) throws EntityNotFoundException;
    void updateEntity(UUID existingEntityId, E updatedEntity);
    void deleteEntity(UUID entityId) throws EntityNotFoundException;
}