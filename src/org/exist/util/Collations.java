begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|CollationElementIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|RuleBasedCollator
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
name|Locale
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
comment|/**  * Utility methods dealing with collations.  *  * @author wolf  */
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
comment|/**      * The default unicode codepoint collation URI as defined by the XQuery      * spec.      */
specifier|public
specifier|final
specifier|static
name|String
name|CODEPOINT
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
name|getCollationFromParams
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|lang
init|=
literal|null
decl_stmt|;
name|String
name|strength
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
if|if
condition|(
literal|"lang"
operator|.
name|equals
argument_list|(
name|kw
argument_list|)
condition|)
block|{
name|lang
operator|=
name|val
expr_stmt|;
block|}
if|else if
condition|(
literal|"strength"
operator|.
name|equals
argument_list|(
name|kw
argument_list|)
condition|)
block|{
name|strength
operator|=
name|val
expr_stmt|;
block|}
if|else if
condition|(
literal|"decomposition"
operator|.
name|equals
argument_list|(
name|kw
argument_list|)
condition|)
block|{
name|decomposition
operator|=
name|val
expr_stmt|;
block|}
block|}
block|}
return|return
name|getCollationFromParams
argument_list|(
name|lang
argument_list|,
name|strength
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
comment|// java.text.RuleBasedCollator
name|uri
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|"java:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
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
name|uri
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
name|logger
operator|.
name|error
argument_list|(
literal|"The specified collator class is not a subclass of java.text.Collator"
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
literal|"The specified collator class is not a subclass of java.text.Collator"
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
name|logger
operator|.
name|error
argument_list|(
literal|"The specified collator class "
operator|+
name|uri
operator|+
literal|" could not be found"
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
literal|"The specified collator class "
operator|+
name|uri
operator|+
literal|" could not be found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|CODEPOINT
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
name|logger
operator|.
name|error
argument_list|(
literal|"Unknown collation : '"
operator|+
name|uri
operator|+
literal|"'"
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
literal|"Unknown collation : '"
operator|+
name|uri
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
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
name|RuleBasedCollator
name|rbc
init|=
operator|(
name|RuleBasedCollator
operator|)
name|collator
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i1
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i2
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s2
argument_list|)
decl_stmt|;
return|return
name|collationStartsWith
argument_list|(
name|i1
argument_list|,
name|i2
argument_list|)
return|;
block|}
block|}
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
name|RuleBasedCollator
name|rbc
init|=
operator|(
name|RuleBasedCollator
operator|)
name|collator
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i1
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i2
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s2
argument_list|)
decl_stmt|;
return|return
name|collationContains
argument_list|(
name|i1
argument_list|,
name|i2
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
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
name|RuleBasedCollator
name|rbc
init|=
operator|(
name|RuleBasedCollator
operator|)
name|collator
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i1
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i2
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s2
argument_list|)
decl_stmt|;
return|return
name|collationContains
argument_list|(
name|i1
argument_list|,
name|i2
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
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
name|int
name|offsets
index|[]
init|=
operator|new
name|int
index|[
literal|2
index|]
decl_stmt|;
specifier|final
name|RuleBasedCollator
name|rbc
init|=
operator|(
name|RuleBasedCollator
operator|)
name|collator
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i1
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|CollationElementIterator
name|i2
init|=
name|rbc
operator|.
name|getCollationElementIterator
argument_list|(
name|s2
argument_list|)
decl_stmt|;
if|if
condition|(
name|collationContains
argument_list|(
name|i1
argument_list|,
name|i2
argument_list|,
name|offsets
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
name|offsets
index|[
literal|0
index|]
return|;
block|}
else|else
block|{
return|return
name|Constants
operator|.
name|STRING_NOT_FOUND
return|;
block|}
block|}
block|}
specifier|private
specifier|static
name|boolean
name|collationStartsWith
parameter_list|(
specifier|final
name|CollationElementIterator
name|s0
parameter_list|,
specifier|final
name|CollationElementIterator
name|s1
parameter_list|)
block|{
comment|//Copied from Saxon
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|e0
decl_stmt|,
name|e1
decl_stmt|;
do|do
block|{
name|e1
operator|=
name|s1
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|e1
operator|==
literal|0
condition|)
do|;
if|if
condition|(
name|e1
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
do|do
block|{
name|e0
operator|=
name|s0
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|e0
operator|==
literal|0
condition|)
do|;
if|if
condition|(
name|e0
operator|!=
name|e1
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|//End of copy
block|}
specifier|private
specifier|static
name|boolean
name|collationContains
parameter_list|(
specifier|final
name|CollationElementIterator
name|s0
parameter_list|,
specifier|final
name|CollationElementIterator
name|s1
parameter_list|,
specifier|final
name|int
index|[]
name|offsets
parameter_list|,
specifier|final
name|boolean
name|matchAtEnd
parameter_list|)
block|{
comment|//Copy from Saxon
name|int
name|e0
decl_stmt|,
name|e1
decl_stmt|;
do|do
block|{
name|e1
operator|=
name|s1
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|e1
operator|==
literal|0
condition|)
do|;
if|if
condition|(
name|e1
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
name|e0
operator|=
operator|-
literal|1
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// scan the first string to find a matching character
while|while
condition|(
name|e0
operator|!=
name|e1
condition|)
block|{
do|do
block|{
name|e0
operator|=
name|s0
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|e0
operator|==
literal|0
condition|)
do|;
if|if
condition|(
name|e0
operator|==
operator|-
literal|1
condition|)
block|{
comment|// hit the end, no match
return|return
literal|false
return|;
block|}
block|}
comment|// matched first character, note the position of the possible match
specifier|final
name|int
name|start
init|=
name|s0
operator|.
name|getOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|collationStartsWith
argument_list|(
name|s0
argument_list|,
name|s1
argument_list|)
condition|)
block|{
if|if
condition|(
name|matchAtEnd
condition|)
block|{
do|do
block|{
name|e0
operator|=
name|s0
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|e0
operator|==
literal|0
condition|)
do|;
if|if
condition|(
name|e0
operator|==
operator|-
literal|1
condition|)
block|{
comment|// the match is at the end
return|return
literal|true
return|;
block|}
comment|// else ignore this match and keep looking
block|}
else|else
block|{
if|if
condition|(
name|offsets
operator|!=
literal|null
condition|)
block|{
name|offsets
index|[
literal|0
index|]
operator|=
name|start
operator|-
literal|1
expr_stmt|;
name|offsets
index|[
literal|1
index|]
operator|=
name|s0
operator|.
name|getOffset
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
comment|// reset the position and try again
name|s0
operator|.
name|setOffset
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// workaround for a difference between JDK 1.4.0 and JDK 1.4.1
if|if
condition|(
name|s0
operator|.
name|getOffset
argument_list|()
operator|!=
name|start
condition|)
block|{
comment|// JDK 1.4.0 takes this path
name|s0
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|s1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|e0
operator|=
operator|-
literal|1
expr_stmt|;
do|do
block|{
name|e1
operator|=
name|s1
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|e1
operator|==
literal|0
condition|)
do|;
comment|// loop round to try again
block|}
comment|//End of copy
block|}
comment|/**      * Get a Collator with the provided settings.      *      * @param lang The language      * @param strength The strength      * @param decomposition The decomposition      * @return The collator      */
specifier|private
specifier|static
name|Collator
name|getCollationFromParams
parameter_list|(
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
name|strength
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
name|lang
operator|==
literal|null
condition|)
block|{
name|collator
operator|=
name|Collator
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
if|else if
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
name|ParseException
name|pe
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
specifier|final
name|Locale
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
name|strength
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"primary"
operator|.
name|equals
argument_list|(
name|strength
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"secondary"
operator|.
name|equals
argument_list|(
name|strength
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|SECONDARY
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"tertiary"
operator|.
name|equals
argument_list|(
name|strength
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|TERTIARY
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|strength
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
literal|"identical"
operator|.
name|equals
argument_list|(
name|strength
argument_list|)
condition|)
block|{
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
block|}
else|else
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Collation strength should be either 'primary', 'secondary', 'tertiary' or 'identical"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Collation strength should be either 'primary', 'secondary', 'tertiary' or 'identical"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|decomposition
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|decomposition
argument_list|)
condition|)
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
if|else if
condition|(
literal|"full"
operator|.
name|equals
argument_list|(
name|decomposition
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|FULL_DECOMPOSITION
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|decomposition
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
literal|"standard"
operator|.
name|equals
argument_list|(
name|decomposition
argument_list|)
condition|)
comment|// the default setting
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
name|logger
operator|.
name|error
argument_list|(
literal|"Collation decomposition should be either 'none', 'full' or 'standard"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Collation decomposition should be either 'none', 'full' or 'standard"
argument_list|)
throw|;
block|}
block|}
return|return
name|collator
return|;
block|}
comment|/**      * Get a locale for the provided language.      *      * @param lang The language      *      * @return The locale      */
specifier|private
specifier|static
name|Locale
name|getLocale
parameter_list|(
specifier|final
name|String
name|lang
parameter_list|)
block|{
specifier|final
name|int
name|dashPos
init|=
name|lang
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
if|if
condition|(
name|dashPos
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
return|return
operator|new
name|Locale
argument_list|(
name|lang
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Locale
argument_list|(
name|lang
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dashPos
argument_list|)
argument_list|,
name|lang
operator|.
name|substring
argument_list|(
name|dashPos
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

