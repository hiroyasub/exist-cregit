begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|DOMException
import|;
end_import

begin_comment
comment|/**  * A node with a QName, i.e. an element or attribute.  *  * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NamedNode
parameter_list|<
name|T
extends|extends
name|NamedNode
parameter_list|>
extends|extends
name|StoredNode
argument_list|<
name|T
argument_list|>
block|{
specifier|protected
name|QName
name|nodeName
init|=
literal|null
decl_stmt|;
specifier|public
name|NamedNode
parameter_list|(
specifier|final
name|short
name|nodeType
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NamedNode
parameter_list|(
specifier|final
name|short
name|nodeType
parameter_list|,
specifier|final
name|QName
name|qname
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|qname
expr_stmt|;
block|}
specifier|protected
name|NamedNode
parameter_list|(
specifier|final
name|short
name|nodeType
parameter_list|,
specifier|final
name|NodeId
name|nodeId
parameter_list|,
specifier|final
name|QName
name|qname
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|qname
expr_stmt|;
block|}
specifier|protected
name|NamedNode
parameter_list|(
specifier|final
name|NamedNode
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|other
operator|.
name|nodeName
expr_stmt|;
block|}
comment|/**      * Extracts just the details of the NamedNode      */
specifier|public
name|NamedNode
name|extract
parameter_list|()
block|{
return|return
operator|new
name|NamedNode
argument_list|(
name|this
argument_list|)
block|{         }
return|;
block|}
annotation|@
name|Override
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setQName
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|qname
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
comment|/**      * @deprecated use #setQName(qname) instead      * @param name qname of the node      */
annotation|@
name|Deprecated
specifier|public
name|void
name|setNodeName
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|)
block|{
name|nodeName
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|setNodeName
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|SymbolTable
name|symbols
parameter_list|)
throws|throws
name|DOMException
block|{
name|this
operator|.
name|nodeName
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|symbols
operator|.
name|getSymbol
argument_list|(
name|nodeName
operator|.
name|getLocalPart
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|INVALID_ACCESS_ERR
argument_list|,
literal|"Too many element/attribute names registered in the database. No of distinct names is limited to 16bit. Aborting store."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

