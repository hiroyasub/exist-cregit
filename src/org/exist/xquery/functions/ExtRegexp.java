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
name|QName
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
name|Constants
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
name|Expression
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ExtRegexp
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
literal|"match-all"
argument_list|,
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"eXist-specific extension function. Tries to match each of the regular expression "
operator|+
literal|"strings passed in $b and all following parameters against the keywords contained in "
operator|+
literal|"the fulltext index. The keywords found are then compared to the node set in $a. Every "
operator|+
literal|"node containing all of the keywords is copied to the result sequence."
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|,
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
specifier|protected
name|int
name|type
init|=
name|Constants
operator|.
name|FULLTEXT_AND
decl_stmt|;
specifier|public
name|ExtRegexp
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
comment|/** 	 * @param type 	 */
specifier|public
name|ExtRegexp
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|ExtRegexp
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|type
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.functions.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
name|int
name|deps
init|=
literal|0
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
name|deps
operator|=
name|deps
operator||
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|getDependencies
argument_list|()
expr_stmt|;
return|return
name|deps
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
name|getArgumentCount
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"function requires at least two arguments"
argument_list|)
throw|;
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
name|Expression
name|path
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
name|Dependency
operator|.
name|NO_DEPENDENCY
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"single execution"
argument_list|)
expr_stmt|;
name|NodeSet
name|nodes
init|=
name|path
operator|==
literal|null
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
name|path
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|List
name|terms
init|=
name|getSearchTerms
argument_list|(
name|context
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
return|return
name|evalQuery
argument_list|(
name|context
argument_list|,
name|nodes
argument_list|,
name|terms
argument_list|)
return|;
block|}
else|else
block|{
name|Item
name|current
decl_stmt|;
name|String
name|arg
decl_stmt|;
name|NodeSet
name|nodes
decl_stmt|;
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|Sequence
name|temp
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|contextSequence
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|current
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|List
name|terms
init|=
name|getSearchTerms
argument_list|(
name|context
argument_list|,
name|current
operator|.
name|toSequence
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|nodes
operator|=
name|path
operator|==
literal|null
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
name|path
operator|.
name|eval
argument_list|(
name|current
operator|.
name|toSequence
argument_list|()
argument_list|)
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
name|temp
operator|=
name|evalQuery
argument_list|(
name|context
argument_list|,
name|nodes
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|temp
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|temp
operator|.
name|getLength
argument_list|()
operator|+
literal|" in "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.functions.ExtFulltext#evalQuery(org.exist.xpath.StaticContext, org.exist.dom.DocumentSet, java.lang.String, org.exist.dom.NodeSet) 	 */
specifier|public
name|Sequence
name|evalQuery
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|nodes
parameter_list|,
name|List
name|terms
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|terms
operator|==
literal|null
operator|||
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
comment|// no search terms
name|NodeSet
name|hits
index|[]
init|=
operator|new
name|NodeSet
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|terms
operator|.
name|size
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|hits
index|[
name|k
index|]
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getTextEngine
argument_list|()
operator|.
name|getNodesContaining
argument_list|(
name|nodes
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|nodes
argument_list|,
operator|(
name|String
operator|)
name|terms
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|,
name|DBBroker
operator|.
name|MATCH_REGEXP
argument_list|)
expr_stmt|;
block|}
name|NodeSet
name|result
init|=
name|hits
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|1
init|;
name|k
operator|<
name|hits
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|hits
index|[
name|k
index|]
operator|!=
literal|null
condition|)
name|result
operator|=
operator|(
name|type
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
condition|?
name|result
operator|.
name|deepIntersection
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
else|:
name|result
operator|.
name|union
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
else|else
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
block|}
specifier|protected
name|List
name|getSearchTerms
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"function requires at least 2 arguments"
argument_list|)
throw|;
name|List
name|terms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Expression
name|next
decl_stmt|;
name|Sequence
name|seq
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|=
name|getArgument
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|seq
operator|=
name|next
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|seq
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
name|terms
operator|.
name|add
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
block|{
for|for
control|(
name|SequenceIterator
name|it
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|it
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|terms
return|;
block|}
block|}
end_class

end_unit

