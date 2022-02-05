package com.footballstatsdashboard.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.footballstatsdashboard.api.model.CountryCodeMetadata;
import com.footballstatsdashboard.api.model.CountryFlagMetadata;
import com.footballstatsdashboard.core.utils.FixtureLoader;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_CODE_MAPPING_FNAME;
import static org.junit.Assert.assertEquals;

public class CountryFlagsLookupServiceTest {
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(Jackson.newObjectMapper().copy());
    private CountryFlagsLookupService countryFlagsLookupService;

    @Before
    public void setup() {
        countryFlagsLookupService = new CountryFlagsLookupService();
    }

    @Test
    public void getCountryFlags() throws IOException {
        // setup
        TypeReference<List<CountryCodeMetadata>> countryCodeMetadataTypeRef = new TypeReference<>() { };
        List<CountryCodeMetadata> countryCodeMetadataList =
                FIXTURE_LOADER.loadFixture(COUNTRY_CODE_MAPPING_FNAME, countryCodeMetadataTypeRef);

        // execute
        List<CountryFlagMetadata> countryFlagMetadataList = countryFlagsLookupService.getCountryFlags();

        // assert
        assertEquals(countryCodeMetadataList.size(), countryFlagMetadataList.size());
    }
}