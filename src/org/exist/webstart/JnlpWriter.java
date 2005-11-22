begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-03 Wolfgang M.  * Meier meier@ifs.tu-darmstadt.de http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  * $Id$  */
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  *  Class for writing JNLP file, jar files and image files.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|JnlpWriter
block|{
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
comment|/**      *  Write JNLP file to browser.      * @param response  Object for writing to end user.      * @throws java.io.IOException      */
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
name|String
name|codeBase
init|=
name|existBaseUrl
operator|+
literal|"/webstart/"
decl_stmt|;
comment|// Find URL to connect to with client
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
name|replaceAll
argument_list|(
literal|"-"
argument_list|,
literal|"%2D"
argument_list|)
operator|+
literal|"/xmlrpc"
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/x-java-jnlp-file"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"utf-8\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<jnlp spec=\"1.0+\" codebase=\""
operator|+
name|codeBase
operator|+
literal|"\" href=\"exist.jnlp\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<information>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<title>eXist XML-DB client</title>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<vendor>exist-db.org</vendor>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<homepage href=\"http://exist-db.org/\"/>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<description>Integrated command-line and gui client, "
operator|+
literal|"entirely based on the XML:DB API and provides commands "
operator|+
literal|"for most database related tasks, like creating and "
operator|+
literal|"removing collections, user management, batch-loading "
operator|+
literal|"XML data or querying.</description>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<description kind=\"short\">eXist XML-DB client</description>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<description kind=\"tooltip\">eXist XML-DB client</description>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<icon href=\"jnlp_logo.jpg\" kind=\"splash\"/>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<icon href=\"jnlp_logo.jpg\" />"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<icon href=\"jnlp_icon_64x64.gif\" width=\"64\" height=\"64\" />"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<icon href=\"jnlp_icon_32x32.gif\" width=\"32\" height=\"32\" />"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</information>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<security>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<all-permissions />"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</security>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<resources>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<j2se version=\"1.4+\"/>"
argument_list|)
expr_stmt|;
name|File
name|coreJars
index|[]
init|=
name|jnlpFiles
operator|.
name|getCoreJars
argument_list|()
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
name|coreJars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<jar href=\""
operator|+
name|coreJars
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|"\" size=\""
operator|+
name|coreJars
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|+
literal|"\" />"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"<jar href=\""
operator|+
name|jnlpFiles
operator|.
name|getMainJar
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"\" size=\""
operator|+
name|jnlpFiles
operator|.
name|getMainJar
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|"\"  main=\"true\" />"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</resources>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<application-desc main-class=\"org.exist.client.InteractiveClient\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<argument>-ouri="
operator|+
name|startUrl
operator|+
literal|"</argument>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</application-desc>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</jnlp>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Send JAR file to end user.      * @param filename  Name of JAR file      * @param response  Object for writing to end user.      * @throws java.io.IOException      */
name|void
name|sendJar
parameter_list|(
name|JnlpJarFiles
name|jnlpFiles
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
literal|"Send jar file "
operator|+
name|filename
argument_list|)
expr_stmt|;
name|File
name|jarFile
init|=
name|jnlpFiles
operator|.
name|getFile
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/x-java-archive"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentLength
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|jarFile
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|jarFile
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|jarFile
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
comment|// Transfer bytes from in to out
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|8096
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
name|File
name|imagesFolder
init|=
operator|new
name|File
argument_list|(
name|jh
operator|.
name|getWebappFolder
argument_list|()
argument_list|,
literal|"resources"
argument_list|)
decl_stmt|;
name|String
name|type
init|=
literal|""
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
else|else
block|{
name|type
operator|=
literal|"image/jpeg"
expr_stmt|;
block|}
name|response
operator|.
name|setContentType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|File
name|imageFile
init|=
operator|new
name|File
argument_list|(
name|imagesFolder
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentLength
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|imageFile
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|imageFile
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|imageFile
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
comment|// Transfer bytes from in to out
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|8096
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
block|}
end_class

end_unit

