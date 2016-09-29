package com.satalyst.powerbi.operations;

import static com.google.common.base.Preconditions.*;
import static com.satalyst.powerbi.operations.MapUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import com.google.common.base.Objects;
import com.google.gson.Gson;

/**
 * @author Aidan Morgan
 */
public class ListTables extends AbstractGetOperation<List<String>> {
    private UUID datasetId;

    public ListTables(UUID datasetId) {
        this.datasetId = checkNotNull(datasetId);
    }

    @Override
    public void buildUri(UriBuilder uri) {
        uri.path("myorg").path("datasets").path(datasetId.toString()).path("tables");

    }

    protected List<String> parseJson(Gson parser, String s) {
        List<String> result = new ArrayList<>();

        Map parsed = parser.fromJson(s, Map.class);

        List<Map> tables = getList(parsed, "value");

        for (Map table : tables) {
            result.add(getString(table, "name"));
        }

        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ListTables that = (ListTables) o;
        return Objects.equal(datasetId, that.datasetId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(datasetId);
    }
}
