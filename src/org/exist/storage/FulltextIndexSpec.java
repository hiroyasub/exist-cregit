begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04,  The eXist Project  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|DatabaseConfigurationException
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

begin_comment
comment|/**  * Contains information about which parts of a document should be  * fulltext-indexed for a specified doctype. It basically keeps a list of paths  * to include and exclude from indexing. Paths are specified using  * simple XPath syntax, e.g. //SPEECH will select any SPEECH elements,  * //title/@id will select all id attributes being children of title elements.  *  *<pre>  *&lt;fulltext default="all" attributes="no">  *&lt;include path="//div/para"/>  *&lt;include path="//nested" content="mixed"/>  *&lt;/fulltext>  *</pre>  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|FulltextIndexSpec
block|{
specifier|private
specifier|static
specifier|final
name|String
name|PATH_ATTRIB
init|=
literal|"path"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_ATTRIB
init|=
literal|"content"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_MIXED
init|=
literal|"mixed"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PRESERVE_CONTENT_ELEMENT
init|=
literal|"preserveContent"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EXCLUDE_INTERFACE
init|=
literal|"exclude"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INCLUDE_ELEMENT
init|=
literal|"include"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ALPHANUM_ATTRIB
init|=
literal|"alphanum"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ATTRIBUTES_ATTRIB
init|=
literal|"attributes"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_ATTRIB
init|=
literal|"default"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_ELEMENT
init|=
literal|"create"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QNAME_ATTRIB
init|=
literal|"qname"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|NodePath
index|[]
name|ARRAY_TYPE
init|=
operator|new
name|NodePath
index|[
literal|0
index|]
decl_stmt|;
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
name|FulltextIndexSpec
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|NodePath
index|[]
name|includePath
decl_stmt|;
specifier|protected
name|NodePath
index|[]
name|excludePath
decl_stmt|;
specifier|protected
name|NodePath
index|[]
name|mixedPath
decl_stmt|;
specifier|protected
name|NodePath
index|[]
name|preserveContent
decl_stmt|;
specifier|protected
name|Set
name|qnameSpecs
init|=
operator|new
name|TreeSet
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|includeByDefault
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|includeAttributes
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|includeAlphaNum
init|=
literal|true
decl_stmt|;
comment|/**      * Constructor for the IndexPaths object      *      * param def if set to true, include everything by default. In this case      * use exclude elements to specify the excluded parts.      */
specifier|public
name|FulltextIndexSpec
parameter_list|(
name|Map
name|namespaces
parameter_list|,
name|Element
name|node
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|includeByDefault
operator|=
literal|true
expr_stmt|;
name|ArrayList
name|includeList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ArrayList
name|excludeList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ArrayList
name|preserveList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ArrayList
name|mixedList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|// check default settings
name|String
name|def
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|DEFAULT_ATTRIB
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|!=
literal|null
operator|&&
name|def
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|includeByDefault
operator|=
name|def
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
expr_stmt|;
block|}
name|String
name|indexAttributes
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|ATTRIBUTES_ATTRIB
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexAttributes
operator|!=
literal|null
operator|&&
name|indexAttributes
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|includeAttributes
operator|=
name|indexAttributes
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
operator|||
name|indexAttributes
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
block|}
name|String
name|indexAlphaNum
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|ALPHANUM_ATTRIB
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexAlphaNum
operator|!=
literal|null
operator|&&
name|indexAlphaNum
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|setIncludeAlphaNum
argument_list|(
name|indexAlphaNum
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
operator|||
name|indexAlphaNum
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check paths to include/exclude
name|NodeList
name|children
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|String
name|ps
decl_stmt|,
name|content
decl_stmt|;
name|Node
name|next
decl_stmt|;
name|Element
name|elem
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|next
operator|=
name|children
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|INCLUDE_ELEMENT
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|elem
operator|=
operator|(
name|Element
operator|)
name|next
expr_stmt|;
name|content
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|CONTENT_ATTRIB
argument_list|)
expr_stmt|;
name|ps
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|PATH_ATTRIB
argument_list|)
expr_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
operator|&&
name|content
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
name|CONTENT_MIXED
operator|.
name|equals
argument_list|(
name|content
argument_list|)
condition|)
block|{
name|mixedList
operator|.
name|add
argument_list|(
operator|new
name|NodePath
argument_list|(
name|namespaces
argument_list|,
name|ps
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|includeList
operator|.
name|add
argument_list|(
operator|new
name|NodePath
argument_list|(
name|namespaces
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|EXCLUDE_INTERFACE
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|ps
operator|=
operator|(
operator|(
name|Element
operator|)
name|next
operator|)
operator|.
name|getAttribute
argument_list|(
name|PATH_ATTRIB
argument_list|)
expr_stmt|;
name|excludeList
operator|.
name|add
argument_list|(
operator|new
name|NodePath
argument_list|(
name|namespaces
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|PRESERVE_CONTENT_ELEMENT
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|ps
operator|=
operator|(
operator|(
name|Element
operator|)
name|next
operator|)
operator|.
name|getAttribute
argument_list|(
name|PATH_ATTRIB
argument_list|)
expr_stmt|;
name|preserveList
operator|.
name|add
argument_list|(
operator|new
name|NodePath
argument_list|(
name|namespaces
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|CREATE_ELEMENT
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|elem
operator|=
operator|(
name|Element
operator|)
name|next
expr_stmt|;
name|String
name|name
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTRIB
argument_list|)
decl_stmt|;
name|boolean
name|isAttribute
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|isAttribute
operator|=
literal|true
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|prefix
init|=
name|QName
operator|.
name|extractPrefix
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|namespaceURI
init|=
literal|""
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
operator|(
name|String
operator|)
name|namespaces
operator|.
name|get
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
name|DatabaseConfigurationException
argument_list|(
literal|"No namespace defined for prefix: "
operator|+
name|prefix
operator|+
literal|" in index definition"
argument_list|)
throw|;
block|}
block|}
name|QName
name|qname
init|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAttribute
condition|)
name|qname
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|qnameSpecs
operator|.
name|add
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
block|}
name|includePath
operator|=
operator|(
name|NodePath
index|[]
operator|)
name|includeList
operator|.
name|toArray
argument_list|(
name|ARRAY_TYPE
argument_list|)
expr_stmt|;
name|excludePath
operator|=
operator|(
name|NodePath
index|[]
operator|)
name|excludeList
operator|.
name|toArray
argument_list|(
name|ARRAY_TYPE
argument_list|)
expr_stmt|;
name|preserveContent
operator|=
operator|(
name|NodePath
index|[]
operator|)
name|preserveList
operator|.
name|toArray
argument_list|(
name|ARRAY_TYPE
argument_list|)
expr_stmt|;
name|mixedPath
operator|=
operator|(
name|NodePath
index|[]
operator|)
name|mixedList
operator|.
name|toArray
argument_list|(
name|ARRAY_TYPE
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @return False if all elements are indexed, True if indexation is selective. 	 */
specifier|public
name|boolean
name|isSelective
parameter_list|()
block|{
if|if
condition|(
operator|(
name|includeByDefault
operator|&&
name|excludePath
operator|.
name|length
operator|>
literal|0
operator|)
operator|||
operator|(
operator|(
operator|!
name|includeByDefault
operator|)
operator|&&
name|includePath
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/**      * Include alpha-numeric data, i.e. numbers, serials, URLs and so on?      *      * @param index include alpha-numeric data      */
specifier|private
name|void
name|setIncludeAlphaNum
parameter_list|(
name|boolean
name|index
parameter_list|)
block|{
name|includeAlphaNum
operator|=
name|index
expr_stmt|;
block|}
comment|/**      * Include alpha-numeric data?      */
specifier|public
name|boolean
name|getIncludeAlphaNum
parameter_list|(  )
block|{
return|return
name|includeAlphaNum
return|;
block|}
comment|/**      * Check if a given path should be indexed.      *      * @param path path to the node      *      * @return Description of the Return Value      */
specifier|public
name|boolean
name|match
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|includeByDefault
condition|)
block|{
comment|// check exclusions
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|excludePath
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|excludePath
index|[
name|i
index|]
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|includePath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|includePath
index|[
name|i
index|]
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Check if a given path should be indexed.      *      * @param path path to the node      *      * @return Description of the Return Value      */
specifier|public
name|boolean
name|matchAttribute
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|includeAttributes
condition|)
block|{
comment|// check exclusions
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|excludePath
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|excludePath
index|[
name|i
index|]
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|includePath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|includePath
index|[
name|i
index|]
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Check if the element corresponding to the given path      * should be indexed as an element with mixed content,      * i.e. the string value of the element will be indexed      * as a single text sequence. Descendant elements will be ignored and      * will not break the text into chunks.      *      * Example: a mixed content index on the element      *<![CDATA[<mixed><s>un</s>even</mixed>]]>       *      * @param path      */
specifier|public
name|boolean
name|matchMixedElement
parameter_list|(
name|NodePath
name|path
parameter_list|)
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
name|mixedPath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|mixedPath
index|[
name|i
index|]
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|hasQNameIndex
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
name|qnameSpecs
operator|.
name|contains
argument_list|(
name|qname
argument_list|)
return|;
block|}
comment|/**      * Check if a given path should be preserveContent.      *      * @param path path to the node      *      * @return Description of the Return Value      */
specifier|public
name|boolean
name|preserveContent
parameter_list|(
name|NodePath
name|path
parameter_list|)
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
name|preserveContent
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|preserveContent
index|[
name|i
index|]
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"Full-text index\n"
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\tincludeByDefault : "
argument_list|)
operator|.
name|append
argument_list|(
name|includeByDefault
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\tincludeAttributes : "
argument_list|)
operator|.
name|append
argument_list|(
name|includeAttributes
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\tincludeAlphaNum : "
argument_list|)
operator|.
name|append
argument_list|(
name|includeAlphaNum
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|includePath
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
name|includePath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|NodePath
name|path
init|=
name|includePath
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"\tinclude : "
argument_list|)
operator|.
name|append
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|excludePath
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
name|excludePath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|NodePath
name|path
init|=
name|excludePath
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"\texclude : "
argument_list|)
operator|.
name|append
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|preserveContent
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
name|preserveContent
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|NodePath
name|path
init|=
name|preserveContent
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"\tpreserve content : "
argument_list|)
operator|.
name|append
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

