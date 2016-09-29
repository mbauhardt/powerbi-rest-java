package com.satalyst.powerbi.impl.model;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;
import com.satalyst.powerbi.model.Column;
import com.satalyst.powerbi.model.Table;

/**
 * @author Aidan Morgan
 */
public class DefaultTable implements Table {
    private String name;
    private List<Column> columns;

    public DefaultTable(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    public DefaultTable addColumn(Column col) {
        columns.add(checkNotNull(col));

        return this;
    }

    @Override
    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultTable that = (DefaultTable) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, columns);
    }
}
