/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm;

import org.hibernate.dialect.MySQL5Dialect;

import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.Hibernate52Tools;

public class CrmGenSql {

    private static final String SQL_FILE = "sql/mysql.sql";

    public static void main(String[] args) {

        System.setProperty("hibernate.dialect.storage_engine", "innodb");
        FileTools.deleteFile(SQL_FILE);
        Hibernate52Tools.generateSqlSchema(MySQL5Dialect.class, SQL_FILE, true, "com.foilen.crm.db.entities");
    }

}
