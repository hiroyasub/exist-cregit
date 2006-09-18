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
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
block|{
name|String
name|result
init|=
name|urlEncodeUtf8
argument_list|(
name|uriPart
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
block|{
name|String
name|result
init|=
name|urlEncodeUtf8
argument_list|(
name|uriPart
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
literal|"\\$"
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
literal|"]"
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
block|{
name|String
name|result
init|=
name|urlEncodeUtf8
argument_list|(
name|uri
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
literal|"%20"
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
literal|"\\$"
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
literal|"]"
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
comment|/** 	 * This method is a wrapper for {@link java.net.URLEncoder.encode(java.lang.String,java.lang.String)} 	 * It calls this method, suppying the url parameter as 	 * the first parameter, and "UTF-8" (the W3C recommended 	 * encoding) as the second.  UnsupportedEncodingExceptions 	 * are wrapped in a runtime exception. 	 *  	 * IMPORTANT: the java.net.URLEncoder class encodes a space (" ") 	 * as a "+".  The proper method of encoding spaces in the path of 	 * a URI is with "%20", so this method will replace all instances of "+" 	 * in the encoded string with "%20" before returning.  This means that 	 * XmldbURIs constructed from java.net.URLEncoder.encoded strings 	 * will not be String equivalents of XmldbURIs created with the result of 	 * calls to this function. 	 *  	 * @param uri The uri to encode 	 * @return The UTF-8 encoded value of the supplied uri 	 */
specifier|public
specifier|static
name|String
name|urlEncodeUtf8
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
try|try
block|{
name|String
name|almostEncoded
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
return|return
name|almostEncoded
operator|.
name|replaceAll
argument_list|(
literal|"\\+"
argument_list|,
literal|"%20"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|//wrap with a runtime Exception
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * This method decodes the provided uri for human readability.  The 	 * method simply wraps URLDecoder.decode(uri,"UTF-8).  It is places here 	 * to provide a friendly way to decode URIs encoded by urlEncodeUtf8() 	 *  	 * @param uri The uri to decode 	 * @return The decoded value of the supplied uri 	 */
specifier|public
specifier|static
name|String
name|urlDecodeUtf8
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
try|try
block|{
return|return
name|URLDecoder
operator|.
name|decode
argument_list|(
name|uri
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|//wrap with a runtime Exception
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * This method decodes the provided uri for human readability.  The 	 * method simply wraps URLDecoder.decode(uri,"UTF-8).  It is places here 	 * to provide a friendly way to decode URIs encoded by urlEncodeUtf8() 	 *  	 * @param uri The uri to decode 	 * @return The decoded value of the supplied uri 	 */
specifier|public
specifier|static
name|String
name|urlDecodeUtf8
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
block|{
try|try
block|{
return|return
name|URLDecoder
operator|.
name|decode
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|//wrap with a runtime Exception
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * This method splits the supplied url on the character 	 * '/' then URL encodes the segments between, returning 	 * a URL encoded version of the passed url, leaving any 	 * occurrence of '/' as it is. 	 *  	 * @param url The path to encode 	 * @return A UTF-8 URL encoded string 	 */
specifier|public
specifier|static
name|String
name|urlEncodePartsUtf8
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|String
index|[]
name|split
init|=
name|url
operator|.
name|split
argument_list|(
literal|"/"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|StringBuffer
name|ret
init|=
operator|new
name|StringBuffer
argument_list|(
name|url
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|split
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|urlEncodeUtf8
argument_list|(
name|split
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|split
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * This method ensure that a collection path (e.g. /db/[]) 	 * is properly URL encoded.  Uses W3C recommended UTF-8 	 * encoding. 	 *  	 * @param path The path to check 	 * @return A UTF-8 URL encoded string 	 */
specifier|public
specifier|static
name|String
name|ensureUrlEncodedUtf8
parameter_list|(
name|String
name|path
parameter_list|)
block|{
try|try
block|{
name|XmldbURI
name|uri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|uri
operator|.
name|getRawCollectionPath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
return|return
name|URIUtils
operator|.
name|urlEncodePartsUtf8
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
comment|/** 	 * This method creates an<code>XmldbURI</code> by encoding the provided 	 * string, then calling XmldbURI.xmldbUriFor(String) with the result of that 	 * encoding 	 *  	 * @param path The path to encode and create an XmldbURI from 	 * @return A UTF-8 URI encoded string 	 * @throws URISyntaxException A URISyntaxException is thrown if the path 	 * cannot be parsed by XmldbURI, after being encoded by 	 *<code>urlEncodePartsUtf8</code> 	 */
specifier|public
specifier|static
name|XmldbURI
name|encodeXmldbUriFor
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|URIUtils
operator|.
name|urlEncodePartsUtf8
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

