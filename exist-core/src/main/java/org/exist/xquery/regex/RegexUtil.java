begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|regex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|ErrorCodes
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
name|StringValue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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

begin_comment
comment|/**  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|RegexUtil
block|{
comment|/**      * Parses the flags for an XQuery Regular Expression.      *      * @param context The calling expression      * @param strFlags The XQuery Regular Expression flags.      *      * @return The flags for a Java Regular Expression.      * @throws XPathException in case of invalid flag      */
specifier|public
specifier|static
name|int
name|parseFlags
parameter_list|(
specifier|final
name|Expression
name|context
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|strFlags
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|strFlags
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|strFlags
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|ch
init|=
name|strFlags
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
case|case
literal|'q'
case|:
name|flags
operator||=
name|Pattern
operator|.
name|LITERAL
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|context
argument_list|,
name|ErrorCodes
operator|.
name|FORX0001
argument_list|,
literal|"Invalid regular expression flag: "
operator|+
name|ch
argument_list|,
operator|new
name|StringValue
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ch
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|flags
return|;
block|}
comment|/**      * Determines if the Java Regular Expression flags have the literal flag set.      *      * @param flags The Java Regular Expression flags      *      * @return true if the literal flag is set      */
specifier|public
specifier|static
name|boolean
name|hasLiteral
parameter_list|(
specifier|final
name|int
name|flags
parameter_list|)
block|{
return|return
operator|(
name|flags
operator|&
name|Pattern
operator|.
name|LITERAL
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * Determines if the Java Regular Expression flags have the case-insensitive flag set.      *      * @param flags The Java Regular Expression flags      *      * @return true if the case-insensitive flag is set      */
specifier|public
specifier|static
name|boolean
name|hasCaseInsensitive
parameter_list|(
specifier|final
name|int
name|flags
parameter_list|)
block|{
return|return
operator|(
name|flags
operator|&
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator|)
operator|!=
literal|0
operator|||
operator|(
name|flags
operator|&
name|Pattern
operator|.
name|UNICODE_CASE
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * Determines if the Java Regular Expression flags have the ignore-whitespace flag set.      *      * @param flags The Java Regular Expression flags      *      * @return true if the ignore-whitespace flag is set      */
specifier|public
specifier|static
name|boolean
name|hasIgnoreWhitespace
parameter_list|(
specifier|final
name|int
name|flags
parameter_list|)
block|{
return|return
operator|(
name|flags
operator|&
name|Pattern
operator|.
name|COMMENTS
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * Translates the Regular Expression from XPath3 syntax to Java regex      * syntax.      *      * @param context the context expression - used for error reporting      * @param pattern a String containing a regular expression in the syntax of XPath Functions and Operators 3.0.      * @param ignoreWhitespace true if whitespace is to be ignored ('x' flag)      * @param caseBlind true if case is to be ignored ('i' flag)      *      * @return The Java Regular Expression      *      * @throws XPathException if the XQuery Regular Expression is invalid.      */
specifier|public
specifier|static
name|String
name|translateRegexp
parameter_list|(
specifier|final
name|Expression
name|context
parameter_list|,
specifier|final
name|String
name|pattern
parameter_list|,
specifier|final
name|boolean
name|ignoreWhitespace
parameter_list|,
specifier|final
name|boolean
name|caseBlind
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// convert pattern to Java regex syntax
try|try
block|{
specifier|final
name|int
name|xmlVersion
init|=
literal|11
decl_stmt|;
return|return
name|JDK15RegexTranslator
operator|.
name|translate
argument_list|(
name|pattern
argument_list|,
name|xmlVersion
argument_list|,
literal|true
argument_list|,
name|ignoreWhitespace
argument_list|,
name|caseBlind
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|RegexSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|context
argument_list|,
name|ErrorCodes
operator|.
name|FORX0002
argument_list|,
literal|"Conversion from XPath F&O 3.0 regular expression syntax to Java regular expression syntax failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
operator|new
name|StringValue
argument_list|(
name|pattern
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

