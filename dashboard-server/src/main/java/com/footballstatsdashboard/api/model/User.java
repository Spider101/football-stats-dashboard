package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.LocalDate;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableUser.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface User {
    /**
     * user ID
     */
    @Valid
    @Value.Default
    default UUID getId() { return UUID.randomUUID(); }

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
}
