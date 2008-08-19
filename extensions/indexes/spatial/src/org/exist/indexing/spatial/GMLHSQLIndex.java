begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  *    *  @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|spatial
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexWorker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|btree
operator|.
name|DBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|DatabaseConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|GMLHSQLIndex
extends|extends
name|AbstractGMLJDBCIndex
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|GMLHSQLIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|String
name|db_file_name_prefix
init|=
literal|"spatial_index"
decl_stmt|;
comment|//Keep this upper case ;-)
specifier|public
specifier|static
name|String
name|TABLE_NAME
init|=
literal|"SPATIAL_INDEX_V1"
decl_stmt|;
specifier|private
name|DBBroker
name|connectionOwner
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|connectionTimeout
init|=
literal|100000L
decl_stmt|;
specifier|public
name|GMLHSQLIndex
parameter_list|()
block|{
block|}
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|super
operator|.
name|configure
argument_list|(
name|pool
argument_list|,
name|dataDir
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|String
name|param
init|=
operator|(
operator|(
name|Element
operator|)
name|config
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"connectionTimeout"
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connectionTimeout
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|param
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid value for 'connectionTimeout'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|param
operator|=
operator|(
operator|(
name|Element
operator|)
name|config
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"max_docs_in_context_to_refine_query"
argument_list|)
expr_stmt|;
if|if
condition|(
name|param
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|max_docs_in_context_to_refine_query
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|param
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid value for 'max_docs_in_context_to_refine_query', using default:"
operator|+
name|max_docs_in_context_to_refine_query
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"max_docs_in_context_to_refine_query = "
operator|+
name|max_docs_in_context_to_refine_query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|GMLHSQLIndexWorker
name|worker
init|=
operator|(
name|GMLHSQLIndexWorker
operator|)
name|workers
operator|.
name|get
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|worker
operator|==
literal|null
condition|)
block|{
name|worker
operator|=
operator|new
name|GMLHSQLIndexWorker
argument_list|(
name|this
argument_list|,
name|broker
argument_list|)
expr_stmt|;
name|workers
operator|.
name|put
argument_list|(
name|broker
argument_list|,
name|worker
argument_list|)
expr_stmt|;
block|}
return|return
name|worker
return|;
block|}
specifier|protected
name|void
name|checkDatabase
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
comment|//Test to see if we have a HSQL driver in the classpath
name|Class
operator|.
name|forName
argument_list|(
literal|"org.hsqldb.jdbcDriver"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|shutdownDatabase
parameter_list|()
throws|throws
name|DBException
block|{
try|try
block|{
comment|//No need to shutdown if we haven't opened anything
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SHUTDOWN"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"GML index: "
operator|+
name|getDataDir
argument_list|()
operator|+
literal|"/"
operator|+
name|db_file_name_prefix
operator|+
literal|" closed"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DBException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|conn
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|deleteDatabase
parameter_list|()
throws|throws
name|DBException
block|{
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|getDataDir
argument_list|()
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
name|db_file_name_prefix
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|boolean
name|deleted
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|deleted
operator|&=
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|//TODO : raise an error if deleted == false ?
block|}
specifier|protected
name|void
name|removeIndexContent
parameter_list|()
throws|throws
name|DBException
block|{
try|try
block|{
comment|//Let's be lazy here : we only delete the index content if we have a connection
comment|//deleteDatabase() should be far more efficient ;-)
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|int
name|nodeCount
init|=
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"DELETE FROM "
operator|+
name|GMLHSQLIndex
operator|.
name|TABLE_NAME
operator|+
literal|";"
argument_list|)
decl_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"GML index: "
operator|+
name|getDataDir
argument_list|()
operator|+
literal|"/"
operator|+
name|db_file_name_prefix
operator|+
literal|". "
operator|+
name|nodeCount
operator|+
literal|" nodes removed"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DBException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|//Horrible "locking" mechanism
specifier|protected
name|Connection
name|acquireConnection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|SQLException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|connectionOwner
operator|==
literal|null
condition|)
block|{
name|connectionOwner
operator|=
name|broker
expr_stmt|;
if|if
condition|(
name|conn
operator|==
literal|null
condition|)
name|initializeConnection
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
else|else
block|{
name|long
name|timeOut_
init|=
name|connectionTimeout
decl_stmt|;
name|long
name|waitTime
init|=
name|timeOut_
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|wait
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|connectionOwner
operator|==
literal|null
condition|)
block|{
name|connectionOwner
operator|=
name|broker
expr_stmt|;
if|if
condition|(
name|conn
operator|==
literal|null
condition|)
comment|//We should never get there since the connection should have been initialized
comment|//by the first request from a worker
name|initializeConnection
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
else|else
block|{
name|waitTime
operator|=
name|timeOut_
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
if|if
condition|(
name|waitTime
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Time out while trying to get connection"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|notify
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"interrupted while waiting for lock"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|protected
specifier|synchronized
name|void
name|releaseConnection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|connectionOwner
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Attempted to release a connection that wasn't acquired"
argument_list|)
throw|;
name|connectionOwner
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|initializeConnection
parameter_list|()
throws|throws
name|SQLException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"hsqldb.cache_scale"
argument_list|,
literal|"11"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hsqldb.cache_size_scale"
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hsqldb.default_table_type"
argument_list|,
literal|"cached"
argument_list|)
expr_stmt|;
comment|//Get a connection to the DB... and keep it
name|this
operator|.
name|conn
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:hsqldb:"
operator|+
name|getDataDir
argument_list|()
operator|+
literal|"/"
operator|+
name|db_file_name_prefix
comment|/* + ";shutdown=true" */
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rs
operator|=
name|this
operator|.
name|conn
operator|.
name|getMetaData
argument_list|()
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|TABLE_NAME
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TABLE"
block|}
argument_list|)
expr_stmt|;
name|rs
operator|.
name|last
argument_list|()
expr_stmt|;
if|if
condition|(
name|rs
operator|.
name|getRow
argument_list|()
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Opened GML index: "
operator|+
name|getDataDir
argument_list|()
operator|+
literal|"/"
operator|+
name|db_file_name_prefix
argument_list|)
expr_stmt|;
comment|//Create the data structure if it doesn't exist
block|}
if|else if
condition|(
name|rs
operator|.
name|getRow
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE TABLE "
operator|+
name|TABLE_NAME
operator|+
literal|"("
operator|+
comment|/*1*/
literal|"DOCUMENT_URI VARCHAR, "
operator|+
comment|/*2*/
literal|"NODE_ID_UNITS INTEGER, "
operator|+
comment|/*3*/
literal|"NODE_ID BINARY, "
operator|+
comment|/*4*/
literal|"GEOMETRY_TYPE VARCHAR, "
operator|+
comment|/*5*/
literal|"SRS_NAME VARCHAR, "
operator|+
comment|/*6*/
literal|"WKT VARCHAR, "
operator|+
comment|/*7*/
literal|"WKB BINARY, "
operator|+
comment|/*8*/
literal|"MINX DOUBLE, "
operator|+
comment|/*9*/
literal|"MAXX DOUBLE, "
operator|+
comment|/*10*/
literal|"MINY DOUBLE, "
operator|+
comment|/*11*/
literal|"MAXY DOUBLE, "
operator|+
comment|/*12*/
literal|"CENTROID_X DOUBLE, "
operator|+
comment|/*13*/
literal|"CENTROID_Y DOUBLE, "
operator|+
comment|/*14*/
literal|"AREA DOUBLE, "
operator|+
comment|//Boundary ?
comment|/*15*/
literal|"EPSG4326_WKT VARCHAR, "
operator|+
comment|/*16*/
literal|"EPSG4326_WKB BINARY, "
operator|+
comment|/*17*/
literal|"EPSG4326_MINX DOUBLE, "
operator|+
comment|/*18*/
literal|"EPSG4326_MAXX DOUBLE, "
operator|+
comment|/*19*/
literal|"EPSG4326_MINY DOUBLE, "
operator|+
comment|/*20*/
literal|"EPSG4326_MAXY DOUBLE, "
operator|+
comment|/*21*/
literal|"EPSG4326_CENTROID_X DOUBLE, "
operator|+
comment|/*22*/
literal|"EPSG4326_CENTROID_Y DOUBLE, "
operator|+
comment|/*23*/
literal|"EPSG4326_AREA DOUBLE, "
operator|+
comment|//Boundary ?
comment|/*24*/
literal|"IS_CLOSED BOOLEAN, "
operator|+
comment|/*25*/
literal|"IS_SIMPLE BOOLEAN, "
operator|+
comment|/*26*/
literal|"IS_VALID BOOLEAN, "
operator|+
comment|//Enforce uniqueness
literal|"UNIQUE ("
operator|+
literal|"DOCUMENT_URI, NODE_ID_UNITS, NODE_ID"
operator|+
literal|")"
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX DOCUMENT_URI ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (DOCUMENT_URI);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX NODE_ID ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (NODE_ID);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX GEOMETRY_TYPE ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (GEOMETRY_TYPE);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX SRS_NAME ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (SRS_NAME);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX WKB ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (WKB);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX EPSG4326_WKB ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (EPSG4326_WKB);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX EPSG4326_MINX ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (EPSG4326_MINX);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX EPSG4326_MAXX ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (EPSG4326_MAXX);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX EPSG4326_MINY ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (EPSG4326_MINY);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX EPSG4326_MAXY ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (EPSG4326_MAXY);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX EPSG4326_CENTROID_X ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (EPSG4326_CENTROID_X);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX EPSG4326_CENTROID_Y ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (EPSG4326_CENTROID_Y);"
argument_list|)
expr_stmt|;
comment|//AREA ?
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Created GML index: "
operator|+
name|getDataDir
argument_list|()
operator|+
literal|"/"
operator|+
name|db_file_name_prefix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"2 tables with the same name ?"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|rs
operator|!=
literal|null
condition|)
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

