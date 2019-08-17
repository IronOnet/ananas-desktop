package org.ananas.runner.steprunner.jdbc.derby;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.ananas.runner.steprunner.jdbc.DDL;
import org.ananas.runner.steprunner.jdbc.JDBCDataType;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.FieldType;

public enum DerbyDataTypes implements JDBCDataType, DDL {

  // See https://db.apache.org/derby/docs/10.2/ref/crefsqlj31068.html
  BIGINT("bigint", Schema.FieldType.INT64.withNullable(true), true),
  BIGSERIAL("bigserial", Schema.FieldType.INT64.withNullable(true), true),
  BIT("bit", Schema.FieldType.STRING, false),
  BITVAR("bit varying", Schema.FieldType.STRING, false),
  BOOLEAN("boolean", Schema.FieldType.BOOLEAN.withNullable(true), true),
  BOOL("bool", Schema.FieldType.BOOLEAN, false),
  BYTE("byte", Schema.FieldType.BYTE, false),
  BYTES("bytes", Schema.FieldType.BYTE.withNullable(true), true),
  CHAR("char", Schema.FieldType.STRING, false),
  BPCHAR("bpchar", Schema.FieldType.STRING, false),
  CHARVAR("character varying", Schema.FieldType.STRING, false),
  CHARACTER("character", Schema.FieldType.STRING, false),
  DATE_metadata(
      "date", Schema.FieldType.DATETIME.withMetadata("subtype", "DATE").withNullable(true), true),
  TIME_metadata(
      "time", Schema.FieldType.DATETIME.withMetadata("subtype", "TIME").withNullable(true), true),
  TIMESTAMP_metadata(
      "timestamp",
      Schema.FieldType.DATETIME.withMetadata("subtype", "TS").withNullable(true),
      true),
  TIMESTAMP_WITHOUT_TS_metadata(
      "timestamp without time zone",
      Schema.FieldType.DATETIME.withMetadata("subtype", "TS").withNullable(true),
      true),
  TIMESTAMP_WITH_TIME_ZONE_metadata(
      "timestamp with time zone",
      Schema.FieldType.DATETIME.withMetadata("subtype", "TS_WITH_LOCAL_TZ").withNullable(true),
      true),
  TIMESTAMPZ_metadata(
      "timestamptz", Schema.FieldType.DATETIME.withMetadata("subtype", "TS_WITH_LOCAL_TZ"), false),
  DECIMAL00("decimal(29,2)", Schema.FieldType.DECIMAL.withNullable(true), true),
  DECIMAL("decimal", Schema.FieldType.DECIMAL, false),
  REAL("real", Schema.FieldType.FLOAT, false),
  NUMERIC("numeric", Schema.FieldType.DECIMAL, false),
  FLOAT("float", FieldType.FLOAT.withNullable(true), true),
  DOUBLE("double", Schema.FieldType.DOUBLE.withNullable(true), true),
  DOUBLE_PRECISION("double precision", Schema.FieldType.DOUBLE.withNullable(true), false),
  INT("int", Schema.FieldType.INT32, false),
  INTEGER("integer", Schema.FieldType.INT32.withNullable(true), true),
  INT8("int8", Schema.FieldType.INT64, false),
  INT4("integer", Schema.FieldType.INT32, false),
  LONG("long", Schema.FieldType.INT64.withNullable(true), true),
  SERIAL("serial", Schema.FieldType.INT64, false),
  SERIAL8("serial8", Schema.FieldType.INT64, false),
  SMALLFLOAT("smallfloat", Schema.FieldType.FLOAT, false),
  SMALLINT("smallint", Schema.FieldType.INT32, false),
  STRING("string", Schema.FieldType.STRING, false),
  TEXT("text", Schema.FieldType.STRING, false),
  TIMESTAMP("timestamp", Schema.FieldType.DATETIME.withNullable(true), true),
  TIME("time", Schema.FieldType.DATETIME.withNullable(true), true),
  VARCHAR255("varchar(255)", FieldType.STRING.withNullable(true).withNullable(true), true),
  VARCHAR("varchar", FieldType.STRING, false),
  DATE("date", Schema.FieldType.DATETIME.withNullable(true), true),
  XML("xml", Schema.FieldType.STRING, false);

  private static final Map<String, Schema.FieldType> dataTypes;

  static {
    dataTypes = new HashMap<>();
    for (JDBCDataType t : DerbyDataTypes.values()) {
      dataTypes.put(t.getDatatypeLiteral().toLowerCase(), t.getFieldType());
    }
  }

  private String datatypeLiteral;
  private Schema.FieldType fieldType;
  private boolean isDefault;

  DerbyDataTypes(String datatypeLiteral, Schema.FieldType fieldType, boolean isDefault) {
    this.datatypeLiteral = datatypeLiteral;
    this.fieldType = fieldType;
    this.isDefault = isDefault;
  }

  public String getDatatypeLiteral() {
    return this.datatypeLiteral;
  }

  public Schema.FieldType getFieldType() {
    return this.fieldType;
  }

  public boolean isDefault() {
    return this.isDefault;
  }

  @Override
  public JDBCDataType getDefaultDataType(Schema.FieldType type) {
    for (JDBCDataType t : DerbyDataTypes.values()) {
      if ((Objects.deepEquals(t.getFieldType().withNullable(false), type)
              || Objects.deepEquals(t.getFieldType().withNullable(true), type))
          && t.isDefault()) {
        return t;
      }
    }
    return null;
  }

  public Schema.FieldType getDefaultDataTypeLiteral(String datatypeLiteral) {
    return dataTypes.get(datatypeLiteral);
  }

  public String rewrite(String url) {
    return url;
  }
}
