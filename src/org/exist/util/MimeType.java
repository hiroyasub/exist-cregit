begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_class
specifier|public
class|class
name|MimeType
block|{
specifier|public
specifier|final
specifier|static
name|int
name|XML
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|BINARY
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|BINARY_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"application/octet-stream"
argument_list|,
name|BINARY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|XML_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"text/xml"
argument_list|,
name|XML
argument_list|)
decl_stmt|;
comment|//public final static MimeType XML_APPLICATION_TYPE =
comment|//    new MimeType("application/xml", XML);
specifier|public
specifier|final
specifier|static
name|MimeType
name|XML_CONTENT_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"text/xml; charset=UTF-8"
argument_list|,
name|XML
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|XSL_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"text/xsl"
argument_list|,
name|XML
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|XSLT_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"application/xslt+xml"
argument_list|,
name|XML
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|XQUERY_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"application/xquery"
argument_list|,
name|BINARY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|XPROC_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"application/xml+xproc"
argument_list|,
name|XML
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|CSS_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"text/css"
argument_list|,
name|BINARY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|HTML_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"text/html"
argument_list|,
name|BINARY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|MimeType
name|TEXT_TYPE
init|=
operator|new
name|MimeType
argument_list|(
literal|"text/plain"
argument_list|,
name|BINARY
argument_list|)
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|description
decl_stmt|;
specifier|private
name|int
name|type
init|=
name|MimeType
operator|.
name|XML
decl_stmt|;
specifier|public
name|MimeType
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
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
name|description
operator|=
name|description
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|String
name|getXMLDBType
parameter_list|()
block|{
return|return
name|isXMLType
argument_list|()
condition|?
literal|"XMLResource"
else|:
literal|"BinaryResource"
return|;
block|}
specifier|public
name|boolean
name|isXMLType
parameter_list|()
block|{
return|return
name|type
operator|==
name|XML
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|": "
operator|+
name|description
return|;
block|}
block|}
end_class

end_unit

