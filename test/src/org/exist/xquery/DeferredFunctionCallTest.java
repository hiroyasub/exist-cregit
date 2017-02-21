begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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
name|Item
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
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
name|Type
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|anyObject
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|DeferredFunctionCallTest
block|{
comment|/**      * resetState() make be called on the UserDefinedFunction of a DeferredFunctionCall      * before the function is eval'd, this is because the evaluation is deferred!      * resetState() clears the currentArguments to the function, however for a deferred      * function call we must ensure that we still have these when eval() is called      * otherwise we will get an NPE!      *       * This test tries to prove that eval can be called after resetState without      * causing problems for a DeferredFunctionCall      *      * The test implementation, due to the nature of the code under test, is rather horrible      * and mostly consists of tightly coupled mocking code making it very brittle.      * The interesting aspect of this test case is at the bottom of the function itself.      */
annotation|@
name|Test
specifier|public
name|void
name|ensure_argumentsToDeferredFunctionCall_AreNotLost_AfterReset_And_BeforeEval
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//mocks for FunctionCall constructor
name|XQueryContext
name|mockContext
init|=
name|EasyMock
operator|.
name|createNiceMock
argument_list|(
name|XQueryContext
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//mocks for evalFunction()
name|Sequence
name|mockContextSequence
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Sequence
operator|.
name|class
argument_list|)
decl_stmt|;
name|Item
name|mockContextItem
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Item
operator|.
name|class
argument_list|)
decl_stmt|;
name|Sequence
index|[]
name|mockSeq
init|=
block|{
name|Sequence
operator|.
name|EMPTY_SEQUENCE
block|}
decl_stmt|;
name|int
name|nextExpressionId
init|=
literal|1234
decl_stmt|;
name|SequenceType
index|[]
name|mockArgumentTypes
init|=
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
block|}
decl_stmt|;
comment|//mock for functionDef
name|FunctionSignature
name|mockFunctionSignature
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|FunctionSignature
operator|.
name|class
argument_list|)
decl_stmt|;
name|SequenceType
name|mockReturnType
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|SequenceType
operator|.
name|class
argument_list|)
decl_stmt|;
name|LocalVariable
name|mockMark
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|LocalVariable
operator|.
name|class
argument_list|)
decl_stmt|;
name|Expression
name|mockExpression
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Expression
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//expectations for UserDefinedFunction constructor
name|expect
argument_list|(
name|mockContext
operator|.
name|nextExpressionId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|nextExpressionId
operator|++
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockExpression
operator|.
name|simplify
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockExpression
argument_list|)
expr_stmt|;
comment|//expectations for FunctionCall constructor
name|expect
argument_list|(
name|mockContext
operator|.
name|nextExpressionId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|nextExpressionId
operator|++
argument_list|)
expr_stmt|;
comment|//expectations for FunctionCall.setFunction
name|expect
argument_list|(
name|mockFunctionSignature
operator|.
name|getReturnType
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockReturnType
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockReturnType
operator|.
name|getCardinality
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockReturnType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Type
operator|.
name|NODE
argument_list|)
operator|.
name|times
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockContext
operator|.
name|nextExpressionId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|nextExpressionId
operator|++
argument_list|)
expr_stmt|;
comment|//expectations for functionCall.evalFunction
name|expect
argument_list|(
name|mockContext
operator|.
name|isProfilingEnabled
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//expectations for DeferredFunctionCallImpl.setup
name|expect
argument_list|(
name|mockFunctionSignature
operator|.
name|getReturnType
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockReturnType
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockReturnType
operator|.
name|getCardinality
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockReturnType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Type
operator|.
name|NODE
argument_list|)
operator|.
name|times
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockContext
operator|.
name|nextExpressionId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|nextExpressionId
operator|++
argument_list|)
expr_stmt|;
comment|//expectations for DeferredFunctionCall.execute
name|mockContext
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|mockContext
operator|.
name|functionStart
argument_list|(
name|mockFunctionSignature
argument_list|)
expr_stmt|;
name|mockContext
operator|.
name|stackEnter
argument_list|(
operator|(
name|Expression
operator|)
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockContext
operator|.
name|declareVariableBinding
argument_list|(
operator|(
name|LocalVariable
operator|)
name|anyObject
argument_list|()
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
name|mockFunctionSignature
operator|.
name|getArgumentTypes
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockArgumentTypes
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockExpression
operator|.
name|eval
argument_list|(
name|mockContextSequence
argument_list|,
name|mockContextItem
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
name|mockExpression
operator|.
name|resetState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|mockContext
operator|.
name|stackLeave
argument_list|(
operator|(
name|Expression
operator|)
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
name|mockContext
operator|.
name|functionEnd
argument_list|()
expr_stmt|;
name|mockContext
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|mockContext
argument_list|,
name|mockFunctionSignature
argument_list|,
name|mockReturnType
argument_list|,
name|mockExpression
argument_list|)
expr_stmt|;
name|UserDefinedFunction
name|userDefinedFunction
init|=
operator|new
name|UserDefinedFunction
argument_list|(
name|mockContext
argument_list|,
name|mockFunctionSignature
argument_list|)
decl_stmt|;
name|userDefinedFunction
operator|.
name|addVariable
argument_list|(
literal|"testParam"
argument_list|)
expr_stmt|;
name|userDefinedFunction
operator|.
name|setFunctionBody
argument_list|(
name|mockExpression
argument_list|)
expr_stmt|;
name|FunctionCall
name|functionCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|mockContext
argument_list|,
name|userDefinedFunction
argument_list|)
decl_stmt|;
name|functionCall
operator|.
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//ensure DeferredFunction
comment|/*** this is the interesting bit ***/
comment|// 1) Call reset, this should set current arguments to null
name|functionCall
operator|.
name|resetState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|functionCall
operator|.
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//ensure DeferredFunction
comment|// 2) check UserDefinedFunction.currentArguments == null
name|assertNull
argument_list|(
name|userDefinedFunction
operator|.
name|getCurrentArguments
argument_list|()
argument_list|)
expr_stmt|;
comment|//so the currentArguments have been set to null, but deferredFunction should have its own copy
comment|// 3) Call functionCall.eval, if we dont get an NPE on reading currentArguments, then success :-)
name|DeferredFunctionCall
name|dfc
init|=
operator|(
name|DeferredFunctionCall
operator|)
name|functionCall
operator|.
name|evalFunction
argument_list|(
name|mockContextSequence
argument_list|,
name|mockContextItem
argument_list|,
name|mockSeq
argument_list|)
decl_stmt|;
name|dfc
operator|.
name|execute
argument_list|()
expr_stmt|;
comment|/** end interesting bit ***/
name|verify
argument_list|(
name|mockContext
argument_list|,
name|mockFunctionSignature
argument_list|,
name|mockReturnType
argument_list|,
name|mockExpression
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

