package com.footballstatsdashboard.api.deserializers;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class CustomInstantDeserializer extends InstantDeserializer<Instant> {

    public CustomInstantDeserializer() {
        super(
                Instant.class, DateTimeFormatter.ISO_INSTANT,
                Instant::from,
                a -> Instant.ofEpochMilli(a.value),
                a -> Instant.ofEpochSecond(a.integer, a.fraction),
                null,
                true // yes, replace +0000 with Z
        );
    }
}