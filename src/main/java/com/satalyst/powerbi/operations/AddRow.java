package com.satalyst.powerbi.operations;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.satalyst.powerbi.model.Column;

/**
 * @author Aidan Morgan
 */
public class AddRow implements PowerBiOperation<Void> {
    private UUID datasetId;
    private String tableName;
    private List<Column> types;
    private List<List<Object>> values;

    private Gson parser;

    public AddRow(UUID datasetId, String tableName, List<Column> types) {
        this.datasetId = checkNotNull(datasetId);
        this.tableName = checkNotNull(tableName);
        this.types = checkNotNull(types);
        this.values = new ArrayList<>();

        this.parser = new Gson();
    }

    public AddRow addRow(List<Object> row) {
        values.add(row);
        return this;
    }

    public int size() {
        return values.size();
    }

    public void clear() {
        values.clear();
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
        PowerBiResponse response = request.post(createRequestJson());

        if (response.getStatus() != 200) {
            throw new PowerBiOperationExecutionException("Expected 200 response.", response.getStatus(), response.getBody());
        }
    }

    private String createRequestJson() {
        List<Map<String, Object>> rowCollection = new ArrayList<>();

        for (List<Object> valueList : values) {
            Map<String, Object> row = new HashMap<>();

            for (int j = 0; j < types.size(); j++) {
                Column ct = types.get(j);
                Object value = valueList.get(j);

                row.put(ct.getName(), ct.getColumnType().parse(value));
            }

            rowCollection.add(row);
        }

        Map<String, Object> out = new HashMap<>();
        out.put("rows", rowCollection);

        return parser.toJson(out);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AddRow addRow = (AddRow) o;
        return Objects.equal(datasetId, addRow.datasetId) &&
                Objects.equal(tableName, addRow.tableName) &&
                Objects.equal(types, addRow.types);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(datasetId, tableName, types);
    }
}
