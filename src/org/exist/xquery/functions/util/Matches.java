begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: MD5.java 2933 2006-03-20 04:27:24Z alexmilowski $  */
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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
name|StringValue
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
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * Generate an MD5 key from a string.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Matches
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
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Tries to match the string in $a to the regular expression in $b. "
operator|+
literal|"Returns an empty sequence if the string does not match, or a sequence whose "
operator|+
literal|"first item is the entire string, and whose following items are the matched groups."
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
block|, 				}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
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
literal|"matches"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Tries to match the string in $a to the regular expression in $b, using "
operator|+
literal|"the flags specified in $c. Returns an empty sequence if the string does "
operator|+
literal|"not match, or a sequence whose first item is the entire string, and whose "
operator|+
literal|"following items are the matched groups."
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
block|, 				}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|protected
name|Matcher
name|matcher
init|=
literal|null
decl_stmt|;
specifier|protected
name|Pattern
name|pat
init|=
literal|null
decl_stmt|;
specifier|public
name|Matches
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
name|result
decl_stmt|;
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
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|evalGeneric
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|input
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
return|return
name|result
return|;
block|}
comment|/** 	 * Translates the regular expression from XPath2 syntax to java regex 	 * syntax. 	 *  	 * @param pattern 	 * @return 	 * @throws XPathException 	 */
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
name|translateRegexp
argument_list|(
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
argument_list|)
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
name|match
argument_list|(
name|string
argument_list|,
name|pattern
argument_list|,
name|flags
argument_list|)
return|;
block|}
comment|/**      * @param string      * @param pattern      * @param flags      * @return      * @throws XPathException      */
specifier|private
name|Sequence
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
name|pat
operator|==
literal|null
operator|||
operator|(
operator|!
name|pattern
operator|.
name|equals
argument_list|(
name|pat
operator|.
name|pattern
argument_list|()
argument_list|)
operator|)
operator|||
name|flags
operator|!=
name|pat
operator|.
name|flags
argument_list|()
condition|)
block|{
name|pat
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|pattern
argument_list|,
name|flags
argument_list|)
expr_stmt|;
comment|//TODO : make matches('&#x212A;', '[A-Z]', 'i') work !
name|matcher
operator|=
name|pat
operator|.
name|matcher
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matcher
operator|.
name|reset
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
name|int
name|items
init|=
name|matcher
operator|.
name|groupCount
argument_list|()
operator|+
literal|1
decl_stmt|;
name|Sequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|(
name|items
argument_list|)
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|string
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|items
condition|;
name|i
operator|++
control|)
block|{
name|String
name|val
init|=
name|matcher
operator|.
name|group
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
literal|""
expr_stmt|;
block|}
name|seq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
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
name|Pattern
operator|.
name|MULTILINE
expr_stmt|;
break|break;
case|case
literal|'i'
case|:
name|flags
operator|=
name|flags
operator||
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|UNICODE_CASE
expr_stmt|;
break|break;
case|case
literal|'x'
case|:
name|flags
operator||=
name|Pattern
operator|.
name|COMMENTS
expr_stmt|;
break|break;
case|case
literal|'s'
case|:
name|flags
operator||=
name|Pattern
operator|.
name|DOTALL
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

