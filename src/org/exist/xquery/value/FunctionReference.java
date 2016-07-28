begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-12 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

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
name|java
operator|.
name|text
operator|.
name|Collator
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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|xquery
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
name|xquery
operator|.
name|Constants
operator|.
name|Comparison
import|;
end_import

begin_comment
comment|/**  * Represents a function item, i.e. a reference to a function that can be called dynamically.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunctionReference
extends|extends
name|AtomicValue
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FunctionReference
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|FunctionCall
name|functionCall
decl_stmt|;
specifier|public
name|FunctionReference
parameter_list|(
name|FunctionCall
name|fcall
parameter_list|)
block|{
name|this
operator|.
name|functionCall
operator|=
name|fcall
expr_stmt|;
block|}
specifier|public
name|FunctionCall
name|getCall
parameter_list|()
block|{
return|return
name|functionCall
return|;
block|}
comment|/**      * Get the signature of the function.      *       * @return signature of this function      */
specifier|public
name|FunctionSignature
name|getSignature
parameter_list|()
block|{
return|return
name|functionCall
operator|.
name|getSignature
argument_list|()
return|;
block|}
comment|/**      * Calls {@link FunctionCall#analyze(AnalyzeContextInfo)}.      *       * @param contextInfo      * @throws XPathException      */
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|functionCall
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|functionCall
operator|.
name|getContext
argument_list|()
operator|.
name|optimizationsEnabled
argument_list|()
condition|)
block|{
specifier|final
name|Optimizer
name|optimizer
init|=
operator|new
name|Optimizer
argument_list|(
name|functionCall
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
name|functionCall
operator|.
name|accept
argument_list|(
name|optimizer
argument_list|)
expr_stmt|;
if|if
condition|(
name|optimizer
operator|.
name|hasOptimized
argument_list|()
condition|)
block|{
name|functionCall
operator|.
name|resetState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|functionCall
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Calls {@link FunctionCall#eval(Sequence)}.      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|functionCall
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
return|;
block|}
comment|/**      * Calls {@link FunctionCall#evalFunction(Sequence, Item, Sequence[])}.      *       */
specifier|public
name|Sequence
name|evalFunction
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Sequence
index|[]
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|functionCall
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|seq
argument_list|)
return|;
block|}
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|functionCall
operator|.
name|setArguments
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContext
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|functionCall
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
comment|//        LOG.debug("Resetting state of function item " + functionCall.getSignature().toString());
name|functionCall
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#getType()      */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|FUNCTION_REFERENCE
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#getStringValue()      */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
literal|""
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#convertTo(int)      */
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|requiredType
operator|==
name|Type
operator|.
name|FUNCTION_REFERENCE
condition|)
block|{
return|return
name|this
return|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert function reference to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Called effectiveBooleanValue() on FunctionReference"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|Comparison
name|operator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot compare function reference to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#compareTo(java.text.Collator, org.exist.xquery.value.AtomicValue)      */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot compare function reference to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#max(java.text.Collator, org.exist.xquery.value.AtomicValue)      */
specifier|public
name|AtomicValue
name|max
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid argument to aggregate function: cannot compare function references"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#min(java.text.Collator, org.exist.xquery.value.AtomicValue)      */
specifier|public
name|AtomicValue
name|min
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid argument to aggregate function: cannot compare function references"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|AtomicValue
name|atomize
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOTY0013
argument_list|,
literal|"A function item other than an array cannot be atomized"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

