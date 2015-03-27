/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.dynamic;

import java.util.Collections;
import java.util.Set;

import io.realm.internal.ColumnType;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.Table;

/**
 * Class for interacting with a schema for a given RealmClass. This makes it possible to add, delete
 * or change the fields or objects for given class.
 *
 * This is used when migrating between different versions of a Realm.
 *
 * @see io.realm.RealmMigration
 */
public class RealmObjectSchema {

    private static final String TABLE_PREFIX = "class_";
    private final Table table;
    private final ImplicitTransaction transaction;

    /**
     * Creates a schema object for a given Realm class.
     * @param table Table representation of the Realm class
     */
    RealmObjectSchema(ImplicitTransaction transaction, Table table) {
        this.transaction = transaction;
        this.table = table;
    }

    private String getClassName() {
        return table.getName().substring(TABLE_PREFIX.length());
    }

    public RealmObjectSchema addString(String fieldName) {
        return addString(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addString(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.STRING, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addShort(String fieldName) {
        checkEmpty(fieldName);
        return addShort(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addShort(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.INTEGER, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addInt(String fieldName) {
        return addInt(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addInt(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.INTEGER, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addLong(String fieldName) {
        return addLong(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addLong(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.INTEGER, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addBoolean(String fieldName) {
        return addBoolean(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addBoolean(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.BOOLEAN, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addByteArray(String fieldName) {
        return addByteArray(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addByteArray(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.BINARY, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addFloat(String fieldName) {
        return addFloat(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addFloat(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.FLOAT, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addDouble(String fieldName) {
        return addDouble(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addDouble(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.DOUBLE, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addDate(String fieldName) {
        return addDate(fieldName, Collections.EMPTY_SET);
    }

    public RealmObjectSchema addDate(String fieldName, Set<RealmModifier> modifiers) {
        checkEmpty(fieldName);
        long columnIndex = table.addColumn(ColumnType.DATE, fieldName);
        setModifiers(columnIndex, modifiers);
        return this;
    }

    public RealmObjectSchema addObject(String fieldName, RealmObjectSchema schema) {
        checkEmpty(fieldName);
        table.addColumnLink(ColumnType.LINK, fieldName, transaction.getTable(TABLE_PREFIX + schema.getClassName()));
        return this;
    }

    public RealmObjectSchema addList(String fieldName, RealmObjectSchema schema) {
        checkEmpty(fieldName);
        table.addColumnLink(ColumnType.LINK_LIST, fieldName, transaction.getTable(TABLE_PREFIX + schema.getClassName()));
        return this;
    }

    public RealmObjectSchema removeField(String fieldName) {
        checkEmpty(fieldName);
        long columnIndex = table.getColumnIndex(fieldName);
        if (columnIndex == -1) {
            throw new IllegalArgumentException(
                    String.format("Fieldname '%s' does not exist on schema for '%s",
                    fieldName, getClassName()
            ));
        }
        table.removeColumn(columnIndex);
        return this;
    }

    public RealmObjectSchema renameField(String oldFieldName, String newFieldName) {
        return this;
    }

    public RealmObjectSchema addIndex(String fieldName) {
        return this;
    }

    public RealmObjectSchema removeIndex(String fieldName) {
        return this;
    }

    public RealmObjectSchema addPrimaryKey(String fieldName) {
        return this;
    }

    public RealmObjectSchema removePrimaryKey(String fieldName) {
        return this;
    }

    public DynamicRealmObject createObject() {
        return null;
    }

    public RealmObjectSchema forEach(Iterator iterator) {
        return this;
    }

    private void checkEmpty(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Fieldname must not be null or empty");
        }
    }

    public void setModifiers(long columnIndex, Set<RealmModifier> modifiers) {
        if (modifiers != null && modifiers.size() > 0) {
            if (modifiers.contains(RealmModifier.INDEXED)) {
                table.setIndex(columnIndex);
            }

            if (modifiers.contains(RealmModifier.PRIMARY_KEY)) {
                table.setPrimaryKey(columnIndex);
            }
        }
    }

    /**
     * Iterator used to traverse all objects with a given schema.
     * Objects are unsorted.
     */
    public interface Iterator {
        /**
         * Manipulate a given RealmObject. Due to the nature of objects being volatile during migrations, these objects are not typed
         * but are instead manipulated using a dynamic API, ie. all fields must accessed using their string
         * representation.
         *
         * @param object {@link DynamicRealmObject} representation of a particular object.
         */
        public void next(DynamicRealmObject object);
    }
}
