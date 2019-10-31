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
name|dom
operator|.
name|QName
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
name|*
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|ModuleUtils
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
name|modules
operator|.
name|ModuleUtils
operator|.
name|ContextMapEntryModifier
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
name|value
operator|.
name|FunctionParameterSequenceType
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
name|value
operator|.
name|FunctionReturnSequenceType
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|functionDefs
import|;
end_import

begin_comment
comment|/**  * eXist SQL Module Extension.  *<p>  * An extension module for the eXist Native XML Database that allows queries against SQL Databases, returning an XML representation of the result  * set.  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  * @author ljo  * @version 1.2  * @serial 2010-03-18  * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[], java.util.Map)  */
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
name|LogManager
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
specifier|public
specifier|static
specifier|final
name|FunctionDef
index|[]
name|functions
init|=
name|functionDefs
argument_list|(
name|functionDefs
argument_list|(
name|GetConnectionFunction
operator|.
name|class
argument_list|,
name|GetConnectionFunction
operator|.
name|signatures
argument_list|)
argument_list|,
name|functionDefs
argument_list|(
name|GetJNDIConnectionFunction
operator|.
name|class
argument_list|,
name|GetJNDIConnectionFunction
operator|.
name|signatures
argument_list|)
argument_list|,
name|functionDefs
argument_list|(
name|ExecuteFunction
operator|.
name|class
argument_list|,
name|ExecuteFunction
operator|.
name|FS_EXECUTE
argument_list|)
argument_list|,
name|functionDefs
argument_list|(
name|PrepareFunction
operator|.
name|class
argument_list|,
name|PrepareFunction
operator|.
name|signatures
argument_list|)
argument_list|)
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
comment|/**      * Retrieves a previously stored Connection from the Context of an XQuery.      *      * @param context       The Context of the XQuery containing the Connection      * @param connectionUID The UID of the Connection to retrieve from the Context of the XQuery      * @return DOCUMENT ME!      */
specifier|public
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
name|ModuleUtils
operator|.
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
return|;
block|}
comment|/**      * Stores a Connection in the Context of an XQuery.      *      * @param context The Context of the XQuery to store the Connection in      * @param con     The connection to store      * @return A unique ID representing the connection      */
specifier|public
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
name|ModuleUtils
operator|.
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
return|;
block|}
comment|/**      * Retrieves a previously stored PreparedStatement from the Context of an XQuery.      *      * @param context              The Context of the XQuery containing the PreparedStatement      * @param preparedStatementUID The UID of the PreparedStatement to retrieve from the Context of the XQuery      * @return DOCUMENT ME!      */
specifier|public
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
name|ModuleUtils
operator|.
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
return|;
block|}
comment|/**      * Stores a PreparedStatement in the Context of an XQuery.      *      * @param context The Context of the XQuery to store the PreparedStatement in      * @param stmt    preparedStatement The PreparedStatement to store      * @return A unique ID representing the PreparedStatement      */
specifier|public
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
name|ModuleUtils
operator|.
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
return|;
block|}
comment|/**      * Resets the Module Context and closes any DB connections for the XQueryContext.      *      * @param xqueryContext The XQueryContext      */
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|,
name|boolean
name|keepGlobals
parameter_list|)
block|{
comment|// reset the module context
name|super
operator|.
name|reset
argument_list|(
name|xqueryContext
argument_list|,
name|keepGlobals
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
comment|/**      * Closes all the open DB Connections for the specified XQueryContext.      *      * @param xqueryContext The context to close JDBC Connections for      */
specifier|private
specifier|static
name|void
name|closeAllConnections
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
name|ModuleUtils
operator|.
name|modifyContextMap
argument_list|(
name|xqueryContext
argument_list|,
name|SQLModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|,
operator|new
name|ContextMapEntryModifier
argument_list|<
name|Connection
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|Connection
argument_list|>
name|map
parameter_list|)
block|{
name|super
operator|.
name|modify
argument_list|(
name|map
argument_list|)
expr_stmt|;
comment|//empty the map
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|Connection
argument_list|>
name|entry
parameter_list|)
block|{
specifier|final
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
name|warn
argument_list|(
literal|"Unable to close JDBC Connection: "
operator|+
name|se
operator|.
name|getMessage
argument_list|()
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// update the context
comment|//ModuleUtils.storeContextMap(xqueryContext, SQLModule.CONNECTIONS_CONTEXTVAR, connections);
block|}
comment|/**      * Closes all the open DB PreparedStatements for the specified XQueryContext.      *      * @param xqueryContext The context to close JDBC PreparedStatements for      */
specifier|private
specifier|static
name|void
name|closeAllPreparedStatements
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
name|ModuleUtils
operator|.
name|modifyContextMap
argument_list|(
name|xqueryContext
argument_list|,
name|SQLModule
operator|.
name|PREPARED_STATEMENTS_CONTEXTVAR
argument_list|,
operator|new
name|ContextMapEntryModifier
argument_list|<
name|PreparedStatementWithSQL
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|PreparedStatementWithSQL
argument_list|>
name|map
parameter_list|)
block|{
name|super
operator|.
name|modify
argument_list|(
name|map
argument_list|)
expr_stmt|;
comment|//empty the map
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|PreparedStatementWithSQL
argument_list|>
name|entry
parameter_list|)
block|{
specifier|final
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
name|warn
argument_list|(
literal|"Unable to close JDBC PreparedStatement: "
operator|+
name|se
operator|.
name|getMessage
argument_list|()
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// update the context
comment|//ModuleUtils.storeContextMap(xqueryContext, SQLModule.PREPARED_STATEMENTS_CONTEXTVAR, preparedStatements);
block|}
block|}
end_class

end_unit

