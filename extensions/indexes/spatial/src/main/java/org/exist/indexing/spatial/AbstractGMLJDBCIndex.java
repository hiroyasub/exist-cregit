begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  *    *  @author<a href="mailto:pierrick.brihaye@free.fr">Pierrick Brihaye</a>  */
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
operator|.
name|ReindexMode
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
specifier|abstract
class|class
name|AbstractGMLJDBCIndex
extends|extends
name|AbstractIndex
block|{
comment|/**      * Holds the index ID. Notice that we delegate this task to the abstract JDBC class,      * not to the concrete HSQL (or whatever) one. This allows spatial functions to use      * the available JDBC index, whatever its underlying engine is.      */
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
name|AbstractGMLJDBCIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AbstractGMLJDBCIndex
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * An IndexWorker "pool"      */
specifier|protected
name|HashMap
argument_list|<
name|DBBroker
argument_list|,
name|AbstractGMLJDBCIndexWorker
argument_list|>
name|workers
init|=
operator|new
name|HashMap
argument_list|<
name|DBBroker
argument_list|,
name|AbstractGMLJDBCIndexWorker
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * The connection to the DB that will be needed for global operations       */
specifier|protected
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
comment|/**      * The spatial operators to test spatial relationshipds beween geometries.      * See http://www.vividsolutions.com/jts/bin/JTS%20Technical%20Specs.pdf (chapter 11).      */
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
specifier|protected
name|int
name|max_docs_in_context_to_refine_query
init|=
literal|10
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Path
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
name|int
name|getMaxDocsInContextToRefineQuery
parameter_list|()
block|{
return|return
name|max_docs_in_context_to_refine_query
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
comment|//Nothing particular to do : the connection will be opened on request
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DBException
block|{
name|Iterator
argument_list|<
name|AbstractGMLJDBCIndexWorker
argument_list|>
name|i
init|=
name|workers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AbstractGMLJDBCIndexWorker
name|worker
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//Flush any pending stuff
name|worker
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|//Reset state
name|worker
operator|.
name|setDocument
argument_list|(
literal|null
argument_list|,
name|ReindexMode
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
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
comment|//TODO : something useful here
comment|/*         try {             if (conn != null)                 conn.commit();         } catch (SQLException e) {             throw new DBException(e.getMessage());         }         */
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
name|Iterator
argument_list|<
name|AbstractGMLJDBCIndexWorker
argument_list|>
name|i
init|=
name|workers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AbstractGMLJDBCIndexWorker
name|worker
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//Flush any pending stuff
name|worker
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|//Reset state
name|worker
operator|.
name|setDocument
argument_list|(
literal|null
argument_list|,
name|ReindexMode
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
name|removeIndexContent
argument_list|()
expr_stmt|;
name|shutdownDatabase
argument_list|()
expr_stmt|;
name|deleteDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
specifier|abstract
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
comment|/**      * Checks if the JDBC database that contains the indexed spatial data is available an reachable.      * Creates it if necessary.      *       * @throws ClassNotFoundException if the JDBC driver can not be found      * @throws SQLException if the database is not reachable      */
specifier|protected
specifier|abstract
name|void
name|checkDatabase
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
function_decl|;
comment|/**      * Shuts down the JDBC database that contains the indexed spatial data.      *       * @throws DBException      */
specifier|protected
specifier|abstract
name|void
name|shutdownDatabase
parameter_list|()
throws|throws
name|DBException
function_decl|;
comment|/**      * Deletes the JDBC database that contains the indexed spatial data.      *       * @throws DBException      */
specifier|protected
specifier|abstract
name|void
name|deleteDatabase
parameter_list|()
throws|throws
name|DBException
function_decl|;
comment|/**      * Deletes the spatial data contained in the JDBC database.      *       * @throws DBException      */
specifier|protected
specifier|abstract
name|void
name|removeIndexContent
parameter_list|()
throws|throws
name|DBException
function_decl|;
comment|/**      * Convenience method that can be used by the IndexWorker to acquire a connection       * to the JDBC database that contains the indexed spatial data.      *       * @param broker the broker that will use th connection      * @return the connection      */
specifier|protected
specifier|abstract
name|Connection
name|acquireConnection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|SQLException
function_decl|;
comment|/**      * Convenience method that can be used by the IndexWorker to release a connection       * to the JDBC database that contains the indexed spatial data. This connection should have been      * previously acquired by {@link org.exist.indexing.spatial.AbstractGMLJDBCIndex#acquireConnection(DBBroker)}       *       * @param broker the broker that will use th connection      *       */
specifier|protected
specifier|abstract
name|void
name|releaseConnection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|SQLException
function_decl|;
block|}
end_class

end_unit

