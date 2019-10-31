begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|dom
operator|.
name|memtree
operator|.
name|ElementImpl
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit Tests for sql:execute  */
end_comment

begin_class
specifier|public
class|class
name|ExecuteFunctionTest
block|{
comment|// the function that will be tested
specifier|final
specifier|static
name|QName
name|functionName
init|=
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
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testStringEncoding
parameter_list|()
throws|throws
name|SQLException
throws|,
name|XPathException
block|{
comment|// mocks a simple SQL query returning a single string and checks the result
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContextStub
argument_list|()
decl_stmt|;
name|ExecuteFunction
name|execute
init|=
operator|new
name|ExecuteFunction
argument_list|(
name|context
argument_list|,
name|signatureByArity
argument_list|(
name|ExecuteFunction
operator|.
name|FS_EXECUTE
argument_list|,
name|functionName
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"SELECT NAME FROM BLA"
decl_stmt|;
specifier|final
name|String
name|testValue
init|=
literal|"<&>"
decl_stmt|;
comment|// create mock objects
name|Connection
name|connection
init|=
name|mock
argument_list|(
name|Connection
operator|.
name|class
argument_list|)
decl_stmt|;
name|Statement
name|stmt
init|=
name|mock
argument_list|(
name|Statement
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResultSet
name|rs
init|=
name|mock
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResultSetMetaData
name|rsmd
init|=
name|mock
argument_list|(
name|ResultSetMetaData
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
index|[]
name|mocks
init|=
operator|new
name|Object
index|[]
block|{
name|connection
block|,
name|stmt
block|,
name|rs
block|,
name|rsmd
block|}
decl_stmt|;
comment|// mock behavior
name|expect
argument_list|(
name|connection
operator|.
name|createStatement
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|stmt
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|stmt
operator|.
name|getResultSet
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|stmt
operator|.
name|getUpdateCount
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|getMetaData
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|rsmd
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|getRow
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|testValue
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|wasNull
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnCount
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnLabel
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|"NAME"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnTypeName
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|"VARCHAR(100)"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnType
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|Types
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
comment|// register mocked connection
specifier|final
name|long
name|connId
init|=
name|SQLModule
operator|.
name|storeConnection
argument_list|(
name|context
argument_list|,
name|connection
argument_list|)
decl_stmt|;
comment|// execute function
name|Sequence
name|res
init|=
name|execute
operator|.
name|eval
argument_list|(
operator|new
name|Sequence
index|[]
block|{
operator|new
name|IntegerValue
argument_list|(
name|connId
argument_list|)
block|,
operator|new
name|StringValue
argument_list|(
name|sql
argument_list|)
block|,
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
block|}
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
comment|// assert expectations
name|verify
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|res
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|res
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|root
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|res
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:result"
argument_list|,
name|root
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|row
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:row"
argument_list|,
name|row
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|col
init|=
name|row
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:field"
argument_list|,
name|col
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testValue
argument_list|,
name|col
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyParameters
parameter_list|()
throws|throws
name|SQLException
throws|,
name|XPathException
block|{
comment|// mocks a simple SQL prepared statement with one parameter
comment|// is filled with an empty xsl:param element
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContextStub
argument_list|()
decl_stmt|;
name|ExecuteFunction
name|execute
init|=
operator|new
name|ExecuteFunction
argument_list|(
name|context
argument_list|,
name|signatureByArity
argument_list|(
name|ExecuteFunction
operator|.
name|FS_EXECUTE
argument_list|,
name|functionName
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
comment|// this is what an empty xsl:param element of type varchar should use to fill prepared statement parameters
specifier|final
name|String
name|emptyStringValue
init|=
literal|""
decl_stmt|;
specifier|final
name|Integer
name|emptyIntValue
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"SELECT ? AS COL1, ? AS COL2"
decl_stmt|;
comment|// create mock objects
name|Connection
name|connection
init|=
name|mock
argument_list|(
name|Connection
operator|.
name|class
argument_list|)
decl_stmt|;
name|PreparedStatement
name|preparedStatement
init|=
name|mock
argument_list|(
name|PreparedStatement
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResultSet
name|rs
init|=
name|mock
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResultSetMetaData
name|rsmd
init|=
name|mock
argument_list|(
name|ResultSetMetaData
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
index|[]
name|mocks
init|=
operator|new
name|Object
index|[]
block|{
name|connection
block|,
name|preparedStatement
block|,
name|rs
block|,
name|rsmd
block|}
decl_stmt|;
comment|// register mocked connection and prepared statement
specifier|final
name|long
name|connId
init|=
name|SQLModule
operator|.
name|storeConnection
argument_list|(
name|context
argument_list|,
name|connection
argument_list|)
decl_stmt|;
specifier|final
name|long
name|stmtId
init|=
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
name|preparedStatement
argument_list|)
argument_list|)
decl_stmt|;
comment|// mock behavior
name|preparedStatement
operator|.
name|setObject
argument_list|(
literal|1
argument_list|,
name|emptyStringValue
argument_list|,
name|Types
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|preparedStatement
operator|.
name|setObject
argument_list|(
literal|2
argument_list|,
name|emptyIntValue
argument_list|,
name|Types
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|preparedStatement
operator|.
name|getConnection
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|preparedStatement
operator|.
name|execute
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|preparedStatement
operator|.
name|getResultSet
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|preparedStatement
operator|.
name|getUpdateCount
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|getRow
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|emptyStringValue
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|wasNull
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|emptyStringValue
operator|==
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|wasNull
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|emptyIntValue
operator|==
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rs
operator|.
name|getMetaData
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|rsmd
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnCount
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnLabel
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|"COL1"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnLabel
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|"COL2"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnTypeName
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|"VARCHAR"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnTypeName
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnType
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|Types
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rsmd
operator|.
name|getColumnType
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|Types
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
comment|// execute function
name|MemTreeBuilder
name|paramBuilder
init|=
operator|new
name|MemTreeBuilder
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|paramBuilder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parameters"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"param"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"varchar"
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"param"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"integer"
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
specifier|final
name|ElementImpl
name|sqlParams
init|=
operator|(
name|ElementImpl
operator|)
name|paramBuilder
operator|.
name|getDocument
argument_list|()
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|execute
operator|.
name|eval
argument_list|(
operator|new
name|Sequence
index|[]
block|{
operator|new
name|IntegerValue
argument_list|(
name|connId
argument_list|)
block|,
operator|new
name|IntegerValue
argument_list|(
name|stmtId
argument_list|)
block|,
name|sqlParams
block|,
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
block|}
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
comment|// assert expectations
name|verify
argument_list|(
name|preparedStatement
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSQLException
parameter_list|()
throws|throws
name|SQLException
throws|,
name|XPathException
block|{
comment|// mocks a simple SQL prepared statement with one parameter that fails on execution
comment|// and verifies the error message
comment|// the parameter is filled with an empty sql:param element
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContextStub
argument_list|()
decl_stmt|;
name|ExecuteFunction
name|execute
init|=
operator|new
name|ExecuteFunction
argument_list|(
name|context
argument_list|,
name|signatureByArity
argument_list|(
name|ExecuteFunction
operator|.
name|FS_EXECUTE
argument_list|,
name|functionName
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"SELECT ?"
decl_stmt|;
specifier|final
name|String
name|test_message
init|=
literal|"SQL ERROR"
decl_stmt|;
specifier|final
name|String
name|test_sqlState
init|=
literal|"SQL STATE"
decl_stmt|;
comment|// create mock objects
name|Connection
name|connection
init|=
name|mock
argument_list|(
name|Connection
operator|.
name|class
argument_list|)
decl_stmt|;
name|PreparedStatement
name|preparedStatement
init|=
name|mock
argument_list|(
name|PreparedStatement
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
index|[]
name|mocks
init|=
operator|new
name|Object
index|[]
block|{
name|connection
block|,
name|preparedStatement
block|}
decl_stmt|;
comment|// register mocked connection and prepared statement
specifier|final
name|long
name|connId
init|=
name|SQLModule
operator|.
name|storeConnection
argument_list|(
name|context
argument_list|,
name|connection
argument_list|)
decl_stmt|;
specifier|final
name|long
name|stmtId
init|=
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
name|preparedStatement
argument_list|)
argument_list|)
decl_stmt|;
comment|// mock behavior
name|expect
argument_list|(
name|preparedStatement
operator|.
name|getConnection
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|preparedStatement
operator|.
name|setObject
argument_list|(
literal|1
argument_list|,
literal|""
argument_list|,
name|Types
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|preparedStatement
operator|.
name|execute
argument_list|()
argument_list|)
operator|.
name|andThrow
argument_list|(
operator|new
name|SQLException
argument_list|(
name|test_message
argument_list|,
name|test_sqlState
argument_list|)
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
comment|// execute function
name|MemTreeBuilder
name|paramBuilder
init|=
operator|new
name|MemTreeBuilder
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|paramBuilder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parameters"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"param"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"varchar"
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
specifier|final
name|ElementImpl
name|sqlParams
init|=
operator|(
name|ElementImpl
operator|)
name|paramBuilder
operator|.
name|getDocument
argument_list|()
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|Sequence
name|res
init|=
name|execute
operator|.
name|eval
argument_list|(
operator|new
name|Sequence
index|[]
block|{
operator|new
name|IntegerValue
argument_list|(
name|connId
argument_list|)
block|,
operator|new
name|IntegerValue
argument_list|(
name|stmtId
argument_list|)
block|,
name|sqlParams
block|,
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
block|}
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
comment|// assert expectations
name|verify
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|res
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|res
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|root
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|res
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:exception"
argument_list|,
name|root
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|root
operator|.
name|getChildNodes
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|Node
name|state
init|=
name|node
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:state"
argument_list|,
name|state
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test_sqlState
argument_list|,
name|state
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|Node
name|message
init|=
name|node
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:message"
argument_list|,
name|message
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test_message
argument_list|,
name|message
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|Node
name|stackTrace
init|=
name|node
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:stack-trace"
argument_list|,
name|stackTrace
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|Node
name|sqlErr
init|=
name|node
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:sql"
argument_list|,
name|sqlErr
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sql
argument_list|,
name|sqlErr
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|Node
name|parameters
init|=
name|node
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:parameters"
argument_list|,
name|parameters
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|param1
init|=
name|parameters
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:param"
argument_list|,
name|param1
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"varchar"
argument_list|,
name|param1
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItemNS
argument_list|(
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"type"
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|param1
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|Node
name|xquery
init|=
name|node
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:xquery"
argument_list|,
name|xquery
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMissingParamType
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// mocks a simple SQL prepared statement with one parameter that lacks a type attribute.
comment|// This should throw an informative error.
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContextStub
argument_list|()
decl_stmt|;
name|ExecuteFunction
name|execute
init|=
operator|new
name|ExecuteFunction
argument_list|(
name|context
argument_list|,
name|signatureByArity
argument_list|(
name|ExecuteFunction
operator|.
name|FS_EXECUTE
argument_list|,
name|functionName
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"SELECT ?"
decl_stmt|;
comment|// create mock objects
name|Connection
name|connection
init|=
name|mock
argument_list|(
name|Connection
operator|.
name|class
argument_list|)
decl_stmt|;
name|PreparedStatement
name|preparedStatement
init|=
name|mock
argument_list|(
name|PreparedStatement
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
index|[]
name|mocks
init|=
operator|new
name|Object
index|[]
block|{
name|connection
block|,
name|preparedStatement
block|}
decl_stmt|;
comment|// register mocked connection and prepared statement
specifier|final
name|long
name|connId
init|=
name|SQLModule
operator|.
name|storeConnection
argument_list|(
name|context
argument_list|,
name|connection
argument_list|)
decl_stmt|;
specifier|final
name|long
name|stmtId
init|=
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
name|preparedStatement
argument_list|)
argument_list|)
decl_stmt|;
comment|// mock behavior
name|expect
argument_list|(
name|preparedStatement
operator|.
name|getConnection
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
comment|// no behavior necessary - error should be thrown before first call
name|replay
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
comment|// execute function
name|MemTreeBuilder
name|paramBuilder
init|=
operator|new
name|MemTreeBuilder
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|paramBuilder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parameters"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"param"
argument_list|,
name|SQLModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paramBuilder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|paramBuilder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
specifier|final
name|ElementImpl
name|sqlParams
init|=
operator|(
name|ElementImpl
operator|)
name|paramBuilder
operator|.
name|getDocument
argument_list|()
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
try|try
block|{
name|execute
operator|.
name|eval
argument_list|(
operator|new
name|Sequence
index|[]
block|{
operator|new
name|IntegerValue
argument_list|(
name|connId
argument_list|)
block|,
operator|new
name|IntegerValue
argument_list|(
name|stmtId
argument_list|)
block|,
name|sqlParams
block|,
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
block|}
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This should have thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"<sql:param> must contain attribute sql:type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEncodingInErrorMessage
parameter_list|()
throws|throws
name|SQLException
throws|,
name|XPathException
block|{
comment|// mocks a failing SQL query returning a single string and
comment|// checks the resulting error report
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContextStub
argument_list|()
decl_stmt|;
name|ExecuteFunction
name|execute
init|=
operator|new
name|ExecuteFunction
argument_list|(
name|context
argument_list|,
name|signatureByArity
argument_list|(
name|ExecuteFunction
operator|.
name|FS_EXECUTE
argument_list|,
name|functionName
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"SELECT '<NAME>' FROM BLA"
decl_stmt|;
specifier|final
name|String
name|testMessage
init|=
literal|"Some<&> error occurred!"
decl_stmt|;
comment|// create mock objects
name|Connection
name|connection
init|=
name|mock
argument_list|(
name|Connection
operator|.
name|class
argument_list|)
decl_stmt|;
name|Statement
name|stmt
init|=
name|mock
argument_list|(
name|Statement
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
index|[]
name|mocks
init|=
operator|new
name|Object
index|[]
block|{
name|connection
block|,
name|stmt
block|}
decl_stmt|;
comment|// mock behavior
name|expect
argument_list|(
name|connection
operator|.
name|createStatement
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|stmt
operator|.
name|execute
argument_list|(
name|query
argument_list|)
argument_list|)
operator|.
name|andStubThrow
argument_list|(
operator|new
name|SQLException
argument_list|(
name|testMessage
argument_list|)
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
comment|// register mocked connection
specifier|final
name|long
name|connId
init|=
name|SQLModule
operator|.
name|storeConnection
argument_list|(
name|context
argument_list|,
name|connection
argument_list|)
decl_stmt|;
comment|// execute function
name|Sequence
name|res
init|=
name|execute
operator|.
name|eval
argument_list|(
operator|new
name|Sequence
index|[]
block|{
operator|new
name|IntegerValue
argument_list|(
name|connId
argument_list|)
block|,
operator|new
name|StringValue
argument_list|(
name|query
argument_list|)
block|,
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
block|}
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
comment|// assert expectations
name|verify
argument_list|(
name|mocks
argument_list|)
expr_stmt|;
comment|//<sql:exception><sql:state/><sql:message/><sql:stack-trace/><sql:sql/></sql:exception>
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|res
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|res
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|root
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|res
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:exception"
argument_list|,
name|root
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|state
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:state"
argument_list|,
name|state
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|msg
init|=
name|state
operator|.
name|getNextSibling
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:message"
argument_list|,
name|msg
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testMessage
argument_list|,
name|msg
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|stacktrace
init|=
name|msg
operator|.
name|getNextSibling
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:stack-trace"
argument_list|,
name|stacktrace
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|sql
init|=
name|stacktrace
operator|.
name|getNextSibling
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:sql"
argument_list|,
name|sql
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|,
name|sql
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|xquery
init|=
name|sql
operator|.
name|getNextSibling
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sql:xquery"
argument_list|,
name|xquery
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|XQueryContextStub
extends|extends
name|XQueryContext
block|{
specifier|public
name|XQueryContextStub
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCacheClass
parameter_list|()
block|{
return|return
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|FileFilterInputStreamCache
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
specifier|static
name|FunctionSignature
name|signatureByArity
parameter_list|(
name|FunctionSignature
index|[]
name|signatures
parameter_list|,
name|QName
name|functionName
parameter_list|,
name|int
name|arity
parameter_list|)
block|{
for|for
control|(
name|FunctionSignature
name|signature
range|:
name|signatures
control|)
block|{
if|if
condition|(
name|signature
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
operator|&&
name|signature
operator|.
name|getArgumentCount
argument_list|()
operator|==
name|arity
condition|)
block|{
return|return
name|signature
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

