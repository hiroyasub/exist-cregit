begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webstart
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletOutputStream
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
name|HttpServletRequest
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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLOutputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_comment
comment|/**  * Class for writing JNLP file, jar files and image files.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|JnlpWriter
block|{
specifier|public
specifier|static
specifier|final
name|String
name|JAR_MIME_TYPE
init|=
literal|"application/x-java-archive"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PACK_MIME_TYPE
init|=
literal|"application/x-java-pack200"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ACCEPT_ENCODING
init|=
literal|"accept-encoding"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"content-type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_ENCODING
init|=
literal|"content-encoding"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PACK200_GZIP_ENCODING
init|=
literal|"pack200-gzip"
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|JnlpWriter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Write JNLP xml file to browser.      *      * @param response Object for writing to end user.      * @throws java.io.IOException      */
name|void
name|writeJnlpXML
parameter_list|(
name|JnlpJarFiles
name|jnlpFiles
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Writing JNLP file"
argument_list|)
expr_stmt|;
comment|// Format URL: "http://host:8080/CONTEXT/webstart/exist.jnlp"
specifier|final
name|String
name|currentUrl
init|=
name|request
operator|.
name|getRequestURL
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Find BaseUrl http://host:8080/CONTEXT
specifier|final
name|int
name|webstartPos
init|=
name|currentUrl
operator|.
name|indexOf
argument_list|(
literal|"/webstart"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|existBaseUrl
init|=
name|currentUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|webstartPos
argument_list|)
decl_stmt|;
comment|// Find codeBase for jarfiles http://host:8080/CONTEXT/webstart/
specifier|final
name|String
name|codeBase
init|=
name|existBaseUrl
operator|+
literal|"/webstart/"
decl_stmt|;
comment|// Perfom sanity checks
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|File
name|jar
range|:
name|jnlpFiles
operator|.
name|getAllWebstartJars
argument_list|()
control|)
block|{
name|counter
operator|++
expr_stmt|;
comment|// debugging
if|if
condition|(
name|jar
operator|==
literal|null
operator|||
operator|!
name|jar
operator|.
name|exists
argument_list|()
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
literal|"Missing Jar file! ("
operator|+
name|counter
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// Find URL to connect to with client
specifier|final
name|String
name|startUrl
init|=
name|existBaseUrl
operator|.
name|replaceFirst
argument_list|(
literal|"http:"
argument_list|,
literal|"xmldb:exist:"
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"https:"
argument_list|,
literal|"xmldb:exist:"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"-"
argument_list|,
literal|"%2D"
argument_list|)
operator|+
literal|"/xmlrpc"
decl_stmt|;
comment|//        response.setDateHeader("Last-Modified", mainJar.lastModified());
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/x-java-jnlp-file"
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|XMLStreamWriter
name|writer
init|=
name|XMLOutputFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|createXMLStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeStartDocument
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"jnlp"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"spec"
argument_list|,
literal|"1.0+"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"codebase"
argument_list|,
name|codeBase
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"exist.jnlp"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"information"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"eXist XML-DB client"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"vendor"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"exist-db.org"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"homepage"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"http://exist-db.org"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"description"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"Integrated command-line and gui client, "
operator|+
literal|"entirely based on the XML:DB API and provides commands "
operator|+
literal|"for most database related tasks, like creating and "
operator|+
literal|"removing collections, user management, batch-loading "
operator|+
literal|"XML data or querying."
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"description"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"kind"
argument_list|,
literal|"short"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"eXist XML-DB client"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"description"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"kind"
argument_list|,
literal|"tooltip"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"eXist XML-DB client"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"icon"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"jnlp_logo.jpg"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"icon"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"jnlp_icon_128x128.gif"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"width"
argument_list|,
literal|"128"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"height"
argument_list|,
literal|"128"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"icon"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"jnlp_icon_64x64.gif"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"width"
argument_list|,
literal|"64"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"height"
argument_list|,
literal|"64"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"icon"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"jnlp_icon_32x32.gif"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"width"
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"height"
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
comment|// information
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"security"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEmptyElement
argument_list|(
literal|"all-permissions"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
comment|// ----------
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"resources"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"property"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"name"
argument_list|,
literal|"jnlp.packEnabled"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"value"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"j2se"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"version"
argument_list|,
literal|"1.6+"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|File
name|jar
range|:
name|jnlpFiles
operator|.
name|getAllWebstartJars
argument_list|()
control|)
block|{
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"jar"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"href"
argument_list|,
name|jar
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"size"
argument_list|,
literal|""
operator|+
name|jar
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
comment|// resources
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"application-desc"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"main-class"
argument_list|,
literal|"org.exist.client.InteractiveClient"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"argument"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"-ouri="
operator|+
name|startUrl
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"argument"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"--no-embedded-mode"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|isSecure
argument_list|()
condition|)
block|{
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"argument"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCharacters
argument_list|(
literal|"--use-ssl"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
comment|// application-desc
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
comment|// jnlp
name|writer
operator|.
name|writeEndDocument
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Send JAR or JAR.PACK.GZ file to end user.      *      * @param filename Name of JAR file      * @param response Object for writing to end user.      * @throws java.io.IOException      */
name|void
name|sendJar
parameter_list|(
name|JnlpJarFiles
name|jnlpFiles
parameter_list|,
name|String
name|filename
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Send jar file "
operator|+
name|filename
argument_list|)
expr_stmt|;
specifier|final
name|File
name|localFile
init|=
name|jnlpFiles
operator|.
name|getJarFile
argument_list|(
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
name|localFile
operator|==
literal|null
operator|||
operator|!
name|localFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
literal|"Jar file '"
operator|+
name|filename
operator|+
literal|"' not found."
argument_list|)
expr_stmt|;
return|return;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Actual file "
operator|+
name|localFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|localFile
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
comment|//response.setHeader(CONTENT_ENCODING, JAR_MIME_TYPE);
name|response
operator|.
name|setContentType
argument_list|(
name|JAR_MIME_TYPE
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|localFile
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar.pack.gz"
argument_list|)
condition|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|CONTENT_ENCODING
argument_list|,
name|PACK200_GZIP_ENCODING
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|PACK_MIME_TYPE
argument_list|)
expr_stmt|;
block|}
comment|// It is very improbable that a 64 bit jar is needed, but
comment|// it is better to be ready
comment|// response.setContentLength(Integer.parseInt(Long.toString(localFile.length())));
name|response
operator|.
name|setHeader
argument_list|(
literal|"Content-Length"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|localFile
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|localFile
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|localFile
argument_list|)
decl_stmt|;
specifier|final
name|ServletOutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Transfer bytes from in to out
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalStateException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Ignore IOException for '"
operator|+
name|filename
operator|+
literal|"'"
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
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|void
name|sendImage
parameter_list|(
name|JnlpHelper
name|jh
parameter_list|,
name|JnlpJarFiles
name|jf
parameter_list|,
name|String
name|filename
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Send image "
operator|+
name|filename
argument_list|)
expr_stmt|;
name|String
name|type
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|filename
operator|.
name|endsWith
argument_list|(
literal|".gif"
argument_list|)
condition|)
block|{
name|type
operator|=
literal|"image/gif"
expr_stmt|;
block|}
if|else if
condition|(
name|filename
operator|.
name|endsWith
argument_list|(
literal|".png"
argument_list|)
condition|)
block|{
name|type
operator|=
literal|"image/png"
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
literal|"image/jpeg"
expr_stmt|;
block|}
specifier|final
name|InputStream
name|is
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"resources/"
operator|+
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
literal|"Image file '"
operator|+
name|filename
operator|+
literal|"' not found."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Copy data
specifier|final
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|// It is very improbable that a 64 bit jar is needed, but
comment|// it is better to be ready
name|response
operator|.
name|setContentType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentLength
argument_list|(
name|baos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//response.setHeader("Content-Length", ""+baos.size());
specifier|final
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ServletOutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|bais
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalStateException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Ignored IOException for '"
operator|+
name|filename
operator|+
literal|"' "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Release resources
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
name|bais
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

