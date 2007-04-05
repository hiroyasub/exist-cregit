begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: ExtRegexp.java 5578 2007-04-03 20:34:57Z wolfgang_m $  */
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
operator|.
name|text
package|;
end_package

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
name|storage
operator|.
name|FulltextIndexSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|util
operator|.
name|RegexTranslator
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
name|RegexTranslator
operator|.
name|RegexSyntaxException
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
name|*
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|MatchRegexp
extends|extends
name|Function
implements|implements
name|Optimizable
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
literal|"match-all"
argument_list|,
name|TextModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TextModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Tries to match each of the regular expression "
operator|+
literal|"strings passed in $b against the keywords contained in "
operator|+
literal|"the fulltext index. The keywords found are then compared to the node set in $a. Every "
operator|+
literal|"node containing ALL of the keywords is copied to the result sequence. By default, a keyword "
operator|+
literal|"is considered to match the pattern if any substring of the keyword matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 'w'. With 'w' specified, the regular expression "
operator|+
literal|"is matched against the entire keyword, i.e. 'explain.*' will match 'explained' , but not 'unexplained'."
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
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"match-all"
argument_list|,
name|TextModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TextModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Tries to match each of the regular expression "
operator|+
literal|"strings passed in $b against the keywords contained in "
operator|+
literal|"the fulltext index. The keywords found are then compared to the node set in $a. Every "
operator|+
literal|"node containing ALL of the keywords is copied to the result sequence. By default, a keyword "
operator|+
literal|"is considered to match the pattern if any substring of the keyword matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 'w'. With 'w' specified, the regular expression "
operator|+
literal|"is matched against the entire keyword, i.e. 'explain.*' will match 'explained' , but not 'unexplained'."
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"match-any"
argument_list|,
name|TextModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TextModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Tries to match each of the regular expression "
operator|+
literal|"strings passed in $b against the keywords contained in "
operator|+
literal|"the fulltext index. The keywords found are then compared to the node set in $a. Every "
operator|+
literal|"node containing ANY of the keywords is copied to the result sequence. By default, a keyword "
operator|+
literal|"is considered to match the pattern if any substring of the keyword matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 'w'. With 'w' specified, the regular expression "
operator|+
literal|"is matched against the entire keyword, i.e. 'explain.*' will match 'explained' , but not 'unexplained'."
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
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"match-any"
argument_list|,
name|TextModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TextModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Tries to match each of the regular expression "
operator|+
literal|"strings passed in $b against the keywords contained in "
operator|+
literal|"the fulltext index. The keywords found are then compared to the node set in $a. Every "
operator|+
literal|"node containing ANY of the keywords is copied to the result sequence. By default, a keyword "
operator|+
literal|"is considered to match the pattern if any substring of the keyword matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 'w'. With 'w' specified, the regular expression "
operator|+
literal|"is matched against the entire keyword, i.e. 'explain.*' will match 'explained' , but not 'unexplained'."
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH_ALL_FLAG
init|=
literal|"w"
decl_stmt|;
specifier|protected
name|int
name|type
init|=
name|Constants
operator|.
name|FULLTEXT_AND
decl_stmt|;
specifier|protected
name|CachedResult
name|cached
init|=
literal|null
decl_stmt|;
specifier|private
name|LocationStep
name|contextStep
init|=
literal|null
decl_stmt|;
specifier|protected
name|QName
name|contextQName
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|optimizeSelf
init|=
literal|false
decl_stmt|;
specifier|protected
name|NodeSet
name|preselectResult
init|=
literal|null
decl_stmt|;
specifier|public
name|MatchRegexp
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
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"match-any"
argument_list|)
condition|)
name|this
operator|.
name|type
operator|=
name|Constants
operator|.
name|FULLTEXT_OR
expr_stmt|;
name|LocationStep
name|step
init|=
name|BasicExpressionVisitor
operator|.
name|findFirstStep
argument_list|(
name|getArgument
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|step
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|step
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|)
block|{
name|Expression
name|outerExpr
init|=
name|contextInfo
operator|.
name|getContextStep
argument_list|()
decl_stmt|;
if|if
condition|(
name|outerExpr
operator|!=
literal|null
operator|&&
name|outerExpr
operator|instanceof
name|LocationStep
condition|)
block|{
name|LocationStep
name|outerStep
init|=
operator|(
name|LocationStep
operator|)
name|outerExpr
decl_stmt|;
name|NodeTest
name|test
init|=
name|outerStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|step
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|step
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|contextStep
operator|=
name|step
expr_stmt|;
name|optimizeSelf
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|NodeTest
name|test
init|=
name|step
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|step
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|step
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|contextStep
operator|=
name|step
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|boolean
name|canOptimize
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
if|if
condition|(
name|contextQName
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|checkForQNameIndex
argument_list|(
name|contextSequence
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|optimizeOnSelf
parameter_list|()
block|{
return|return
name|optimizeSelf
return|;
block|}
specifier|public
name|NodeSet
name|preSelect
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|boolean
name|useContext
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// get the search terms
name|Expression
name|termsExpr
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Expression
name|flagsExpr
init|=
operator|(
name|getArgumentCount
argument_list|()
operator|==
literal|3
operator|)
condition|?
name|getArgument
argument_list|(
literal|2
argument_list|)
else|:
literal|null
decl_stmt|;
name|boolean
name|matchAll
init|=
name|getMatchFlag
argument_list|(
name|flagsExpr
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
name|List
name|terms
init|=
name|getSearchTerms
argument_list|(
name|termsExpr
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
comment|// lookup the terms in the fulltext index. returns one node set for each term
name|NodeSet
index|[]
name|hits
init|=
name|getMatches
argument_list|(
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|useContext
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
literal|null
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|contextQName
argument_list|,
name|terms
argument_list|,
name|matchAll
argument_list|)
decl_stmt|;
comment|// walk through the matches and compute the combined node set
name|preselectResult
operator|=
name|hits
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|preselectResult
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
block|{
name|preselectResult
operator|=
operator|(
name|type
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
condition|?
name|preselectResult
operator|.
name|deepIntersection
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
else|:
name|preselectResult
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
block|}
block|}
else|else
block|{
name|preselectResult
operator|=
name|NodeSet
operator|.
name|EMPTY_SET
expr_stmt|;
block|}
return|return
name|preselectResult
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
comment|// if we were optimizing and the preselect did not return anything,
comment|// we won't have any matches and can return
if|if
condition|(
name|preselectResult
operator|!=
literal|null
operator|&&
name|preselectResult
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
name|preselectResult
operator|==
literal|null
operator|&&
operator|!
name|checkForQNameIndex
argument_list|(
name|contextSequence
argument_list|)
condition|)
name|contextQName
operator|=
literal|null
expr_stmt|;
name|Expression
name|path
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Expression
name|termsExpr
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Expression
name|flagsExpr
init|=
operator|(
name|getArgumentCount
argument_list|()
operator|==
literal|3
operator|)
condition|?
name|getArgument
argument_list|(
literal|2
argument_list|)
else|:
literal|null
decl_stmt|;
name|boolean
name|matchAll
init|=
name|getMatchFlag
argument_list|(
name|flagsExpr
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
name|NodeSet
name|result
decl_stmt|;
comment|// if the expression does not depend on the current context item,
comment|// we can evaluate it in one single step
if|if
condition|(
name|path
operator|==
literal|null
operator|||
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|path
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
block|{
name|boolean
name|canCache
init|=
operator|(
name|getTermDependencies
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
decl_stmt|;
if|if
condition|(
name|canCache
operator|&&
name|cached
operator|!=
literal|null
operator|&&
name|cached
operator|.
name|isValid
argument_list|(
name|contextSequence
argument_list|)
condition|)
block|{
return|return
name|cached
operator|.
name|getResult
argument_list|()
return|;
block|}
comment|// do we optimize this expression?
if|if
condition|(
name|contextStep
operator|==
literal|null
operator|||
name|preselectResult
operator|==
literal|null
condition|)
block|{
comment|// no optimization: process the whole expression
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
name|termsExpr
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
name|result
operator|=
name|evalQuery
argument_list|(
name|nodes
argument_list|,
name|terms
argument_list|,
name|matchAll
argument_list|)
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|contextStep
operator|.
name|setPreloadNodeSets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|contextStep
operator|.
name|setPreloadedData
argument_list|(
name|preselectResult
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|preselectResult
argument_list|)
expr_stmt|;
name|result
operator|=
name|path
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|canCache
operator|&&
name|contextSequence
operator|instanceof
name|NodeSet
condition|)
name|cached
operator|=
operator|new
name|CachedResult
argument_list|(
operator|(
name|NodeSet
operator|)
name|contextSequence
argument_list|,
name|result
argument_list|)
expr_stmt|;
comment|// otherwise we have to walk through each item in the context
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
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
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
name|termsExpr
argument_list|,
name|contextSequence
argument_list|)
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
name|nodes
argument_list|,
name|terms
argument_list|,
name|matchAll
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
name|preselectResult
operator|=
literal|null
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|checkForQNameIndex
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
name|contextQName
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|boolean
name|hasQNameIndex
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|contextSequence
operator|.
name|getCollectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Collection
name|collection
init|=
operator|(
name|Collection
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|equals
argument_list|(
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
condition|)
continue|continue;
name|FulltextIndexSpec
name|config
init|=
name|collection
operator|.
name|getFulltextIndexConfiguration
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
comment|//We have a fulltext index
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|hasQNameIndex
operator|=
name|config
operator|.
name|hasQNameIndex
argument_list|(
name|contextQName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hasQNameIndex
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"cannot use index on QName: "
operator|+
name|contextQName
operator|+
literal|". Collection "
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|" does not define an index"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|hasQNameIndex
return|;
block|}
specifier|protected
name|List
name|getSearchTerms
parameter_list|(
name|Expression
name|termsExpr
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|List
name|terms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Sequence
name|seq
init|=
name|termsExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|seq
operator|.
name|hasOne
argument_list|()
condition|)
name|terms
operator|.
name|add
argument_list|(
name|translateRegexp
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
name|translateRegexp
argument_list|(
name|it
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|terms
return|;
block|}
specifier|private
name|boolean
name|getMatchFlag
parameter_list|(
name|Expression
name|flagsExpr
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|boolean
name|matchAll
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|flagsExpr
operator|!=
literal|null
condition|)
block|{
name|String
name|flagStr
init|=
name|flagsExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|matchAll
operator|=
name|flagStr
operator|.
name|equals
argument_list|(
name|MATCH_ALL_FLAG
argument_list|)
expr_stmt|;
block|}
return|return
name|matchAll
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#getDependencies() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.ExtFulltext#evalQuery(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, java.lang.String, org.exist.dom.NodeSet) 	 */
specifier|public
name|Sequence
name|evalQuery
parameter_list|(
name|NodeSet
name|nodes
parameter_list|,
name|List
name|terms
parameter_list|,
name|boolean
name|matchAll
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
index|[]
name|hits
init|=
name|getMatches
argument_list|(
name|nodes
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|nodes
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
name|contextQName
argument_list|,
name|terms
argument_list|,
name|matchAll
argument_list|)
decl_stmt|;
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
name|NodeSet
index|[]
name|getMatches
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|axis
parameter_list|,
name|QName
name|qname
parameter_list|,
name|List
name|terms
parameter_list|,
name|boolean
name|matchAll
parameter_list|)
throws|throws
name|XPathException
block|{
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
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
name|axis
argument_list|,
name|qname
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
argument_list|,
name|matchAll
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Matches for "
operator|+
name|terms
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|+
literal|": "
operator|+
name|hits
index|[
name|k
index|]
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|hits
return|;
block|}
specifier|protected
name|int
name|getTermDependencies
parameter_list|()
throws|throws
name|XPathException
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#resetState() 	 */
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
block|}
comment|/** 	 * Translates the regular expression from XPath2 syntax to java regex 	 * syntax. 	 * 	 * @param pattern 	 * @return 	 * @throws XPathException 	 */
specifier|protected
name|String
name|translateRegexp
parameter_list|(
name|String
name|pattern
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// convert pattern to Java regex syntax
try|try
block|{
name|pattern
operator|=
name|RegexTranslator
operator|.
name|translate
argument_list|(
name|pattern
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RegexSyntaxException
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
literal|"Conversion from XPath2 to Java regular expression "
operator|+
literal|"syntax failed: "
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
return|return
name|pattern
return|;
block|}
block|}
end_class

end_unit

