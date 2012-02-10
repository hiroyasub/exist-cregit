begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist EXPath  *  Copyright (C) 2011 Adam Retter<adam@existsolutions.com>  *  www.existsolutions.com  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|model
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpClientException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|model
operator|.
name|Attribute
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
name|Attr
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|EXistAttribute
implements|implements
name|Attribute
block|{
specifier|final
name|Attr
name|attribute
decl_stmt|;
specifier|public
name|EXistAttribute
parameter_list|(
name|Attr
name|attribute
parameter_list|)
block|{
name|this
operator|.
name|attribute
operator|=
name|attribute
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
name|attribute
operator|.
name|getLocalName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceUri
parameter_list|()
block|{
return|return
name|attribute
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|attribute
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|HttpClientException
block|{
return|return
name|attribute
operator|.
name|getValue
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getInteger
parameter_list|()
throws|throws
name|HttpClientException
block|{
name|String
name|s
init|=
name|attribute
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"@"
operator|+
name|getLocalName
argument_list|()
operator|+
literal|" is not an integer"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

