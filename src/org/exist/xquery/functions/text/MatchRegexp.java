begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|CachedResult
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
name|MatchRegexp
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
literal|"is considered to match the pattern only if the entire string matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 's'. With 's' specified, a string matches "
operator|+
literal|"the pattern if any substring matches, i.e. 'explain.*' will match 'unexplained'."
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
literal|"is considered to match the pattern only if the entire string matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 's'. With 's' specified, a string matches "
operator|+
literal|"the pattern if any substring matches, i.e. 'explain.*' will match 'unexplained'."
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
literal|"is considered to match the pattern only if the entire string matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 's'. With 's' specified, a string matches "
operator|+
literal|"the pattern if any substring matches, i.e. 'explain.*' will match 'unexplained'."
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
literal|"is considered to match the pattern only if the entire string matches. To change this behaviour, "
operator|+
literal|"use the 3-argument version of the function and specify flag 's'. With 's' specified, a string matches "
operator|+
literal|"the pattern if any substring matches, i.e. 'explain.*' will match 'unexplained'."
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
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"match-any"
argument_list|)
condition|)
name|type
operator|=
name|Constants
operator|.
name|FULLTEXT_OR
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
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
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
name|termsExpr
operator|.
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
expr_stmt|;
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
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
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
name|Item
name|current
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|List
name|terms
init|=
name|getSearchTerms
argument_list|(
name|termsExpr
argument_list|,
name|current
operator|.
name|toSequence
argument_list|()
argument_list|)
decl_stmt|;
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
name|current
operator|.
name|toSequence
argument_list|()
argument_list|)
operator|.
name|toNodeSet
argument_list|()
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
name|Sequence
name|temp
init|=
name|evalQuery
argument_list|(
name|nodes
argument_list|,
name|terms
argument_list|,
name|matchAll
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|temp
argument_list|)
expr_stmt|;
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
name|nodes
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|nodes
argument_list|,
literal|null
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
name|Expression
name|next
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
name|deps
operator||=
name|next
operator|.
name|getDependencies
argument_list|()
expr_stmt|;
block|}
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
comment|/** 	 * Translates the regular expression from XPath2 syntax to java regex 	 * syntax. 	 * 	 * @param pattern 	 * @return 	 * @throws org.exist.xquery.XPathException 	 */
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
name|RegexTranslator
operator|.
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

