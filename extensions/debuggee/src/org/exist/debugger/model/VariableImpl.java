begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|model
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|VariableImpl
implements|implements
name|Variable
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|value
decl_stmt|;
specifier|private
name|String
name|type
decl_stmt|;
specifier|private
name|NodeList
name|complex_value
init|=
literal|null
decl_stmt|;
specifier|public
name|VariableImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|VariableImpl
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|NamedNodeMap
name|attrs
init|=
name|node
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
name|Node
name|attr
init|=
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"name"
argument_list|)
condition|)
block|{
name|name
operator|=
name|attr
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"type"
argument_list|)
condition|)
block|{
name|type
operator|=
name|attr
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
block|}
comment|//XXX: how should xml be processed???
name|complex_value
operator|=
name|node
operator|.
name|getChildNodes
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.model.Variable#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.model.Variable#getValue() 	 */
specifier|public
name|String
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|complex_value
operator|==
literal|null
condition|)
return|return
name|value
return|;
if|if
condition|(
operator|(
name|complex_value
operator|.
name|getLength
argument_list|()
operator|==
literal|1
operator|)
operator|&&
operator|(
name|complex_value
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|)
condition|)
return|return
operator|(
operator|(
name|Text
operator|)
name|complex_value
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getData
argument_list|()
return|;
comment|//TODO: xml?
return|return
name|complex_value
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.model.Variable#getType() 	 */
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
operator|+
name|getName
argument_list|()
operator|+
literal|" = "
operator|+
name|getValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

