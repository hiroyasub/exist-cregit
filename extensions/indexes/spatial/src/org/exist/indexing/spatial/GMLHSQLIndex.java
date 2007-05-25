begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|AbstractIndex
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
name|indexing
operator|.
name|StreamListener
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
name|AbstractIndex
block|{
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
name|GMLHSQLIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
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
specifier|private
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
specifier|public
interface|interface
name|SpatialOperator
block|{
specifier|public
specifier|static
name|int
name|UNKNOWN
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
name|int
name|EQUALS
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
name|int
name|DISJOINT
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
name|int
name|INTERSECTS
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
name|int
name|TOUCHES
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
name|int
name|CROSSES
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
name|int
name|WITHIN
init|=
literal|6
decl_stmt|;
specifier|public
specifier|static
name|int
name|CONTAINS
init|=
literal|7
decl_stmt|;
specifier|public
specifier|static
name|int
name|OVERLAPS
init|=
literal|8
decl_stmt|;
block|}
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
comment|//Make a pool for each broker ?
specifier|private
name|AbstractGMLJDBCIndexWorker
name|worker
decl_stmt|;
specifier|private
name|boolean
name|workerHasConnection
init|=
literal|false
decl_stmt|;
specifier|public
name|GMLHSQLIndex
parameter_list|()
block|{
block|}
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|ID
return|;
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
try|try
block|{
name|checkDatabase
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
comment|//Nothing particular to do : the connection will be opened on request
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DBException
block|{
comment|//Provisional workaround to avoid sending flush() events
if|if
condition|(
name|worker
operator|!=
literal|null
condition|)
block|{
name|worker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|worker
operator|.
name|setDocument
argument_list|(
literal|null
argument_list|,
name|StreamListener
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
name|shutdownDatabase
argument_list|()
expr_stmt|;
block|}
comment|//Seems to never be used
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
comment|//TODO : something useful here
comment|/*     	try {      		if (conn != null)     			conn.commit();         } catch (SQLException e) {         	throw new DBException(e.getMessage());          }         */
block|}
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
comment|//Provisional workaround to avoid sending flush() events
name|worker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|worker
operator|.
name|setDocument
argument_list|(
literal|null
argument_list|,
name|StreamListener
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
name|remove
argument_list|(
name|this
operator|.
name|conn
argument_list|)
expr_stmt|;
name|shutdownDatabase
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|checkIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|getWorker
argument_list|(
name|broker
argument_list|)
operator|.
name|checkIndex
argument_list|(
name|broker
argument_list|)
return|;
block|}
specifier|public
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
comment|//TODO : see above. We might want a pool here
if|if
condition|(
name|worker
operator|==
literal|null
condition|)
name|worker
operator|=
operator|new
name|GMLHSQLIndexWorker
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//worker = new GMLHSQLIndexWorker(this, broker);
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
name|remove
parameter_list|(
name|Connection
name|conn
parameter_list|)
throws|throws
name|DBException
block|{
try|try
block|{
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
comment|//TODO : should we remove the db files as well ?
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
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|workerHasConnection
condition|)
block|{
name|workerHasConnection
operator|=
literal|true
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
literal|10000L
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
operator|!
name|workerHasConnection
condition|)
block|{
name|workerHasConnection
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|conn
operator|==
literal|null
condition|)
comment|//We should never get there since the connection should have been initialized
comment|///by the first request from a worker
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
block|{
name|workerHasConnection
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
name|void
name|initializeConnection
parameter_list|()
block|{
try|try
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
comment|//Use CACHED table, not MEMORY one
comment|//TODO : use hsqldb.default_table_type
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
literal|"DOCUMENT_URI VARCHAR, "
operator|+
comment|//TODO : use binary format ?
literal|"NODE_ID VARCHAR, "
operator|+
literal|"GEOMETRY_TYPE VARCHAR, "
operator|+
literal|"SRS_NAME VARCHAR, "
operator|+
literal|"WKT VARCHAR, "
operator|+
comment|//TODO : use binary format ?
literal|"BASE64_WKB VARCHAR, "
operator|+
literal|"WSG84_WKT VARCHAR, "
operator|+
comment|//TODO : use binary format ?
literal|"WSG84_BASE64_WKB VARCHAR, "
operator|+
literal|"WSG84_MINX DOUBLE, "
operator|+
literal|"WSG84_MAXX DOUBLE, "
operator|+
literal|"WSG84_MINY DOUBLE, "
operator|+
literal|"WSG84_MAXY DOUBLE, "
operator|+
literal|"WSG84_CENTROID_X DOUBLE, "
operator|+
literal|"WSG84_CENTROID_Y DOUBLE, "
operator|+
literal|"WSG84_AREA DOUBLE, "
operator|+
comment|//Enforce uniqueness
literal|"UNIQUE ("
operator|+
literal|"DOCUMENT_URI, NODE_ID"
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
literal|"CREATE INDEX WSG84_MINX ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (WSG84_MINX);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX WSG84_MAXX ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (WSG84_MAXX);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX WSG84_MINY ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (WSG84_MINY);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX WSG84_MAXY ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (WSG84_MAXY);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX WSG84_CENTROID_X ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (WSG84_CENTROID_X);"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE INDEX WSG84_CENTROID_Y ON "
operator|+
name|TABLE_NAME
operator|+
literal|" (WSG84_CENTROID_Y);"
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
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|this
operator|.
name|conn
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

