package com.footballstatsdashboard.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.footballstatsdashboard.api.model.CountryCodeMetadata;
import com.footballstatsdashboard.api.model.CountryFlagMetadata;
import com.footballstatsdashboard.api.model.ImmutableCountryFlagMetadata;
import com.footballstatsdashboard.core.utils.FixtureLoader;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_CODE_MAPPING_FNAME;
import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_FLAG_URL_TEMPLATE;

public class CountryFlagsLookupService {
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(Jackson.newObjectMapper().copy());

    public List<CountryFlagMetadata> getCountryFlags() throws IOException {
        TypeReference<List<CountryCodeMetadata>> countryCodeMetadataTypeRef = new TypeReference<>() { };
        List<CountryCodeMetadata> countryCodeMetadataList =
                FIXTURE_LOADER.loadFixture(COUNTRY_CODE_MAPPING_FNAME, countryCodeMetadataTypeRef);
        return countryCodeMetadataList.stream()
                .map(countryCodeMetadata -> ImmutableCountryFlagMetadata.builder()
                        .countryCode(countryCodeMetadata.getCountryCode())
                        .countryName(countryCodeMetadata.getCountryName())
                        .countryFlagUrl(String.format(COUNTRY_FLAG_URL_TEMPLATE, countryCodeMetadata.getCountryCode()))
                        .build()
                ).collect(Collectors.toList());
    }
}