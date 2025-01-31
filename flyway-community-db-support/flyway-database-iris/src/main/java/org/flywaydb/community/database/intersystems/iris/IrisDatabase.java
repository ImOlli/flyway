/*
 * Copyright (C) Red Gate Software Ltd 2010-2024
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.community.database.intersystems.iris;

import java.sql.Connection;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;

/**
 * @author Oliver Stahl
 */
public class IrisDatabase extends Database<IrisConnection>
{

   public IrisDatabase(Configuration configuration, JdbcConnectionFactory jdbcConnectionFactory, StatementInterceptor statementInterceptor)
   {
      super(configuration, jdbcConnectionFactory, statementInterceptor);
   }

   @Override
   protected IrisConnection doGetConnection(Connection connection)
   {
      return new IrisConnection(this, connection);
   }

   @Override
   public void ensureSupported(Configuration configuration)
   {
      ensureDatabaseIsRecentEnough("2019.1");
   }

   @Override
   public boolean supportsDdlTransactions()
   {
      return true;
   }

   @Override
   public String getBooleanTrue()
   {
      return "1";
   }

   @Override
   public String getBooleanFalse()
   {
      return "0";
   }

   @Override
   public String doQuote(String identifier) {
      return "\"" + identifier + "\"";
   }

   @Override
   public boolean catalogIsSchema()
   {
      return false;
   }

   @Override
   public String getRawCreateScript(Table table, boolean baseline)
   {
      return "CREATE TABLE " + table + " (\n" +
             "    \"installed_rank\" INT NOT NULL,\n" +
             "    \"version\" VARCHAR(50),\n" +
             "    \"description\" VARCHAR(200) NOT NULL,\n" +
             "    \"type\" VARCHAR(20) NOT NULL,\n" +
             "    \"script\" VARCHAR(1000) NOT NULL,\n" +
             "    \"checksum\" INTEGER,\n" +
             "    \"installed_by\" VARCHAR(100) NOT NULL,\n" +
             "    \"installed_on\" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
             "    \"execution_time\" INTEGER NOT NULL,\n" +
             "    \"success\" BIT NOT NULL\n" +
             ");\n" +
             (baseline ? getBaselineStatement(table) + ";\n" : "") +
             "ALTER TABLE " + table + " ADD CONSTRAINT \"" + table.getName() + "_pk\" PRIMARY KEY (\"installed_rank\");\n" +
             "CREATE INDEX \"" + table.getName() + "_s_idx\" ON " + table + " (\"success\");";
   }

}