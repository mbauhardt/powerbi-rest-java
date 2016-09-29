package com.satalyst.powerbi.operations;

import static com.google.common.base.Preconditions.*;
import static com.satalyst.powerbi.operations.MapUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.satalyst.powerbi.PowerBiOperation;
import com.satalyst.powerbi.PowerBiOperationExecutionException;
import com.satalyst.powerbi.PowerBiRequest;
import com.satalyst.powerbi.PowerBiResponse;
import com.satalyst.powerbi.RateLimitExceededException;
import com.satalyst.powerbi.RequestAuthenticationException;
import com.satalyst.powerbi.TableSchemaLoader;
import com.satalyst.powerbi.impl.model.DefaultDataset;
import com.satalyst.powerbi.model.Column;
import com.satalyst.powerbi.model.Dataset;
import com.satalyst.powerbi.model.RetentionPolicy;
import com.satalyst.powerbi.model.Table;

/**
 * @author Aidan Morgan
 */
public class CreateDataset implements PowerBiOperation<Dataset> {
    private String name;
    private List<Table> tables;
    private RetentionPolicy retentionPolicy;
    private Dataset result;

    private Gson parser;

    public CreateDataset(String name, List<Table> tables) {
        this(name, tables, RetentionPolicy.NONE);
    }

    public CreateDataset(String name, TableSchemaLoader loader) {
        this(name, loader.getTables(), RetentionPolicy.NONE);
    }

    public CreateDataset(String name, TableSchemaLoader loader, RetentionPolicy policy) {
        this(name, loader.getTables(), policy);
    }

    public CreateDataset(String name, List<Table> tables, RetentionPolicy defaultRetentionPolicy) {
        this.name = checkNotNull(name);
        this.tables = checkNotNull(tables);
        this.retentionPolicy = defaultRetentionPolicy;

        this.parser = new Gson();
    }

    @Override
    public Dataset get() {
        return result;
    }

    @Override
    public void buildUri(UriBuilder uri) {
        uri.path("myorg").path("datasets").queryParam("defaultRetentionPolicy", retentionPolicy.getName());
    }

    @Override
    public void execute(PowerBiRequest request) throws PowerBiOperationExecutionException, RateLimitExceededException, RequestAuthenticationException {
        PowerBiResponse response = request.post(createRequestJson(tables));

        if (response.getStatus() != 201) {
            throw new PowerBiOperationExecutionException("Expected status code of 200.", response.getStatus(), response.getBody());
        }

        result = parseResponseJson(response.getBody());
    }

    private String createRequestJson(List<Table> tables) {
        List<Map<String, Object>> tableList = new ArrayList<>();

        for (Table t : tables) {
            Map<String, Object> table = createTableJson(t);
            tableList.add(table);
        }

        Map<String, Object> out = new HashMap<>();
        out.put("name", name);
        out.put("tables", tableList);

        return parser.toJson(out);
    }

    // TODO : refactor this to a better location
    public static Map<String, Object> createTableJson(Table t) {
        Map<String, Object> table = new HashMap<>();

        table.put("name", t.getName());

        List<Map<String, Object>> columnList = new ArrayList<>();
        table.put("columns", columnList);

        for (Column col : t.getColumns()) {
            Map<String, Object> column = new HashMap<>();
            columnList.add(column);

            column.put("name", col.getName());
            column.put("dataType", col.getColumnType().getName());
        }
        return table;
    }

    private Dataset parseResponseJson(String json) {
        Map result = parser.fromJson(json, Map.class);
        return new DefaultDataset(getString(result, "name"), getUuid(result, "id"));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final CreateDataset that = (CreateDataset) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(tables, that.tables) &&
                retentionPolicy == that.retentionPolicy;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, tables, retentionPolicy);
    }
}
