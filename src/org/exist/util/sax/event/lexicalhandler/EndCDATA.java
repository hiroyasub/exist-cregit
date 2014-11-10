begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|sax
operator|.
name|event
operator|.
name|lexicalhandler
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
comment|/**  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|EndCDATA
implements|implements
name|LexicalHandlerEvent
block|{
specifier|public
specifier|final
specifier|static
name|EndCDATA
name|INSTANCE
init|=
operator|new
name|EndCDATA
argument_list|()
decl_stmt|;
comment|/**      * Constructor is private because this class      * carries no state and so is more efficient for      * re-use as a Singleton {@see INSTANCE}      */
specifier|private
name|EndCDATA
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
specifier|final
name|LexicalHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
block|{
name|handler
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

