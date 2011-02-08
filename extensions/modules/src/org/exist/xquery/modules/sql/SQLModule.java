begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension  *  Copyright (C) 2006-10 Adam Retter<adam@exist-db.org>  *  www.adamretter.co.uk  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|sql
package|;
end_package

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
name|xquery
operator|.
name|AbstractInternalModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_comment
comment|/**  * eXist SQL Module Extension.  *  *<p>An extension module for the eXist Native XML Database that allows queries against SQL Databases, returning an XML representation of the result  * set.</p>  *  * @author   Adam Retter<adam@exist-db.org>  * @author   ljo  * @version  1.2  * @see      org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[], java.util.Map)   * @serial   2010-03-18  */
end_comment

begin_class
specifier|public
class|class
name|SQLModule
extends|extends
name|AbstractInternalModule
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SQLModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/sql"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"sql"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2006-09-25"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.2"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|GetConnectionFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|GetConnectionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetConnectionFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|GetConnectionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetConnectionFunction
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|GetConnectionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetJNDIConnectionFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|GetJNDIConnectionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetJNDIConnectionFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|GetJNDIConnectionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExecuteFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|ExecuteFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExecuteFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|ExecuteFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PrepareFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|PrepareFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|static
name|long
name|currentUID
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONNECTIONS_CONTEXTVAR
init|=
literal|"_eXist_sql_connections"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREPARED_STATEMENTS_CONTEXTVAR
init|=
literal|"_eXist_sql_prepared_statements"
decl_stmt|;
specifier|public
name|SQLModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
operator|(
name|NAMESPACE_URI
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
operator|(
name|PREFIX
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
operator|(
literal|"A module for performing SQL queries against Databases, returning XML representations of the result sets."
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
operator|(
name|RELEASED_IN_VERSION
operator|)
return|;
block|}
comment|/**      * Retrieves a previously stored Connection from the Context of an XQuery.      *      * @param   context        The Context of the XQuery containing the Connection      * @param   connectionUID  The UID of the Connection to retrieve from the Context of the XQuery      *      * @return  DOCUMENT ME!      */
specifier|public
specifier|final
specifier|static
name|Connection
name|retrieveConnection
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|long
name|connectionUID
parameter_list|)
block|{
return|return
operator|(
name|retrieveObjectFromContextMap
argument_list|(
name|context
argument_list|,
name|SQLModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|,
name|connectionUID
argument_list|)
operator|)
return|;
block|}
comment|/**      * Stores a Connection in the Context of an XQuery.      *      * @param   context  The Context of the XQuery to store the Connection in      * @param   con      The connection to store      *      * @return  A unique ID representing the connection      */
specifier|public
specifier|final
specifier|static
specifier|synchronized
name|long
name|storeConnection
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Connection
name|con
parameter_list|)
block|{
return|return
operator|(
name|storeObjectInContextMap
argument_list|(
name|context
argument_list|,
name|SQLModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|,
name|con
argument_list|)
operator|)
return|;
block|}
comment|/**      * Retrieves a previously stored PreparedStatement from the Context of an XQuery.      *      * @param   context               The Context of the XQuery containing the PreparedStatement      * @param   preparedStatementUID  The UID of the PreparedStatement to retrieve from the Context of the XQuery      *      * @return  DOCUMENT ME!      */
specifier|public
specifier|final
specifier|static
name|PreparedStatementWithSQL
name|retrievePreparedStatement
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|long
name|preparedStatementUID
parameter_list|)
block|{
return|return
operator|(
name|retrieveObjectFromContextMap
argument_list|(
name|context
argument_list|,
name|SQLModule
operator|.
name|PREPARED_STATEMENTS_CONTEXTVAR
argument_list|,
name|preparedStatementUID
argument_list|)
operator|)
return|;
block|}
comment|/**      * Stores a PreparedStatement in the Context of an XQuery.      *      * @param   context  The Context of the XQuery to store the PreparedStatement in      * @param   stmt     preparedStatement The PreparedStatement to store      *      * @return  A unique ID representing the PreparedStatement      */
specifier|public
specifier|final
specifier|static
specifier|synchronized
name|long
name|storePreparedStatement
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|PreparedStatementWithSQL
name|stmt
parameter_list|)
block|{
return|return
operator|(
name|storeObjectInContextMap
argument_list|(
name|context
argument_list|,
name|SQLModule
operator|.
name|PREPARED_STATEMENTS_CONTEXTVAR
argument_list|,
name|stmt
argument_list|)
operator|)
return|;
block|}
comment|/**      * Retrieves a previously stored Object from the Context of an XQuery.      *      * @param   context         The Context of the XQuery containing the Object      * @param   contextMapName  DOCUMENT ME!      * @param   objectUID       The UID of the Object to retrieve from the Context of the XQuery      *      * @return  DOCUMENT ME!      */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|retrieveObjectFromContextMap
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|contextMapName
parameter_list|,
name|long
name|objectUID
parameter_list|)
block|{
comment|// get the existing connections map from the context
name|HashMap
argument_list|<
name|Long
argument_list|,
name|T
argument_list|>
name|map
init|=
operator|(
name|HashMap
argument_list|<
name|Long
argument_list|,
name|T
argument_list|>
operator|)
name|context
operator|.
name|getXQueryContextVar
argument_list|(
name|contextMapName
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
return|return
operator|(
literal|null
operator|)
return|;
block|}
comment|// get the connection
return|return
operator|(
name|map
operator|.
name|get
argument_list|(
name|objectUID
argument_list|)
operator|)
return|;
block|}
comment|/**      * Stores an Object in the Context of an XQuery.      *      * @param   context         The Context of the XQuery to store the Object in      * @param   contextMapName  The name of the context map      * @param   o               The Object to store      *      * @return  A unique ID representing the Object      */
specifier|private
specifier|static
specifier|synchronized
parameter_list|<
name|T
parameter_list|>
name|long
name|storeObjectInContextMap
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|contextMapName
parameter_list|,
name|T
name|o
parameter_list|)
block|{
comment|// get the existing map from the context
name|HashMap
argument_list|<
name|Long
argument_list|,
name|T
argument_list|>
name|map
init|=
operator|(
name|HashMap
argument_list|<
name|Long
argument_list|,
name|T
argument_list|>
operator|)
name|context
operator|.
name|getXQueryContextVar
argument_list|(
name|contextMapName
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
comment|// if there is no map, create a new one
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|T
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|// get an id for the map
name|long
name|uid
init|=
name|getUID
argument_list|()
decl_stmt|;
comment|// place the object in the map
name|map
operator|.
name|put
argument_list|(
name|uid
argument_list|,
name|o
argument_list|)
expr_stmt|;
comment|// store the map back in the context
name|context
operator|.
name|setXQueryContextVar
argument_list|(
name|contextMapName
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
operator|(
name|uid
operator|)
return|;
block|}
comment|/**      * Closes all the open DB Connections for the specified XQueryContext.      *      * @param  xqueryContext  The context to close JDBC Connections for      */
specifier|private
specifier|static
name|void
name|closeAllConnections
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
comment|// get the existing Connections map from the context
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Connection
argument_list|>
name|connections
init|=
operator|(
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Connection
argument_list|>
operator|)
name|xqueryContext
operator|.
name|getXQueryContextVar
argument_list|(
name|SQLModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|connections
operator|!=
literal|null
condition|)
block|{
comment|// iterate over each Connection
for|for
control|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|Connection
argument_list|>
name|entry
range|:
name|connections
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|conID
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Connection
name|con
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
comment|// close the Connection
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unable to close JDBC Connection"
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
block|}
comment|//empty the map
name|connections
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// update the context
name|xqueryContext
operator|.
name|setXQueryContextVar
argument_list|(
name|SQLModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|,
name|connections
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Closes all the open DB PreparedStatements for the specified XQueryContext.      *      * @param  xqueryContext  The context to close JDBC PreparedStatements for      */
specifier|private
specifier|static
name|void
name|closeAllPreparedStatements
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
comment|// get the existing PreparedStatements map from the context
name|HashMap
argument_list|<
name|Long
argument_list|,
name|PreparedStatementWithSQL
argument_list|>
name|preparedStatements
init|=
operator|(
name|HashMap
argument_list|<
name|Long
argument_list|,
name|PreparedStatementWithSQL
argument_list|>
operator|)
name|xqueryContext
operator|.
name|getXQueryContextVar
argument_list|(
name|SQLModule
operator|.
name|PREPARED_STATEMENTS_CONTEXTVAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|preparedStatements
operator|!=
literal|null
condition|)
block|{
comment|// iterate over each PreparedStatement
for|for
control|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|PreparedStatementWithSQL
argument_list|>
name|entry
range|:
name|preparedStatements
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|conID
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|PreparedStatementWithSQL
name|stmt
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
comment|// close the PreparedStatement
name|stmt
operator|.
name|getStmt
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unable to close JDBC PreparedStatement"
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// remove it from the connections map
name|preparedStatements
operator|.
name|remove
argument_list|(
name|conID
argument_list|)
expr_stmt|;
name|stmt
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// update the context
name|xqueryContext
operator|.
name|setXQueryContextVar
argument_list|(
name|SQLModule
operator|.
name|PREPARED_STATEMENTS_CONTEXTVAR
argument_list|,
name|preparedStatements
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns a Unique ID based on the System Time.      *      * @return  The Unique ID      */
specifier|private
specifier|static
specifier|synchronized
name|long
name|getUID
parameter_list|()
block|{
return|return
operator|(
name|currentUID
operator|++
operator|)
return|;
block|}
comment|/**      * Resets the Module Context and closes any DB connections for the XQueryContext.      *      * @param  xqueryContext  The XQueryContext      */
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
comment|// reset the module context
name|super
operator|.
name|reset
argument_list|(
name|xqueryContext
argument_list|)
expr_stmt|;
comment|// close any open PreparedStatements
name|closeAllPreparedStatements
argument_list|(
name|xqueryContext
argument_list|)
expr_stmt|;
comment|// close any open Connections
name|closeAllConnections
argument_list|(
name|xqueryContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

