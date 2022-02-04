package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.CountryFlagMetadata;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.services.CountryFlagsLookupService;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

public class CountryFlagsLookupResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClubResource.class);

    private final CountryFlagsLookupService countryFlagsLookupService;

    public CountryFlagsLookupResource(CountryFlagsLookupService countryFlagsLookupService) {
        this.countryFlagsLookupService = countryFlagsLookupService;
    }

    @GET
    public Response getCountryFlagMetadata(
            @Auth User user) throws IOException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getCountryFlagMetadata() request made!");
        }
        List<CountryFlagMetadata> countryFlagMetadataList = this.countryFlagsLookupService.getCountryFlags();
        return Response.ok().entity(countryFlagMetadataList).build();
    }
}