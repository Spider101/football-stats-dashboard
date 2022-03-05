package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.CountryFlagMetadata;
import com.footballstatsdashboard.services.CountryFlagsLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_FLAG_LOOKUP_V1_BASE_PATH;

@Path(COUNTRY_FLAG_LOOKUP_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class CountryFlagsLookupResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClubResource.class);

    private final CountryFlagsLookupService countryFlagsLookupService;

    public CountryFlagsLookupResource(CountryFlagsLookupService countryFlagsLookupService) {
        this.countryFlagsLookupService = countryFlagsLookupService;
    }

    @GET
    // TODO: 2/5/2022 consider rate limiting this endpoint if access without auth token is allowed
    public Response getCountryFlagMetadata() throws IOException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getCountryFlagMetadata() request made!");
        }
        List<CountryFlagMetadata> countryFlagMetadataList = this.countryFlagsLookupService.getCountryFlags();
        return Response.ok().entity(countryFlagMetadataList).build();
    }
}