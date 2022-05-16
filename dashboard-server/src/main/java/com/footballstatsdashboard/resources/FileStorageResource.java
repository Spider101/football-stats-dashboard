package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.services.FileStorageService;
import com.google.common.collect.ImmutableMap;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.InputStream;
import java.net.URI;

import static com.footballstatsdashboard.core.utils.Constants.FILE_UPLOAD_V1_BASE_PATH;

@Path(FILE_UPLOAD_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class FileStorageResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);
    private final FileStorageService fileUploadService;

    public FileStorageResource(FileStorageService fileUploadService) {
        this.fileUploadService = fileUploadService;
        this.fileUploadService.initializeService();
    }

    @POST
    @Path("/image")
    public Response uploadImage(
            @FormDataParam("image") InputStream imageFileStream,
            @FormDataParam("image") FormDataContentDisposition imageFileMetadata,
            @FormDataParam("image") FormDataBodyPart imageFileBody,
            @Context UriInfo uriInfo) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("uploadImage() request");
        }

        String fileKey = this.fileUploadService.storeFile(imageFileStream, imageFileMetadata.getFileName(),
                imageFileBody.getMediaType().toString(), imageFileMetadata.getSize());

        URI location = uriInfo.getAbsolutePathBuilder().path(fileKey).build();
        return Response.created(location).entity(ImmutableMap.of("fileKey", fileKey)).build();
    }
}