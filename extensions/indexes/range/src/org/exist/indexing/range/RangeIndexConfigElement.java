begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
operator|.
name|CollationKeyAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRefBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
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
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndexConfig
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
name|NodePath
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
name|Collations
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
name|util
operator|.
name|XMLString
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
name|value
operator|.
name|*
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
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|XMLGregorianCalendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndexConfig
operator|.
name|MATCH_ATTR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndexConfig
operator|.
name|QNAME_ATTR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndexConfig
operator|.
name|TYPE_ATTR
import|;
end_import

begin_class
specifier|public
class|class
name|RangeIndexConfigElement
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|FILTER_ELEMENT
init|=
literal|"filter"
decl_stmt|;
specifier|protected
name|NodePath
name|path
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|type
init|=
name|Type
operator|.
name|STRING
decl_stmt|;
specifier|private
name|RangeIndexConfigElement
name|nextConfig
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|isQNameIndex
init|=
literal|false
decl_stmt|;
specifier|protected
name|RangeIndexAnalyzer
name|analyzer
init|=
operator|new
name|RangeIndexAnalyzer
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|includeNested
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|caseSensitive
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|usesCollation
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|wsTreatment
init|=
name|XMLString
operator|.
name|SUPPRESS_NONE
decl_stmt|;
specifier|private
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|conversion
operator|.
name|TypeConverter
name|typeConverter
init|=
literal|null
decl_stmt|;
specifier|public
name|RangeIndexConfigElement
parameter_list|(
name|Element
name|node
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|String
name|match
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|MATCH_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
operator|&&
name|match
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|path
operator|=
operator|new
name|NodePath
argument_list|(
name|namespaces
argument_list|,
name|match
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Range index module: Invalid match path in collection config: "
operator|+
name|match
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Range index module: invalid qname in configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|node
operator|.
name|hasAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
condition|)
block|{
name|QName
name|qname
init|=
name|LuceneIndexConfig
operator|.
name|parseQName
argument_list|(
name|node
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
name|NodePath
operator|.
name|SKIP
argument_list|)
expr_stmt|;
name|path
operator|.
name|addComponent
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|isQNameIndex
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|typeStr
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|TYPE_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeStr
operator|!=
literal|null
operator|&&
name|typeStr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|this
operator|.
name|type
operator|=
name|Type
operator|.
name|getType
argument_list|(
name|typeStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Invalid type declared for range index on "
operator|+
name|match
operator|+
literal|": "
operator|+
name|typeStr
argument_list|)
throw|;
block|}
block|}
name|parseChildren
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|String
name|collation
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"collation"
argument_list|)
decl_stmt|;
if|if
condition|(
name|collation
operator|!=
literal|null
operator|&&
name|collation
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|analyzer
operator|.
name|addCollation
argument_list|(
name|collation
argument_list|)
expr_stmt|;
name|usesCollation
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|nested
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"nested"
argument_list|)
decl_stmt|;
name|includeNested
operator|=
operator|(
name|nested
operator|==
literal|null
operator|||
name|nested
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
operator|)
expr_stmt|;
comment|// normalize whitespace if whitespace="normalize"
name|String
name|whitespace
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"whitespace"
argument_list|)
decl_stmt|;
if|if
condition|(
name|whitespace
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"trim"
operator|.
name|equalsIgnoreCase
argument_list|(
name|whitespace
argument_list|)
condition|)
block|{
name|wsTreatment
operator|=
name|XMLString
operator|.
name|SUPPRESS_BOTH
expr_stmt|;
block|}
if|else if
condition|(
literal|"normalize"
operator|.
name|equalsIgnoreCase
argument_list|(
name|whitespace
argument_list|)
condition|)
block|{
name|wsTreatment
operator|=
name|XMLString
operator|.
name|NORMALIZE
expr_stmt|;
block|}
block|}
name|String
name|caseStr
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"case"
argument_list|)
decl_stmt|;
if|if
condition|(
name|caseStr
operator|!=
literal|null
operator|&&
name|caseStr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|caseSensitive
operator|=
name|caseStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
block|}
name|String
name|custom
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"converter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|custom
operator|!=
literal|null
operator|&&
name|custom
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Class
name|customClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|custom
argument_list|)
decl_stmt|;
name|typeConverter
operator|=
operator|(
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|conversion
operator|.
name|TypeConverter
operator|)
name|customClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|RangeIndex
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Class for custom-type not found: "
operator|+
name|custom
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
name|RangeIndex
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to initialize custom-type: "
operator|+
name|custom
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|RangeIndex
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to initialize custom-type: "
operator|+
name|custom
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|parseChildren
parameter_list|(
name|Node
name|root
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|Node
name|child
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
if|if
condition|(
name|FILTER_ELEMENT
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|analyzer
operator|.
name|addFilter
argument_list|(
operator|(
name|Element
operator|)
name|child
argument_list|)
expr_stmt|;
block|}
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Field
name|convertToField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
comment|// check if a converter is defined for this index to handle on-the-fly conversions
specifier|final
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|conversion
operator|.
name|TypeConverter
name|custom
init|=
name|getTypeConverter
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|custom
operator|!=
literal|null
condition|)
block|{
return|return
name|custom
operator|.
name|toField
argument_list|(
name|fieldName
argument_list|,
name|content
argument_list|)
return|;
block|}
comment|// no converter: handle default types
specifier|final
name|int
name|fieldType
init|=
name|getType
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|fieldType
condition|)
block|{
case|case
name|Type
operator|.
name|INTEGER
case|:
case|case
name|Type
operator|.
name|LONG
case|:
case|case
name|Type
operator|.
name|UNSIGNED_LONG
case|:
name|long
name|lvalue
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|content
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongField
argument_list|(
name|fieldName
argument_list|,
name|lvalue
argument_list|,
name|LongField
operator|.
name|TYPE_NOT_STORED
argument_list|)
return|;
case|case
name|Type
operator|.
name|INT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_INT
case|:
case|case
name|Type
operator|.
name|SHORT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_SHORT
case|:
name|int
name|ivalue
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|content
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntField
argument_list|(
name|fieldName
argument_list|,
name|ivalue
argument_list|,
name|IntField
operator|.
name|TYPE_NOT_STORED
argument_list|)
return|;
case|case
name|Type
operator|.
name|DECIMAL
case|:
case|case
name|Type
operator|.
name|DOUBLE
case|:
name|double
name|dvalue
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|content
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoubleField
argument_list|(
name|fieldName
argument_list|,
name|dvalue
argument_list|,
name|DoubleField
operator|.
name|TYPE_NOT_STORED
argument_list|)
return|;
case|case
name|Type
operator|.
name|FLOAT
case|:
name|float
name|fvalue
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|content
argument_list|)
decl_stmt|;
return|return
operator|new
name|FloatField
argument_list|(
name|fieldName
argument_list|,
name|fvalue
argument_list|,
name|FloatField
operator|.
name|TYPE_NOT_STORED
argument_list|)
return|;
case|case
name|Type
operator|.
name|DATE
case|:
name|DateValue
name|dv
init|=
operator|new
name|DateValue
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|long
name|dl
init|=
name|dateToLong
argument_list|(
name|dv
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongField
argument_list|(
name|fieldName
argument_list|,
name|dl
argument_list|,
name|LongField
operator|.
name|TYPE_NOT_STORED
argument_list|)
return|;
case|case
name|Type
operator|.
name|TIME
case|:
name|TimeValue
name|tv
init|=
operator|new
name|TimeValue
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|long
name|tl
init|=
name|timeToLong
argument_list|(
name|tv
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongField
argument_list|(
name|fieldName
argument_list|,
name|tl
argument_list|,
name|LongField
operator|.
name|TYPE_NOT_STORED
argument_list|)
return|;
case|case
name|Type
operator|.
name|DATE_TIME
case|:
name|DateTimeValue
name|dtv
init|=
operator|new
name|DateTimeValue
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|String
name|dateStr
init|=
name|dateTimeToString
argument_list|(
name|dtv
argument_list|)
decl_stmt|;
return|return
operator|new
name|TextField
argument_list|(
name|fieldName
argument_list|,
name|dateStr
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
return|;
default|default:
return|return
operator|new
name|TextField
argument_list|(
name|fieldName
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// wrong type: ignore
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|// wrong type: ignore
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|BytesRef
name|convertToBytes
parameter_list|(
specifier|final
name|AtomicValue
name|content
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|content
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|INTEGER
case|:
case|case
name|Type
operator|.
name|LONG
case|:
case|case
name|Type
operator|.
name|UNSIGNED_LONG
case|:
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|content
operator|)
operator|.
name|getLong
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|SHORT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_SHORT
case|:
case|case
name|Type
operator|.
name|INT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_INT
case|:
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|content
operator|)
operator|.
name|getInt
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DECIMAL
case|:
specifier|final
name|long
name|dv
init|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|content
operator|)
operator|.
name|getDouble
argument_list|()
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|dv
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DOUBLE
case|:
specifier|final
name|long
name|lv
init|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
operator|(
operator|(
name|DoubleValue
operator|)
name|content
operator|)
operator|.
name|getDouble
argument_list|()
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|lv
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|FLOAT
case|:
specifier|final
name|int
name|iv
init|=
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
operator|(
operator|(
name|FloatValue
operator|)
name|content
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|iv
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DATE
case|:
specifier|final
name|long
name|dl
init|=
name|dateToLong
argument_list|(
operator|(
name|DateValue
operator|)
name|content
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|dl
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|TIME
case|:
specifier|final
name|long
name|tl
init|=
name|timeToLong
argument_list|(
operator|(
name|TimeValue
operator|)
name|content
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|tl
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DATE_TIME
case|:
specifier|final
name|String
name|dt
init|=
name|dateTimeToString
argument_list|(
operator|(
name|DateTimeValue
operator|)
name|content
argument_list|)
decl_stmt|;
name|bytes
operator|.
name|copyChars
argument_list|(
name|dt
argument_list|)
expr_stmt|;
break|break;
default|default:
name|bytes
operator|.
name|copyChars
argument_list|(
name|content
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
operator|.
name|toBytesRef
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|long
name|dateToLong
parameter_list|(
name|DateValue
name|date
parameter_list|)
block|{
specifier|final
name|XMLGregorianCalendar
name|utccal
init|=
name|date
operator|.
name|calendar
operator|.
name|normalize
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|long
operator|)
name|utccal
operator|.
name|getYear
argument_list|()
operator|<<
literal|16
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|utccal
operator|.
name|getMonth
argument_list|()
operator|<<
literal|8
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|utccal
operator|.
name|getDay
argument_list|()
operator|)
return|;
block|}
specifier|public
specifier|static
name|long
name|timeToLong
parameter_list|(
name|TimeValue
name|time
parameter_list|)
block|{
return|return
name|time
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|dateTimeToString
parameter_list|(
name|DateTimeValue
name|dtv
parameter_list|)
block|{
specifier|final
name|XMLGregorianCalendar
name|utccal
init|=
name|dtv
operator|.
name|calendar
operator|.
name|normalize
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|formatNumber
argument_list|(
name|utccal
operator|.
name|getMillisecond
argument_list|()
argument_list|,
literal|3
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|formatNumber
argument_list|(
name|utccal
operator|.
name|getSecond
argument_list|()
argument_list|,
literal|2
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|formatNumber
argument_list|(
name|utccal
operator|.
name|getMinute
argument_list|()
argument_list|,
literal|2
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|formatNumber
argument_list|(
name|utccal
operator|.
name|getHour
argument_list|()
argument_list|,
literal|2
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|formatNumber
argument_list|(
name|utccal
operator|.
name|getDay
argument_list|()
argument_list|,
literal|2
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|formatNumber
argument_list|(
name|utccal
operator|.
name|getMonth
argument_list|()
argument_list|,
literal|2
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|formatNumber
argument_list|(
name|utccal
operator|.
name|getYear
argument_list|()
argument_list|,
literal|4
argument_list|,
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|formatNumber
parameter_list|(
name|int
name|number
parameter_list|,
name|int
name|digits
parameter_list|,
name|StringBuilder
name|sb
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|long
name|n
init|=
name|number
decl_stmt|;
while|while
condition|(
name|n
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|digit
init|=
literal|'0'
operator|+
operator|(
name|int
operator|)
name|n
operator|%
literal|10
decl_stmt|;
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
operator|(
name|char
operator|)
name|digit
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|digits
condition|)
block|{
break|break;
block|}
name|n
operator|=
name|n
operator|/
literal|10
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|<
name|digits
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|count
init|;
name|i
operator|<
name|digits
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|'0'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|TextCollector
name|getCollector
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
return|return
operator|new
name|SimpleTextCollector
argument_list|(
name|this
argument_list|,
name|includeNested
argument_list|,
name|wsTreatment
argument_list|,
name|caseSensitive
argument_list|)
return|;
block|}
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|analyzer
return|;
block|}
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|caseSensitive
return|;
block|}
specifier|public
name|boolean
name|usesCollation
parameter_list|()
block|{
return|return
name|usesCollation
return|;
block|}
specifier|public
name|boolean
name|isComplex
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// no fields: return type
return|return
name|type
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|conversion
operator|.
name|TypeConverter
name|getTypeConverter
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|typeConverter
return|;
block|}
specifier|public
name|NodePath
name|getNodePath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|RangeIndexConfigElement
name|config
parameter_list|)
block|{
if|if
condition|(
name|nextConfig
operator|==
literal|null
condition|)
name|nextConfig
operator|=
name|config
expr_stmt|;
else|else
name|nextConfig
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RangeIndexConfigElement
name|getNext
parameter_list|()
block|{
return|return
name|nextConfig
return|;
block|}
specifier|public
name|boolean
name|match
parameter_list|(
name|NodePath
name|other
parameter_list|)
block|{
if|if
condition|(
name|isQNameIndex
condition|)
block|{
specifier|final
name|QName
name|qn1
init|=
name|path
operator|.
name|getLastComponent
argument_list|()
decl_stmt|;
specifier|final
name|QName
name|qn2
init|=
name|other
operator|.
name|getLastComponent
argument_list|()
decl_stmt|;
return|return
name|qn1
operator|.
name|getNameType
argument_list|()
operator|==
name|qn2
operator|.
name|getNameType
argument_list|()
operator|&&
name|qn2
operator|.
name|equals
argument_list|(
name|qn1
argument_list|)
return|;
block|}
return|return
name|other
operator|.
name|match
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|find
parameter_list|(
name|NodePath
name|other
parameter_list|)
block|{
return|return
name|match
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
end_class

end_unit

