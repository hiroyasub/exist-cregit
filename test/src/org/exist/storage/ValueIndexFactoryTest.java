begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|btree
operator|.
name|Value
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
name|ByteConversion
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
name|DecimalValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_class
specifier|public
class|class
name|ValueIndexFactoryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|negativeNumbersComparison
parameter_list|()
block|{
comment|// -8.6...
name|byte
index|[]
name|data1
init|=
name|encode
argument_list|(
operator|-
literal|8.612328
argument_list|)
decl_stmt|;
name|long
name|v1
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// 1.0
name|byte
index|[]
name|data2
init|=
name|encode
argument_list|(
literal|1.0
argument_list|)
decl_stmt|;
name|long
name|v2
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// print data
name|print
argument_list|(
name|data1
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|data2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertTrue
argument_list|(
literal|"v1= "
operator|+
name|v1
operator|+
literal|" v2 = "
operator|+
name|v2
argument_list|,
name|v1
operator|<
name|v2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertEquals
argument_list|(
literal|"v1< v2"
argument_list|,
operator|-
literal|1
argument_list|,
operator|(
operator|new
name|Value
argument_list|(
name|data1
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|Value
argument_list|(
name|data2
argument_list|)
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|negativeNumbersComparison2
parameter_list|()
block|{
comment|// -8.6...
name|byte
index|[]
name|data1
init|=
name|encode
argument_list|(
operator|-
literal|8.612328
argument_list|)
decl_stmt|;
name|long
name|v1
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// 1.0
name|byte
index|[]
name|data2
init|=
name|encode
argument_list|(
operator|-
literal|1.0
argument_list|)
decl_stmt|;
name|long
name|v2
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// print data
name|print
argument_list|(
name|data1
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|data2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertTrue
argument_list|(
literal|"v1= "
operator|+
name|v1
operator|+
literal|" v2 = "
operator|+
name|v2
argument_list|,
name|v1
operator|<
name|v2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertEquals
argument_list|(
literal|"v1< v2"
argument_list|,
operator|-
literal|1
argument_list|,
operator|(
operator|new
name|Value
argument_list|(
name|data1
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|Value
argument_list|(
name|data2
argument_list|)
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|negativeNumbersComparison3
parameter_list|()
block|{
comment|// -8.6...
name|byte
index|[]
name|data1
init|=
name|encode
argument_list|(
literal|8.612328
argument_list|)
decl_stmt|;
name|long
name|v1
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// 1.0
name|byte
index|[]
name|data2
init|=
name|encode
argument_list|(
literal|1.0
argument_list|)
decl_stmt|;
name|long
name|v2
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// print data
name|print
argument_list|(
name|data1
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|data2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertTrue
argument_list|(
literal|"v1= "
operator|+
name|v1
operator|+
literal|" v2 = "
operator|+
name|v2
argument_list|,
name|v1
operator|>
name|v2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertEquals
argument_list|(
literal|"v1< v2"
argument_list|,
literal|1
argument_list|,
operator|(
operator|new
name|Value
argument_list|(
name|data1
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|Value
argument_list|(
name|data2
argument_list|)
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|negativeNumbersComparison4
parameter_list|()
block|{
comment|// -8.6...
name|byte
index|[]
name|data1
init|=
name|encode
argument_list|(
literal|8.612328
argument_list|)
decl_stmt|;
name|long
name|v1
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// 1.0
name|byte
index|[]
name|data2
init|=
name|encode
argument_list|(
operator|-
literal|1.0
argument_list|)
decl_stmt|;
name|long
name|v2
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// print data
name|print
argument_list|(
name|data1
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|data2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertTrue
argument_list|(
literal|"v1= "
operator|+
name|v1
operator|+
literal|" v2 = "
operator|+
name|v2
argument_list|,
name|v1
operator|>
name|v2
argument_list|)
expr_stmt|;
comment|// -8.6< 1.0
name|assertEquals
argument_list|(
literal|"v1< v2"
argument_list|,
literal|1
argument_list|,
operator|(
operator|new
name|Value
argument_list|(
name|data1
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|Value
argument_list|(
name|data2
argument_list|)
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|roundTripDecimal
parameter_list|()
throws|throws
name|EXistException
block|{
name|BigDecimal
name|dec
init|=
operator|new
name|BigDecimal
argument_list|(
literal|"123456789123456789123456789123456789.123456789123456789123456789"
argument_list|)
decl_stmt|;
name|byte
name|data
index|[]
init|=
name|ValueIndexFactory
operator|.
name|serialize
argument_list|(
operator|new
name|DecimalValue
argument_list|(
name|dec
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Indexable
name|value
init|=
name|ValueIndexFactory
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|value
operator|instanceof
name|DecimalValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dec
argument_list|,
operator|(
operator|(
name|DecimalValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|byte
index|[]
name|encode
parameter_list|(
name|double
name|number
parameter_list|)
block|{
specifier|final
name|long
name|bits
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|number
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|bits
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|private
specifier|static
name|void
name|print
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|Byte
operator|.
name|toString
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

