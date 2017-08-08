begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2014 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|interpreter
operator|.
name|Context
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
name|util
operator|.
name|XMLChar
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
name|xml
operator|.
name|XMLConstants
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

begin_comment
comment|/**  * Represents a QName, consisting of a local name, a namespace URI and a prefix.  *  * @author Wolfgang<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|QName
implements|implements
name|Comparable
argument_list|<
name|QName
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|String
name|WILDCARD
init|=
literal|"*"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|char
name|COLON
init|=
literal|':'
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|EMPTY_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|""
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|DOCUMENT_QNAME
init|=
name|EMPTY_QNAME
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|TEXT_QNAME
init|=
name|EMPTY_QNAME
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|COMMENT_QNAME
init|=
name|EMPTY_QNAME
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|DOCTYPE_QNAME
init|=
name|EMPTY_QNAME
decl_stmt|;
specifier|private
specifier|final
name|String
name|localPart
decl_stmt|;
specifier|private
specifier|final
name|String
name|namespaceURI
decl_stmt|;
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
comment|//TODO : use ElementValue.UNKNOWN and type explicitly ?
specifier|private
specifier|final
name|byte
name|nameType
decl_stmt|;
comment|// = ElementValue.ELEMENT;
specifier|public
name|QName
parameter_list|(
specifier|final
name|String
name|localPart
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|byte
name|nameType
parameter_list|)
block|{
name|this
operator|.
name|localPart
operator|=
name|localPart
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|namespaceURI
operator|=
name|XMLConstants
operator|.
name|NULL_NS_URI
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|namespaceURI
operator|=
name|namespaceURI
expr_stmt|;
block|}
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|nameType
operator|=
name|nameType
expr_stmt|;
block|}
comment|/**      * Construct a QName. The prefix might be null for the default namespace or if no prefix       * has been defined for the QName. The namespace URI should be set to the empty       * string, if no namespace URI is defined.      *      * @param localPart      * @param namespaceURI      * @param prefix      */
specifier|public
name|QName
parameter_list|(
specifier|final
name|String
name|localPart
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|this
argument_list|(
name|localPart
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|,
name|ElementValue
operator|.
name|ELEMENT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QName
parameter_list|(
specifier|final
name|String
name|localPart
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|byte
name|nameType
parameter_list|)
block|{
name|this
argument_list|(
name|localPart
argument_list|,
name|namespaceURI
argument_list|,
literal|null
argument_list|,
name|nameType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QName
parameter_list|(
specifier|final
name|String
name|localPart
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|)
block|{
name|this
argument_list|(
name|localPart
argument_list|,
name|namespaceURI
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QName
parameter_list|(
specifier|final
name|QName
name|other
parameter_list|,
specifier|final
name|byte
name|nameType
parameter_list|)
block|{
name|this
argument_list|(
name|other
operator|.
name|localPart
argument_list|,
name|other
operator|.
name|namespaceURI
argument_list|,
name|other
operator|.
name|prefix
argument_list|,
name|nameType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QName
parameter_list|(
specifier|final
name|QName
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
operator|.
name|localPart
argument_list|,
name|other
operator|.
name|namespaceURI
argument_list|,
name|other
operator|.
name|prefix
argument_list|,
name|other
operator|.
name|nameType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|extractLocalName
argument_list|(
name|name
argument_list|)
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|,
name|extractPrefix
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getLocalPart
parameter_list|()
block|{
return|return
name|localPart
return|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|namespaceURI
return|;
block|}
comment|/**      * Returns true if the QName defines a non-default namespace      *      */
specifier|public
name|boolean
name|hasNamespace
parameter_list|()
block|{
return|return
operator|!
name|namespaceURI
operator|.
name|equals
argument_list|(
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
specifier|public
name|byte
name|getNameType
parameter_list|()
block|{
return|return
name|nameType
return|;
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|prefix
operator|+
name|COLON
operator|+
name|localPart
return|;
block|}
return|return
name|localPart
return|;
block|}
comment|/**      * (deprecated) : use for debugging purpose only,      * use getStringValue() for production      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|//TODO : remove this copy of getStringValue()
return|return
name|getStringValue
argument_list|()
return|;
comment|//TODO : replace by something like this
comment|/*         if (prefix != null&& prefix.length()> 0)             return prefix + COLON + localPart;         if (hasNamespace()) {             if (prefix != null&& prefix.length()> 0)                 return "{" + namespaceURI + "}" + prefix + COLON + localPart;             return "{" + namespaceURI + "}" + localPart;         } else              return localPart;         */
block|}
comment|/**      * Compares two QNames by comparing namespace URI      * and local names. The prefixes are not relevant.      *      * @see java.lang.Comparable#compareTo(java.lang.Object)      */
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|QName
name|other
parameter_list|)
block|{
specifier|final
name|int
name|c
init|=
name|namespaceURI
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|namespaceURI
argument_list|)
decl_stmt|;
return|return
name|c
operator|==
name|Constants
operator|.
name|EQUAL
condition|?
name|localPart
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|localPart
argument_list|)
else|:
name|c
return|;
block|}
comment|/**      * Checks two QNames for equality. Two QNames are equal      * if their namespace URIs and local names are equal.      *      * @see java.lang.Object#equals(java.lang.Object)      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|QName
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
specifier|final
name|QName
name|qnOther
init|=
operator|(
name|QName
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|namespaceURI
operator|.
name|equals
argument_list|(
name|qnOther
operator|.
name|namespaceURI
argument_list|)
operator|&&
name|this
operator|.
name|localPart
operator|.
name|equals
argument_list|(
name|qnOther
operator|.
name|localPart
argument_list|)
return|;
block|}
block|}
comment|/**      * Determines whether two QNames match      * similar to {@link #equals(Object)} but      * incorporates wildcards on either side      *      * @param qnOther Another QName to compare against this      * @return true if two qnames match      */
specifier|public
name|boolean
name|matches
parameter_list|(
specifier|final
name|QName
name|qnOther
parameter_list|)
block|{
if|if
condition|(
name|equals
argument_list|(
name|qnOther
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
name|this
operator|==
name|WildcardQName
operator|.
name|instance
operator|||
name|qnOther
operator|==
name|WildcardQName
operator|.
name|instance
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
operator|(
name|localPart
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|||
name|qnOther
operator|.
name|localPart
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|)
operator|&&
name|this
operator|.
name|namespaceURI
operator|.
name|equals
argument_list|(
name|qnOther
operator|.
name|namespaceURI
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
operator|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|||
name|qnOther
operator|.
name|namespaceURI
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|)
operator|&&
name|this
operator|.
name|localPart
operator|.
name|equals
argument_list|(
name|qnOther
operator|.
name|localPart
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
operator|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|&&
name|localPart
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|)
operator|||
operator|(
name|qnOther
operator|.
name|namespaceURI
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|||
name|qnOther
operator|.
name|localPart
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/* (non-Javadoc)      * @see java.lang.Object#hashCode()      */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|namespaceURI
operator|.
name|hashCode
argument_list|()
operator|^
name|localPart
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|toJavaQName
parameter_list|()
block|{
return|return
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
name|namespaceURI
argument_list|,
name|localPart
argument_list|,
name|prefix
operator|==
literal|null
condition|?
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
else|:
name|prefix
argument_list|)
return|;
block|}
comment|/**      * Extract the prefix from a QName string.      *      * @param qname      * @return the prefix, if found      * @exception IllegalArgumentException if the qname starts with a leading :      */
specifier|public
specifier|static
name|String
name|extractPrefix
parameter_list|(
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|int
name|p
init|=
name|qname
operator|.
name|indexOf
argument_list|(
name|COLON
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|p
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: starts with a :"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|qname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: starts with a digit"
argument_list|)
throw|;
block|}
return|return
name|qname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
return|;
block|}
comment|/**      * Extract the local name from a QName string.      *      * @param qname      * @exception IllegalArgumentException if the qname starts with a leading : or ends with a :      */
specifier|public
specifier|static
name|String
name|extractLocalName
parameter_list|(
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|int
name|p
init|=
name|qname
operator|.
name|indexOf
argument_list|(
name|COLON
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
return|return
name|qname
return|;
block|}
if|else if
condition|(
name|p
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: starts with a ':'"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|p
operator|==
name|qname
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: ends with a ':'"
argument_list|)
throw|;
block|}
if|else if
condition|(
operator|!
name|isQName
argument_list|(
name|qname
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: '"
operator|+
name|qname
operator|+
literal|"' not a valid local name."
argument_list|)
throw|;
block|}
return|return
name|qname
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**      * Extract a QName from a namespace and qualified name string      *      * @param namespaceURI A namespace URI      * @param qname A qualified named as a string e.g. 'my:name' or a local name e.g. 'name'      *      * @return The QName      */
specifier|public
specifier|static
name|QName
name|parse
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
block|{
specifier|final
name|int
name|p
init|=
name|qname
operator|.
name|indexOf
argument_list|(
name|COLON
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
return|return
operator|new
name|QName
argument_list|(
name|qname
argument_list|,
name|namespaceURI
argument_list|)
return|;
block|}
if|else if
condition|(
operator|!
name|isQName
argument_list|(
name|qname
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: '"
operator|+
name|qname
operator|+
literal|"'"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
operator|new
name|QName
argument_list|(
name|qname
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
argument_list|,
name|namespaceURI
argument_list|,
name|qname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
specifier|static
name|Pattern
name|ptnClarkNotation
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\{([^&{}]*)\\}([^&{}:]+)"
argument_list|)
decl_stmt|;
comment|/**      * Parses the given string into a QName. The method uses context to look up      * a namespace URI for an existing prefix.      *      * @param context      * @param qname The QName may be either in Clark Notation      *              e.g. `{namespace}local-part` or XDM literal qname form e.g. `prefix:local-part`.      * @param defaultNS the default namespace to use if no namespace prefix is present.      * @return QName      * @exception IllegalArgumentException if no namespace URI is mapped to the prefix      */
specifier|public
specifier|static
name|QName
name|parse
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|,
specifier|final
name|String
name|defaultNS
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// quick test if qname is in clark notation
if|if
condition|(
name|qname
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|qname
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'{'
condition|)
block|{
specifier|final
name|Matcher
name|clarkNotation
init|=
name|ptnClarkNotation
operator|.
name|matcher
argument_list|(
name|qname
argument_list|)
decl_stmt|;
comment|// more expensive check
if|if
condition|(
name|clarkNotation
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|//parse as clark notation
specifier|final
name|String
name|ns
init|=
name|clarkNotation
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|localPart
init|=
name|clarkNotation
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
operator|new
name|QName
argument_list|(
name|localPart
argument_list|,
name|ns
argument_list|)
return|;
block|}
block|}
specifier|final
name|String
name|prefix
init|=
name|extractPrefix
argument_list|(
name|qname
argument_list|)
decl_stmt|;
name|String
name|namespaceURI
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"No namespace defined for prefix "
operator|+
name|prefix
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|namespaceURI
operator|=
name|defaultNS
expr_stmt|;
block|}
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|XMLConstants
operator|.
name|NULL_NS_URI
expr_stmt|;
block|}
return|return
operator|new
name|QName
argument_list|(
name|extractLocalName
argument_list|(
name|qname
argument_list|)
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
return|;
block|}
comment|/**      * Parses the given string into a QName. The method uses context to look up      * a namespace URI for an optional existing prefix.      *      * This method uses the default element namespace for qnames without prefix.      *      * @param context      * @param qname The QName may be either in Clark Notation      *              e.g. `{namespace}local-part` or XDM literal qname form      *              e.g. `prefix:local-part` or `local-part`.      * @exception IllegalArgumentException if no namespace URI is mapped to the prefix      */
specifier|public
specifier|static
name|QName
name|parse
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|parse
argument_list|(
name|context
argument_list|,
name|qname
argument_list|,
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Determines if the local name and prefix of this QName are valid NCNames      *      * @param allowWildcards true if we should permit wildcards to be considered valid (not actually a valid NCName),      *     false otherwise for strict NCName adherence.      *      * @throws XPathException if the QName is invalid      */
specifier|public
specifier|final
name|void
name|isValid
parameter_list|(
specifier|final
name|boolean
name|allowWildcards
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|allowWildcards
operator|&&
name|this
operator|==
name|QName
operator|.
name|WildcardQName
operator|.
name|getInstance
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|(
operator|!
operator|(
name|this
operator|instanceof
name|WildcardLocalPartQName
operator|&&
name|allowWildcards
operator|)
operator|)
operator|&&
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|localPart
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Invalid localPart '"
operator|+
name|localPart
operator|+
literal|"' for QName '"
operator|+
name|this
operator|+
literal|"'."
argument_list|)
throw|;
block|}
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Invalid prefix '"
operator|+
name|prefix
operator|+
literal|"' for QName '"
operator|+
name|this
operator|+
literal|"'."
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
specifier|final
name|boolean
name|isQName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
name|COLON
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
return|return
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|name
argument_list|)
return|;
block|}
if|else if
condition|(
name|colon
operator|==
literal|0
operator|||
name|colon
operator|==
name|name
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|static
name|QName
name|fromJavaQName
parameter_list|(
specifier|final
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|jQn
parameter_list|)
block|{
return|return
operator|new
name|QName
argument_list|(
name|jQn
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|jQn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|jQn
operator|.
name|getPrefix
argument_list|()
argument_list|)
return|;
block|}
specifier|public
interface|interface
name|PartialQName
block|{}
specifier|public
specifier|static
class|class
name|WildcardQName
extends|extends
name|QName
implements|implements
name|PartialQName
block|{
specifier|private
specifier|final
specifier|static
name|WildcardQName
name|instance
init|=
operator|new
name|WildcardQName
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|WildcardQName
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
specifier|private
name|WildcardQName
parameter_list|()
block|{
name|super
argument_list|(
name|WILDCARD
argument_list|,
name|WILDCARD
argument_list|,
name|WILDCARD
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|WildcardNamespaceURIQName
extends|extends
name|QName
implements|implements
name|PartialQName
block|{
specifier|public
name|WildcardNamespaceURIQName
parameter_list|(
specifier|final
name|String
name|localPart
parameter_list|)
block|{
name|super
argument_list|(
name|localPart
argument_list|,
name|WILDCARD
argument_list|)
expr_stmt|;
block|}
specifier|public
name|WildcardNamespaceURIQName
parameter_list|(
specifier|final
name|String
name|localPart
parameter_list|,
specifier|final
name|byte
name|nameType
parameter_list|)
block|{
name|super
argument_list|(
name|localPart
argument_list|,
name|WILDCARD
argument_list|,
name|nameType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|WildcardLocalPartQName
extends|extends
name|QName
implements|implements
name|PartialQName
block|{
specifier|public
name|WildcardLocalPartQName
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|)
block|{
name|super
argument_list|(
name|WILDCARD
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
specifier|public
name|WildcardLocalPartQName
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|byte
name|nameType
parameter_list|)
block|{
name|super
argument_list|(
name|WILDCARD
argument_list|,
name|namespaceURI
argument_list|,
name|nameType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|WildcardLocalPartQName
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|WILDCARD
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/**          * Parses the given prefix into a WildcardLocalPartQName. The method uses context to look up          * a namespace URI for an existing prefix.          *          * @param context          * @param prefix The namepspace prefix          * @param defaultNS the default namespace to use if no namespace prefix is present.          * @return WildcardLocalPartQName          * @exception IllegalArgumentException if no namespace URI is mapped to the prefix          */
specifier|public
specifier|static
name|WildcardLocalPartQName
name|parseFromPrefix
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|defaultNS
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|namespaceURI
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"No namespace defined for prefix "
operator|+
name|prefix
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|namespaceURI
operator|=
name|defaultNS
expr_stmt|;
block|}
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|XMLConstants
operator|.
name|NULL_NS_URI
expr_stmt|;
block|}
return|return
operator|new
name|WildcardLocalPartQName
argument_list|(
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
return|;
block|}
comment|/**          * Parses the given prefix into a WildcardLocalPartQName. The method uses context to look up          * a namespace URI for an existing prefix.          *          * @param context          * @param prefix The namepspace prefix          * @return WildcardLocalPartQName          * @exception IllegalArgumentException if no namespace URI is mapped to the prefix          */
specifier|public
specifier|static
name|WildcardLocalPartQName
name|parseFromPrefix
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|parseFromPrefix
argument_list|(
name|context
argument_list|,
name|prefix
argument_list|,
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

