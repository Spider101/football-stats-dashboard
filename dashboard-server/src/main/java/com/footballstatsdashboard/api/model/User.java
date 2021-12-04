package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.footballstatsdashboard.core.utils.InternalField;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableUser.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface User extends Principal {
    int MIN_PASSWORD_LENGTH = 3;
    int MAX_PASSWORD_LENGTH = 16;

    /**
     * user ID
     */
    @Valid
    @Value.Default
    default UUID getId() {
        return UUID.randomUUID();
    }

    /**
     * user's first name
     */
    @Valid
    @Nullable
    String getFirstName();

    /**
     * user's last name
     */
    @Valid
    @Nullable
    String getLastName();

    /**
     * user's email ID
     */
    @Valid
    @Email
    String getEmail();

    /**
     * user's role when accessing the API
     */
    @Nullable
    @InternalField
    String getRole();

    /**
     * user's password
     */
    @Valid
    @NotNull
    @Size(min = MAX_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH,
            message = "cannot be less than 3 or more than 16 characters")
    String getPassword();

    /**
     * represents entity that created the user
     */
    @Nullable
    @InternalField
    String getCreatedBy();

    /**
     * timestamp when user data was created
     */
    @Valid
    @Nullable
    @InternalField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getCreatedDate();

    /**
     * timestamp when user data was last modified
     */
    @Valid
    @Nullable
    @InternalField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getLastModifiedDate();

    @Override
    @JsonIgnore
    @Value.Derived
    default String getName() {
        return getEmail();
    }

    /**
     * represent the type of entity
     */
    @Nullable
    @InternalField
    @Value.Default
    default String getType() {
        return "User";
    }
}