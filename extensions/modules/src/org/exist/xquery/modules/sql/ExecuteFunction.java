begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension ExecuteFunction  *  Copyright (C) 2006 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: ExecuteFunction.java 4126 2006-09-18 21:20:17 +0000 (Mon, 18 Sep 2006) deliriumsky $  */
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
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
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
name|java
operator|.
name|sql
operator|.
name|Types
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
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|BooleanValue
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
comment|/**  * eXist SQL Module Extension ExecuteFunction   *   * Execute a sql statement against a sql db  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-09-24  * @version 1.0  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|ExecuteFunction
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
literal|"execute"
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
literal|"Executes a SQL statement $b against a SQL db using the connection indicated by the connection handle in $a. $c indicates whether the xml nodes should be formed from the column names (in this mode a space in a Column Name will be replaced by an underscore!)"
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
name|INTEGER
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
name|BOOLEAN
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * ExecuteFunction Constructor 	 *  	 * @param context	The Context of the calling XQuery 	 */
specifier|public
name|ExecuteFunction
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
comment|/** 	 * evaluate the call to the xquery execute() function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the execute() function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		An xs:node representing the sql result set 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
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
comment|//was a connection and sql statement specified?
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
comment|//get the existing connections map from the context
name|HashMap
name|connections
init|=
operator|(
name|HashMap
operator|)
name|context
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
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
comment|//get the connection
name|long
name|conID
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
operator|(
name|Connection
operator|)
name|connections
operator|.
name|get
argument_list|(
operator|new
name|Long
argument_list|(
name|conID
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|con
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
try|try
block|{
name|StringBuffer
name|xmlBuf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
comment|//get the sql statement
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
comment|//execute the sql statement
name|Statement
name|stmt
init|=
name|con
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|//execute the query statement
if|if
condition|(
name|stmt
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
condition|)
block|{
comment|/* SQL Query returned results */
comment|//iterate through the result set building an xml document
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
name|ResultSetMetaData
name|rsmd
init|=
name|rs
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|int
name|iColumns
init|=
name|rsmd
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
name|int
name|iRows
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"<sql:row index=\""
operator|+
name|rs
operator|.
name|getRow
argument_list|()
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
comment|//get each tuple in the row
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iColumns
condition|;
name|i
operator|++
control|)
block|{
name|String
name|columnName
init|=
name|rsmd
operator|.
name|getColumnName
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|columnName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|BooleanValue
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
name|effectiveBooleanValue
argument_list|()
condition|)
block|{
comment|//use column names as the xml node
comment|/** Spaces in column names are replaced with underscore's */
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"<"
operator|+
name|columnName
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'_'
argument_list|)
operator|+
literal|" sql:type=\""
operator|+
name|rsmd
operator|.
name|getColumnTypeName
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|"\" xs:type=\""
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|sqlTypeToXMLType
argument_list|(
name|rsmd
operator|.
name|getColumnType
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
name|xmlBuf
operator|.
name|append
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"</"
operator|+
name|columnName
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'_'
argument_list|)
operator|+
literal|">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//DONT use column names as the xml node
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"<sql:field name=\""
operator|+
name|columnName
operator|+
literal|"\" sql:type=\""
operator|+
name|rsmd
operator|.
name|getColumnTypeName
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|"\" xs:type=\""
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|sqlTypeToXMLType
argument_list|(
name|rsmd
operator|.
name|getColumnType
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
name|xmlBuf
operator|.
name|append
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"</sql:field>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"</sql:row>"
argument_list|)
expr_stmt|;
name|iRows
operator|++
expr_stmt|;
block|}
name|xmlBuf
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|"<sql:result xmlns:sql=\""
operator|+
name|SQLModule
operator|.
name|NAMESPACE_URI
operator|+
literal|"\" xmlns:xs=\""
operator|+
name|Namespaces
operator|.
name|SCHEMA_NS
operator|+
literal|"\" count=\""
operator|+
name|iRows
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"</sql:result>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/* SQL Query performed updates */
name|xmlBuf
operator|.
name|append
argument_list|(
literal|"<sql:result xmlns:sql=\""
operator|+
name|SQLModule
operator|.
name|NAMESPACE_URI
operator|+
literal|"\" updateCount=\""
operator|+
name|stmt
operator|.
name|getUpdateCount
argument_list|()
operator|+
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
comment|//return the xml result set
return|return
name|ModuleUtils
operator|.
name|stringToXML
argument_list|(
name|context
argument_list|,
name|xmlBuf
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Converts a SQL data type to an XML data type 	 *  	 * @param	sqlType	The SQL data type as specified by JDBC 	 * 	 * @return	The XML Type as specified by eXist 	 */
specifier|private
name|int
name|sqlTypeToXMLType
parameter_list|(
name|int
name|sqlType
parameter_list|)
block|{
switch|switch
condition|(
name|sqlType
condition|)
block|{
case|case
name|Types
operator|.
name|ARRAY
case|:
return|return
name|Type
operator|.
name|NODE
return|;
case|case
name|Types
operator|.
name|BIGINT
case|:
return|return
name|Type
operator|.
name|INT
return|;
case|case
name|Types
operator|.
name|BINARY
case|:
return|return
name|Type
operator|.
name|BASE64_BINARY
return|;
case|case
name|Types
operator|.
name|BIT
case|:
return|return
name|Type
operator|.
name|INT
return|;
case|case
name|Types
operator|.
name|BLOB
case|:
return|return
name|Type
operator|.
name|BASE64_BINARY
return|;
case|case
name|Types
operator|.
name|BOOLEAN
case|:
return|return
name|Type
operator|.
name|BOOLEAN
return|;
case|case
name|Types
operator|.
name|CHAR
case|:
return|return
name|Type
operator|.
name|STRING
return|;
case|case
name|Types
operator|.
name|CLOB
case|:
return|return
name|Type
operator|.
name|STRING
return|;
case|case
name|Types
operator|.
name|DECIMAL
case|:
return|return
name|Type
operator|.
name|DECIMAL
return|;
case|case
name|Types
operator|.
name|DOUBLE
case|:
return|return
name|Type
operator|.
name|DOUBLE
return|;
case|case
name|Types
operator|.
name|FLOAT
case|:
return|return
name|Type
operator|.
name|FLOAT
return|;
case|case
name|Types
operator|.
name|LONGVARCHAR
case|:
return|return
name|Type
operator|.
name|STRING
return|;
case|case
name|Types
operator|.
name|NUMERIC
case|:
return|return
name|Type
operator|.
name|NUMBER
return|;
case|case
name|Types
operator|.
name|SMALLINT
case|:
return|return
name|Type
operator|.
name|INT
return|;
case|case
name|Types
operator|.
name|TINYINT
case|:
return|return
name|Type
operator|.
name|INT
return|;
case|case
name|Types
operator|.
name|INTEGER
case|:
return|return
name|Type
operator|.
name|INTEGER
return|;
case|case
name|Types
operator|.
name|VARCHAR
case|:
return|return
name|Type
operator|.
name|STRING
return|;
default|default:
return|return
name|Type
operator|.
name|ANY_TYPE
return|;
block|}
block|}
block|}
end_class

end_unit

