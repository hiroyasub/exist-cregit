begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
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
name|Attributes
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  *  Simple contenthandler to determine the NamespaceUri of  * the document root node.  *   * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|ValidationContentHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
name|boolean
name|isFirstElement
init|=
literal|true
decl_stmt|;
specifier|private
name|String
name|namespaceUri
init|=
literal|null
decl_stmt|;
comment|/**      * @see org.xml.sax.helpers.DefaultHandler#startElement(String,String,String,Attributes)      */
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|isFirstElement
condition|)
block|{
name|namespaceUri
operator|=
name|uri
expr_stmt|;
name|isFirstElement
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**      *  Get namespace of root element. To be used for reporting.      *       * @return Namespace of root element.      */
specifier|public
name|String
name|getNamespaceUri
parameter_list|()
block|{
return|return
name|namespaceUri
return|;
block|}
block|}
end_class

end_unit

