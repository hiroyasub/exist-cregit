begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|NodeProxy
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
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|StaXUtil
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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_comment
comment|/**  * Tests if a node is of a given node type.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|TypeTest
implements|implements
name|NodeTest
block|{
specifier|protected
name|int
name|nodeType
init|=
literal|0
decl_stmt|;
specifier|public
name|TypeTest
parameter_list|(
name|int
name|nodeType
parameter_list|)
block|{
name|setType
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|int
name|nodeType
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|protected
name|boolean
name|isOfType
parameter_list|(
name|short
name|type
parameter_list|)
block|{
name|int
name|domType
decl_stmt|;
switch|switch
condition|(
name|nodeType
condition|)
block|{
case|case
name|Type
operator|.
name|ELEMENT
case|:
name|domType
operator|=
name|Node
operator|.
name|ELEMENT_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|TEXT
case|:
name|domType
operator|=
name|Node
operator|.
name|TEXT_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|ATTRIBUTE
case|:
name|domType
operator|=
name|Node
operator|.
name|ATTRIBUTE_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|COMMENT
case|:
name|domType
operator|=
name|Node
operator|.
name|COMMENT_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|PROCESSING_INSTRUCTION
case|:
name|domType
operator|=
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DOCUMENT
case|:
name|domType
operator|=
name|Node
operator|.
name|DOCUMENT_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|NODE
case|:
default|default :
return|return
literal|true
return|;
block|}
if|if
condition|(
name|type
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
name|type
operator|=
name|Node
operator|.
name|TEXT_NODE
expr_stmt|;
block|}
return|return
operator|(
name|type
operator|==
name|domType
operator|)
return|;
block|}
specifier|protected
name|boolean
name|isOfEventType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|nodeType
operator|==
name|Type
operator|.
name|NODE
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|int
name|xpathType
init|=
name|StaXUtil
operator|.
name|streamType2Type
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|xpathType
operator|==
name|nodeType
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Type
operator|.
name|getTypeName
argument_list|(
name|nodeType
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.NodeTest#matches(org.exist.dom.persistent.NodeProxy)      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
specifier|final
name|short
name|otherNodeType
init|=
name|proxy
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherNodeType
operator|==
name|Type
operator|.
name|ITEM
operator|||
name|otherNodeType
operator|==
name|Type
operator|.
name|NODE
condition|)
block|{
comment|//TODO : what are the semantics of Type.NODE ?
if|if
condition|(
name|this
operator|.
name|nodeType
operator|==
name|Type
operator|.
name|NODE
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|isOfType
argument_list|(
name|proxy
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeType
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|isOfType
argument_list|(
name|otherNodeType
argument_list|)
return|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.NodeTest#matches(org.exist.dom.persistent.NodeProxy)      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|isOfType
argument_list|(
name|other
operator|.
name|getNodeType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|)
block|{
return|return
name|isOfEventType
argument_list|(
name|reader
operator|.
name|getEventType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
comment|// always false because there's no name
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.NodeTest#isWildcardTest()      */
specifier|public
name|boolean
name|isWildcardTest
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

