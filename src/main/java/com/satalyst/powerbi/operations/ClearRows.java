package com.satalyst.powerbi.operations;

import static com.google.common.base.Preconditions.*;

import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import com.google.common.base.Objects;
import com.satalyst.powerbi.PowerBiOperation;
import com.satalyst.powerbi.PowerBiOperationExecutionException;
import com.satalyst.powerbi.PowerBiRequest;
import com.satalyst.powerbi.PowerBiResponse;
import com.satalyst.powerbi.RateLimitExceededException;
import com.satalyst.powerbi.RequestAuthenticationException;

/**
 * @author Aidan Morgan
 */
public class ClearRows implements PowerBiOperation<Void> {
    private UUID datasetId;
    private String tableName;

    public ClearRows(UUID datasetId, String tableName) {
        this.datasetId = checkNotNull(datasetId);
        this.tableName = checkNotNull(tableName);
    }

    @Override
    public Void get() {
        return null;
    }

    @Override
    public void buildUri(UriBuilder uri) {
        uri.path("myorg").path("datasets").path(datasetId.toString()).path("tables").path(tableName).path("rows");
    }

    @Override
    public void execute(PowerBiRequest request) throws PowerBiOperationExecutionException, RateLimitExceededException, RequestAuthenticationException {
       PowerBiResponse response = request.delete();

        if(response.getStatus() != 200) {
            throw new PowerBiOperationExecutionException("Expected 200 response.", response.getStatus(), response.getBody());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClearRows clearRows = (ClearRows) o;
        return Objects.equal(datasetId, clearRows.datasetId) &&
                Objects.equal(tableName, clearRows.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(datasetId, tableName);
    }
}
