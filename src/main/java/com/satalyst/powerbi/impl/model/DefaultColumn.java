package com.satalyst.powerbi.impl.model;

import com.google.common.base.Objects;
import com.satalyst.powerbi.model.Column;
import com.satalyst.powerbi.model.ColumnType;

/**
 * @author Aidan Morgan
 */
public class DefaultColumn implements Column {
    private String name;
    private ColumnType columnType;

    public DefaultColumn(String name, ColumnType columnType) {
        this.name = name;
        this.columnType = columnType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ColumnType getColumnType() {
        return columnType;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultColumn that = (DefaultColumn) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(columnType, that.columnType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, columnType);
    }
}
