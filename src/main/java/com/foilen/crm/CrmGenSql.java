package com.foilen.crm;

import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.Hibernate63Tools;
import org.hibernate.dialect.MySQLDialect;

public class CrmGenSql {

    private static final String SQL_FILE = "sql/mysql.sql";

    public static void main(String[] args) {

        System.setProperty("hibernate.dialect.storage_engine", "innodb");
        FileTools.deleteFile(SQL_FILE);
        Hibernate63Tools.generateSqlSchema(MySQLDialect.class, SQL_FILE, true, "com.foilen.crm.db.entities");
    }

}
