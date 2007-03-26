begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|QName
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
name|StoredNode
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
name|DBBroker
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
name|UpdateListener
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
name|Profiler
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
name|util
operator|.
name|DocUtils
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
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_comment
comment|/**  * Implements the built-in fn:doc() function.  *   * This will be replaced by XQuery's fn:doc() function.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunDoc
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
literal|"doc"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the documents specified in the input sequence. "
operator|+
literal|"The arguments are either document paths like '"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/shakespeare/plays/hamlet.xml' or "
operator|+
literal|"XMLDB URIs like 'xmldb:exist://localhost:8081/"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/shakespeare/plays/hamlet.xml' or "
operator|+
literal|"standard URLs starting with http://, file://, etc."
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
name|ZERO_OR_ONE
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
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|Sequence
name|cached
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|cachedPath
init|=
literal|null
decl_stmt|;
specifier|private
name|UpdateListener
name|listener
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|FunDoc
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
comment|/** 	 * @see org.exist.xquery.Function#getDependencies() 	 */
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
comment|/** 	 * @see org.exist.xquery.Expression#eval(Sequence, Item) 	 */
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
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|result
decl_stmt|;
name|Sequence
name|arg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
name|String
name|path
init|=
name|arg
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|// check if we can return a cached sequence
if|if
condition|(
name|cached
operator|!=
literal|null
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|cachedPath
argument_list|)
condition|)
block|{
return|return
name|cached
return|;
block|}
try|try
block|{
name|result
operator|=
name|DocUtils
operator|.
name|getDocument
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|//			TODO: we still need a final decision about this. Also check base-uri.
comment|//			if (result == Sequence.EMPTY_SEQUENCE)
comment|//				throw new XPathException(getASTNode(), path + " is not an XML document");
name|DocumentSet
name|docs
init|=
name|result
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
operator|&&
name|DocumentSet
operator|.
name|EMPTY_DOCUMENT_SET
operator|!=
name|docs
condition|)
block|{
comment|// only cache node sets (which have a non-empty document set)
name|cachedPath
operator|=
name|path
expr_stmt|;
name|cached
operator|=
name|result
expr_stmt|;
name|registerUpdateListener
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|void
name|registerUpdateListener
parameter_list|()
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
name|listener
operator|=
operator|new
name|UpdateListener
argument_list|()
block|{
specifier|public
name|void
name|documentUpdated
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|event
parameter_list|)
block|{
if|if
condition|(
name|document
operator|==
literal|null
operator|||
name|event
operator|==
name|UpdateListener
operator|.
name|ADD
operator|||
name|event
operator|==
name|UpdateListener
operator|.
name|REMOVE
condition|)
block|{
comment|// clear all
name|cachedPath
operator|=
literal|null
expr_stmt|;
name|cached
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|cachedPath
operator|!=
literal|null
operator|&&
operator|(
name|document
operator|==
literal|null
operator|||
name|cachedPath
operator|.
name|equals
argument_list|(
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|cached
operator|=
literal|null
expr_stmt|;
name|cachedPath
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|unsubscribe
parameter_list|()
block|{
name|FunDoc
operator|.
name|this
operator|.
name|listener
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|StoredNode
name|newNode
parameter_list|)
block|{
comment|// not relevant
block|}
specifier|public
name|void
name|debug
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"UpdateListener: Line: "
operator|+
name|getASTNode
argument_list|()
operator|.
name|getLine
argument_list|()
operator|+
literal|": "
operator|+
name|FunDoc
operator|.
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|context
operator|.
name|registerUpdateListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * @see org.exist.xquery.PathExpr#resetState() 	 */
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
name|cachedPath
operator|=
literal|null
expr_stmt|;
name|listener
operator|=
literal|null
expr_stmt|;
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

