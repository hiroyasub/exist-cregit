begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
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
name|Comparison
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
name|assertFalse
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

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|DayTimeDurationTest
extends|extends
name|AbstractTimeRelatedTestCase
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XPathException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|create1
parameter_list|()
throws|throws
name|XPathException
block|{
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1Y4M"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XPathException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|create2
parameter_list|()
throws|throws
name|XPathException
block|{
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1Y"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XPathException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|create3
parameter_list|()
throws|throws
name|XPathException
block|{
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P4M"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|stringFormat1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P3DT1H2M3S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"P3DT1H2M3S"
argument_list|,
name|dv
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
name|stringFormat2
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT25H65M66.5S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"P2DT2H6M6.5S"
argument_list|,
operator|new
name|DurationValue
argument_list|(
name|dv
operator|.
name|getCanonicalDuration
argument_list|()
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
name|stringFormat3
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P0DT0H"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"PT0S"
argument_list|,
name|dv
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
name|stringFormat4
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"PT5H0M0S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"PT5H"
argument_list|,
name|dv
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
name|convert1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DayTimeDurationValue
name|dtdv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P3DT1H2M3S"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv
init|=
operator|(
name|DurationValue
operator|)
name|dtdv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DURATION
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"P3DT1H2M3S"
argument_list|,
name|dv
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
name|convert2
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DayTimeDurationValue
name|dtdv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P3DT1H2M3S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"P0M"
argument_list|,
name|dtdv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
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
name|getPart1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P3DT4H5M6S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|YEAR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|MONTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|DAY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|HOUR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|MINUTE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|dv
operator|.
name|getSeconds
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getPart2
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"-P3DT4H5M6S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|YEAR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|MONTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|DAY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|4
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|HOUR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5
argument_list|,
name|dv
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|MINUTE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|6
argument_list|,
name|dv
operator|.
name|getSeconds
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getValue1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DayTimeDurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT30S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|+
literal|30.0
argument_list|,
name|dv
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getValue2
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DayTimeDurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1D"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
argument_list|,
name|dv
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getType
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P3DT4H5M6S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|dv
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compare1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M4S"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M5S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|+
literal|1
argument_list|,
name|dv2
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|dv1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compare2
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M4S"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M4S"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv2
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|dv1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compare3
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M4S"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M5S"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|EQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|NEQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|GT
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|LT
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|GTEQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|LTEQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compare4
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M4S"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M4S"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|EQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|NEQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|GT
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|LT
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|GTEQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|LTEQ
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compare5
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"PT2H"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"PT2H0M"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv2
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|dv1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minMax1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M4S"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H3M5S"
argument_list|)
decl_stmt|;
name|assertDurationEquals
argument_list|(
name|dv2
argument_list|,
name|dv1
operator|.
name|max
argument_list|(
literal|null
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertDurationEquals
argument_list|(
name|dv2
argument_list|,
name|dv2
operator|.
name|max
argument_list|(
literal|null
argument_list|,
name|dv1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDurationEquals
argument_list|(
name|dv1
argument_list|,
name|dv1
operator|.
name|min
argument_list|(
literal|null
argument_list|,
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertDurationEquals
argument_list|(
name|dv1
argument_list|,
name|dv2
operator|.
name|min
argument_list|(
literal|null
argument_list|,
name|dv1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|plus1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P2DT12H5M"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P5DT12H"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv3
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P8DT5M"
argument_list|)
decl_stmt|;
name|assertDurationEquals
argument_list|(
name|dv3
argument_list|,
name|dv1
operator|.
name|plus
argument_list|(
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertDurationEquals
argument_list|(
name|dv3
argument_list|,
name|dv2
operator|.
name|plus
argument_list|(
name|dv1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|minus1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P2DT12H"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT10H30M"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv3
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT1H30M"
argument_list|)
decl_stmt|;
name|assertDurationEquals
argument_list|(
name|dv3
argument_list|,
name|dv1
operator|.
name|minus
argument_list|(
name|dv2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mult1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"PT2H10M"
argument_list|)
decl_stmt|;
specifier|final
name|DecimalValue
name|f
init|=
operator|new
name|DecimalValue
argument_list|(
literal|"2.1"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"PT4H33M"
argument_list|)
decl_stmt|;
name|assertDurationEquals
argument_list|(
name|dv2
argument_list|,
name|dv1
operator|.
name|mult
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|assertDurationEquals
argument_list|(
name|dv2
argument_list|,
name|f
operator|.
name|mult
argument_list|(
name|dv1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|div1
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT2H30M10.5S"
argument_list|)
decl_stmt|;
specifier|final
name|DecimalValue
name|f
init|=
operator|new
name|DecimalValue
argument_list|(
literal|"1.5"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"PT17H40M7S"
argument_list|)
decl_stmt|;
name|assertDurationEquals
argument_list|(
name|dv2
argument_list|,
name|dv1
operator|.
name|div
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|div2
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|DurationValue
name|dv1
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P2DT53M11S"
argument_list|)
decl_stmt|;
specifier|final
name|DurationValue
name|dv2
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
literal|"P1DT10H"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.4378349
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|dv1
operator|.
name|div
argument_list|(
name|dv2
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|Double
operator|.
name|class
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.0000001
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

