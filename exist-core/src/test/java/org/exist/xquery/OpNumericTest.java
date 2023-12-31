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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|DatabaseConfigurationException
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
name|Constants
operator|.
name|ArithmeticOperator
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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

begin_class
specifier|public
class|class
name|OpNumericTest
block|{
specifier|private
specifier|static
name|DBBroker
name|broker
decl_stmt|;
specifier|private
specifier|static
name|XQueryContext
name|context
decl_stmt|;
specifier|private
specifier|static
name|DayTimeDurationValue
name|dtDuration
decl_stmt|;
specifier|private
specifier|static
name|YearMonthDurationValue
name|ymDuration
decl_stmt|;
specifier|private
specifier|static
name|DateTimeValue
name|dateTime
decl_stmt|;
specifier|private
specifier|static
name|DateValue
name|date
decl_stmt|;
specifier|private
specifier|static
name|TimeValue
name|time
decl_stmt|;
specifier|private
specifier|static
name|IntegerValue
name|integer
decl_stmt|;
specifier|private
specifier|static
name|DecimalValue
name|decimal
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|=
operator|new
name|XQueryContext
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|dtDuration
operator|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1D"
argument_list|)
expr_stmt|;
name|ymDuration
operator|=
operator|new
name|YearMonthDurationValue
argument_list|(
literal|"P1Y"
argument_list|)
expr_stmt|;
name|dateTime
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-06-02T16:28:00Z"
argument_list|)
expr_stmt|;
name|date
operator|=
operator|new
name|DateValue
argument_list|(
literal|"2005-06-02"
argument_list|)
expr_stmt|;
name|time
operator|=
operator|new
name|TimeValue
argument_list|(
literal|"16:28:00Z"
argument_list|)
expr_stmt|;
name|integer
operator|=
operator|new
name|IntegerValue
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|decimal
operator|=
operator|new
name|DecimalValue
argument_list|(
literal|"1.5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|EXistException
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|broker
operator|=
literal|null
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|OpNumeric
name|buildOp
parameter_list|(
name|ArithmeticOperator
name|op
parameter_list|,
name|AtomicValue
name|a
parameter_list|,
name|AtomicValue
name|b
parameter_list|)
block|{
return|return
operator|new
name|OpNumeric
argument_list|(
name|context
argument_list|,
operator|new
name|LiteralValue
argument_list|(
name|context
argument_list|,
name|a
argument_list|)
argument_list|,
operator|new
name|LiteralValue
argument_list|(
name|context
argument_list|,
name|b
argument_list|)
argument_list|,
name|op
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertOp
parameter_list|(
name|String
name|result
parameter_list|,
name|ArithmeticOperator
name|op
parameter_list|,
name|AtomicValue
name|a
parameter_list|,
name|AtomicValue
name|b
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|r
init|=
name|buildOp
argument_list|(
name|op
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
operator|.
name|eval
argument_list|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
name|r
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|idiv1
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertOp
argument_list|(
literal|"2"
argument_list|,
name|ArithmeticOperator
operator|.
name|DIVISION_INTEGER
argument_list|,
operator|new
name|IntegerValue
argument_list|(
literal|3
argument_list|)
argument_list|,
operator|new
name|DecimalValue
argument_list|(
literal|"1.5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|idiv2
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertOp
argument_list|(
literal|"2"
argument_list|,
name|ArithmeticOperator
operator|.
name|DIVISION_INTEGER
argument_list|,
operator|new
name|IntegerValue
argument_list|(
literal|4
argument_list|)
argument_list|,
operator|new
name|IntegerValue
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|idiv3
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertOp
argument_list|(
literal|"2"
argument_list|,
name|ArithmeticOperator
operator|.
name|DIVISION_INTEGER
argument_list|,
operator|new
name|IntegerValue
argument_list|(
literal|5
argument_list|)
argument_list|,
operator|new
name|IntegerValue
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|idivReturnType1
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION_INTEGER
argument_list|,
name|integer
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|idivReturnType2
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION_INTEGER
argument_list|,
name|integer
argument_list|,
name|decimal
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|idivReturnType3
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION_INTEGER
argument_list|,
name|decimal
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|divReturnType1
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION
argument_list|,
name|integer
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|divReturnType2
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION
argument_list|,
name|integer
argument_list|,
name|decimal
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|divReturnType3
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION
argument_list|,
name|decimal
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|divReturnType4
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION
argument_list|,
name|dtDuration
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|divReturnType5
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION
argument_list|,
name|ymDuration
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|divReturnType6
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION
argument_list|,
name|dtDuration
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|divReturnType7
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|DIVISION
argument_list|,
name|ymDuration
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multReturnType1
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|MULTIPLICATION
argument_list|,
name|dtDuration
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multReturnType2
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|MULTIPLICATION
argument_list|,
name|integer
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multReturnType3
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|MULTIPLICATION
argument_list|,
name|ymDuration
argument_list|,
name|integer
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multReturnType4
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|MULTIPLICATION
argument_list|,
name|integer
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType1
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|dtDuration
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType2
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|ymDuration
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType3
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|date
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType4
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|dateTime
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType5
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|time
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType6
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|dtDuration
argument_list|,
name|date
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType7
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|dtDuration
argument_list|,
name|dateTime
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType8
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|dtDuration
argument_list|,
name|time
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType9
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|date
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType10
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|dateTime
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType11
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|ymDuration
argument_list|,
name|date
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plusReturnType12
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|ADDITION
argument_list|,
name|ymDuration
argument_list|,
name|dateTime
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType1
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|dtDuration
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType2
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|ymDuration
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType3
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|dateTime
argument_list|,
name|dateTime
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType4
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|date
argument_list|,
name|date
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType5
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|time
argument_list|,
name|time
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType6
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|dateTime
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType7
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|dateTime
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType8
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|date
argument_list|,
name|ymDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType9
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|date
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minusReturnType10
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|TIME
argument_list|,
name|buildOp
argument_list|(
name|ArithmeticOperator
operator|.
name|SUBTRACTION
argument_list|,
name|time
argument_list|,
name|dtDuration
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

