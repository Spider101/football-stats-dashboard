package com.footballstatsdashboard.core.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

    @Override
    public Response toResponse(ServiceException serviceException) {
        String exceptionMessage = serviceException.getResponseBody() != null
                ? serviceException.getResponseBody() : serviceException.getMessage();
        return Response
                .status(serviceException.getResponseStatus())
                .entity(exceptionMessage)
                .build();
    }
}