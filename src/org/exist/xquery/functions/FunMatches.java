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
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|MalformedPatternException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|Perl5Compiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|Perl5Matcher
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
name|BooleanValue
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

begin_comment
comment|/**  * Implements the fn:matches() function.  *   * Based on the jakarta ORO package for regular expression support.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FunMatches
extends|extends
name|Function
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"matches"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns true if the first argument string matches the regular expression specified "
operator|+
literal|"by the second argument. This function is optimized internally if a range index of type xs:string "
operator|+
literal|"is defined on the nodes passed to the first argument."
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
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"matches"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns true if the first argument string matches the regular expression specified "
operator|+
literal|"by the second argument. This function is optimized internally if a range index of type xs:string "
operator|+
literal|"is defined on the nodes passed to the first argument."
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
name|EXACTLY_ONE
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
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|protected
name|Perl5Compiler
name|compiler
init|=
operator|new
name|Perl5Compiler
argument_list|()
decl_stmt|;
specifier|protected
name|Perl5Matcher
name|matcher
init|=
operator|new
name|Perl5Matcher
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|prevPattern
init|=
literal|null
decl_stmt|;
specifier|protected
name|Pattern
name|pat
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|prevFlags
init|=
operator|-
literal|1
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|FunMatches
parameter_list|(
name|XQueryContext
name|context
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
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Function#setArguments(java.util.List) 	 */
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|steps
operator|.
name|addAll
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#getDependencies()      */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
specifier|final
name|Expression
name|stringArg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|patternArg
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|stringArg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
operator|(
name|stringArg
operator|.
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
operator|&&
operator|(
name|patternArg
operator|.
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
else|else
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#returnsType()      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
if|if
condition|(
name|inPredicate
operator|&&
operator|(
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
block|{
comment|/* If one argument is a node set we directly 			 * return the matching nodes from the context set. This works 			 * only inside predicates. 			 */
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|// In all other cases, we return boolean
return|return
name|Type
operator|.
name|BOOLEAN
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
name|Sequence
name|input
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
name|input
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
if|if
condition|(
name|inPredicate
operator|&&
operator|(
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
return|return
name|evalWithIndex
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|input
argument_list|)
return|;
else|else
return|return
name|evalGeneric
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|input
argument_list|)
return|;
block|}
comment|/**      * @param contextSequence      * @param contextItem      * @param stringArg      * @return 	 * @throws XPathException      */
specifier|private
name|Sequence
name|evalWithIndex
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Sequence
name|input
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|pattern
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
name|flags
operator|=
name|parseFlags
argument_list|(
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|NodeSet
name|nodes
init|=
name|input
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
comment|// get the type of a possible index
name|int
name|indexType
init|=
name|nodes
operator|.
name|getIndexType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|indexType
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|DocumentSet
name|docs
init|=
name|nodes
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using index ..."
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getValueIndex
argument_list|()
operator|.
name|match
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
name|pattern
argument_list|,
name|DBBroker
operator|.
name|MATCH_REGEXP
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
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
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|ExtArrayNodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|nodes
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
name|NodeProxy
name|node
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
argument_list|(
name|node
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|pattern
argument_list|,
name|flags
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/**      * @param contextSequence      * @param contextItem      * @param stringArg      * @return      * @throws XPathException      */
specifier|private
name|Sequence
name|evalGeneric
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Sequence
name|stringArg
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|string
init|=
name|stringArg
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
name|flags
operator|=
name|parseFlags
argument_list|(
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|match
argument_list|(
name|string
argument_list|,
name|pattern
argument_list|,
name|flags
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @param string      * @param pattern      * @param flags      * @return      * @throws XPathException      */
specifier|private
name|boolean
name|match
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|pattern
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
name|prevPattern
operator|==
literal|null
operator|||
operator|(
operator|!
name|pattern
operator|.
name|equals
argument_list|(
name|prevPattern
argument_list|)
operator|)
operator|||
name|flags
operator|!=
name|prevFlags
condition|)
name|pat
operator|=
name|compiler
operator|.
name|compile
argument_list|(
name|pattern
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|prevPattern
operator|=
name|pattern
expr_stmt|;
name|prevFlags
operator|=
name|flags
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|(
name|string
argument_list|,
name|pat
argument_list|)
condition|)
return|return
literal|true
return|;
else|else
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|MalformedPatternException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid regular expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|final
specifier|static
name|int
name|parseFlags
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|flags
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'m'
case|:
name|flags
operator||=
name|Perl5Compiler
operator|.
name|MULTILINE_MASK
expr_stmt|;
break|break;
case|case
literal|'i'
case|:
name|flags
operator||=
name|Perl5Compiler
operator|.
name|CASE_INSENSITIVE_MASK
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid regular expression flag: "
operator|+
name|ch
argument_list|)
throw|;
block|}
block|}
return|return
name|flags
return|;
block|}
block|}
end_class

end_unit

