begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ReferenceNode
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
name|serializers
operator|.
name|Serializer
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
name|SAXException
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
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ExtendedDOMStreamer
extends|extends
name|DOMStreamer
block|{
specifier|private
name|Serializer
name|xmlSerializer
decl_stmt|;
specifier|public
name|ExtendedDOMStreamer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      *       */
specifier|public
name|ExtendedDOMStreamer
parameter_list|(
name|Serializer
name|xmlSerializer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|xmlSerializer
operator|=
name|xmlSerializer
expr_stmt|;
block|}
comment|/**      * @param contentHandler      * @param lexicalHandler      */
specifier|public
name|ExtendedDOMStreamer
parameter_list|(
name|Serializer
name|xmlSerializer
parameter_list|,
name|ContentHandler
name|contentHandler
parameter_list|,
name|LexicalHandler
name|lexicalHandler
parameter_list|)
block|{
name|super
argument_list|(
name|contentHandler
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
name|this
operator|.
name|xmlSerializer
operator|=
name|xmlSerializer
expr_stmt|;
block|}
specifier|public
name|void
name|setSerializer
parameter_list|(
name|Serializer
name|serializer
parameter_list|)
block|{
name|this
operator|.
name|xmlSerializer
operator|=
name|serializer
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.util.serializer.DOMStreamer#startNode(org.w3c.dom.Node)      */
annotation|@
name|Override
specifier|protected
name|void
name|startNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
if|if
condition|(
name|xmlSerializer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Cannot serialize node reference. Serializer is undefined."
argument_list|)
throw|;
name|xmlSerializer
operator|.
name|toReceiver
argument_list|(
operator|(
operator|(
name|ReferenceNode
operator|)
name|node
operator|)
operator|.
name|getReference
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
name|super
operator|.
name|startNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.util.serializer.DOMStreamer#reset()      */
annotation|@
name|Override
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
name|xmlSerializer
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

