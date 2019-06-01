begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|collections
operator|.
name|Collection
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
name|DocumentSet
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
name|NodeHandle
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
name|NodeSet
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
name|value
operator|.
name|AtomicValue
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|MemoryNodeSet
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
name|SequenceIterator
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DeferredFunctionCall
implements|implements
name|Sequence
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
name|DeferredFunctionCall
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|FunctionSignature
name|signature
decl_stmt|;
specifier|private
name|Sequence
name|sequence
init|=
literal|null
decl_stmt|;
specifier|private
name|XPathException
name|caughtException
init|=
literal|null
decl_stmt|;
specifier|protected
name|DeferredFunctionCall
parameter_list|(
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|this
operator|.
name|signature
operator|=
name|signature
expr_stmt|;
block|}
specifier|private
name|void
name|realize
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|caughtException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|caughtException
throw|;
block|}
if|if
condition|(
name|sequence
operator|==
literal|null
condition|)
block|{
name|sequence
operator|=
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|FunctionSignature
name|getSignature
parameter_list|()
block|{
return|return
name|signature
return|;
block|}
specifier|protected
specifier|abstract
name|Sequence
name|execute
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
name|sequence
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
name|sequence
operator|.
name|addAll
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clearContext
parameter_list|(
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
name|sequence
operator|.
name|clearContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
return|return
name|sequence
operator|.
name|conversionPreference
argument_list|(
name|javaClass
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
block|}
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
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|effectiveBooleanValue
argument_list|()
return|;
block|}
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|getCardinality
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|getDocumentSet
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|getCollectionIterator
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|getCollectionIterator
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|getItemType
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Type
operator|.
name|ANY_TYPE
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getItemCountLong
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|getItemCountLong
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|getStringValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|tail
parameter_list|()
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|tail
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|hasMany
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|hasMany
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|boolean
name|hasOne
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|hasOne
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|boolean
name|isCached
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|isCached
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|isEmpty
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|isPersistentSet
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|iterate
argument_list|()
return|;
block|}
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
name|sequence
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setIsCached
parameter_list|(
name|boolean
name|cached
parameter_list|)
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
name|sequence
operator|.
name|setIsCached
argument_list|(
name|cached
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setSelfAsContext
parameter_list|(
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|realize
argument_list|()
expr_stmt|;
name|sequence
operator|.
name|setSelfAsContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in deferred function: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|toJavaObject
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|target
parameter_list|)
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|toJavaObject
argument_list|(
name|target
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|toNodeSet
argument_list|()
return|;
block|}
specifier|public
name|MemoryNodeSet
name|toMemNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|toMemNodeSet
argument_list|()
return|;
block|}
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//try {
name|realize
argument_list|()
expr_stmt|;
return|return
name|sequence
operator|.
name|unorderedIterator
argument_list|()
return|;
comment|/*} catch (XPathException e) {             LOG.error("Exception in deferred function: " + e.getMessage());             return null;         }*/
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|NodeHandle
name|newNode
parameter_list|)
block|{
comment|// not applicable
block|}
specifier|public
name|int
name|getState
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|int
name|previousState
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
block|{
comment|// nothing to do
block|}
block|}
end_class

end_unit
