begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
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
name|util
operator|.
name|XMLString
import|;
end_import

begin_comment
comment|/**  * Extract text from an XML fragment to be indexed with Lucene.  * This interface provides an additional abstraction to handle whitespace  * between elements or ignore certain elements.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TextExtractor
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|LuceneConfig
name|config
parameter_list|,
name|LuceneIndexConfig
name|idxConfig
parameter_list|)
function_decl|;
specifier|public
name|int
name|startElement
parameter_list|(
name|QName
name|name
parameter_list|)
function_decl|;
specifier|public
name|int
name|endElement
parameter_list|(
name|QName
name|name
parameter_list|)
function_decl|;
specifier|public
name|int
name|characters
parameter_list|(
name|XMLString
name|value
parameter_list|)
function_decl|;
specifier|public
name|float
name|getBoost
parameter_list|()
function_decl|;
specifier|public
name|XMLString
name|getText
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

