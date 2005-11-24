begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-05 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_comment
comment|/**  * Utilities for URI related functions  *   * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|URIUtils
block|{
specifier|public
specifier|static
name|String
name|encodeForURI
parameter_list|(
name|String
name|uriPart
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|String
name|result
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|uriPart
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"\\+"
argument_list|,
literal|"%20"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%23"
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2D"
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%5F"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2E"
argument_list|,
literal|"."
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%21"
argument_list|,
literal|"!"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%7E"
argument_list|,
literal|"~"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2A"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%27"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%28"
argument_list|,
literal|"("
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%29"
argument_list|,
literal|")"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|String
name|iriToURI
parameter_list|(
name|String
name|uriPart
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|String
name|result
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|uriPart
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%23"
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2D"
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%5F"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2E"
argument_list|,
literal|"."
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%21"
argument_list|,
literal|"!"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%7E"
argument_list|,
literal|"~"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2A"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%27"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%28"
argument_list|,
literal|"("
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%29"
argument_list|,
literal|")"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3B"
argument_list|,
literal|";"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2F"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3F"
argument_list|,
literal|"?"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3A"
argument_list|,
literal|":"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%40"
argument_list|,
literal|"@"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%26"
argument_list|,
literal|"&"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3D"
argument_list|,
literal|"="
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2B"
argument_list|,
literal|"+"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%24"
argument_list|,
literal|"$"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2C"
argument_list|,
literal|","
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%5B"
argument_list|,
literal|"["
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%5D"
argument_list|,
literal|"])"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%25"
argument_list|,
literal|"%"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|String
name|escapeHtmlURI
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|String
name|result
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|uri
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|//TODO : to be continued
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"\\+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%23"
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2D"
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%5F"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2E"
argument_list|,
literal|"."
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%21"
argument_list|,
literal|"!"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%7E"
argument_list|,
literal|"~"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2A"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%27"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%28"
argument_list|,
literal|"("
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%29"
argument_list|,
literal|")"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3B"
argument_list|,
literal|";"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2F"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3F"
argument_list|,
literal|"?"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3A"
argument_list|,
literal|":"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%40"
argument_list|,
literal|"@"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%26"
argument_list|,
literal|"&"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%3D"
argument_list|,
literal|"="
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2B"
argument_list|,
literal|"+"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%24"
argument_list|,
literal|"$"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%2C"
argument_list|,
literal|","
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%5B"
argument_list|,
literal|"["
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%5D"
argument_list|,
literal|"])"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"%25"
argument_list|,
literal|"%"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

