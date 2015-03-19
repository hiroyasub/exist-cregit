begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension GetConnectionFunction  *  Copyright (C) 2008-09 Adam Retter<adam@exist-db.org>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|jndi
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingEnumeration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|BasicAttributes
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchControls
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchResult
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
name|dom
operator|.
name|memtree
operator|.
name|MemTreeBuilder
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
name|BasicFunction
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|IntegerValue
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
name|NodeValue
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
comment|/**  * eXist JNDI Module Extension SearchFunction  *   * Search a JNDI Directory  *   * @author Andrzej Taramina<andrzej@chaeron.com>  * @serial 2008-12-02  * @version 1.0  *   * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext,  *      org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|SearchFunction
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SearchFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DSML_NAMESPACE
init|=
literal|"http://www.dsml.org/DSML"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DSML_PREFIX
init|=
literal|"dsml"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"search"
argument_list|,
name|JNDIModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|JNDIModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Searches a JNDI Directory by attributes."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"directory-context"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The directory context handle from a jndi:get-dir-context() call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dn"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The Distinguished Name"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"search-attributes"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The search attributes in the form<attributes><attribute name=\"\" value=\"\"/></attributes>."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the search results in DSML format"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"search"
argument_list|,
name|JNDIModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|JNDIModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Searches a JNDI Directory by filter."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"directory-context"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The directory context handle from a jndi:get-dir-context() call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dn"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The Distinguished Name"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"filter"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The filter.  The format and interpretation of filter follows RFC 2254 with the following interpretations for \'attr\' and \'value\'  mentioned in the RFC. \'attr\' is the attribute's identifier. \'value\' is the string represention the attribute's value. The translation of this string representation into the attribute's value is directory-specific. "
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"scope"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The scope, which has a value of 'object', 'onelevel' or 'subtree'"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the search results in DSML format"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * SearchFunction Constructor 	 *  	 * @param context 	The Context of the calling XQuery 	 */
specifier|public
name|SearchFunction
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
comment|/** 	 * evaluate the call to the xquery search() function, it is really 	 * the main entry point of this class 	 *  	 * @param args				arguments from the get-connection() function call 	 * @param contextSequence 	the Context Sequence to operate on (not used here internally!) 	 * @return 					A xs:long representing a handle to the connection 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], 	 *      org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|xmlResult
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
comment|// Was context handle or DN specified?
if|if
condition|(
operator|!
operator|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|!
operator|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|String
name|dn
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|ctxID
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|DirContext
name|ctx
init|=
operator|(
name|DirContext
operator|)
name|JNDIModule
operator|.
name|retrieveJNDIContext
argument_list|(
name|context
argument_list|,
name|ctxID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"jndi:search() - Invalid JNDI context handle provided: "
operator|+
name|ctxID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NamingEnumeration
argument_list|<
name|SearchResult
argument_list|>
name|results
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
comment|// Attributes search
name|BasicAttributes
name|attributes
init|=
name|JNDIModule
operator|.
name|parseAttributes
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|results
operator|=
name|ctx
operator|.
name|search
argument_list|(
name|dn
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Filter search
name|int
name|scopeCode
init|=
literal|0
decl_stmt|;
name|String
name|filter
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|scope
init|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|scope
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"object"
argument_list|)
condition|)
block|{
name|scopeCode
operator|=
literal|0
expr_stmt|;
block|}
if|else if
condition|(
name|scope
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"onelevel"
argument_list|)
condition|)
block|{
name|scopeCode
operator|=
literal|1
expr_stmt|;
block|}
if|else if
condition|(
name|scope
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"subtree"
argument_list|)
condition|)
block|{
name|scopeCode
operator|=
literal|2
expr_stmt|;
block|}
name|results
operator|=
name|ctx
operator|.
name|search
argument_list|(
name|dn
argument_list|,
name|filter
argument_list|,
operator|new
name|SearchControls
argument_list|(
name|scopeCode
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|xmlResult
operator|=
name|renderSearchResultsAsDSML
argument_list|(
name|results
argument_list|,
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NameNotFoundException
name|nf
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"jndi:search() Not found for dn ["
operator|+
name|dn
operator|+
literal|"]"
argument_list|,
name|nf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ne
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"jndi:search() Search failed for dn ["
operator|+
name|dn
operator|+
literal|"]: "
argument_list|,
name|ne
argument_list|)
expr_stmt|;
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"jndi:search() Search failed for dn ["
operator|+
name|dn
operator|+
literal|"]: "
operator|+
name|ne
argument_list|)
operator|)
throw|;
block|}
block|}
return|return
operator|(
name|xmlResult
operator|)
return|;
block|}
specifier|private
name|Sequence
name|renderSearchResultsAsDSML
parameter_list|(
name|NamingEnumeration
name|results
parameter_list|,
name|String
name|dn
parameter_list|)
throws|throws
name|NamingException
block|{
name|Sequence
name|xmlResult
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"dsml"
argument_list|,
name|DSML_NAMESPACE
argument_list|,
name|DSML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"dn"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"directory-entries"
argument_list|,
name|DSML_NAMESPACE
argument_list|,
name|DSML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
while|while
condition|(
name|results
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|results
operator|.
name|next
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"entry"
argument_list|,
name|DSML_NAMESPACE
argument_list|,
name|DSML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"dn"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|result
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Handle objectClass attributes
name|Attribute
name|ocattr
init|=
name|result
operator|.
name|getAttributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"objectClass"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ocattr
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"objectclass"
argument_list|,
name|DSML_NAMESPACE
argument_list|,
name|DSML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ocattr
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|value
init|=
name|ocattr
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"oc-value"
argument_list|,
name|DSML_NAMESPACE
argument_list|,
name|DSML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|NamingEnumeration
name|attrs
init|=
name|result
operator|.
name|getAttributes
argument_list|()
operator|.
name|getAll
argument_list|()
decl_stmt|;
comment|// Handle all other attributes
while|while
condition|(
name|attrs
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|Attribute
name|attr
init|=
operator|(
name|Attribute
operator|)
name|attrs
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|attr
operator|.
name|getID
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"objectClass"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"attr"
argument_list|,
name|DSML_NAMESPACE
argument_list|,
name|DSML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attr
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|value
init|=
name|attr
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"value"
argument_list|,
name|DSML_NAMESPACE
argument_list|,
name|DSML_PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"userPassword"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
operator|new
name|String
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|xmlResult
operator|=
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
return|return
operator|(
name|xmlResult
operator|)
return|;
block|}
block|}
end_class

end_unit

