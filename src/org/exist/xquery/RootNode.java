begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04 The eXist Team  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111307, USA.  *   *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|*
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
name|util
operator|.
name|ExpressionDumper
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
comment|/**  * Reads a set of document root nodes from the context. Used for  * absolute path expression that do not start with fn:doc() or fn:collection().  *   * @author Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
end_comment

begin_class
specifier|public
class|class
name|RootNode
extends|extends
name|Step
block|{
specifier|private
name|NodeSet
name|cached
init|=
literal|null
decl_stmt|;
specifier|private
name|DocumentSet
name|cachedDocs
init|=
literal|null
decl_stmt|;
specifier|private
name|UpdateListener
name|listener
init|=
literal|null
decl_stmt|;
comment|/** Constructor for the RootNode object */
specifier|public
name|RootNode
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|SELF_AXIS
argument_list|)
expr_stmt|;
block|}
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
comment|// get statically known documents from the context
name|DocumentSet
name|ds
init|=
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
decl_stmt|;
if|if
condition|(
name|ds
operator|==
literal|null
operator|||
name|ds
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
comment|// if the expression occurs in a nested context, we might have cached the
comment|// document set
if|if
condition|(
name|cachedDocs
operator|!=
literal|null
operator|&&
name|cachedDocs
operator|.
name|equals
argument_list|(
name|ds
argument_list|)
condition|)
return|return
name|cached
return|;
comment|// check if the loaded documents should remain locked
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
comment|// wait for pending updates
name|ds
operator|.
name|lock
argument_list|(
literal|false
argument_list|,
literal|true
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
name|ds
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
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
block|{
comment|// skip binary resources
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
block|}
block|}
name|cached
operator|=
name|result
expr_stmt|;
name|cachedDocs
operator|=
name|ds
expr_stmt|;
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
literal|"Failed to acquire lock on the context document set"
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// release all locks
name|ds
operator|.
name|unlock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|registerUpdateListener
argument_list|()
expr_stmt|;
comment|//actualReturnType = result.getItemType();
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Step#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
comment|//TODO : find a better message
name|dumper
operator|.
name|display
argument_list|(
literal|"[root-node]"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|//TODO : find a better message
return|return
literal|"[root-node]"
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.Step#returnsType()      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
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
name|cachedDocs
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
name|cachedDocs
operator|!=
literal|null
operator|&&
name|cachedDocs
operator|.
name|contains
argument_list|(
name|document
operator|.
name|getDocId
argument_list|()
argument_list|)
condition|)
block|{
name|cachedDocs
operator|=
literal|null
expr_stmt|;
name|cached
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
name|RootNode
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
name|RootNode
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
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.Step#resetState()      */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|cached
operator|=
literal|null
expr_stmt|;
name|cachedDocs
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

