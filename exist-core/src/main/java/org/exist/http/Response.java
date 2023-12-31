begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|MimeType
import|;
end_import

begin_class
specifier|public
class|class
name|Response
block|{
specifier|private
specifier|final
specifier|static
name|String
name|stdHeaders
init|=
literal|"Allow: POST GET PUT DELETE\n"
operator|+
literal|"Server: eXist\n"
operator|+
literal|"Cache-control: no-cache\n"
decl_stmt|;
specifier|private
name|int
name|code
init|=
name|HttpServletResponse
operator|.
name|SC_OK
decl_stmt|;
specifier|private
name|String
name|statusDesc
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|content
init|=
literal|null
decl_stmt|;
specifier|private
name|byte
index|[]
name|binaryContent
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|private
name|String
name|contentType
init|=
name|MimeType
operator|.
name|XML_TYPE
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|public
name|Response
parameter_list|()
block|{
block|}
specifier|public
name|Response
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|this
operator|.
name|statusDesc
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|Response
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
specifier|public
name|void
name|setResponseCode
parameter_list|(
name|int
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
specifier|public
name|int
name|getResponseCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|code
return|;
block|}
specifier|public
name|void
name|setEncoding
parameter_list|(
name|String
name|encoding
parameter_list|)
block|{
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
block|}
specifier|public
name|String
name|getEncoding
parameter_list|()
block|{
return|return
name|this
operator|.
name|encoding
return|;
block|}
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|statusDesc
operator|=
name|description
expr_stmt|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|this
operator|.
name|statusDesc
return|;
block|}
specifier|public
name|void
name|setContent
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
specifier|public
name|void
name|setContent
parameter_list|(
name|byte
index|[]
name|content
parameter_list|)
block|{
name|this
operator|.
name|binaryContent
operator|=
name|content
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|content
operator|==
literal|null
condition|?
operator|(
name|binaryContent
operator|!=
literal|null
condition|?
name|binaryContent
else|:
literal|null
operator|)
else|:
name|content
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
return|;
block|}
specifier|public
name|void
name|setContentType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|contentType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|this
operator|.
name|contentType
return|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|DataOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|contentData
init|=
name|content
operator|==
literal|null
condition|?
operator|(
name|binaryContent
operator|!=
literal|null
condition|?
name|binaryContent
else|:
literal|null
operator|)
else|:
name|content
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"HTTP/1.0 "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|code
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|statusDesc
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|statusDesc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" OK"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|stdHeaders
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"Content-Type: "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\nContent-Length: "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|contentData
operator|==
literal|null
condition|?
literal|0
else|:
name|contentData
operator|.
name|length
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentData
operator|!=
literal|null
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|contentData
argument_list|,
literal|0
argument_list|,
name|contentData
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

