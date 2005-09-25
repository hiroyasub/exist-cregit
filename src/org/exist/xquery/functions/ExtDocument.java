begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
package|;
end_package

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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
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
name|ExtArrayNodeSet
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
name|NodeProxy
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
name|security
operator|.
name|Permission
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
name|PermissionDeniedException
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
name|LockException
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
name|Cardinality
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
name|Dependency
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
name|Function
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
name|FunctionSignature
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
name|Module
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
name|XQueryContext
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
name|Type
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

begin_comment
comment|/**  * Implements eXist's document() function.  *   * This will be replaced by XQuery's fn:doc() function.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ExtDocument
extends|extends
name|Function
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"document"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Includes one or more documents "
operator|+
literal|"into the input sequence. This function is specific to eXist and "
operator|+
literal|"will be replaced with the corresponding fn:doc function. Currently, "
operator|+
literal|"eXist interprets each argument as an absolute path pointing to a "
operator|+
literal|"document in the database, as for example, '/db/shakespeare/plays/hamlet.xml'. "
operator|+
literal|"If no arguments are specified, the function will load all documents in the "
operator|+
literal|"database."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
name|List
name|cachedArgs
init|=
literal|null
decl_stmt|;
specifier|private
name|Sequence
name|cached
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|ExtDocument
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|DocumentSet
name|docs
init|=
literal|null
decl_stmt|;
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
comment|// check if the loaded documents should remain locked
name|boolean
name|lockOnLoad
init|=
name|context
operator|.
name|lockDocumentsOnLoad
argument_list|()
decl_stmt|;
name|boolean
name|cacheIsValid
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|cached
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|cached
expr_stmt|;
name|docs
operator|=
name|cached
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|docs
operator|=
operator|new
name|DocumentSet
argument_list|()
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getAllDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|List
name|args
init|=
name|getParameterValues
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedArgs
operator|!=
literal|null
condition|)
name|cacheIsValid
operator|=
name|compareArguments
argument_list|(
name|cachedArgs
argument_list|,
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|cacheIsValid
condition|)
block|{
name|result
operator|=
name|cached
expr_stmt|;
name|docs
operator|=
name|cached
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|docs
operator|=
operator|new
name|DocumentSet
argument_list|()
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
name|args
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|next
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid argument to fn:doc function: empty string is not allowed here."
argument_list|)
throw|;
if|if
condition|(
name|next
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'/'
condition|)
name|next
operator|=
name|context
operator|.
name|getBaseURI
argument_list|()
operator|+
literal|'/'
operator|+
name|next
expr_stmt|;
try|try
block|{
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getDocument
argument_list|(
name|next
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|doc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|context
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Insufficient privileges to read resource "
operator|+
name|next
argument_list|)
throw|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Permission denied: unable to load document "
operator|+
name|next
argument_list|)
throw|;
block|}
block|}
name|cachedArgs
operator|=
name|args
expr_stmt|;
block|}
block|}
try|try
block|{
if|if
condition|(
operator|!
name|cacheIsValid
condition|)
comment|// wait for pending updates
name|docs
operator|.
name|lock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// wait for pending updates
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|(
name|docs
operator|.
name|getLength
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|//, -1, Node.DOCUMENT_NODE));
if|if
condition|(
name|lockOnLoad
condition|)
block|{
name|context
operator|.
name|getLockedDocuments
argument_list|()
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Could not acquire lock on document set."
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
operator|(
name|cacheIsValid
operator|||
name|lockOnLoad
operator|)
condition|)
comment|// release all locks
name|docs
operator|.
name|unlock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|cached
operator|=
name|result
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|List
name|getParameterValues
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|List
name|args
init|=
operator|new
name|ArrayList
argument_list|(
name|getArgumentCount
argument_list|()
operator|+
literal|10
argument_list|)
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
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Sequence
name|seq
init|=
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|j
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|next
init|=
name|j
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|args
return|;
block|}
specifier|private
name|boolean
name|compareArguments
parameter_list|(
name|List
name|args1
parameter_list|,
name|List
name|args2
parameter_list|)
block|{
if|if
condition|(
name|args1
operator|.
name|size
argument_list|()
operator|!=
name|args2
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args1
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|arg1
init|=
operator|(
name|String
operator|)
name|args1
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|arg2
init|=
operator|(
name|String
operator|)
name|args2
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|arg1
operator|.
name|equals
argument_list|(
name|arg2
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#resetState()      */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|super
operator|.
name|resetState
argument_list|()
expr_stmt|;
name|cached
operator|=
literal|null
expr_stmt|;
name|cachedArgs
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

