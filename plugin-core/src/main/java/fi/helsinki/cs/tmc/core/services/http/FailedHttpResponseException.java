package fi.helsinki.cs.tmc.core.services.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class FailedHttpResponseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final int statusCode;
    private final HttpEntity entity;

    public FailedHttpResponseException(final int statusCode, final HttpEntity entity) {

        super("Response code: " + statusCode);
        this.statusCode = statusCode;
        this.entity = entity;
    }

    public int getStatusCode() {

        return statusCode;
    }

    public HttpEntity getEntity() {

        return entity;
    }

    public String getEntityAsString() {

        try {
            return EntityUtils.toString(entity, "UTF-8");
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
