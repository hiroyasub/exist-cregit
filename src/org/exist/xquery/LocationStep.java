begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|dom
operator|.
name|XMLUtil
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
name|ElementValue
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Processes all location path steps (like descendant::*, ancestor::XXX).  *   * The results of the first evaluation of the expression  are cached for the   * lifetime of the object and only reloaded if the context sequence  * (as passed to the {@link #eval(Sequence, Item)} method) has changed.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|LocationStep
extends|extends
name|Step
block|{
specifier|protected
name|NodeSet
name|currentSet
init|=
literal|null
decl_stmt|;
specifier|protected
name|DocumentSet
name|currentDocs
init|=
literal|null
decl_stmt|;
comment|// Fields for caching the last result
specifier|protected
name|Sequence
name|cachedResult
init|=
literal|null
decl_stmt|;
specifier|protected
name|Sequence
name|cachedContext
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|timestamp
init|=
literal|0
decl_stmt|;
specifier|public
name|LocationStep
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|axis
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|axis
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LocationStep
parameter_list|(
name|XQueryContext
name|context
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
name|context
argument_list|,
name|axis
argument_list|,
name|test
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Step#returnsType() 	 */
comment|//	public int returnsType() {
comment|//		if(axis == Constants.SELF_AXIS)
comment|//			return Type.ITEM;
comment|//		else
comment|//			return Type.NODE;
comment|//	}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
name|int
name|deps
init|=
name|Dependency
operator|.
name|CONTEXT_SET
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
name|deps
operator||=
operator|(
operator|(
name|Predicate
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getDependencies
argument_list|()
expr_stmt|;
block|}
return|return
name|deps
return|;
block|}
specifier|protected
name|Sequence
name|applyPredicate
parameter_list|(
name|Sequence
name|outerSequence
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|contextSequence
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|Predicate
name|pred
decl_stmt|;
name|Sequence
name|result
init|=
name|contextSequence
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
name|pred
operator|.
name|evalPredicate
argument_list|(
name|outerSequence
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
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
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
if|if
condition|(
name|cachedResult
operator|!=
literal|null
operator|&&
name|cachedContext
operator|==
name|contextSequence
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|NodeSet
operator|)
name|contextSequence
operator|)
operator|.
name|hasChanged
argument_list|(
name|timestamp
argument_list|)
condition|)
block|{
comment|//				LOG.debug("returning cached result");
return|return
operator|(
name|predicates
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|cachedResult
else|:
name|applyPredicate
argument_list|(
name|contextSequence
argument_list|,
name|cachedResult
argument_list|)
return|;
block|}
block|}
name|Sequence
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
name|context
argument_list|,
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
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
name|context
argument_list|,
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
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
name|context
argument_list|,
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|SELF_AXIS
case|:
if|if
condition|(
name|inPredicate
condition|)
block|{
if|if
condition|(
name|contextSequence
operator|instanceof
name|VirtualNodeSet
condition|)
block|{
operator|(
operator|(
name|VirtualNodeSet
operator|)
name|contextSequence
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
name|contextSequence
operator|)
operator|.
name|setSelfIsContext
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
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
name|temp
operator|=
name|contextSequence
expr_stmt|;
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
name|context
argument_list|,
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|ATTRIBUTE_AXIS
case|:
comment|// combines /descendant-or-self::node()/attribute:*
case|case
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
case|:
name|temp
operator|=
name|getAttributes
argument_list|(
name|context
argument_list|,
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
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
name|context
argument_list|,
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|FOLLOWING_AXIS
case|:
comment|//temp = getFollowing(context, contextSequence.toNodeSet());
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The following axis is not yet supported"
argument_list|)
throw|;
case|case
name|Constants
operator|.
name|PRECEDING_AXIS
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The preceding axis is not yet supported"
argument_list|)
throw|;
default|default :
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported axis specified"
argument_list|)
throw|;
block|}
if|if
condition|(
name|contextSequence
operator|instanceof
name|NodeSet
condition|)
name|timestamp
operator|=
operator|(
operator|(
name|NodeSet
operator|)
name|contextSequence
operator|)
operator|.
name|getState
argument_list|()
expr_stmt|;
name|cachedResult
operator|=
name|temp
expr_stmt|;
name|cachedContext
operator|=
name|contextSequence
expr_stmt|;
return|return
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
name|contextSequence
argument_list|,
name|temp
argument_list|)
return|;
block|}
specifier|protected
name|NodeSet
name|getAttributes
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
block|{
name|NodeSet
name|result
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|isWildcardTest
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|VirtualNodeSet
argument_list|(
name|axis
argument_list|,
name|test
argument_list|,
name|contextSet
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
block|}
else|else
block|{
comment|//		    DocumentSet docs = contextSet.getDocumentSet();
name|DocumentSet
name|docs
init|=
name|getDocumentSet
argument_list|(
name|contextSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentSet
operator|==
literal|null
operator|||
name|currentDocs
operator|==
literal|null
operator|||
operator|!
operator|(
name|docs
operator|.
name|equals
argument_list|(
name|currentDocs
argument_list|)
operator|)
condition|)
block|{
name|currentDocs
operator|=
name|docs
expr_stmt|;
name|currentSet
operator|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getAttributesByName
argument_list|(
name|currentDocs
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
block|{
name|result
operator|=
name|currentSet
operator|.
name|selectAncestorDescendant
argument_list|(
name|contextSet
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|inPredicate
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|currentSet
operator|.
name|selectParentChild
argument_list|(
name|contextSet
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|inPredicate
argument_list|)
expr_stmt|;
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
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
block|{
if|if
condition|(
name|test
operator|.
name|isWildcardTest
argument_list|()
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
name|test
argument_list|,
name|contextSet
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
else|else
block|{
comment|//		    DocumentSet docs = contextSet.getDocumentSet();
name|DocumentSet
name|docs
init|=
name|getDocumentSet
argument_list|(
name|contextSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentSet
operator|==
literal|null
operator|||
name|currentDocs
operator|==
literal|null
operator|||
operator|!
operator|(
name|docs
operator|.
name|equals
argument_list|(
name|currentDocs
argument_list|)
operator|)
condition|)
block|{
name|currentDocs
operator|=
name|docs
expr_stmt|;
name|currentSet
operator|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|findElementsByTagName
argument_list|(
name|ElementValue
operator|.
name|ELEMENT
argument_list|,
name|currentDocs
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|NodeSet
name|result
init|=
name|currentSet
operator|.
name|selectParentChild
argument_list|(
name|contextSet
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|inPredicate
argument_list|)
decl_stmt|;
comment|//			DocumentSet docs = contextSet.getDocumentSet();
comment|//			NodeSelector selector = new ChildSelector(contextSet, inPredicate);
comment|//			NodeSet result = context.getBroker().findElementsByTagName(
comment|//					ElementValue.ELEMENT, docs, test.getName(), selector
comment|//			);
return|return
name|result
return|;
block|}
block|}
specifier|protected
name|NodeSet
name|getDescendants
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
block|{
if|if
condition|(
name|test
operator|.
name|isWildcardTest
argument_list|()
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
name|test
argument_list|,
name|contextSet
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
else|else
block|{
name|DocumentSet
name|docs
init|=
name|getDocumentSet
argument_list|(
name|contextSet
argument_list|)
decl_stmt|;
comment|//		    DocumentSet docs = contextSet.getDocumentSet();
if|if
condition|(
name|currentSet
operator|==
literal|null
operator|||
name|currentDocs
operator|==
literal|null
operator|||
operator|!
operator|(
name|docs
operator|.
name|equals
argument_list|(
name|currentDocs
argument_list|)
operator|)
condition|)
block|{
name|currentDocs
operator|=
name|docs
expr_stmt|;
name|currentSet
operator|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|findElementsByTagName
argument_list|(
name|ElementValue
operator|.
name|ELEMENT
argument_list|,
name|currentDocs
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|NodeSet
name|result
init|=
name|currentSet
operator|.
name|selectAncestorDescendant
argument_list|(
name|contextSet
argument_list|,
name|NodeSet
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
decl_stmt|;
comment|//			DocumentSet docs = contextSet.getDocumentSet();
comment|//			NodeSelector selector = axis == Constants.DESCENDANT_SELF_AXIS ?
comment|//			        new DescendantOrSelfSelector(contextSet, inPredicate) :
comment|//			    new DescendantSelector(contextSet, inPredicate);
comment|//			NodeSet result = context.getBroker().findElementsByTagName(
comment|//					ElementValue.ELEMENT, docs, test.getName(), selector
comment|//			);
return|return
name|result
return|;
block|}
block|}
specifier|protected
name|NodeSet
name|getSiblings
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
block|{
name|NodeSet
name|result
decl_stmt|;
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
condition|)
block|{
name|DocumentSet
name|docs
init|=
name|getDocumentSet
argument_list|(
name|contextSet
argument_list|)
decl_stmt|;
comment|//			DocumentSet docs = contextSet.getDocumentSet();
if|if
condition|(
name|currentSet
operator|==
literal|null
operator|||
name|currentDocs
operator|==
literal|null
operator|||
operator|!
operator|(
name|docs
operator|.
name|equals
argument_list|(
name|currentDocs
argument_list|)
operator|)
condition|)
block|{
name|currentDocs
operator|=
name|docs
expr_stmt|;
name|currentSet
operator|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|findElementsByTagName
argument_list|(
name|ElementValue
operator|.
name|ELEMENT
argument_list|,
name|currentDocs
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|contextSet
operator|.
name|selectSiblings
argument_list|(
name|currentSet
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
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ArraySet
argument_list|(
name|contextSet
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
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
name|contextSet
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
name|test
operator|.
name|matches
argument_list|(
name|n
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
block|}
return|return
name|result
return|;
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
name|getFollowing
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodeSet
name|result
init|=
name|NodeSet
operator|.
name|EMPTY_SET
decl_stmt|;
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
condition|)
block|{
name|DocumentSet
name|docs
init|=
name|getDocumentSet
argument_list|(
name|contextSet
argument_list|)
decl_stmt|;
comment|//			DocumentSet docs = contextSet.getDocumentSet();
if|if
condition|(
name|currentSet
operator|==
literal|null
operator|||
name|currentDocs
operator|==
literal|null
operator|||
operator|!
operator|(
name|docs
operator|.
name|equals
argument_list|(
name|currentDocs
argument_list|)
operator|)
condition|)
block|{
name|currentDocs
operator|=
name|docs
expr_stmt|;
name|currentSet
operator|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|findElementsByTagName
argument_list|(
name|ElementValue
operator|.
name|ELEMENT
argument_list|,
name|currentDocs
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|contextSet
operator|.
name|selectFollowing
argument_list|(
name|currentSet
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|NodeSet
name|getAncestors
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
block|{
name|NodeSet
name|result
decl_stmt|;
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
condition|)
block|{
comment|//			DocumentSet docs = contextSet.getDocumentSet();
name|DocumentSet
name|docs
init|=
name|getDocumentSet
argument_list|(
name|contextSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentSet
operator|==
literal|null
operator|||
name|currentDocs
operator|==
literal|null
operator|||
operator|!
operator|(
name|docs
operator|.
name|equals
argument_list|(
name|currentDocs
argument_list|)
operator|)
condition|)
block|{
name|currentDocs
operator|=
name|docs
expr_stmt|;
name|currentSet
operator|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|findElementsByTagName
argument_list|(
name|ElementValue
operator|.
name|ELEMENT
argument_list|,
name|currentDocs
argument_list|,
name|test
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|contextSet
operator|.
name|selectAncestors
argument_list|(
name|currentSet
argument_list|,
name|axis
operator|==
name|Constants
operator|.
name|ANCESTOR_SELF_AXIS
argument_list|,
name|inPredicate
argument_list|)
expr_stmt|;
comment|//LOG.debug("getAncestors found " + r.getLength());
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|contextSet
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
name|test
operator|.
name|matches
argument_list|(
name|p
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
name|getInternalAddress
argument_list|()
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
name|p
operator|.
name|nodeType
operator|=
name|Node
operator|.
name|ELEMENT_NODE
expr_stmt|;
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|p
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
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|NodeSet
name|getParents
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
block|{
return|return
name|contextSet
operator|.
name|getParents
argument_list|()
return|;
block|}
specifier|protected
name|DocumentSet
name|getDocumentSet
parameter_list|(
name|NodeSet
name|contextSet
parameter_list|)
block|{
name|DocumentSet
name|ds
init|=
name|getContextDocSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|ds
operator|==
literal|null
condition|)
name|ds
operator|=
name|contextSet
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
return|return
name|ds
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Step#resetState() 	 */
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
name|currentSet
operator|=
literal|null
expr_stmt|;
name|currentDocs
operator|=
literal|null
expr_stmt|;
name|cachedContext
operator|=
literal|null
expr_stmt|;
name|cachedResult
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

