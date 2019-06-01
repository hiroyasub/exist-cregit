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
name|indexing
operator|.
name|lucene
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
name|facet
operator|.
name|Facets
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
name|index
operator|.
name|IndexableField
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
name|search
operator|.
name|Query
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
name|persistent
operator|.
name|Match
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|modules
operator|.
name|lucene
operator|.
name|LuceneModule
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Match class containing the score of a match and a reference to  * the query that generated it.  */
end_comment

begin_class
specifier|public
class|class
name|LuceneMatch
extends|extends
name|Match
block|{
specifier|private
name|float
name|score
init|=
literal|0.0f
decl_stmt|;
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
specifier|private
name|LuceneIndexWorker
operator|.
name|LuceneFacets
name|facets
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FieldValue
index|[]
argument_list|>
name|fields
init|=
literal|null
decl_stmt|;
specifier|private
name|LuceneMatch
parameter_list|(
name|int
name|contextId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
name|this
argument_list|(
name|contextId
argument_list|,
name|nodeId
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|LuceneMatch
parameter_list|(
name|int
name|contextId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|Query
name|query
parameter_list|,
name|LuceneIndexWorker
operator|.
name|LuceneFacets
name|facets
parameter_list|)
block|{
name|super
argument_list|(
name|contextId
argument_list|,
name|nodeId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|facets
operator|=
name|facets
expr_stmt|;
block|}
specifier|private
name|LuceneMatch
parameter_list|(
name|LuceneMatch
name|copy
parameter_list|)
block|{
name|super
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|copy
operator|.
name|score
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|copy
operator|.
name|query
expr_stmt|;
name|this
operator|.
name|facets
operator|=
name|copy
operator|.
name|facets
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|copy
operator|.
name|fields
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Match
name|createInstance
parameter_list|(
name|int
name|contextId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|matchTerm
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Match
name|createInstance
parameter_list|(
name|int
name|contextId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
return|return
operator|new
name|LuceneMatch
argument_list|(
name|contextId
argument_list|,
name|nodeId
argument_list|,
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Match
name|newCopy
parameter_list|()
block|{
return|return
operator|new
name|LuceneMatch
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|LuceneIndex
operator|.
name|ID
return|;
block|}
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|score
return|;
block|}
specifier|protected
name|void
name|setScore
parameter_list|(
name|float
name|score
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
specifier|public
name|Facets
name|getFacets
parameter_list|()
block|{
return|return
name|this
operator|.
name|facets
operator|.
name|getFacets
argument_list|()
return|;
block|}
specifier|protected
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableField
index|[]
name|values
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
specifier|final
name|FieldValue
index|[]
name|v
init|=
operator|new
name|FieldValue
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexableField
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|.
name|numericValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|v
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|NumericField
argument_list|(
name|value
operator|.
name|numericValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|v
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|StringField
argument_list|(
name|value
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|fields
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
annotation|@
name|Nullable
name|Sequence
name|getField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|FieldValue
index|[]
name|values
init|=
name|fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|values
index|[
literal|0
index|]
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
return|;
block|}
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|(
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|FieldValue
name|value
range|:
name|values
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|value
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|// DW: missing hashCode() ?
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|LuceneMatch
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|LuceneMatch
name|o
init|=
operator|(
name|LuceneMatch
operator|)
name|other
decl_stmt|;
return|return
operator|(
name|nodeId
operator|==
name|o
operator|.
name|nodeId
operator|||
name|nodeId
operator|.
name|equals
argument_list|(
name|o
operator|.
name|nodeId
argument_list|)
operator|)
operator|&&
name|query
operator|==
operator|(
operator|(
name|LuceneMatch
operator|)
name|other
operator|)
operator|.
name|query
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matchEquals
parameter_list|(
name|Match
name|other
parameter_list|)
block|{
return|return
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
specifier|private
interface|interface
name|FieldValue
block|{
name|AtomicValue
name|getValue
parameter_list|(
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
function_decl|;
block|}
specifier|private
specifier|static
class|class
name|StringField
implements|implements
name|FieldValue
block|{
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
name|StringField
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|AtomicValue
name|getValue
parameter_list|(
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Type
operator|.
name|TIME
case|:
return|return
operator|new
name|TimeValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DATE_TIME
case|:
return|return
operator|new
name|DateTimeValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DATE
case|:
return|return
operator|new
name|DateValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|FLOAT
case|:
return|return
operator|new
name|FloatValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|INTEGER
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
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
argument_list|)
return|;
default|default:
return|return
operator|new
name|StringValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|NumericField
implements|implements
name|FieldValue
block|{
specifier|private
specifier|final
name|Number
name|value
decl_stmt|;
name|NumericField
parameter_list|(
name|Number
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|AtomicValue
name|getValue
parameter_list|(
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Type
operator|.
name|TIME
case|:
specifier|final
name|Date
name|time
init|=
operator|new
name|Date
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|GregorianCalendar
name|gregorianCalendar
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|gregorianCalendar
operator|.
name|setTime
argument_list|(
name|time
argument_list|)
expr_stmt|;
specifier|final
name|XMLGregorianCalendar
name|calendar
init|=
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|gregorianCalendar
argument_list|)
decl_stmt|;
return|return
operator|new
name|TimeValue
argument_list|(
name|calendar
argument_list|)
return|;
case|case
name|Type
operator|.
name|DATE_TIME
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
name|LuceneModule
operator|.
name|EXXQDYFT0004
argument_list|,
literal|"Cannot convert numeric field to xs:dateTime"
argument_list|)
throw|;
case|case
name|Type
operator|.
name|DATE
case|:
specifier|final
name|long
name|dl
init|=
name|value
operator|.
name|longValue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|year
init|=
operator|(
name|int
operator|)
operator|(
name|dl
operator|>>
literal|16
operator|)
operator|&
literal|0xFFFF
decl_stmt|;
specifier|final
name|int
name|month
init|=
operator|(
name|int
operator|)
operator|(
name|dl
operator|>>
literal|8
operator|)
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|day
init|=
operator|(
name|int
operator|)
operator|(
name|dl
operator|&
literal|0xFF
operator|)
decl_stmt|;
specifier|final
name|DateValue
name|date
init|=
operator|new
name|DateValue
argument_list|()
decl_stmt|;
name|date
operator|.
name|calendar
operator|.
name|setYear
argument_list|(
name|year
argument_list|)
expr_stmt|;
name|date
operator|.
name|calendar
operator|.
name|setMonth
argument_list|(
name|month
argument_list|)
expr_stmt|;
name|date
operator|.
name|calendar
operator|.
name|setDay
argument_list|(
name|day
argument_list|)
expr_stmt|;
return|return
name|date
return|;
case|case
name|Type
operator|.
name|FLOAT
case|:
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|INTEGER
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
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
default|default:
return|return
operator|new
name|StringValue
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit
