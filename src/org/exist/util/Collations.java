begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|StringCharacterIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|ULocale
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|VersionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|XPathException
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

begin_comment
comment|/**  * Utility methods dealing with collations.  *  * @author wolf  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|Collations
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Collations
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The default Unicode Codepoint Collation URI as defined by the XQuery      * spec.      */
specifier|public
specifier|final
specifier|static
name|String
name|UNICODE_CODEPOINT_COLLATION_URI
init|=
literal|"http://www.w3.org/2005/xpath-functions/collation/codepoint"
decl_stmt|;
comment|/**      * Short string to select the default codepoint collation      */
specifier|public
specifier|final
specifier|static
name|String
name|CODEPOINT_SHORT
init|=
literal|"codepoint"
decl_stmt|;
comment|/**      * The UCA (Unicode Collation Algorithm) Codepoint URI as defined by the XQuery      * spec.      */
specifier|public
specifier|final
specifier|static
name|String
name|UCA_COLLATION_URI
init|=
literal|"http://www.w3.org/2013/collation/UCA"
decl_stmt|;
comment|/**      * The URI used to select collations in eXist.      */
specifier|public
specifier|final
specifier|static
name|String
name|EXIST_COLLATION_URI
init|=
literal|"http://exist-db.org/collation"
decl_stmt|;
comment|/**      * Get a {@link Comparator}from the specified URI.      *<p>      * The original code is from saxon (@linkplain http://saxon.sf.net).      *      * @param uri The URI describing the collation and settings      *      * @return The Collator for the URI, or null.      *      * @throws XPathException If an error occurs whilst constructing the Collator      */
specifier|public
specifier|static
annotation|@
name|Nullable
name|Collator
name|getCollationFromURI
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
name|EXIST_COLLATION_URI
argument_list|)
operator|||
name|uri
operator|.
name|startsWith
argument_list|(
name|UCA_COLLATION_URI
argument_list|)
operator|||
name|uri
operator|.
name|startsWith
argument_list|(
literal|"?"
argument_list|)
condition|)
block|{
name|URI
name|u
decl_stmt|;
try|try
block|{
name|u
operator|=
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|query
init|=
name|u
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
name|Collator
operator|.
name|getInstance
argument_list|()
return|;
block|}
else|else
block|{
name|boolean
name|fallback
init|=
literal|true
decl_stmt|;
comment|// default is "yes"
name|String
name|lang
init|=
literal|null
decl_stmt|;
name|String
name|version
init|=
literal|null
decl_stmt|;
name|String
name|strength
init|=
literal|null
decl_stmt|;
name|String
name|maxVariable
init|=
literal|"punct"
decl_stmt|;
comment|// default is punct
name|String
name|alternate
init|=
literal|"non-ignorable"
decl_stmt|;
comment|// default is non-ignorable
name|boolean
name|backwards
init|=
literal|false
decl_stmt|;
comment|// default is "no"
name|boolean
name|normalization
init|=
literal|false
decl_stmt|;
comment|// default is "no"
name|boolean
name|caseLevel
init|=
literal|false
decl_stmt|;
comment|// default is "no"
name|String
name|caseFirst
init|=
literal|null
decl_stmt|;
name|boolean
name|numeric
init|=
literal|false
decl_stmt|;
comment|// default is "no"
name|String
name|reorder
init|=
literal|null
decl_stmt|;
name|String
name|decomposition
init|=
literal|null
decl_stmt|;
specifier|final
name|StringTokenizer
name|queryTokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|query
argument_list|,
literal|";&"
argument_list|)
decl_stmt|;
while|while
condition|(
name|queryTokenizer
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|String
name|param
init|=
name|queryTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
specifier|final
name|int
name|eq
init|=
name|param
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|eq
operator|>
literal|0
condition|)
block|{
specifier|final
name|String
name|kw
init|=
name|param
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|eq
argument_list|)
decl_stmt|;
if|if
condition|(
name|kw
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|val
init|=
name|param
operator|.
name|substring
argument_list|(
name|eq
operator|+
literal|1
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|kw
condition|)
block|{
case|case
literal|"fallback"
case|:
name|fallback
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"lang"
case|:
name|lang
operator|=
name|val
expr_stmt|;
break|break;
case|case
literal|"version"
case|:
name|version
operator|=
name|val
expr_stmt|;
break|break;
case|case
literal|"strength"
case|:
name|strength
operator|=
name|val
expr_stmt|;
break|break;
case|case
literal|"maxVariable"
case|:
name|maxVariable
operator|=
name|val
expr_stmt|;
break|break;
case|case
literal|"alternate"
case|:
name|alternate
operator|=
name|val
expr_stmt|;
break|break;
case|case
literal|"backwards"
case|:
name|backwards
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"normalization"
case|:
name|normalization
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"caseLevel"
case|:
name|caseLevel
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"caseFirst"
case|:
name|caseFirst
operator|=
name|val
expr_stmt|;
break|break;
case|case
literal|"numeric"
case|:
name|numeric
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"reorder"
case|:
name|reorder
operator|=
name|val
expr_stmt|;
break|break;
case|case
literal|"decomposition"
case|:
name|decomposition
operator|=
name|val
expr_stmt|;
break|break;
default|default:
name|logger
operator|.
name|warn
argument_list|(
literal|"Unrecognized Collation parameter: "
operator|+
name|kw
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|getCollationFromParams
argument_list|(
name|fallback
argument_list|,
name|lang
argument_list|,
name|version
argument_list|,
name|strength
argument_list|,
name|maxVariable
argument_list|,
name|alternate
argument_list|,
name|backwards
argument_list|,
name|normalization
argument_list|,
name|caseLevel
argument_list|,
name|caseFirst
argument_list|,
name|numeric
argument_list|,
name|reorder
argument_list|,
name|decomposition
argument_list|)
return|;
block|}
block|}
if|else if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"java:"
argument_list|)
condition|)
block|{
comment|// java class specified: this should be a subclass of
comment|// com.ibm.icu.text.RuleBasedCollator
specifier|final
name|String
name|uriClassName
init|=
name|uri
operator|.
name|substring
argument_list|(
literal|"java:"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|collatorClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|uriClassName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Collator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|collatorClass
argument_list|)
condition|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"The specified collator class '"
operator|+
name|collatorClass
operator|.
name|getName
argument_list|()
operator|+
literal|"' is not a subclass of com.ibm.icu.text.Collator"
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
name|msg
argument_list|)
throw|;
block|}
return|return
operator|(
name|Collator
operator|)
name|collatorClass
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"The specified collator class "
operator|+
name|uriClassName
operator|+
literal|" could not be found"
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|UNICODE_CODEPOINT_COLLATION_URI
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
specifier|final
name|String
name|msg
init|=
literal|"Unknown collation : '"
operator|+
name|uri
operator|+
literal|"'"
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
comment|/**      * Determines if the two strings are equal with regards to a Collation.      *      * @param collator The collation, or null if no collation should be used.      * @param s1 The first string to compare against the second.      * @param s2 The second string to compare against the first.      *      * @return true if the Strings are equal.      */
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|String
name|s1
parameter_list|,
specifier|final
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|collator
operator|==
literal|null
condition|)
block|{
return|return
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|collator
operator|.
name|equals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
return|;
block|}
block|}
comment|/**      * Compares two strings with regards to a Collation.      *      * @param collator The collation, or null if no collation should be used.      * @param s1 The first string to compare against the second.      * @param s2 The second string to compare against the first.      *      * @return a negative integer, zero, or a positive integer if the      *     {@code s1} is less than, equal to, or greater than {@code s2}.      *      * @throws UnsupportedOperationException if ICU4J does not support collation      */
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|String
name|s1
parameter_list|,
specifier|final
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|collator
operator|==
literal|null
condition|)
block|{
return|return
name|s1
operator|==
literal|null
condition|?
operator|(
name|s2
operator|==
literal|null
condition|?
literal|0
else|:
operator|-
literal|1
operator|)
else|:
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|collator
operator|.
name|compare
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
return|;
block|}
block|}
comment|/**      * Determines if one string starts with another with regards to a Collation.      *      * @param collator The collation, or null if no collation should be used.      * @param s1 The first string to compare against the second.      * @param s2 The second string to compare against the first.      *      * @return true if {@code s1} starts with {@code @s2}.      *      * @throws UnsupportedOperationException if ICU4J does not support collation      */
specifier|public
specifier|static
name|boolean
name|startsWith
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|String
name|s1
parameter_list|,
specifier|final
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|collator
operator|==
literal|null
condition|)
block|{
return|return
name|s1
operator|.
name|startsWith
argument_list|(
name|s2
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|SearchIterator
name|searchIterator
init|=
operator|new
name|StringSearch
argument_list|(
name|s2
argument_list|,
operator|new
name|StringCharacterIterator
argument_list|(
name|s1
argument_list|)
argument_list|,
operator|(
name|RuleBasedCollator
operator|)
name|collator
argument_list|)
decl_stmt|;
return|return
name|searchIterator
operator|.
name|first
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
comment|/**      * Determines if one string ends with another with regards to a Collation.      *      * @param collator The collation, or null if no collation should be used.      * @param s1 The first string to compare against the second.      * @param s2 The second string to compare against the first.      *      * @return true if {@code s1} ends with {@code @s2}.      *      * @throws UnsupportedOperationException if ICU4J does not support collation      */
specifier|public
specifier|static
name|boolean
name|endsWith
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|String
name|s1
parameter_list|,
specifier|final
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|collator
operator|==
literal|null
condition|)
block|{
return|return
name|s1
operator|.
name|endsWith
argument_list|(
name|s2
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|SearchIterator
name|searchIterator
init|=
operator|new
name|StringSearch
argument_list|(
name|s2
argument_list|,
operator|new
name|StringCharacterIterator
argument_list|(
name|s1
argument_list|)
argument_list|,
operator|(
name|RuleBasedCollator
operator|)
name|collator
argument_list|)
decl_stmt|;
name|int
name|lastPos
init|=
name|SearchIterator
operator|.
name|DONE
decl_stmt|;
name|int
name|lastLen
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
name|searchIterator
operator|.
name|first
argument_list|()
init|;
name|pos
operator|!=
name|SearchIterator
operator|.
name|DONE
condition|;
name|pos
operator|=
name|searchIterator
operator|.
name|next
argument_list|()
control|)
block|{
name|lastPos
operator|=
name|pos
expr_stmt|;
name|lastLen
operator|=
name|searchIterator
operator|.
name|getMatchLength
argument_list|()
expr_stmt|;
block|}
return|return
name|lastPos
operator|>
name|SearchIterator
operator|.
name|DONE
operator|&&
name|lastPos
operator|+
name|lastLen
operator|==
name|s1
operator|.
name|length
argument_list|()
return|;
block|}
block|}
comment|/**      * Determines if one string contains another with regards to a Collation.      *      * @param collator The collation, or null if no collation should be used.      * @param s1 The first string to compare against the second.      * @param s2 The second string to compare against the first.      *      * @return true if {@code s1} contains {@code @s2}.      *      * @throws UnsupportedOperationException if ICU4J does not support collation      */
specifier|public
specifier|static
name|boolean
name|contains
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|String
name|s1
parameter_list|,
specifier|final
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|collator
operator|==
literal|null
condition|)
block|{
return|return
name|s1
operator|.
name|contains
argument_list|(
name|s2
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|SearchIterator
name|searchIterator
init|=
operator|new
name|StringSearch
argument_list|(
name|s2
argument_list|,
operator|new
name|StringCharacterIterator
argument_list|(
name|s1
argument_list|)
argument_list|,
operator|(
name|RuleBasedCollator
operator|)
name|collator
argument_list|)
decl_stmt|;
return|return
name|searchIterator
operator|.
name|first
argument_list|()
operator|>=
literal|0
return|;
block|}
block|}
comment|/**      * Finds the index of one string within another string with regards to a Collation.      *      * @param collator The collation, or null if no collation should be used.      * @param s1 The string to look for {@code s2} in      * @param s2 The substring to look for in {@code s1}.      *      * @return the index of the first occurrence of the specified substring,      *          or {@code -1} if there is no such occurrence.      */
specifier|public
specifier|static
name|int
name|indexOf
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|String
name|s1
parameter_list|,
specifier|final
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|collator
operator|==
literal|null
condition|)
block|{
return|return
name|s1
operator|.
name|indexOf
argument_list|(
name|s2
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|SearchIterator
name|searchIterator
init|=
operator|new
name|StringSearch
argument_list|(
name|s2
argument_list|,
operator|new
name|StringCharacterIterator
argument_list|(
name|s1
argument_list|)
argument_list|,
operator|(
name|RuleBasedCollator
operator|)
name|collator
argument_list|)
decl_stmt|;
return|return
name|searchIterator
operator|.
name|first
argument_list|()
return|;
block|}
block|}
comment|/**      * Get a Collator with the provided settings.      *      * @param fallback Determines whether the processor uses a fallback      *     collation if a conformant collation is not available.      * @param lang language code: a string in the lexical space of xs:language.      * @param strength The collation strength as defined in UCA.      * @param maxVariable Indicates that all characters in the specified group      *     and earlier groups are treated as "noise" characters to be handled      *     as defined by the alternate parameter. "space" | "punct" | "symbol".      *     | "currency".      * @param alternate Controls the handling of characters such as spaces and      *     hyphens; specifically, the "noise" characters in the groups selected      *     by the maxVariable parameter. "non-ignorable" | "shifted" |      *     "blanked".      * @param backwards indicates that the last accent in the string is the      *     most significant.      * @param normalization Indicates whether strings are converted to      *     normalization form D.      * @param caseLevel When used with primary strength, setting caseLevel has      *     the effect of ignoring accents while taking account of case.      * @param caseFirst Indicates whether upper-case precedes lower-case or      *     vice versa.      * @param numeric When numeric is specified, a sequence of consecutive      *     digits is interpreted as a number, for example chap2 sorts before      *     chap12.      * @param reorder Determines the relative ordering of text in different      *     scripts; for example the value digit,Grek,Latn indicates that      *     digits precede Greek letters, which precede Latin letters.      * @param decomposition The decomposition      *      * @return The collator of null if a Collator could not be retrieved      *      * @throws XPathException if an error occurs whilst getting the Collator      */
specifier|private
specifier|static
annotation|@
name|Nullable
name|Collator
name|getCollationFromParams
parameter_list|(
specifier|final
name|boolean
name|fallback
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|lang
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|version
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|strength
parameter_list|,
specifier|final
name|String
name|maxVariable
parameter_list|,
specifier|final
name|String
name|alternate
parameter_list|,
specifier|final
name|boolean
name|backwards
parameter_list|,
specifier|final
name|boolean
name|normalization
parameter_list|,
specifier|final
name|boolean
name|caseLevel
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|caseFirst
parameter_list|,
specifier|final
name|boolean
name|numeric
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|reorder
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|decomposition
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Collator
name|collator
decl_stmt|;
if|if
condition|(
literal|"sme-SE"
operator|.
name|equals
argument_list|(
name|lang
argument_list|)
condition|)
block|{
comment|// Collation rules contained in a String object.
comment|// Codes for the representation of names of languages:
comment|// http://www.loc.gov/standards/iso639-2/englangn.html
comment|// UTF-8 characters from:
comment|// http://chouette.info/entities/table-utf8.php
specifier|final
name|String
name|Samisk
init|=
literal|"< a,A< \u00E1,\u00C1< b,B< c,C"
operator|+
literal|"< \u010d,\u010c< d,D< \u0111,\u0110< e,E"
operator|+
literal|"< f,F< g,G< h,H< i,I< j,J< k,K< l,L< m,M"
operator|+
literal|"< n,N< \u014b,\u014a< o,O< p,P< r,R< s,S"
operator|+
literal|"< \u0161,\u0160< t,T< \u0167,\u0166< u,U"
operator|+
literal|"< v,V< z,Z< \u017e,\u017d"
decl_stmt|;
try|try
block|{
name|collator
operator|=
operator|new
name|RuleBasedCollator
argument_list|(
name|Samisk
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|pe
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pe
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
specifier|final
name|ULocale
name|locale
init|=
name|getLocale
argument_list|(
name|lang
argument_list|)
decl_stmt|;
name|collator
operator|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|fallback
condition|)
block|{
comment|//TODO(AR) how to disable fallback in ICU?
name|logger
operator|.
name|warn
argument_list|(
literal|"eXist-db does not yet support disabling collation fallback"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
specifier|final
name|VersionInfo
name|versionInfo
decl_stmt|;
try|try
block|{
name|versionInfo
operator|=
name|VersionInfo
operator|.
name|getInstance
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|iae
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|iae
argument_list|)
throw|;
block|}
if|if
condition|(
name|collator
operator|.
name|getVersion
argument_list|()
operator|.
name|compareTo
argument_list|(
name|versionInfo
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Requested UCA Collation version: "
operator|+
name|version
operator|+
literal|", however eXist-db only has ICU UCA: "
operator|+
name|collator
operator|.
name|getVersion
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|strength
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|strength
condition|)
block|{
case|case
literal|"identical"
case|:
comment|// the default setting
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|IDENTICAL
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"1"
case|:
case|case
literal|"primary"
case|:
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"2"
case|:
case|case
literal|"secondary"
case|:
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|SECONDARY
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"3"
case|:
case|case
literal|"tertiary"
case|:
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|TERTIARY
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"4"
case|:
case|case
literal|"quaternary"
case|:
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|QUATERNARY
argument_list|)
expr_stmt|;
break|break;
default|default:
specifier|final
name|String
name|msg
init|=
literal|"eXist-db only supports Collation strengths of 'identical', 'primary', 'secondary', 'tertiary' or 'quaternary', requested: "
operator|+
name|strength
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|maxVariable
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|maxVariable
condition|)
block|{
case|case
literal|"space"
case|:
name|collator
operator|.
name|setMaxVariable
argument_list|(
name|Collator
operator|.
name|ReorderCodes
operator|.
name|SPACE
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"punct"
case|:
name|collator
operator|.
name|setMaxVariable
argument_list|(
name|Collator
operator|.
name|ReorderCodes
operator|.
name|PUNCTUATION
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"symbol"
case|:
name|collator
operator|.
name|setMaxVariable
argument_list|(
name|Collator
operator|.
name|ReorderCodes
operator|.
name|SYMBOL
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"currency"
case|:
name|collator
operator|.
name|setMaxVariable
argument_list|(
name|Collator
operator|.
name|ReorderCodes
operator|.
name|CURRENCY
argument_list|)
expr_stmt|;
break|break;
default|default:
specifier|final
name|String
name|msg
init|=
literal|"eXist-db only supports Collation maxVariables of 'space', 'punct', 'symbol', or 'currency', requested: "
operator|+
name|maxVariable
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|alternate
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|alternate
condition|)
block|{
case|case
literal|"non-ignorable"
case|:
operator|(
operator|(
name|RuleBasedCollator
operator|)
name|collator
operator|)
operator|.
name|setAlternateHandlingShifted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"shifted"
case|:
case|case
literal|"blanked"
case|:
operator|(
operator|(
name|RuleBasedCollator
operator|)
name|collator
operator|)
operator|.
name|setAlternateHandlingShifted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
default|default:
specifier|final
name|String
name|msg
init|=
literal|"Collation alternate should be either 'non-ignorable', 'shifted' or 'blanked', but received: "
operator|+
name|caseFirst
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|backwards
condition|)
block|{
operator|(
operator|(
name|RuleBasedCollator
operator|)
name|collator
operator|)
operator|.
name|setFrenchCollation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|normalization
condition|)
block|{
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|CANONICAL_DECOMPOSITION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|NO_DECOMPOSITION
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|caseLevel
operator|&&
name|collator
operator|.
name|getStrength
argument_list|()
operator|==
name|Collator
operator|.
name|PRIMARY
condition|)
block|{
operator|(
operator|(
name|RuleBasedCollator
operator|)
name|collator
operator|)
operator|.
name|setCaseLevel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|caseFirst
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|caseFirst
condition|)
block|{
case|case
literal|"upper"
case|:
operator|(
operator|(
name|RuleBasedCollator
operator|)
name|collator
operator|)
operator|.
name|setUpperCaseFirst
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"lower"
case|:
operator|(
operator|(
name|RuleBasedCollator
operator|)
name|collator
operator|)
operator|.
name|setLowerCaseFirst
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
default|default:
specifier|final
name|String
name|msg
init|=
literal|"Collation case first should be either 'upper' or 'lower', but received: "
operator|+
name|caseFirst
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|numeric
condition|)
block|{
operator|(
operator|(
name|RuleBasedCollator
operator|)
name|collator
operator|)
operator|.
name|setNumericCollation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reorder
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|reorderCodes
index|[]
init|=
name|reorder
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|icuCollatorReorderCodes
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|reorderCodes
argument_list|)
operator|.
name|map
argument_list|(
name|Collations
operator|::
name|toICUCollatorReorderCode
argument_list|)
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|i
operator|>
operator|-
literal|1
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|icuCollatorReorderCodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|int
index|[]
name|codes
init|=
operator|new
name|int
index|[
name|icuCollatorReorderCodes
operator|.
name|size
argument_list|()
index|]
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
name|codes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|codes
index|[
name|i
index|]
operator|=
name|icuCollatorReorderCodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|collator
operator|.
name|setReorderCodes
argument_list|(
name|codes
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|decomposition
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|decomposition
condition|)
block|{
case|case
literal|"none"
case|:
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|NO_DECOMPOSITION
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"full"
case|:
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|FULL_DECOMPOSITION
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"standard"
case|:
case|case
literal|""
case|:
comment|// the default setting
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|CANONICAL_DECOMPOSITION
argument_list|)
expr_stmt|;
break|break;
default|default:
specifier|final
name|String
name|msg
init|=
literal|"Collation decomposition should be either 'none', 'full' or 'standard', but received: "
operator|+
name|decomposition
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
return|return
name|collator
return|;
block|}
specifier|private
specifier|static
name|int
name|toICUCollatorReorderCode
parameter_list|(
specifier|final
name|String
name|reorderCode
parameter_list|)
block|{
switch|switch
condition|(
name|reorderCode
operator|.
name|toLowerCase
argument_list|()
condition|)
block|{
case|case
literal|"default"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|DEFAULT
return|;
case|case
literal|"none"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|NONE
return|;
case|case
literal|"others"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|OTHERS
return|;
case|case
literal|"space"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|SPACE
return|;
case|case
literal|"first"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|FIRST
return|;
case|case
literal|"punctuation"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|PUNCTUATION
return|;
case|case
literal|"symbol"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|SYMBOL
return|;
case|case
literal|"currency"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|CURRENCY
return|;
case|case
literal|"digit"
case|:
return|return
name|Collator
operator|.
name|ReorderCodes
operator|.
name|DIGIT
return|;
default|default:
name|logger
operator|.
name|warn
argument_list|(
literal|"eXist-db does not support the collation reorderCode: "
operator|+
name|reorderCode
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * Get a locale for the provided language.      *      * @param lang The language      *      * @return The locale      */
specifier|private
specifier|static
name|ULocale
name|getLocale
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|String
name|lang
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
return|return
name|ULocale
operator|.
name|getDefault
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|String
index|[]
name|components
init|=
name|lang
operator|.
name|split
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|components
operator|.
name|length
condition|)
block|{
case|case
literal|3
case|:
return|return
operator|new
name|ULocale
argument_list|(
name|components
index|[
literal|0
index|]
argument_list|,
name|components
index|[
literal|1
index|]
argument_list|,
name|components
index|[
literal|2
index|]
argument_list|)
return|;
case|case
literal|2
case|:
return|return
operator|new
name|ULocale
argument_list|(
name|components
index|[
literal|0
index|]
argument_list|,
name|components
index|[
literal|1
index|]
argument_list|)
return|;
case|case
literal|1
case|:
return|return
operator|new
name|ULocale
argument_list|(
name|components
index|[
literal|0
index|]
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unrecognized lang="
operator|+
name|lang
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

