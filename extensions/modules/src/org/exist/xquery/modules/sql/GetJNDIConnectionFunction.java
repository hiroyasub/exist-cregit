begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension GetJNDIConnectionFunction  *  Copyright (C) 2008 Adam Retter<adam@exist-db.org>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
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

begin_comment
comment|/**  * eXist SQL Module Extension GetJNDIConnectionFunction  *   * Get a connection to a SQL Database via JNDI  *   * @author Adam Retter<adam@exist-db.org>  * @author Loren Cahlander  * @serial 2008-05-19  * @version 1.2  *   * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext,  *      org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|GetJNDIConnectionFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|GetJNDIConnectionFunction
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
literal|"get-jndi-connection"
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
literal|"Open's a connection to a SQL Database. Expects a JNDI name. Returns an xs:long representing the connection handle."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"jndi-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
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
name|ZERO_OR_ONE
argument_list|,
literal|"handle"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-jndi-connection"
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
literal|"Open's a connection to a SQL Database. Expects a JNDI name, a username and a password. Returns an xs:long representing the connection handle."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"jndi-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
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
name|ZERO_OR_ONE
argument_list|,
literal|"handle"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * GetJNDIConnectionFunction Constructor 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 */
specifier|public
name|GetJNDIConnectionFunction
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
comment|/** 	 * evaluate the call to the xquery get-jndi-connection() function, it is 	 * really the main entry point of this class 	 *  	 * @param args 	 *            arguments from the get-jndi-connection() function call 	 * @param contextSequence 	 *            the Context Sequence to operate on (not used here internally!) 	 * @return A xs:long representing a handle to the connection 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], 	 *      org.exist.xquery.value.Sequence) 	 */
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
comment|// was a JNDI name specified?
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
try|try
block|{
name|Connection
name|con
init|=
literal|null
decl_stmt|;
comment|// get the JNDI source
name|String
name|jndiName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Context
name|ctx
init|=
operator|new
name|InitialContext
argument_list|()
decl_stmt|;
name|DataSource
name|ds
init|=
operator|(
name|DataSource
operator|)
name|ctx
operator|.
name|lookup
argument_list|(
name|jndiName
argument_list|)
decl_stmt|;
comment|// try and get the connection
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|con
operator|=
name|ds
operator|.
name|getConnection
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|String
name|jndiUser
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|jndiPassword
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|con
operator|=
name|ds
operator|.
name|getConnection
argument_list|(
name|jndiUser
argument_list|,
name|jndiPassword
argument_list|)
expr_stmt|;
block|}
comment|// store the connection and return the uid handle of the connection
return|return
operator|new
name|IntegerValue
argument_list|(
name|SQLModule
operator|.
name|storeConnection
argument_list|(
name|context
argument_list|,
name|con
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

