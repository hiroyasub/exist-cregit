begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|xacml
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TableModelEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TableModelListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|table
operator|.
name|TableModel
import|;
end_import

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
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|XACMLConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|XACMLUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|TargetMatch
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|UnknownIdentifierException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|attr
operator|.
name|AttributeDesignator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|attr
operator|.
name|AttributeValue
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|cond
operator|.
name|Evaluatable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|cond
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|cond
operator|.
name|FunctionFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|cond
operator|.
name|FunctionTypeException
import|;
end_import

begin_class
specifier|public
class|class
name|TargetTableModel
implements|implements
name|TableModel
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
name|TargetTableModel
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|UNSPECIFIED
init|=
literal|"[match all]"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AttributeDesignator
index|[]
name|SUBJECT_ATTRIBUTES
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AttributeDesignator
index|[]
name|ACTION_ATTRIBUTES
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AttributeDesignator
index|[]
name|RESOURCE_ATTRIBUTES
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AttributeDesignator
index|[]
name|ENVIRONMENT_ATTRIBUTES
decl_stmt|;
static|static
block|{
name|SUBJECT_ATTRIBUTES
operator|=
operator|new
name|AttributeDesignator
index|[
literal|4
index|]
expr_stmt|;
name|SUBJECT_ATTRIBUTES
index|[
literal|0
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|SUBJECT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|SUBJECT_ID_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SUBJECT_ATTRIBUTES
index|[
literal|1
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|SUBJECT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|URI_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|SUBJECT_NS_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SUBJECT_ATTRIBUTES
index|[
literal|2
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|SUBJECT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|GROUP_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SUBJECT_ATTRIBUTES
index|[
literal|3
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|SUBJECT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|USER_NAME_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ACTION_ATTRIBUTES
operator|=
operator|new
name|AttributeDesignator
index|[
literal|2
index|]
expr_stmt|;
name|ACTION_ATTRIBUTES
index|[
literal|0
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|ACTION_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|ACTION_ID_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ACTION_ATTRIBUTES
index|[
literal|1
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|ACTION_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|URI_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|ACTION_NS_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RESOURCE_ATTRIBUTES
operator|=
operator|new
name|AttributeDesignator
index|[
literal|6
index|]
expr_stmt|;
name|RESOURCE_ATTRIBUTES
index|[
literal|0
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|RESOURCE_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_ID_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RESOURCE_ATTRIBUTES
index|[
literal|1
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|RESOURCE_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|URI_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|MODULE_NS_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RESOURCE_ATTRIBUTES
index|[
literal|2
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|RESOURCE_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_CATEGORY_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RESOURCE_ATTRIBUTES
index|[
literal|3
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|RESOURCE_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|MODULE_CATEGORY_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RESOURCE_ATTRIBUTES
index|[
literal|4
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|RESOURCE_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|SOURCE_KEY_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RESOURCE_ATTRIBUTES
index|[
literal|5
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|RESOURCE_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|SOURCE_TYPE_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ENVIRONMENT_ATTRIBUTES
operator|=
operator|new
name|AttributeDesignator
index|[
literal|4
index|]
expr_stmt|;
name|ENVIRONMENT_ATTRIBUTES
index|[
literal|0
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|ENVIRONMENT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|DATE_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|CURRENT_DATE_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ENVIRONMENT_ATTRIBUTES
index|[
literal|1
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|ENVIRONMENT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|TIME_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|CURRENT_TIME_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ENVIRONMENT_ATTRIBUTES
index|[
literal|2
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|ENVIRONMENT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|DATETIME_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|CURRENT_DATETIME_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ENVIRONMENT_ATTRIBUTES
index|[
literal|3
index|]
operator|=
operator|new
name|AttributeDesignator
argument_list|(
name|AttributeDesignator
operator|.
name|ENVIRONMENT_TARGET
argument_list|,
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|XACMLConstants
operator|.
name|ACCESS_CONTEXT_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|type
decl_stmt|;
specifier|private
name|List
argument_list|<
name|TableModelListener
argument_list|>
name|listeners
decl_stmt|;
specifier|private
name|AttributeDesignator
index|[]
name|attributes
decl_stmt|;
specifier|private
name|Abbreviator
name|abbrev
decl_stmt|;
specifier|private
name|AttributeValue
index|[]
index|[]
name|values
decl_stmt|;
specifier|private
name|URI
index|[]
index|[]
name|functions
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|TargetTableModel
parameter_list|()
block|{
block|}
specifier|public
name|TargetTableModel
parameter_list|(
name|int
name|type
parameter_list|,
name|Abbreviator
name|abbrev
parameter_list|)
block|{
name|this
operator|.
name|abbrev
operator|=
name|abbrev
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|attributes
operator|=
name|getAttributes
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|AttributeValue
index|[
literal|0
index|]
index|[
literal|0
index|]
expr_stmt|;
name|functions
operator|=
operator|new
name|URI
index|[
literal|0
index|]
index|[
literal|0
index|]
expr_stmt|;
block|}
specifier|private
specifier|static
name|AttributeDesignator
index|[]
name|getAttributes
parameter_list|(
name|int
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|AttributeDesignator
operator|.
name|ACTION_TARGET
case|:
return|return
name|ACTION_ATTRIBUTES
return|;
case|case
name|AttributeDesignator
operator|.
name|RESOURCE_TARGET
case|:
return|return
name|RESOURCE_ATTRIBUTES
return|;
case|case
name|AttributeDesignator
operator|.
name|SUBJECT_TARGET
case|:
return|return
name|SUBJECT_ATTRIBUTES
return|;
case|case
name|AttributeDesignator
operator|.
name|ENVIRONMENT_TARGET
case|:
return|return
name|ENVIRONMENT_ATTRIBUTES
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid target type"
argument_list|)
throw|;
block|}
block|}
comment|//TableModel method implementations
specifier|public
name|int
name|getColumnCount
parameter_list|()
block|{
return|return
name|attributes
operator|.
name|length
return|;
block|}
specifier|public
name|int
name|getRowCount
parameter_list|()
block|{
return|return
name|values
operator|.
name|length
operator|+
literal|1
return|;
block|}
specifier|public
name|boolean
name|isCellEditable
parameter_list|(
name|int
name|row
parameter_list|,
name|int
name|col
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getColumnClass
parameter_list|(
name|int
name|col
parameter_list|)
block|{
return|return
name|String
operator|.
name|class
return|;
block|}
specifier|public
name|Object
name|getValueAt
parameter_list|(
name|int
name|row
parameter_list|,
name|int
name|col
parameter_list|)
block|{
if|if
condition|(
name|row
operator|==
name|values
operator|.
name|length
condition|)
return|return
literal|""
return|;
name|AttributeValue
name|value
init|=
name|values
index|[
name|row
index|]
index|[
name|col
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
name|UNSPECIFIED
return|;
name|URI
name|functionId
init|=
name|functions
index|[
name|row
index|]
index|[
name|col
index|]
decl_stmt|;
if|if
condition|(
name|functionId
operator|==
literal|null
condition|)
return|return
name|UNSPECIFIED
return|;
name|String
name|functionString
init|=
name|abbrev
operator|.
name|getAbbreviatedTargetFunctionId
argument_list|(
name|functionId
argument_list|,
name|attributes
index|[
name|col
index|]
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|functionString
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Abbreviated function string was unexpectedly null.  FunctionId URI was '"
operator|+
name|functionId
operator|+
literal|"' (Row "
operator|+
name|row
operator|+
literal|", column "
operator|+
name|col
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|UNSPECIFIED
return|;
block|}
name|String
name|stringValue
init|=
name|value
operator|.
name|encode
argument_list|()
decl_stmt|;
if|if
condition|(
name|stringValue
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"String representation of a non-null attribute value was unexpectedly null.  (Row "
operator|+
name|row
operator|+
literal|", column "
operator|+
name|col
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|UNSPECIFIED
return|;
block|}
return|return
literal|"<html>"
operator|+
name|XACMLUtil
operator|.
name|XMLEscape
argument_list|(
name|functionString
argument_list|)
operator|+
literal|"&nbsp;<b>"
operator|+
name|XACMLUtil
operator|.
name|XMLEscape
argument_list|(
name|stringValue
argument_list|)
operator|+
literal|"</b>"
return|;
block|}
specifier|public
name|void
name|setValueAt
parameter_list|(
name|Object
name|value
parameter_list|,
name|int
name|row
parameter_list|,
name|int
name|col
parameter_list|)
block|{
comment|//do nothing
block|}
specifier|public
name|AttributeDesignator
name|getAttribute
parameter_list|(
name|int
name|col
parameter_list|)
block|{
return|return
name|attributes
index|[
name|col
index|]
return|;
block|}
specifier|public
name|URI
name|getFunctionId
parameter_list|(
name|int
name|row
parameter_list|,
name|int
name|col
parameter_list|)
block|{
return|return
operator|(
name|row
operator|==
name|values
operator|.
name|length
operator|)
condition|?
literal|null
else|:
name|functions
index|[
name|row
index|]
index|[
name|col
index|]
return|;
block|}
specifier|public
name|AttributeValue
name|getValue
parameter_list|(
name|int
name|row
parameter_list|,
name|int
name|col
parameter_list|)
block|{
return|return
operator|(
name|row
operator|==
name|values
operator|.
name|length
operator|)
condition|?
literal|null
else|:
name|values
index|[
name|row
index|]
index|[
name|col
index|]
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|URI
name|functionId
parameter_list|,
name|AttributeValue
name|value
parameter_list|,
name|int
name|row
parameter_list|,
name|int
name|col
parameter_list|)
block|{
name|TableModelEvent
name|event
decl_stmt|;
if|if
condition|(
name|row
operator|==
name|values
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
comment|//add a new row if a value was entered into the last row
name|addRow
argument_list|()
expr_stmt|;
name|event
operator|=
operator|new
name|TableModelEvent
argument_list|(
name|this
argument_list|,
name|row
argument_list|,
name|row
argument_list|,
name|TableModelEvent
operator|.
name|ALL_COLUMNS
argument_list|,
name|TableModelEvent
operator|.
name|INSERT
argument_list|)
expr_stmt|;
block|}
else|else
return|return;
block|}
else|else
name|event
operator|=
operator|new
name|TableModelEvent
argument_list|(
name|this
argument_list|,
name|row
argument_list|,
name|row
argument_list|,
name|col
argument_list|,
name|TableModelEvent
operator|.
name|UPDATE
argument_list|)
expr_stmt|;
name|values
index|[
name|row
index|]
index|[
name|col
index|]
operator|=
name|value
expr_stmt|;
name|functions
index|[
name|row
index|]
index|[
name|col
index|]
operator|=
name|functionId
expr_stmt|;
comment|//remove row if empty
if|if
condition|(
name|value
operator|==
literal|null
operator|&&
name|row
operator|<
name|values
operator|.
name|length
condition|)
block|{
name|boolean
name|empty
init|=
literal|true
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
name|attributes
operator|.
name|length
operator|&&
name|empty
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|values
index|[
name|row
index|]
index|[
name|i
index|]
operator|!=
literal|null
condition|)
name|empty
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|empty
condition|)
block|{
name|removeRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|fireTableChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addRow
parameter_list|()
block|{
name|URI
index|[]
index|[]
name|newF
init|=
operator|new
name|URI
index|[
name|functions
operator|.
name|length
operator|+
literal|1
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|functions
argument_list|,
literal|0
argument_list|,
name|newF
argument_list|,
literal|0
argument_list|,
name|functions
operator|.
name|length
argument_list|)
expr_stmt|;
name|newF
index|[
name|functions
operator|.
name|length
index|]
operator|=
operator|new
name|URI
index|[
name|attributes
operator|.
name|length
index|]
expr_stmt|;
name|functions
operator|=
name|newF
expr_stmt|;
name|AttributeValue
index|[]
index|[]
name|newV
init|=
operator|new
name|AttributeValue
index|[
name|values
operator|.
name|length
operator|+
literal|1
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|newV
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|newV
index|[
name|values
operator|.
name|length
index|]
operator|=
operator|new
name|AttributeValue
index|[
name|attributes
operator|.
name|length
index|]
expr_stmt|;
name|values
operator|=
name|newV
expr_stmt|;
block|}
specifier|public
name|void
name|removeRow
parameter_list|(
name|int
name|row
parameter_list|)
block|{
if|if
condition|(
name|functions
operator|.
name|length
operator|==
literal|0
operator|||
name|functions
operator|.
name|length
operator|>=
name|row
operator|||
name|row
operator|<
literal|0
condition|)
return|return;
name|int
name|row1
init|=
name|row
operator|+
literal|1
decl_stmt|;
name|URI
index|[]
index|[]
name|newF
init|=
operator|new
name|URI
index|[
name|functions
operator|.
name|length
operator|-
literal|1
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|functions
argument_list|,
literal|0
argument_list|,
name|newF
argument_list|,
literal|0
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|functions
argument_list|,
name|row1
argument_list|,
name|newF
argument_list|,
name|row1
argument_list|,
name|functions
operator|.
name|length
operator|-
name|row1
argument_list|)
expr_stmt|;
name|functions
operator|=
name|newF
expr_stmt|;
name|AttributeValue
index|[]
index|[]
name|newV
init|=
operator|new
name|AttributeValue
index|[
name|values
operator|.
name|length
operator|-
literal|1
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|newV
argument_list|,
literal|0
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
name|row1
argument_list|,
name|newV
argument_list|,
name|row1
argument_list|,
name|values
operator|.
name|length
operator|-
name|row1
argument_list|)
expr_stmt|;
name|values
operator|=
name|newV
expr_stmt|;
name|TableModelEvent
name|event
init|=
operator|new
name|TableModelEvent
argument_list|(
name|this
argument_list|,
name|row
argument_list|,
name|row
argument_list|,
name|TableModelEvent
operator|.
name|ALL_COLUMNS
argument_list|,
name|TableModelEvent
operator|.
name|DELETE
argument_list|)
decl_stmt|;
name|fireTableChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTarget
parameter_list|(
name|List
name|target
parameter_list|)
block|{
name|int
name|length
init|=
operator|(
name|target
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|target
operator|.
name|size
argument_list|()
decl_stmt|;
name|functions
operator|=
operator|new
name|URI
index|[
name|length
index|]
index|[
name|attributes
operator|.
name|length
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|AttributeValue
index|[
name|length
index|]
index|[
name|attributes
operator|.
name|length
index|]
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|List
name|matches
init|=
operator|(
name|List
operator|)
name|target
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|col
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|matches
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|col
operator|<
literal|0
condition|;
control|)
block|{
name|TargetMatch
name|match
init|=
operator|(
name|TargetMatch
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Evaluatable
name|attribute
init|=
name|match
operator|.
name|getMatchEvaluatable
argument_list|()
decl_stmt|;
if|if
condition|(
name|attribute
operator|instanceof
name|AttributeDesignator
condition|)
block|{
name|col
operator|=
name|getIndex
argument_list|(
operator|(
name|AttributeDesignator
operator|)
name|attribute
argument_list|)
expr_stmt|;
if|if
condition|(
name|col
operator|>=
literal|0
condition|)
name|setValue
argument_list|(
name|i
argument_list|,
name|col
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|fireTableChanged
argument_list|(
operator|new
name|TableModelEvent
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|fireTableChanged
parameter_list|(
name|TableModelEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|listeners
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|TableModelListener
name|listener
range|:
name|listeners
control|)
name|listener
operator|.
name|tableChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|TargetMatch
argument_list|>
argument_list|>
name|createTarget
parameter_list|()
block|{
name|List
argument_list|<
name|List
argument_list|<
name|TargetMatch
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|TargetMatch
argument_list|>
argument_list|>
argument_list|(
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|values
operator|.
name|length
condition|;
operator|++
name|row
control|)
block|{
name|List
argument_list|<
name|TargetMatch
argument_list|>
name|matches
init|=
operator|new
name|ArrayList
argument_list|<
name|TargetMatch
argument_list|>
argument_list|(
name|attributes
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|col
init|=
literal|0
init|;
name|col
operator|<
name|attributes
operator|.
name|length
condition|;
operator|++
name|col
control|)
block|{
name|AttributeValue
name|value
init|=
name|values
index|[
name|row
index|]
index|[
name|col
index|]
decl_stmt|;
name|URI
name|functionId
init|=
name|functions
index|[
name|row
index|]
index|[
name|col
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|functionId
operator|!=
literal|null
condition|)
block|{
name|Function
name|f
decl_stmt|;
try|try
block|{
name|f
operator|=
name|FunctionFactory
operator|.
name|getTargetInstance
argument_list|()
operator|.
name|createFunction
argument_list|(
name|functionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
name|matches
operator|.
name|add
argument_list|(
operator|new
name|TargetMatch
argument_list|(
name|type
argument_list|,
name|f
argument_list|,
name|attributes
index|[
name|col
index|]
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownIdentifierException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FunctionTypeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|matches
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|list
operator|.
name|add
argument_list|(
name|matches
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|list
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|int
name|row
parameter_list|,
name|int
name|col
parameter_list|,
name|TargetMatch
name|match
parameter_list|)
block|{
name|AttributeValue
name|value
init|=
name|match
operator|.
name|getMatchValue
argument_list|()
decl_stmt|;
name|URI
name|functionId
init|=
name|match
operator|.
name|getMatchFunction
argument_list|()
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|setValue
argument_list|(
name|functionId
argument_list|,
name|value
argument_list|,
name|row
argument_list|,
name|col
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getIndex
parameter_list|(
name|AttributeDesignator
name|attribute
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
name|attributes
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|equals
argument_list|(
name|attribute
argument_list|,
operator|(
name|attributes
index|[
name|i
index|]
operator|)
argument_list|)
condition|)
return|return
name|i
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|AttributeDesignator
name|one
parameter_list|,
name|AttributeDesignator
name|two
parameter_list|)
block|{
if|if
condition|(
name|one
operator|==
literal|null
condition|)
return|return
name|two
operator|==
literal|null
return|;
if|if
condition|(
name|two
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|one
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|two
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|one
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|two
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|equals
argument_list|(
name|one
operator|.
name|getCategory
argument_list|()
argument_list|,
name|two
operator|.
name|getCategory
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|equals
argument_list|(
name|one
operator|.
name|getIssuer
argument_list|()
argument_list|,
name|two
operator|.
name|getIssuer
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|URI
name|one
parameter_list|,
name|URI
name|two
parameter_list|)
block|{
if|if
condition|(
name|one
operator|==
literal|null
condition|)
return|return
name|two
operator|==
literal|null
return|;
if|if
condition|(
name|two
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|one
operator|.
name|equals
argument_list|(
name|two
argument_list|)
return|;
block|}
specifier|public
name|String
name|getColumnName
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|URI
name|attributeID
init|=
name|attributes
index|[
name|pos
index|]
operator|.
name|getId
argument_list|()
decl_stmt|;
return|return
name|abbrev
operator|.
name|getAbbreviatedId
argument_list|(
name|attributeID
argument_list|)
return|;
block|}
specifier|public
name|void
name|addTableModelListener
parameter_list|(
name|TableModelListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|listeners
operator|==
literal|null
condition|)
name|listeners
operator|=
operator|new
name|ArrayList
argument_list|<
name|TableModelListener
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeTableModelListener
parameter_list|(
name|TableModelListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listeners
operator|==
literal|null
operator|||
name|listener
operator|==
literal|null
condition|)
return|return;
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

