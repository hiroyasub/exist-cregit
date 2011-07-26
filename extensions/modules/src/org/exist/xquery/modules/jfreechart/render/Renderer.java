begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|jfreechart
operator|.
name|render
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|modules
operator|.
name|jfreechart
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jfree
operator|.
name|chart
operator|.
name|JFreeChart
import|;
end_import

begin_comment
comment|/**  *   Renderer interface.  *  * @author Dannes Wessels (dannes@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|Renderer
block|{
comment|/**      *  Render chart to outputstream.      *       * @param chart     The jfreechart      * @param config    Chart configuration      * @param os        The Outputstream      * @throws IOException Thrown when something bad happens      */
specifier|public
name|void
name|render
parameter_list|(
name|JFreeChart
name|chart
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      *  Render chart to byte arrau.      * @param chart     The jfreechart      * @param config    Chart configuration      * @return  The rendered image      * @throws IOException Thrown when something bad happens      */
specifier|public
name|byte
index|[]
name|render
parameter_list|(
name|JFreeChart
name|chart
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      *  Get the Content type of the rendered image (mimetype).      *       * @return Content type of in=mage      */
specifier|public
name|String
name|getContentType
parameter_list|()
function_decl|;
comment|/**      *  Get content encoding of image.      *       * @return Normally returns null, or gzip when result is gzip-ped.      */
specifier|public
name|String
name|getContentEncoding
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

