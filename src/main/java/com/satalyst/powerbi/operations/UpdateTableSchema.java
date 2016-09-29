package com.satalyst.powerbi.operations;

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.satalyst.powerbi.PowerBiOperation;
import com.satalyst.powerbi.PowerBiOperationExecutionException;
import com.satalyst.powerbi.PowerBiRequest;
import com.satalyst.powerbi.PowerBiResponse;
import com.satalyst.powerbi.RateLimitExceededException;
import com.satalyst.powerbi.RequestAuthenticationException;
import com.satalyst.powerbi.model.Table;

/**
 * @author Aidan Morgan
 */
public class UpdateTableSchema implements PowerBiOperation<Void> {
    private UUID datasetId;
    private String tableName;
    private Table schema;
    private Gson parser;

    public UpdateTableSchema(UUID datasetId, String tableName, Table newSchema) {
        this.datasetId = checkNotNull(datasetId);
        this.tableName = checkNotNull(tableName);
        this.schema = checkNotNull(newSchema);
        this.parser = new Gson();
    }

    @Override
    public Void get() {
        return null;
    }

    @Override
    public void buildUri(UriBuilder uri) {
        uri.path("myorg").path("datasets").path(datasetId.toString()).path("tables").path(tableName);

    }

    // 200 for no change, 201 for a change made - need to handle both scenarios?
    private static List<Integer> SUCCESS_RESPONSES = Arrays.asList(200, 201);

    @Override
    public void execute(PowerBiRequest request) throws PowerBiOperationExecutionException, RateLimitExceededException, RequestAuthenticationException {
        PowerBiResponse response = request.put(parser.toJson(CreateDataset.createTableJson(schema)));

        if(!SUCCESS_RESPONSES.contains(response.getStatus())) {
            throw new PowerBiOperationExecutionException("Expected response of 201.", response.getStatus(), response.getBody());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UpdateTableSchema that = (UpdateTableSchema) o;
        return Objects.equal(datasetId, that.datasetId) &&
                Objects.equal(tableName, that.tableName) &&
                Objects.equal(schema, that.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(datasetId, tableName, schema);
    }
}
