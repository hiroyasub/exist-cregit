begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|apache
operator|.
name|log4j
operator|.
name|Category
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|ArraySet
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
name|NodeImpl
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
name|NodeSet
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
name|VirtualNodeSet
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
name|BrokerPool
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
name|util
operator|.
name|XMLUtil
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
name|NamedNodeMap
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

begin_class
specifier|public
class|class
name|LocationStep
extends|extends
name|Step
block|{
specifier|protected
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|LocationStep
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|NodeSet
name|buf
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|keepVirtual
init|=
literal|false
decl_stmt|;
specifier|public
name|LocationStep
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|int
name|axis
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|axis
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LocationStep
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|int
name|axis
parameter_list|,
name|NodeTest
name|test
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|axis
argument_list|,
name|test
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|NodeSet
name|applyPredicate
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
name|Predicate
name|pred
decl_stmt|;
name|NodeSet
name|result
init|=
name|context
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|predicates
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
name|pred
operator|=
operator|(
name|Predicate
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|result
operator|=
operator|(
name|NodeSet
operator|)
name|pred
operator|.
name|eval
argument_list|(
name|documents
argument_list|,
name|result
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeList
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Value
name|eval
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
name|NodeSet
name|temp
decl_stmt|;
switch|switch
condition|(
name|axis
condition|)
block|{
case|case
name|Constants
operator|.
name|DESCENDANT_AXIS
case|:
case|case
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
case|:
name|temp
operator|=
name|getDescendants
argument_list|(
name|documents
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|CHILD_AXIS
case|:
name|temp
operator|=
name|getChildren
argument_list|(
name|documents
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|ANCESTOR_AXIS
case|:
case|case
name|Constants
operator|.
name|ANCESTOR_SELF_AXIS
case|:
name|temp
operator|=
name|getAncestors
argument_list|(
name|documents
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|SELF_AXIS
case|:
name|temp
operator|=
name|context
expr_stmt|;
if|if
condition|(
name|inPredicate
condition|)
block|{
if|if
condition|(
name|context
operator|instanceof
name|VirtualNodeSet
condition|)
block|{
operator|(
operator|(
name|VirtualNodeSet
operator|)
name|context
operator|)
operator|.
name|setInPredicate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|VirtualNodeSet
operator|)
name|context
operator|)
operator|.
name|setSelfIsContext
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|temp
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|p
operator|.
name|addContextNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
break|break;
case|case
name|Constants
operator|.
name|PARENT_AXIS
case|:
name|temp
operator|=
name|getParents
argument_list|(
name|documents
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|ATTRIBUTE_AXIS
case|:
name|temp
operator|=
name|getAttributes
argument_list|(
name|documents
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|PRECEDING_SIBLING_AXIS
case|:
case|case
name|Constants
operator|.
name|FOLLOWING_SIBLING_AXIS
case|:
name|temp
operator|=
name|getSiblings
argument_list|(
name|documents
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
default|default :
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported axis specified"
argument_list|)
throw|;
block|}
name|temp
operator|=
operator|(
name|predicates
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|temp
else|:
name|applyPredicate
argument_list|(
name|documents
argument_list|,
name|temp
argument_list|)
expr_stmt|;
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|temp
argument_list|)
return|;
block|}
specifier|protected
name|NodeSet
name|getAttributes
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
name|NodeSet
name|result
decl_stmt|;
switch|switch
condition|(
name|test
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|NodeTest
operator|.
name|TYPE_TEST
case|:
name|result
operator|=
operator|new
name|VirtualNodeSet
argument_list|(
name|axis
argument_list|,
operator|(
name|TypeTest
operator|)
name|test
argument_list|,
name|context
argument_list|)
expr_stmt|;
operator|(
operator|(
name|VirtualNodeSet
operator|)
name|result
operator|)
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
break|break;
case|case
name|NodeTest
operator|.
name|NAME_TEST
case|:
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|(
name|NodeSet
operator|)
name|broker
operator|.
name|getAttributesByName
argument_list|(
name|documents
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"exception while retrieving elements"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|=
operator|(
operator|(
name|ArraySet
operator|)
name|buf
operator|)
operator|.
name|getChildren
argument_list|(
name|context
argument_list|,
name|ArraySet
operator|.
name|DESCENDANT
argument_list|,
name|inPredicate
argument_list|)
expr_stmt|;
break|break;
default|default :
name|Node
name|n
decl_stmt|;
name|Node
name|attr
decl_stmt|;
name|NamedNodeMap
name|map
decl_stmt|;
name|result
operator|=
operator|new
name|ArraySet
argument_list|(
name|context
operator|.
name|getLength
argument_list|()
argument_list|)
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
name|context
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|=
name|context
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|map
operator|=
operator|(
operator|(
name|Element
operator|)
name|n
operator|)
operator|.
name|getAttributes
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|map
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|attr
operator|=
name|map
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|NodeSet
name|getChildren
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
if|if
condition|(
name|test
operator|.
name|getType
argument_list|()
operator|==
name|NodeTest
operator|.
name|TYPE_TEST
condition|)
block|{
comment|// test is one out of *, text(), node()
name|VirtualNodeSet
name|vset
init|=
operator|new
name|VirtualNodeSet
argument_list|(
name|axis
argument_list|,
operator|(
name|TypeTest
operator|)
name|test
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|vset
operator|.
name|setInPredicate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|vset
return|;
block|}
else|else
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
name|buf
operator|=
operator|(
name|NodeSet
operator|)
name|broker
operator|.
name|findElementsByTagName
argument_list|(
name|documents
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|getChildren
argument_list|(
name|context
argument_list|,
name|ArraySet
operator|.
name|DESCENDANT
argument_list|,
name|inPredicate
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|NodeSet
name|getDescendants
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
if|if
condition|(
name|test
operator|.
name|getType
argument_list|()
operator|==
name|NodeTest
operator|.
name|NAME_TEST
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
name|buf
operator|=
operator|(
name|NodeSet
operator|)
name|broker
operator|.
name|findElementsByTagName
argument_list|(
name|documents
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|getDescendants
argument_list|(
name|context
argument_list|,
name|ArraySet
operator|.
name|DESCENDANT
argument_list|,
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
argument_list|,
name|inPredicate
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|VirtualNodeSet
name|vset
init|=
operator|new
name|VirtualNodeSet
argument_list|(
name|axis
argument_list|,
operator|(
name|TypeTest
operator|)
name|test
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|vset
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
return|return
name|vset
return|;
block|}
block|}
specifier|protected
name|NodeSet
name|getSiblings
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
if|if
condition|(
name|test
operator|.
name|getType
argument_list|()
operator|==
name|NodeTest
operator|.
name|NAME_TEST
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
name|buf
operator|=
operator|(
name|NodeSet
operator|)
name|broker
operator|.
name|findElementsByTagName
argument_list|(
name|documents
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|getSiblings
argument_list|(
name|buf
argument_list|,
name|axis
operator|==
name|Constants
operator|.
name|PRECEDING_SIBLING_AXIS
condition|?
name|NodeSet
operator|.
name|PRECEDING
else|:
name|NodeSet
operator|.
name|FOLLOWING
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|context
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|NodeImpl
name|n
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|context
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|n
operator|=
operator|(
name|NodeImpl
operator|)
name|p
operator|.
name|getNode
argument_list|()
expr_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|getNextSibling
argument_list|(
name|n
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|TypeTest
operator|)
name|test
operator|)
operator|.
name|isOfType
argument_list|(
name|n
operator|.
name|getNodeType
argument_list|()
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|n
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|n
operator|.
name|getGID
argument_list|()
argument_list|,
name|n
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
specifier|protected
name|NodeImpl
name|getNextSibling
parameter_list|(
name|NodeImpl
name|last
parameter_list|)
block|{
switch|switch
condition|(
name|axis
condition|)
block|{
case|case
name|Constants
operator|.
name|FOLLOWING_SIBLING_AXIS
case|:
return|return
operator|(
name|NodeImpl
operator|)
name|last
operator|.
name|getNextSibling
argument_list|()
return|;
default|default :
return|return
operator|(
name|NodeImpl
operator|)
name|last
operator|.
name|getPreviousSibling
argument_list|()
return|;
block|}
block|}
specifier|protected
name|NodeSet
name|getAncestors
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
if|if
condition|(
name|test
operator|.
name|getType
argument_list|()
operator|==
name|NodeTest
operator|.
name|NAME_TEST
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
name|buf
operator|=
operator|(
name|NodeSet
operator|)
name|broker
operator|.
name|findElementsByTagName
argument_list|(
name|documents
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|NodeSet
name|r
init|=
name|context
operator|.
name|getAncestors
argument_list|(
operator|(
name|ArraySet
operator|)
name|buf
argument_list|,
name|axis
operator|==
name|Constants
operator|.
name|ANCESTOR_SELF_AXIS
argument_list|,
name|inPredicate
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"getAncestors found "
operator|+
name|r
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|NodeSet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|context
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|context
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|ANCESTOR_SELF_AXIS
operator|&&
operator|(
operator|(
name|TypeTest
operator|)
name|test
operator|)
operator|.
name|isOfType
argument_list|(
name|p
argument_list|,
name|p
operator|.
name|nodeType
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|,
name|p
operator|.
name|internalAddress
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|p
operator|.
name|gid
operator|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|TypeTest
operator|)
name|test
operator|)
operator|.
name|isOfType
argument_list|(
name|p
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
specifier|protected
name|NodeSet
name|getParents
parameter_list|(
name|DocumentSet
name|documents
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getParents
argument_list|()
return|;
block|}
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|inDocs
parameter_list|)
block|{
return|return
name|super
operator|.
name|preselect
argument_list|(
name|inDocs
argument_list|)
return|;
block|}
specifier|public
name|void
name|setKeepVirtual
parameter_list|(
name|boolean
name|virtual
parameter_list|)
block|{
name|keepVirtual
operator|=
name|virtual
expr_stmt|;
block|}
block|}
end_class

end_unit

