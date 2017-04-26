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
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|ClassNotFoundException
throws|,
name|SQLException
throws|,
name|EXistException
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
name|signatures
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
name|connection
argument_list|,
name|stmt
argument_list|,
name|rs
argument_list|,
name|rsmd
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
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
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
name|signatures
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
name|connection
argument_list|,
name|stmt
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
name|getNodeValue
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
name|getNodeValue
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
