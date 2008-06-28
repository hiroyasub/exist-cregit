begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension GetConnectionFunction  *  Copyright (C) 2008 Adam Retter<adam@exist-db.org>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: GetConnectionFunction.java 4126 2006-09-18 21:20:17 +0000 (Mon, 18 Sep 2006) deliriumsky $  */
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
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|NodeValue
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
comment|/**  * eXist SQL Module Extension GetConnectionFunction  *   * Get a connection to a SQL Database  *   * @author Adam Retter<adam@exist-db.org>  * @serial 2008-05-29  * @version 1.21  *   * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext,  *      org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|GetConnectionFunction
extends|extends
name|BasicFunction
block|{
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
literal|"get-connection"
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
literal|"Open's a connection to a SQL Database. Expects a JDBC Driver class name in $a and a JDBC URL in $b. Returns an xs:long representing the connection handle."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-connection"
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
literal|"Open's a connection to a SQL Database. Expects "
operator|+
literal|"a JDBC Driver class name in $a and a JDBC URL in $b."
operator|+
literal|" Additional JDBC properties may be set in $c in the"
operator|+
literal|" form<properties><property name=\"\" value=\"\"/></properties>. "
operator|+
literal|"Returns an xs:long representing the connection handle."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-connection"
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
literal|"Open's a connection to a SQL Database. Expects a JDBC Driver class name in $a, a JDBC URL in $b, a username in $c and a password in $d. Returns an xs:long representing the connection handle."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * GetConnectionFunction Constructor 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 */
specifier|public
name|GetConnectionFunction
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
comment|/** 	 * evaluate the call to the xquery get-connection() function, it is really 	 * the main entry point of this class 	 *  	 * @param args 	 *            arguments from the get-connection() function call 	 * @param contextSequence 	 *            the Context Sequence to operate on (not used here internally!) 	 * @return A xs:long representing a handle to the connection 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], 	 *      org.exist.xquery.value.Sequence) 	 */
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
comment|// was a db driver and url specified?
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
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
comment|// get the db connection details
name|String
name|dbDriver
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|dbURL
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
comment|// load the driver
name|Class
operator|.
name|forName
argument_list|(
name|dbDriver
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|Connection
name|con
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
comment|// try and get the connection
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|dbURL
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
comment|// try and get the connection
name|Properties
name|props
init|=
name|ModuleUtils
operator|.
name|parseProperties
argument_list|(
operator|(
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
argument_list|)
decl_stmt|;
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|dbURL
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|args
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|String
name|dbUser
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|dbPassword
init|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|// try and get the connection
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|dbURL
argument_list|,
name|dbUser
argument_list|,
name|dbPassword
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
name|IllegalAccessException
name|iae
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"sql:get-connection() Illegal Access to database driver class: "
operator|+
name|dbDriver
argument_list|,
name|iae
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"sql:get-connection() Illegal Access to database driver class: "
operator|+
name|dbDriver
argument_list|,
name|iae
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"sql:get-connection() Cannot find database driver class: "
operator|+
name|dbDriver
argument_list|,
name|cnfe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"sql:get-connection() Cannot find database driver class: "
operator|+
name|dbDriver
argument_list|,
name|cnfe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"sql:get-connection() Cannot instantiate database driver class: "
operator|+
name|dbDriver
argument_list|,
name|ie
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"sql:get-connection() Cannot instantiate database driver class: "
operator|+
name|dbDriver
argument_list|,
name|ie
argument_list|)
throw|;
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
literal|"sql:get-connection() Cannot connect to database: "
operator|+
name|dbURL
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"sql:get-connection() Cannot connect to database: "
operator|+
name|dbURL
argument_list|,
name|sqle
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

