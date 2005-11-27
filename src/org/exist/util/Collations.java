begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Utility methods dealing with collations.  *   * @author wolf  */
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
name|LOG
init|=
name|Logger
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
literal|"http://www.w3.org/2004/07/xpath-functions/collation/codepoint"
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
comment|/**      * Get a {@link Comparator}from the specified URI.      *       * The original code is from saxon (@linkplain http://saxon.sf.net).      *       * @param uri      * @return      * @throws XPathException      */
specifier|public
specifier|final
specifier|static
name|Collator
name|getCollationFromURI
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
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
init|=
literal|null
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
name|URISyntaxException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|query
init|=
name|u
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|String
name|strength
init|=
literal|null
decl_stmt|;
comment|/*              * Check if the db broker is configured to be case insensitive. If              * yes, we assume "primary" strength unless the user specified              * something different.              *               * TODO: bad idea: using primary strength as default also ignores              * German Umlaute.              */
comment|// if(!context.getBroker().isCaseSensitive())
comment|// strength = "primary";
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
name|strength
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading collation: "
operator|+
name|query
argument_list|)
expr_stmt|;
name|String
name|lang
init|=
literal|null
decl_stmt|;
name|String
name|decomposition
init|=
literal|null
decl_stmt|;
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
name|String
name|param
init|=
name|queryTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
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
name|kw
operator|.
name|equals
argument_list|(
literal|"lang"
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
name|kw
operator|.
name|equals
argument_list|(
literal|"strength"
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
name|kw
operator|.
name|equals
argument_list|(
literal|"decomposition"
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
name|Class
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The specified collator class is not a subclass of java.text.Collator"
argument_list|)
throw|;
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
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
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
else|else
comment|// unknown collation
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
specifier|static
name|boolean
name|equals
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|String
name|s1
parameter_list|,
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
return|return
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
return|;
else|else
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
specifier|public
specifier|final
specifier|static
name|int
name|compare
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|String
name|s1
parameter_list|,
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
return|return
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
return|;
else|else
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
specifier|public
specifier|final
specifier|static
name|boolean
name|startsWith
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|String
name|s1
parameter_list|,
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
return|return
name|s1
operator|.
name|startsWith
argument_list|(
name|s2
argument_list|)
return|;
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
specifier|final
specifier|static
name|boolean
name|endsWith
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|String
name|s1
parameter_list|,
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
return|return
name|s1
operator|.
name|endsWith
argument_list|(
name|s2
argument_list|)
return|;
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
specifier|final
specifier|static
name|boolean
name|contains
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|String
name|s1
parameter_list|,
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
return|return
name|s1
operator|.
name|indexOf
argument_list|(
name|s2
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
return|;
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
specifier|final
specifier|static
name|int
name|indexOf
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|String
name|s1
parameter_list|,
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
return|return
name|s1
operator|.
name|indexOf
argument_list|(
name|s2
argument_list|)
return|;
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
specifier|final
name|boolean
name|found
init|=
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
decl_stmt|;
if|if
condition|(
name|found
condition|)
return|return
name|offsets
index|[
literal|0
index|]
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|private
specifier|final
specifier|static
name|boolean
name|collationStartsWith
parameter_list|(
name|CollationElementIterator
name|s0
parameter_list|,
name|CollationElementIterator
name|s1
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|e1
init|=
name|s1
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e1
operator|==
name|CollationElementIterator
operator|.
name|NULLORDER
condition|)
block|{
return|return
literal|true
return|;
block|}
name|int
name|e0
init|=
name|s0
operator|.
name|next
argument_list|()
decl_stmt|;
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
block|}
specifier|private
specifier|final
specifier|static
name|boolean
name|collationContains
parameter_list|(
name|CollationElementIterator
name|s0
parameter_list|,
name|CollationElementIterator
name|s1
parameter_list|,
name|int
index|[]
name|offsets
parameter_list|,
name|boolean
name|endsWith
parameter_list|)
block|{
name|int
name|e1
init|=
name|s1
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e1
operator|==
name|CollationElementIterator
operator|.
name|NULLORDER
condition|)
block|{
return|return
literal|true
return|;
block|}
name|int
name|e0
init|=
name|CollationElementIterator
operator|.
name|NULLORDER
decl_stmt|;
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
name|e0
operator|=
name|s0
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|e0
operator|==
name|CollationElementIterator
operator|.
name|NULLORDER
condition|)
block|{
comment|// hit the end, no match
return|return
literal|false
return|;
block|}
block|}
comment|// matched first character, note the position of the possible match
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
operator|!
name|endsWith
condition|)
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
else|else
block|{
comment|// operation == ENDSWITH
if|if
condition|(
name|s0
operator|.
name|next
argument_list|()
operator|==
name|CollationElementIterator
operator|.
name|NULLORDER
condition|)
block|{
comment|// the match is at the end
return|return
literal|true
return|;
block|}
comment|// else ignore this match and keep looking
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
name|CollationElementIterator
operator|.
name|NULLORDER
expr_stmt|;
name|e1
operator|=
name|s1
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// loop round to try again
block|}
block|}
comment|/**      * @param lang      * @param strength      * @param decomposition      * @return      */
specifier|private
specifier|static
name|Collator
name|getCollationFromParams
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|strength
parameter_list|,
name|String
name|decomposition
parameter_list|)
throws|throws
name|XPathException
block|{
name|Collator
name|collator
init|=
literal|null
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
name|lang
operator|.
name|equals
argument_list|(
literal|"sme-SE"
argument_list|)
condition|)
block|{
comment|// Collation rules contained in a String object.
comment|// Codes for the representation of names of languages:
comment|// http://www.loc.gov/standards/iso639-2/englangn.html
comment|// UTF-8 characters from:
comment|// http://chouette.info/entities/table-utf8.php
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
name|Locale
name|locale
init|=
name|getLocale
argument_list|(
name|lang
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using locale: "
operator|+
name|locale
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"secondary"
operator|.
name|equals
argument_list|(
name|strength
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|SECONDARY
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"tertiary"
operator|.
name|equals
argument_list|(
name|strength
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|TERTIARY
argument_list|)
expr_stmt|;
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
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Collation strength should be either 'primary', 'secondary', 'tertiary' or 'identical"
argument_list|)
throw|;
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
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|NO_DECOMPOSITION
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"full"
operator|.
name|equals
argument_list|(
name|decomposition
argument_list|)
condition|)
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|FULL_DECOMPOSITION
argument_list|)
expr_stmt|;
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
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|CANONICAL_DECOMPOSITION
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Collation decomposition should be either 'none', 'full' or 'standard"
argument_list|)
throw|;
block|}
return|return
name|collator
return|;
block|}
comment|/**      * @param lang      * @return      */
specifier|private
specifier|static
name|Locale
name|getLocale
parameter_list|(
name|String
name|lang
parameter_list|)
block|{
name|int
name|dash
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
name|dash
operator|<
literal|0
condition|)
return|return
operator|new
name|Locale
argument_list|(
name|lang
argument_list|)
return|;
else|else
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
name|dash
argument_list|)
argument_list|,
name|lang
operator|.
name|substring
argument_list|(
name|dash
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

