begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

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
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|LexicalHandlerWrapper
implements|implements
name|LexicalHandler
block|{
specifier|private
specifier|final
name|LexicalHandler
name|output
decl_stmt|;
name|LexicalHandlerWrapper
parameter_list|(
name|LexicalHandler
name|output
parameter_list|,
name|DocumentTrigger
name|trigger
parameter_list|)
block|{
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
name|trigger
operator|.
name|setLexicalOutputHandler
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDTD
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
name|output
operator|.
name|startDTD
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
name|output
operator|.
name|endDTD
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|output
operator|.
name|startEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|output
operator|.
name|endEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|output
operator|.
name|startCDATA
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|output
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|comment
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|output
operator|.
name|comment
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

