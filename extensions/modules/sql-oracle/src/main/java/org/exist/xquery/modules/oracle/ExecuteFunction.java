begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|oracle
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|CallableStatement
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
name|SQLRecoverableException
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
name|Timestamp
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
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|oracle
operator|.
name|jdbc
operator|.
name|OracleTypes
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
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|sql
operator|.
name|SQLModule
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
name|sql
operator|.
name|SQLUtils
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|NodeList
import|;
end_import

begin_comment
comment|/**  * eXist Oracle Module Extension ExecuteFunction  *   * Execute a PL/SQL stored procedure within an Oracle RDBMS.  *   * @author<a href="mailto:robert.walpole@metoffice.gov.uk">Robert Walpole</a>  * @serial 2009-03-23  * @version 1.0  *   * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext,  *      org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|ExecuteFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ExecuteFunction
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Executes a PL/SQL stored procedure passed as the second argument against an Oracle RDBMS specified by the connection "
operator|+
literal|"in the first argument with the position of the result set cursor at the fourth argument. Stored procedure parameters "
operator|+
literal|"may be passed in the third argument using an XML fragment with the following structure: "
operator|+
literal|"<oracle:parameters><orace:param oracle:pos=\"{param-position}\" oracle:type=\"{param-type}\"/>{param-value}"
operator|+
literal|"</oracle:parameters>."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"connection-handle"
argument_list|,
name|Type
operator|.
name|INTEGER
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
literal|"plsql-statement"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The PL/SQL stored procedure"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Input parameters for the stored procedure (if any)"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result-set-position"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The position of the result set cursor"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"make-node-from-column-name"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The flag that indicates whether "
operator|+
literal|""
operator|+
literal|"the xml nodes should be formed from the column names (in this mode a space in a Column Name will be replaced by an underscore!)"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the results"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"execute"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Executes a PL/SQL stored procedure passed as the second argument against an Oracle RDBMS specified by the connection "
operator|+
literal|"in the first argument with the position of the result set cursor at the fourth argument. Stored procedure parameters "
operator|+
literal|"may be passed in the third argument using an XML fragment with the following structure: "
operator|+
literal|"<oracle:parameters><orace:param oracle:pos=\"{param-position}\" oracle:type=\"{param-type}\"/>{param-value}"
operator|+
literal|"</oracle:parameters>. An additional return code parameter is supported which can be used to specify an integer value returned "
operator|+
literal|"in the first position of the statement to indicate success of the PL/SQL call."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"connection-handle"
argument_list|,
name|Type
operator|.
name|INTEGER
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
literal|"plsql-statement"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The PL/SQL stored procedure"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Input parameters for the stored procedure (if any)"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result-set-position"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The position of the result set cursor"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"make-node-from-column-name"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The flag that indicates whether "
operator|+
literal|"the xml nodes should be formed from the column names (in this mode a space in a Column Name will be replaced by an underscore!)"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"return-code"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The expected function return code which indicates successful execution"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the results"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PARAMETERS_ELEMENT_NAME
init|=
literal|"parameters"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PARAM_ELEMENT_NAME
init|=
literal|"param"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TYPE_ATTRIBUTE_NAME
init|=
literal|"type"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|POSITION_ATTRIBUTE_NAME
init|=
literal|"pos"
decl_stmt|;
specifier|private
name|DateFormat
name|xmlDf
decl_stmt|;
comment|/**      * ExecuteFunction Constructor      *      * @param context      *            The Context of the calling XQuery      */
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
name|xmlDf
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS"
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|5
operator|||
name|args
operator|.
name|length
operator|==
literal|6
condition|)
block|{
comment|// was a connection and PL/SQL statement specified?
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
name|connection
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
name|connection
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
comment|// get the PL/SQL statement
name|String
name|plSql
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|// get the input parameters (if any)
name|Element
name|parameters
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|parameters
operator|=
operator|(
name|Element
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
expr_stmt|;
block|}
comment|// was a result set position specified?
name|int
name|resultSetPos
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resultSetPos
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
name|boolean
name|haveReturnCode
init|=
literal|false
decl_stmt|;
name|int
name|plSqlSuccess
init|=
literal|1
decl_stmt|;
comment|// default value of 1 for success
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|6
condition|)
block|{
comment|// a return code is expected so what is the value indicating success?
name|plSqlSuccess
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|5
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|haveReturnCode
operator|=
literal|true
expr_stmt|;
block|}
name|CallableStatement
name|statement
init|=
literal|null
decl_stmt|;
name|ResultSet
name|resultSet
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|int
name|iRow
init|=
literal|0
decl_stmt|;
name|statement
operator|=
name|connection
operator|.
name|prepareCall
argument_list|(
name|plSql
argument_list|)
expr_stmt|;
if|if
condition|(
name|haveReturnCode
condition|)
block|{
name|statement
operator|.
name|registerOutParameter
argument_list|(
literal|1
argument_list|,
name|Types
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resultSetPos
operator|!=
literal|0
condition|)
block|{
name|statement
operator|.
name|registerOutParameter
argument_list|(
name|resultSetPos
argument_list|,
name|OracleTypes
operator|.
name|CURSOR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setParametersOnPreparedStatement
argument_list|(
name|statement
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
name|statement
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
name|haveReturnCode
condition|)
block|{
name|int
name|returnCode
init|=
name|statement
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnCode
operator|!=
name|plSqlSuccess
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|plSql
operator|+
literal|" failed ["
operator|+
name|returnCode
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
if|if
condition|(
name|resultSetPos
operator|!=
literal|0
condition|)
block|{
comment|// iterate through the result set building an XML document
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"result"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"count"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|=
operator|(
name|ResultSet
operator|)
name|statement
operator|.
name|getObject
argument_list|(
name|resultSetPos
argument_list|)
expr_stmt|;
name|ResultSetMetaData
name|rsmd
init|=
name|resultSet
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
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"row"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|resultSet
operator|.
name|getRow
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// get each tuple in the row
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
name|getColumnLabel
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
name|String
name|colValue
init|=
name|resultSet
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|colElement
init|=
literal|"field"
decl_stmt|;
if|if
condition|(
operator|(
operator|(
name|BooleanValue
operator|)
name|args
index|[
literal|4
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
operator|&&
name|columnName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// use column names as the XML node
comment|/** 									 * Spaces in column names are replaced with 									 * underscore's 									 */
name|colElement
operator|=
name|SQLUtils
operator|.
name|escapeXmlAttr
argument_list|(
name|columnName
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'_'
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|colElement
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|BooleanValue
operator|)
name|args
index|[
literal|4
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
operator|||
name|columnName
operator|.
name|length
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|String
name|name
decl_stmt|;
if|if
condition|(
name|columnName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|name
operator|=
name|SQLUtils
operator|.
name|escapeXmlAttr
argument_list|(
name|columnName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"Column: "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|rsmd
operator|.
name|getColumnTypeName
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
name|Namespaces
operator|.
name|SCHEMA_NS
argument_list|,
literal|"xs"
argument_list|)
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|SQLUtils
operator|.
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
argument_list|)
expr_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
comment|// Add a null indicator attribute if the value was SQL Null
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"null"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|colValue
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
name|SQLUtils
operator|.
name|escapeXmlText
argument_list|(
name|colValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|iRow
operator|++
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// Change the root element count attribute to have the correct value
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|Node
name|count
init|=
name|node
operator|.
name|getNode
argument_list|()
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"count"
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|count
operator|.
name|setNodeValue
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|iRow
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
comment|// return the XML result set
return|return
operator|(
name|node
operator|)
return|;
block|}
else|else
block|{
comment|// there was no result set so just return an empty sequence
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
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
literal|"oracle:execute() Caught SQLException \""
operator|+
name|sqle
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\" for PL/SQL: \""
operator|+
name|plSql
operator|+
literal|"\""
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
comment|//return details about the SQLException
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"exception"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|boolean
name|recoverable
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|sqle
operator|instanceof
name|SQLRecoverableException
condition|)
block|{
name|recoverable
operator|=
literal|true
expr_stmt|;
block|}
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"recoverable"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|recoverable
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"state"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|sqlState
init|=
name|sqle
operator|.
name|getSQLState
argument_list|()
decl_stmt|;
if|if
condition|(
name|sqlState
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
name|sqle
operator|.
name|getSQLState
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|characters
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"message"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|sqle
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"stack-trace"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|bufStackTrace
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|sqle
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bufStackTrace
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
operator|new
name|String
argument_list|(
name|bufStackTrace
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"oracle"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|SQLUtils
operator|.
name|escapeXmlText
argument_list|(
name|plSql
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|int
name|line
init|=
name|getLine
argument_list|()
decl_stmt|;
name|int
name|column
init|=
name|getColumn
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"xquery"
argument_list|,
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|OracleModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"line"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"column"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|column
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
finally|finally
block|{
name|release
argument_list|(
name|connection
argument_list|,
name|statement
argument_list|,
name|resultSet
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid number of arguments ["
operator|+
name|args
operator|.
name|length
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Release DB resources 	 * @param connection 	 * @param statement 	 * @param rs 	 */
specifier|protected
name|void
name|release
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Statement
name|statement
parameter_list|,
name|ResultSet
name|rs
parameter_list|)
block|{
if|if
condition|(
name|rs
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Unable to close ResultSet: "
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Unable to close Statement: "
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Unable to close Connection: "
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|setParametersOnPreparedStatement
parameter_list|(
name|Statement
name|stmt
parameter_list|,
name|Element
name|parametersElement
parameter_list|)
throws|throws
name|SQLException
throws|,
name|XPathException
block|{
if|if
condition|(
name|parametersElement
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|)
operator|&&
name|parametersElement
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|PARAMETERS_ELEMENT_NAME
argument_list|)
condition|)
block|{
name|NodeList
name|paramElements
init|=
name|parametersElement
operator|.
name|getElementsByTagNameNS
argument_list|(
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|PARAM_ELEMENT_NAME
argument_list|)
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
name|paramElements
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|param
init|=
operator|(
operator|(
name|Element
operator|)
name|paramElements
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|)
decl_stmt|;
name|String
name|value
init|=
name|param
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|param
operator|.
name|getAttributeNS
argument_list|(
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TYPE_ATTRIBUTE_NAME
argument_list|)
decl_stmt|;
name|int
name|position
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|param
operator|.
name|getAttributeNS
argument_list|(
name|OracleModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|POSITION_ATTRIBUTE_NAME
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|sqlType
init|=
name|SQLUtils
operator|.
name|sqlTypeFromString
argument_list|(
name|type
argument_list|)
decl_stmt|;
comment|// What if SQL type is date???
if|if
condition|(
name|sqlType
operator|==
name|Types
operator|.
name|DATE
condition|)
block|{
name|Date
name|date
init|=
name|xmlDf
operator|.
name|parse
argument_list|(
name|value
argument_list|)
decl_stmt|;
operator|(
operator|(
name|PreparedStatement
operator|)
name|stmt
operator|)
operator|.
name|setTimestamp
argument_list|(
name|position
argument_list|,
operator|new
name|Timestamp
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|PreparedStatement
operator|)
name|stmt
operator|)
operator|.
name|setObject
argument_list|(
name|position
argument_list|,
name|value
argument_list|,
name|sqlType
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|pex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unable to parse date from value "
operator|+
name|value
operator|+
literal|". Expected format is YYYY-MM-DDThh:mm:ss.sss"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to set stored procedure parameter at position "
operator|+
name|position
operator|+
literal|" as "
operator|+
name|type
operator|+
literal|" with value "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

