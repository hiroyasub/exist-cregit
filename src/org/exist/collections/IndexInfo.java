begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Indexer
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
name|DocumentImpl
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
name|serializer
operator|.
name|DOMStreamer
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
name|XMLReader
import|;
end_import

begin_comment
comment|/**  * Internal class used to track some required fields when calling  * {@link org.exist.collections.Collection#validate(DBBroker, String, Node)} and  * {@link org.exist.collections.Collection#store(DBBroker, IndexInfo, Node, boolean)}.  * This class is not publicly readable.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|IndexInfo
block|{
specifier|protected
name|Indexer
name|indexer
decl_stmt|;
specifier|protected
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
specifier|protected
name|DOMStreamer
name|streamer
init|=
literal|null
decl_stmt|;
specifier|protected
name|IndexInfo
parameter_list|(
name|Indexer
name|indexer
parameter_list|)
block|{
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
block|}
specifier|protected
name|Indexer
name|getIndexer
parameter_list|()
block|{
return|return
name|indexer
return|;
block|}
specifier|protected
name|void
name|setReader
parameter_list|(
name|XMLReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
specifier|protected
name|XMLReader
name|getReader
parameter_list|()
block|{
return|return
name|this
operator|.
name|reader
return|;
block|}
specifier|public
name|void
name|setDOMStreamer
parameter_list|(
name|DOMStreamer
name|streamer
parameter_list|)
block|{
name|this
operator|.
name|streamer
operator|=
name|streamer
expr_stmt|;
block|}
specifier|public
name|DOMStreamer
name|getDOMStreamer
parameter_list|()
block|{
return|return
name|this
operator|.
name|streamer
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|indexer
operator|.
name|getDocument
argument_list|()
return|;
block|}
block|}
end_class

end_unit

