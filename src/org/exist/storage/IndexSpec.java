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
name|Map
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
comment|/**  * @author wolf  */
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
specifier|private
name|FulltextIndexSpec
name|ftSpec
init|=
literal|null
decl_stmt|;
specifier|private
name|ValueIndexSpec
name|specs
index|[]
init|=
literal|null
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
comment|/**      * @param index      * @param namespaces      * @throws DatabaseConfigurationException      */
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
name|int
name|depth
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|indexDepth
argument_list|)
decl_stmt|;
name|setIndexDepth
argument_list|(
name|depth
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
comment|//	                if(ftSpec != null)
comment|//	                    throw new DatabaseConfigurationException("Only one fulltext section is allowed per index");
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
name|path
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|PATH_ATTRIB
argument_list|)
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
name|ValueIndexSpec
name|valueIdx
init|=
operator|new
name|ValueIndexSpec
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
block|}
block|}
block|}
specifier|public
name|int
name|getIndexDepth
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
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
specifier|public
name|FulltextIndexSpec
name|getFulltextIndexSpec
parameter_list|()
block|{
return|return
name|ftSpec
return|;
block|}
specifier|public
name|ValueIndexSpec
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
comment|/**      * @param valueIdx      */
specifier|private
name|void
name|addValueIndex
parameter_list|(
name|ValueIndexSpec
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
name|ValueIndexSpec
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
name|ValueIndexSpec
name|nspecs
index|[]
init|=
operator|new
name|ValueIndexSpec
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
block|}
end_class

end_unit

