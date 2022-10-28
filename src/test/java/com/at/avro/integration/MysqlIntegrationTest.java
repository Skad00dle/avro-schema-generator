package com.at.avro.integration;

import com.at.avro.AvroSchema;
import com.at.avro.DbSchemaExtractor;
import com.at.avro.SchemaGenerator;
import com.at.avro.config.AvroConfig;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import java.util.Comparator;
import java.util.List;

import static helper.Utils.classPathResourceContent;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MysqlIntegrationTest {

    @ClassRule
    public static MySQLContainer mysql = new MySQLContainer("mysql").withUsername("root");

    private AvroConfig avroConfig = new AvroConfig("mysql");

    private DbSchemaExtractor extractor;

    @BeforeClass
    public static void setupClass() {
        Flyway.configure()
            .dataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
            .locations("classpath:mysql/db/migration")
            .load().migrate();
    }

    @Before
    public void setup() {
        extractor = new DbSchemaExtractor(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
    }

    @Test
    public void testDefaultTable() {
        AvroSchema avroSchema = extractor.getForTable(avroConfig, null, "default_table");
        assertThat(SchemaGenerator.generate(avroSchema), is(classPathResourceContent("/mysql/avro/default_table.avsc")));
    }

    @Test
    public void testDefaultTableWithDoc() {
        avroConfig.setUseSqlCommentsAsDoc(true);
        AvroSchema avroSchema = extractor.getForTable(avroConfig, null, "comment_table");
        assertThat(SchemaGenerator.generate(avroSchema), is(classPathResourceContent("/mysql/avro/comment_table.avsc")));
    }

    @Test
    public void testMultipleTables() {
        List<AvroSchema> schemas = extractor.getForTables(avroConfig, null, "default_table", "comment_table");
        assertThat(schemas.size(), is(2));
        AvroSchema arrayTableSchema = schemas.stream().sorted(Comparator.comparing(schema -> schema.getName())).findFirst().get();
        assertThat(arrayTableSchema.getFields().size(), is(8));
    }

    @Test
    public void testSchemaWithDashes() {
        AvroSchema avroSchema = extractor.getForTable(avroConfig, "\"test-database\"", "small_table");
        assertThat(SchemaGenerator.generate(avroSchema), is(classPathResourceContent("/mysql/avro/small_table.avsc")));
    }
}
