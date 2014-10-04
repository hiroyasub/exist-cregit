begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension ExecuteFunction  *  Copyright (C) 2006-10 Adam Retter<adam@exist-db.org>  *  www.adamretter.co.uk  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|dom
operator|.
name|persistent
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XPathException
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
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|IntegerValue
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
name|Sequence
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
name|SequenceType
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
name|Type
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
name|PreparedStatement
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

begin_comment
comment|/**  * eXist SQL Module Extension PrepareFunction.  *  *<p>Prepare a SQL statement against a SQL capable Database</p>  *  * @author   Adam Retter<adam@exist-db.org>  * @version  1.0  * @see      org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  * @serial   2010-03-17  */
end_comment

begin_class
specifier|public
class|class
name|PrepareFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|PrepareFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"prepare"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Prepares a SQL statement against a SQL db using the connection indicated by the connection handle."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"handle"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The connection handle"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"sql-statement"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The SQL statement"
argument_list|)
block|, 			}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"an xs:long representing the statement handle"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/**      * PrepareFunction Constructor.      *      * @param  context    The Context of the calling XQuery      * @param  signature  DOCUMENT ME!      */
specifier|public
name|PrepareFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/**      * evaluate the call to the XQuery prepare() function, it is really the main entry point of this class.      *      * @param   args             arguments from the prepare() function call      * @param   contextSequence  the Context Sequence to operate on (not used here internally!)      *      * @return  A xs:long representing the handle to the prepared statement      *      * @throws  XPathException  DOCUMENT ME!      *      * @see     org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)      */
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// was a connection and SQL statement specified?
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|||
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
comment|// get the Connection
name|long
name|connectionUID
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|Connection
name|con
init|=
name|SQLModule
operator|.
name|retrieveConnection
argument_list|(
name|context
argument_list|,
name|connectionUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|con
operator|==
literal|null
condition|)
block|{
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
comment|// get the SQL statement
name|String
name|sql
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|PreparedStatement
name|stmt
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// execute the SQL statement
name|stmt
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
expr_stmt|;
comment|// store the PreparedStatement and return the uid handle of the PreparedStatement
return|return
operator|(
operator|new
name|IntegerValue
argument_list|(
name|SQLModule
operator|.
name|storePreparedStatement
argument_list|(
name|context
argument_list|,
operator|new
name|PreparedStatementWithSQL
argument_list|(
name|sql
argument_list|,
name|stmt
argument_list|)
argument_list|)
argument_list|)
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqle
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"sql:prepare() Caught SQLException \""
operator|+
name|sqle
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\" for SQL: \""
operator|+
name|sql
operator|+
literal|"\""
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"sql:prepare() Caught SQLException \""
operator|+
name|sqle
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\" for SQL: \""
operator|+
name|sql
operator|+
literal|"\""
argument_list|,
name|sqle
argument_list|)
operator|)
throw|;
block|}
block|}
block|}
end_class

end_unit

