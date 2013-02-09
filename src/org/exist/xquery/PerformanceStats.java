begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

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
name|Database
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_class
specifier|public
class|class
name|PerformanceStats
block|{
specifier|public
specifier|final
specifier|static
name|String
name|RANGE_IDX_TYPE
init|=
literal|"range"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XML_NAMESPACE
init|=
literal|"http://exist-db.org/xquery/profiling"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XML_PREFIX
init|=
literal|"stats"
decl_stmt|;
specifier|public
specifier|static
name|String
name|CONFIG_PROPERTY_TRACE
init|=
literal|"xquery.profiling.trace"
decl_stmt|;
specifier|public
specifier|static
name|String
name|CONFIG_ATTR_TRACE
init|=
literal|"trace"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NO_INDEX
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|BASIC_INDEX
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|OPTIMIZED_INDEX
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
class|class
name|IndexStats
block|{
name|String
name|source
decl_stmt|;
name|String
name|indexType
decl_stmt|;
name|int
name|line
decl_stmt|;
name|int
name|column
decl_stmt|;
name|int
name|mode
init|=
literal|0
decl_stmt|;
name|int
name|usageCount
init|=
literal|1
decl_stmt|;
name|long
name|executionTime
init|=
literal|0
decl_stmt|;
specifier|private
name|IndexStats
parameter_list|(
name|String
name|indexType
parameter_list|,
name|String
name|source
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|indexType
operator|=
name|indexType
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
specifier|public
name|void
name|recordUsage
parameter_list|(
name|long
name|elapsed
parameter_list|)
block|{
name|executionTime
operator|+=
name|elapsed
expr_stmt|;
name|usageCount
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|indexType
operator|.
name|hashCode
argument_list|()
operator|+
name|source
operator|.
name|hashCode
argument_list|()
operator|+
name|line
operator|+
name|column
operator|+
name|mode
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
name|obj
operator|instanceof
name|IndexStats
condition|)
block|{
specifier|final
name|IndexStats
name|other
init|=
operator|(
name|IndexStats
operator|)
name|obj
decl_stmt|;
return|return
name|other
operator|.
name|indexType
operator|.
name|equals
argument_list|(
name|indexType
argument_list|)
operator|&&
name|other
operator|.
name|source
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|&&
name|other
operator|.
name|line
operator|==
name|line
operator|&&
name|other
operator|.
name|column
operator|==
name|column
operator|&&
name|other
operator|.
name|mode
operator|==
name|mode
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|QueryStats
block|{
name|String
name|source
decl_stmt|;
name|long
name|executionTime
init|=
literal|0
decl_stmt|;
name|int
name|callCount
init|=
literal|1
decl_stmt|;
name|QueryStats
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|source
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|source
operator|=
literal|""
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|recordCall
parameter_list|(
name|long
name|elapsed
parameter_list|)
block|{
name|executionTime
operator|+=
name|elapsed
expr_stmt|;
name|callCount
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|source
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
name|obj
operator|instanceof
name|QueryStats
condition|)
block|{
return|return
operator|(
operator|(
name|QueryStats
operator|)
name|obj
operator|)
operator|.
name|source
operator|.
name|equals
argument_list|(
name|source
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|FunctionStats
extends|extends
name|QueryStats
block|{
name|QName
name|qname
decl_stmt|;
name|FunctionStats
parameter_list|(
name|String
name|source
parameter_list|,
name|QName
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|qname
operator|.
name|hashCode
argument_list|()
operator|+
name|source
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
name|obj
operator|instanceof
name|FunctionStats
condition|)
block|{
specifier|final
name|FunctionStats
name|ostats
init|=
operator|(
name|FunctionStats
operator|)
name|obj
decl_stmt|;
return|return
name|qname
operator|.
name|equalsSimple
argument_list|(
name|ostats
operator|.
name|qname
argument_list|)
operator|&&
name|source
operator|.
name|equals
argument_list|(
name|ostats
operator|.
name|source
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|CompareByTime
implements|implements
name|Comparator
argument_list|<
name|FunctionStats
argument_list|>
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|FunctionStats
name|o1
parameter_list|,
name|FunctionStats
name|o2
parameter_list|)
block|{
specifier|final
name|long
name|t1
init|=
name|o1
operator|.
name|executionTime
decl_stmt|;
specifier|final
name|long
name|t2
init|=
name|o2
operator|.
name|executionTime
decl_stmt|;
return|return
name|t1
operator|==
name|t2
condition|?
literal|0
else|:
operator|(
name|t1
operator|>
name|t2
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
return|;
block|}
block|}
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueryStats
argument_list|>
name|queries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueryStats
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|FunctionStats
argument_list|,
name|FunctionStats
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|FunctionStats
argument_list|,
name|FunctionStats
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|IndexStats
argument_list|,
name|IndexStats
argument_list|>
name|indexStats
init|=
operator|new
name|HashMap
argument_list|<
name|IndexStats
argument_list|,
name|IndexStats
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|enabled
init|=
literal|false
decl_stmt|;
specifier|private
name|Database
name|db
decl_stmt|;
specifier|public
name|PerformanceStats
parameter_list|(
name|Database
name|db
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|config
init|=
operator|(
name|String
operator|)
name|db
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PerformanceStats
operator|.
name|CONFIG_PROPERTY_TRACE
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
name|enabled
operator|=
name|config
operator|.
name|equals
argument_list|(
literal|"functions"
argument_list|)
operator|||
literal|"yes"
operator|.
name|equals
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|enabled
operator|=
name|enable
expr_stmt|;
block|}
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
operator|||
operator|(
name|db
operator|!=
literal|null
operator|&&
name|db
operator|.
name|getPerformanceStats
argument_list|()
operator|!=
name|this
operator|&&
name|db
operator|.
name|getPerformanceStats
argument_list|()
operator|.
name|isEnabled
argument_list|()
operator|)
return|;
block|}
specifier|public
name|void
name|recordQuery
parameter_list|(
name|String
name|source
parameter_list|,
name|long
name|elapsed
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|QueryStats
name|stats
init|=
name|queries
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|stats
operator|=
operator|new
name|QueryStats
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|stats
operator|.
name|executionTime
operator|=
name|elapsed
expr_stmt|;
name|queries
operator|.
name|put
argument_list|(
name|source
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|recordCall
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|recordFunctionCall
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|source
parameter_list|,
name|long
name|elapsed
parameter_list|)
block|{
specifier|final
name|FunctionStats
name|newStats
init|=
operator|new
name|FunctionStats
argument_list|(
name|source
argument_list|,
name|qname
argument_list|)
decl_stmt|;
specifier|final
name|FunctionStats
name|stats
init|=
name|functions
operator|.
name|get
argument_list|(
name|newStats
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|newStats
operator|.
name|executionTime
operator|=
name|elapsed
expr_stmt|;
name|functions
operator|.
name|put
argument_list|(
name|newStats
argument_list|,
name|newStats
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|recordCall
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|recordIndexUse
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|source
parameter_list|,
name|int
name|mode
parameter_list|,
name|long
name|elapsed
parameter_list|)
block|{
specifier|final
name|IndexStats
name|newStats
init|=
operator|new
name|IndexStats
argument_list|(
name|indexName
argument_list|,
name|source
argument_list|,
name|expression
operator|.
name|getLine
argument_list|()
argument_list|,
name|expression
operator|.
name|getColumn
argument_list|()
argument_list|,
name|mode
argument_list|)
decl_stmt|;
specifier|final
name|IndexStats
name|stats
init|=
name|indexStats
operator|.
name|get
argument_list|(
name|newStats
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|newStats
operator|.
name|executionTime
operator|=
name|elapsed
expr_stmt|;
name|indexStats
operator|.
name|put
argument_list|(
name|newStats
argument_list|,
name|newStats
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|recordUsage
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|merge
parameter_list|(
name|PerformanceStats
name|otherStats
parameter_list|)
block|{
for|for
control|(
specifier|final
name|QueryStats
name|other
range|:
name|otherStats
operator|.
name|queries
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|QueryStats
name|mine
init|=
name|queries
operator|.
name|get
argument_list|(
name|other
operator|.
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|mine
operator|==
literal|null
condition|)
block|{
name|queries
operator|.
name|put
argument_list|(
name|other
operator|.
name|source
argument_list|,
name|other
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mine
operator|.
name|callCount
operator|+=
name|other
operator|.
name|callCount
expr_stmt|;
name|mine
operator|.
name|executionTime
operator|+=
name|other
operator|.
name|executionTime
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|FunctionStats
name|other
range|:
name|otherStats
operator|.
name|functions
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|FunctionStats
name|mine
init|=
name|functions
operator|.
name|get
argument_list|(
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|mine
operator|==
literal|null
condition|)
block|{
name|functions
operator|.
name|put
argument_list|(
name|other
argument_list|,
name|other
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mine
operator|.
name|callCount
operator|+=
name|other
operator|.
name|callCount
expr_stmt|;
name|mine
operator|.
name|executionTime
operator|+=
name|other
operator|.
name|executionTime
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|IndexStats
name|other
range|:
name|otherStats
operator|.
name|indexStats
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|IndexStats
name|mine
init|=
name|indexStats
operator|.
name|get
argument_list|(
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|mine
operator|==
literal|null
condition|)
block|{
name|indexStats
operator|.
name|put
argument_list|(
name|other
argument_list|,
name|other
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mine
operator|.
name|usageCount
operator|+=
name|other
operator|.
name|usageCount
expr_stmt|;
name|mine
operator|.
name|executionTime
operator|+=
name|other
operator|.
name|executionTime
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|String
name|createKey
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|source
parameter_list|)
block|{
return|return
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|":"
operator|+
name|qname
operator|.
name|getLocalName
argument_list|()
operator|+
literal|":"
operator|+
name|source
return|;
block|}
specifier|public
name|boolean
name|hasData
parameter_list|()
block|{
return|return
operator|!
operator|(
name|functions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|queries
operator|.
name|isEmpty
argument_list|()
operator|)
return|;
block|}
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
specifier|final
name|FunctionStats
index|[]
name|stats
init|=
name|sort
argument_list|()
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
name|stats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|pw
operator|.
name|format
argument_list|(
literal|"\n%30s %8.3f %8d"
argument_list|,
name|stats
index|[
name|i
index|]
operator|.
name|qname
argument_list|,
name|stats
index|[
name|i
index|]
operator|.
name|executionTime
operator|/
literal|1000.0
argument_list|,
name|stats
index|[
name|i
index|]
operator|.
name|callCount
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|FunctionStats
index|[]
name|sort
parameter_list|()
block|{
specifier|final
name|FunctionStats
name|stats
index|[]
init|=
operator|new
name|FunctionStats
index|[
name|functions
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FunctionStats
name|next
range|:
name|functions
operator|.
name|values
argument_list|()
control|)
block|{
name|stats
index|[
name|j
index|]
operator|=
name|next
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|stats
argument_list|,
operator|new
name|CompareByTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|toXML
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
specifier|final
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"calls"
argument_list|,
name|XML_NAMESPACE
argument_list|,
name|XML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|QueryStats
name|stats
range|:
name|queries
operator|.
name|values
argument_list|()
control|)
block|{
name|attrs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"source"
argument_list|,
literal|"source"
argument_list|,
literal|"CDATA"
argument_list|,
name|stats
operator|.
name|source
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"elapsed"
argument_list|,
literal|"elapsed"
argument_list|,
literal|"CDATA"
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|stats
operator|.
name|executionTime
operator|/
literal|1000.0
argument_list|)
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"calls"
argument_list|,
literal|"calls"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|stats
operator|.
name|callCount
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
literal|"query"
argument_list|,
name|XML_NAMESPACE
argument_list|,
name|XML_PREFIX
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|FunctionStats
name|stats
range|:
name|functions
operator|.
name|values
argument_list|()
control|)
block|{
name|attrs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|stats
operator|.
name|qname
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"elapsed"
argument_list|,
literal|"elapsed"
argument_list|,
literal|"CDATA"
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|stats
operator|.
name|executionTime
operator|/
literal|1000.0
argument_list|)
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"calls"
argument_list|,
literal|"calls"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|stats
operator|.
name|callCount
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|stats
operator|.
name|source
operator|!=
literal|null
condition|)
block|{
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"source"
argument_list|,
literal|"source"
argument_list|,
literal|"CDATA"
argument_list|,
name|stats
operator|.
name|source
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
literal|"function"
argument_list|,
name|XML_NAMESPACE
argument_list|,
name|XML_PREFIX
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|IndexStats
name|stats
range|:
name|indexStats
operator|.
name|values
argument_list|()
control|)
block|{
name|attrs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
name|stats
operator|.
name|indexType
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"source"
argument_list|,
literal|"source"
argument_list|,
literal|"CDATA"
argument_list|,
name|stats
operator|.
name|source
operator|+
literal|" ["
operator|+
name|stats
operator|.
name|line
operator|+
literal|":"
operator|+
name|stats
operator|.
name|column
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"elapsed"
argument_list|,
literal|"elapsed"
argument_list|,
literal|"CDATA"
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|stats
operator|.
name|executionTime
operator|/
literal|1000.0
argument_list|)
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"calls"
argument_list|,
literal|"calls"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|stats
operator|.
name|usageCount
argument_list|)
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"optimization"
argument_list|,
literal|"optimization"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|stats
operator|.
name|mode
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
literal|"index"
argument_list|,
name|XML_NAMESPACE
argument_list|,
name|XML_PREFIX
argument_list|)
argument_list|,
name|attrs
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
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|queries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|functions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|indexStats
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|queries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|functions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|indexStats
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

