begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|util
operator|.
name|DatabaseConfigurationException
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
name|Attr
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
name|NamedNodeMap
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

begin_comment
comment|/**  * Top class for index definitions as specified in a collection configuration  * or the main configuration file. The IndexSpec for a given collection can be retrieved through method  * {@link org.exist.collections.Collection#getIdxConf(DBBroker)}.  *    *  An index definition should have the following structure:  *    *<pre>  *&lt;index index-depth="idx-depth"&gt;  *&lt;fulltext default="all|none" attributes="true|false"&gt;  *&lt;include path="node-path"/&gt;  *&lt;exclude path="node-path"/&gt;  *&lt;/fulltext&gt;  *&lt;create path="node-path" type="schema-type"&gt;  *&lt;/index&gt;  *</pre>  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|IndexSpec
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_ATTRIB
init|=
literal|"type"
decl_stmt|;
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
name|String
name|FULLTEXT_ELEMENT
init|=
literal|"fulltext"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_DEPTH_ATTRIB
init|=
literal|"index-depth"
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
name|IndexSpec
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @uml.associationEnd multiplicity="(0 1)"      */
specifier|private
name|FulltextIndexSpec
name|ftSpec
init|=
literal|null
decl_stmt|;
specifier|private
name|GeneralRangeIndexSpec
name|specs
index|[]
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
name|qnameSpecs
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|depth
init|=
literal|1
decl_stmt|;
specifier|public
name|IndexSpec
parameter_list|(
name|Element
name|index
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|read
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read index configurations from an "index" element node. The node should have      * exactly one "fulltext" child node and zero or more "create" nodes. The "fulltext"      * section  is forwarded to class {@link FulltextIndexSpec}. The "create" elements      * add a {@link GeneralRangeIndexSpec} to the current configuration.      *        * @param index      * @throws DatabaseConfigurationException      */
specifier|public
name|void
name|read
parameter_list|(
name|Element
name|index
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading configuration ..."
argument_list|)
expr_stmt|;
name|Map
name|namespaces
init|=
name|getNamespaceMap
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|String
name|indexDepth
init|=
name|index
operator|.
name|getAttribute
argument_list|(
name|INDEX_DEPTH_ATTRIB
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexDepth
operator|!=
literal|null
operator|&&
name|indexDepth
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
try|try
block|{
name|setIndexDepth
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|indexDepth
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
name|NodeList
name|cl
init|=
name|index
operator|.
name|getChildNodes
argument_list|()
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
name|cl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|cl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
if|if
condition|(
name|FULLTEXT_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|ftSpec
operator|=
operator|new
name|FulltextIndexSpec
argument_list|(
name|namespaces
argument_list|,
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|CREATE_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|String
name|type
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|TYPE_ATTRIB
argument_list|)
decl_stmt|;
if|if
condition|(
name|elem
operator|.
name|hasAttribute
argument_list|(
name|QNAME_ATTRIB
argument_list|)
condition|)
block|{
name|String
name|qname
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTRIB
argument_list|)
decl_stmt|;
name|QNameRangeIndexSpec
name|qnIdx
init|=
operator|new
name|QNameRangeIndexSpec
argument_list|(
name|namespaces
argument_list|,
name|qname
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|qnameSpecs
operator|.
name|put
argument_list|(
name|qnIdx
operator|.
name|getQName
argument_list|()
argument_list|,
name|qnIdx
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|elem
operator|.
name|hasAttribute
argument_list|(
name|PATH_ATTRIB
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|PATH_ATTRIB
argument_list|)
decl_stmt|;
name|GeneralRangeIndexSpec
name|valueIdx
init|=
operator|new
name|GeneralRangeIndexSpec
argument_list|(
name|namespaces
argument_list|,
name|path
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|addValueIndex
argument_list|(
name|valueIdx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|error_message
init|=
literal|"Configuration error: element "
operator|+
name|elem
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" must have attribute "
operator|+
name|PATH_ATTRIB
operator|+
literal|" or "
operator|+
name|QNAME_ATTRIB
decl_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
name|error_message
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * Returns the current index depth, i.e. the level in the tree up to which      * node ids are added to the B+-tree in the main dom.dbx. Nodes below      * the current index depth are not added. The main B+-tree is only required when      * retrieving nodes for display. Usually, it is not necessary to add all node levels      * there. Nodes in lower levels of the tree can be retrieved via their parent      * nodes.      *       * @return Current index depth.      */
specifier|public
name|int
name|getIndexDepth
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
comment|/**      * Set the current index depth.      *       * @see #getIndexDepth()      * @param depth Current index depth      */
specifier|public
name|void
name|setIndexDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
comment|/**      * Returns the fulltext index configuration object for the current      * configuration.      */
specifier|public
name|FulltextIndexSpec
name|getFulltextIndexSpec
parameter_list|()
block|{
return|return
name|ftSpec
return|;
block|}
comment|/**      * Returns the {@link GeneralRangeIndexSpec} defined for the given      * node path or null if no index has been configured.      *       * @param path      */
specifier|public
name|GeneralRangeIndexSpec
name|getIndexByPath
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|specs
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
name|specs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|specs
index|[
name|i
index|]
operator|.
name|matches
argument_list|(
name|path
argument_list|)
condition|)
return|return
name|specs
index|[
name|i
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|QNameRangeIndexSpec
name|getIndexByQName
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
return|return
operator|(
name|QNameRangeIndexSpec
operator|)
name|qnameSpecs
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Add a {@link GeneralRangeIndexSpec}.      *       * @param valueIdx      */
specifier|private
name|void
name|addValueIndex
parameter_list|(
name|GeneralRangeIndexSpec
name|valueIdx
parameter_list|)
block|{
if|if
condition|(
name|specs
operator|==
literal|null
condition|)
block|{
name|specs
operator|=
operator|new
name|GeneralRangeIndexSpec
index|[
literal|1
index|]
expr_stmt|;
name|specs
index|[
literal|0
index|]
operator|=
name|valueIdx
expr_stmt|;
block|}
else|else
block|{
name|GeneralRangeIndexSpec
name|nspecs
index|[]
init|=
operator|new
name|GeneralRangeIndexSpec
index|[
name|specs
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|specs
argument_list|,
literal|0
argument_list|,
name|nspecs
argument_list|,
literal|0
argument_list|,
name|specs
operator|.
name|length
argument_list|)
expr_stmt|;
name|nspecs
index|[
name|specs
operator|.
name|length
index|]
operator|=
name|valueIdx
expr_stmt|;
name|specs
operator|=
name|nspecs
expr_stmt|;
block|}
block|}
comment|/**      * Returns a map containing all prefix/namespace mappings declared in      * the index element.      *       * @param elem      * @return      */
specifier|private
name|Map
name|getNamespaceMap
parameter_list|(
name|Element
name|elem
parameter_list|)
block|{
name|HashMap
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|elem
operator|.
name|getAttributes
argument_list|()
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
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Attr
name|attr
init|=
operator|(
name|Attr
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|attr
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|attr
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|attr
operator|.
name|getPrefix
argument_list|()
operator|.
name|equals
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|attr
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
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
argument_list|()
decl_stmt|;
if|if
condition|(
name|ftSpec
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
name|ftSpec
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
if|if
condition|(
name|specs
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
name|specs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|GeneralRangeIndexSpec
name|spec
init|=
name|specs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|spec
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
name|spec
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
name|Iterator
name|i
init|=
name|qnameSpecs
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|qnameSpecs
operator|.
name|get
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
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

