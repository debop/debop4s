package com.github.debop4s

/**
 * JDBC Client & Hibernate Dialect
 * Created by debop on 2014. 2. 25.
 */
package object data {

    val DATASOURCE_CLASS_H2 = "org.h2.jdbcx.JdbcDataSource"
    val DRIVER_CLASS_H2 = "org.h2.Driver"
    val DIALECT_H2 = "org.hibernate.dialect.H2Dialect"

    val DATASOURCE_CLASS_HSQL = "org.hsqldb.jdbc.JDBCDataSource"
    val DRIVER_CLASS_HSQL = "org.hsqldb.jdbcDriver"
    val DIALECT_HSQL = "org.hibernate.dialect.HSQLDialect"

    val DATASOURCE_CLASS_MYSQL = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
    val DRIVER_CLASS_MYSQL = "com.mysql.jdbc.Driver"
    val DIALECT_MYSQL = "org.hibernate.dialect.MySQL5InnoDBDialect"

    val DRIVER_CLASS_MARIADB = "org.mariadb.jdbc.Driver"

    val DATASOURCE_CLASS_POSTGRESQL = "org.postgresql.ds.PGPoolingDataSource"
    val DRIVER_CLASS_POSTGRESQL = "org.postgresql.Driver"
    val DIALECT_POSTGRESQL = "org.hibernate.dialect.PostgreSQL82Dialect"
}
