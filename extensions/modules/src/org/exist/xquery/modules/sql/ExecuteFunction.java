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
name|SQLXML
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|AppendingSAXAdapter
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
name|ReferenceNode
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
name|SAXAdapter
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
name|DateTimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_comment
comment|/**  * eXist SQL Module Extension ExecuteFunction.  *  *<p>Execute a SQL statement against a SQL capable Database</p>  *  * @author   Adam Retter<adam@exist-db.org>  * @version  1.13  * @see      org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  * @serial   2009-01-25  */
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|ExecuteFunction
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
literal|"Executes a SQL statement against a SQL db using the connection indicated by the connection handle."
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
literal|"The flag that indicates whether the xml nodes should be formed from the column names (in this mode a space in a Column Name will be replaced by an underscore!)"
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Executes a prepared SQL statement against a SQL db."
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
literal|"statement-handle"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The prepared statement handle"
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
literal|"Parameters for the prepared statement. e.g.<sql:parameters><sql:param sql:type=\"varchar\">value</sql:param></sql:parameters>"
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
literal|"The flag that indicates whether the xml nodes should be formed from the column names (in this mode a space in a Column Name will be replaced by an underscore!)"
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
comment|/**      * ExecuteFunction Constructor.      *      * @param  context    The Context of the calling XQuery      * @param  signature  DOCUMENT ME!      */
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
comment|/**      * evaluate the call to the XQuery execute() function, it is really the main entry point of this class.      *      * @param   args             arguments from the execute() function call      * @param   contextSequence  the Context Sequence to operate on (not used here internally!)      *      * @return  A node representing the SQL result set      *      * @throws  XPathException  DOCUMENT ME!      *      * @see     org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)      */
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
name|boolean
name|preparedStmt
init|=
literal|false
decl_stmt|;
comment|//setup the SQL statement
name|String
name|sql
init|=
literal|null
decl_stmt|;
name|Statement
name|stmt
init|=
literal|null
decl_stmt|;
name|boolean
name|executeResult
init|=
literal|false
decl_stmt|;
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|boolean
name|makeNodeFromColumnName
init|=
literal|false
decl_stmt|;
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
comment|//SQL or PreparedStatement?
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
comment|// get the SQL statement
name|sql
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|makeNodeFromColumnName
operator|=
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
expr_stmt|;
comment|//execute the statement
name|executeResult
operator|=
name|stmt
operator|.
name|execute
argument_list|(
name|sql
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
name|preparedStmt
operator|=
literal|true
expr_stmt|;
comment|//get the prepared statement
name|long
name|statementUID
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
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
name|PreparedStatementWithSQL
name|stmtWithSQL
init|=
name|SQLModule
operator|.
name|retrievePreparedStatement
argument_list|(
name|context
argument_list|,
name|statementUID
argument_list|)
decl_stmt|;
name|sql
operator|=
name|stmtWithSQL
operator|.
name|getSql
argument_list|()
expr_stmt|;
name|stmt
operator|=
name|stmtWithSQL
operator|.
name|getStmt
argument_list|()
expr_stmt|;
name|makeNodeFromColumnName
operator|=
operator|(
operator|(
name|BooleanValue
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
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
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
name|stmt
argument_list|,
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
argument_list|)
expr_stmt|;
block|}
comment|//execute the prepared statement
name|executeResult
operator|=
operator|(
operator|(
name|PreparedStatement
operator|)
name|stmt
operator|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//TODO throw exception
block|}
comment|// DW: stmt can be null ?
comment|// execute the query statement
if|if
condition|(
name|executeResult
condition|)
block|{
comment|/* SQL Query returned results */
comment|// iterate through the result set building an XML document
name|rs
operator|=
name|stmt
operator|.
name|getResultSet
argument_list|()
expr_stmt|;
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
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
while|while
condition|(
name|rs
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
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
name|rs
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
name|colElement
init|=
literal|"field"
decl_stmt|;
if|if
condition|(
name|makeNodeFromColumnName
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
comment|/**                                  * Spaces in column names are replaced with                                  * underscore's                                  */
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
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
name|makeNodeFromColumnName
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
name|TYPE_ATTRIBUTE_NAME
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
name|TYPE_ATTRIBUTE_NAME
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
comment|//get the content
if|if
condition|(
name|rsmd
operator|.
name|getColumnType
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
name|Types
operator|.
name|SQLXML
condition|)
block|{
comment|//parse sqlxml value
try|try
block|{
specifier|final
name|SQLXML
name|sqlXml
init|=
name|rs
operator|.
name|getSQLXML
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|rs
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|sqlXml
operator|.
name|getCharacterStream
argument_list|()
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|xr
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|AppendingSAXAdapter
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|xr
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
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
literal|"Could not parse column of type SQLXML: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|//otherwise assume string value
specifier|final
name|String
name|colValue
init|=
name|rs
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|rs
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
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
block|}
else|else
block|{
comment|/* SQL Query performed updates */
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
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
literal|"updateCount"
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
name|stmt
operator|.
name|getUpdateCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
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
literal|"sql:execute() Caught SQLException \""
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SQLModule
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
name|getSQLState
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
literal|"message"
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
literal|null
argument_list|)
expr_stmt|;
name|String
name|state
init|=
name|sqle
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
name|state
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
literal|"stack-trace"
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
literal|"sql"
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
name|sql
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
if|if
condition|(
name|stmt
operator|instanceof
name|PreparedStatement
condition|)
block|{
name|Element
name|parametersElement
init|=
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
decl_stmt|;
if|if
condition|(
name|parametersElement
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|SQLModule
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|PARAM_ELEMENT_NAME
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|PARAMETERS_ELEMENT_NAME
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
literal|null
argument_list|)
expr_stmt|;
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
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TYPE_ATTRIBUTE_NAME
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|PARAM_ELEMENT_NAME
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
name|TYPE_ATTRIBUTE_NAME
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
name|type
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
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
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
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"xquery"
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
name|getLine
argument_list|()
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
name|getColumn
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
operator|)
return|;
block|}
finally|finally
block|{
comment|// close any record set or statement
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
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to cleanup JDBC results"
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
name|rs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|preparedStmt
operator|&&
name|stmt
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stmt
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
literal|"Unable to cleanup JDBC results"
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|=
literal|null
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
name|SQLModule
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
name|SQLModule
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
name|Node
name|child
init|=
name|param
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
comment|// Prevent NPE
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|ReferenceNode
condition|)
block|{
name|child
operator|=
operator|(
operator|(
name|ReferenceNode
operator|)
name|child
operator|)
operator|.
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|value
init|=
name|child
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|type
init|=
name|param
operator|.
name|getAttributeNS
argument_list|(
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TYPE_ATTRIBUTE_NAME
argument_list|)
decl_stmt|;
specifier|final
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
if|if
condition|(
name|sqlType
operator|==
name|Types
operator|.
name|TIMESTAMP
condition|)
block|{
specifier|final
name|DateTimeValue
name|dv
init|=
operator|new
name|DateTimeValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
specifier|final
name|Timestamp
name|timestampValue
init|=
operator|new
name|Timestamp
argument_list|(
name|dv
operator|.
name|getDate
argument_list|()
operator|.
name|getTime
argument_list|()
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
name|i
operator|+
literal|1
argument_list|,
name|timestampValue
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
name|i
operator|+
literal|1
argument_list|,
name|value
argument_list|,
name|sqlType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

