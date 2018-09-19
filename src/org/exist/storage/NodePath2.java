begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|exist
operator|.
name|dom
operator|.
name|INode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  * @author Stanislav Jordanov<stenlee@gmail.com>  * @version 1.0  *<p>  * This is an extension of class NodePath, that keeps track of a *real* node/element path, not just a QName path  * as its base class -- NodePath does.  * This is required in order to implement the feature requested/discussed here:  * @see<a href='https://sourceforge.net/p/exist/mailman/message/36392026/'>  * [Exist-open] Are more elaborate xpath expressions allowed in Lucene's index config<text match='...'/>  *</a>  */
end_comment

begin_class
specifier|public
class|class
name|NodePath2
extends|extends
name|NodePath
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|NodePath2
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attribs
index|[]
init|=
operator|new
name|HashMap
index|[
literal|4
index|]
decl_stmt|;
specifier|private
name|int
name|n_pos
init|=
literal|0
decl_stmt|;
specifier|public
name|NodePath2
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|NodePath2
parameter_list|(
name|NodePath2
name|o
parameter_list|)
block|{
name|super
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|n_pos
operator|=
name|o
operator|.
name|n_pos
expr_stmt|;
name|attribs
operator|=
operator|new
name|HashMap
index|[
name|n_pos
index|]
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
name|n_pos
condition|;
name|i
operator|++
control|)
block|{
name|attribs
index|[
name|i
index|]
operator|=
name|o
operator|.
name|attribs
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addNode
parameter_list|(
specifier|final
name|Node
name|node
parameter_list|)
block|{
name|addNode
argument_list|(
name|node
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addNode
parameter_list|(
specifier|final
name|Node
name|node
parameter_list|,
name|Attributes
name|saxAttribs
parameter_list|)
block|{
assert|assert
name|node
operator|instanceof
name|Element
assert|;
name|super
operator|.
name|addComponent
argument_list|(
operator|(
operator|(
name|INode
operator|)
name|node
operator|)
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|n_pos
operator|==
name|attribs
operator|.
name|length
condition|)
block|{
comment|//final HashMap<String, String>[] t = new HashMap[n_pos + 4];
specifier|final
name|HashMap
index|[]
name|t
init|=
operator|new
name|HashMap
index|[
name|n_pos
operator|+
literal|4
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|attribs
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
name|n_pos
argument_list|)
expr_stmt|;
name|attribs
operator|=
name|t
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|amap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|saxAttribs
operator|!=
literal|null
condition|)
block|{
name|int
name|alen
init|=
name|saxAttribs
operator|.
name|getLength
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
name|alen
condition|;
operator|++
name|i
control|)
block|{
name|amap
operator|.
name|put
argument_list|(
name|saxAttribs
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
argument_list|,
name|saxAttribs
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|NamedNodeMap
name|nnm
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|int
name|alen
init|=
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|getLength
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
name|alen
condition|;
operator|++
name|i
control|)
block|{
name|Node
name|child
init|=
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|)
name|amap
operator|.
name|put
argument_list|(
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|child
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|attribs
index|[
name|n_pos
operator|++
index|]
operator|=
name|amap
expr_stmt|;
block|}
specifier|public
name|void
name|reverseNodes
parameter_list|()
block|{
name|super
operator|.
name|reverseComponents
argument_list|()
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
name|n_pos
operator|/
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|HashMap
name|tmp
init|=
name|attribs
index|[
name|i
index|]
decl_stmt|;
name|attribs
index|[
name|i
index|]
operator|=
name|attribs
index|[
name|attribs
operator|.
name|length
operator|-
literal|1
operator|-
name|i
index|]
expr_stmt|;
name|attribs
index|[
name|attribs
operator|.
name|length
operator|-
literal|1
operator|-
name|i
index|]
operator|=
name|tmp
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeLastNode
parameter_list|()
block|{
name|super
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
if|if
condition|(
name|n_pos
operator|>
literal|0
condition|)
block|{
name|attribs
index|[
operator|--
name|n_pos
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeLastComponent
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|length
argument_list|()
operator|<=
name|n_pos
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Whoa!!! addNode() possibly paired with removeLastComponent() instead of removeLastNode()"
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
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
name|n_pos
condition|;
name|i
operator|++
control|)
block|{
name|attribs
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|//public HashMap<String, String>  attribs(int elementIdx) {
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attribs
parameter_list|(
name|int
name|elementIdx
parameter_list|)
block|{
return|return
name|attribs
index|[
name|elementIdx
index|]
return|;
block|}
block|}
end_class

end_unit

