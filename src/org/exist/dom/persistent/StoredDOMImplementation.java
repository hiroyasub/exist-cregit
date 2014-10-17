begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|DocumentType
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DOMImplementation
import|;
end_import

begin_class
specifier|public
class|class
name|StoredDOMImplementation
implements|implements
name|DOMImplementation
block|{
annotation|@
name|Override
specifier|public
name|Document
name|createDocument
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|qualifiedName
parameter_list|,
specifier|final
name|DocumentType
name|docType
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentType
name|createDocumentType
parameter_list|(
specifier|final
name|String
name|qualifiedName
parameter_list|,
specifier|final
name|String
name|publicId
parameter_list|,
specifier|final
name|String
name|systemId
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getFeature
parameter_list|(
specifier|final
name|String
name|feature
parameter_list|,
specifier|final
name|String
name|version
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasFeature
parameter_list|(
specifier|final
name|String
name|feature
parameter_list|,
specifier|final
name|String
name|version
parameter_list|)
block|{
return|return
literal|"XML"
operator|.
name|equals
argument_list|(
name|feature
argument_list|)
operator|&&
operator|(
literal|"1.0"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
operator|||
literal|"2.0"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

