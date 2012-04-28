begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2008 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|examples
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
name|BufferedReader
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
name|InputStreamReader
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
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
comment|/**  * PostExample  * Execute: bin\run.bat org.exist.examples.http.PostExample  * Make sure you have the server started with bin\startup.bat beforehand.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|PostExample
block|{
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_HEADER
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<query xmlns=\""
operator|+
name|Namespaces
operator|.
name|EXIST_NS
operator|+
literal|"\" "
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_FOOTER
init|=
literal|"</query>"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTIES
init|=
literal|"<properties>"
operator|+
literal|"<property name=\"indent\" value=\"yes\"/>"
operator|+
literal|"<property name=\"encoding\" value=\"UTF-8\"/>"
operator|+
literal|"</properties>"
decl_stmt|;
specifier|public
name|void
name|query
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|request
init|=
name|REQUEST_HEADER
operator|+
literal|" howmany=\"-1\">"
operator|+
literal|"<text>"
operator|+
name|query
operator|+
literal|"</text>"
operator|+
name|PROPERTIES
operator|+
name|REQUEST_FOOTER
decl_stmt|;
name|doPost
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doPost
parameter_list|(
name|String
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8080/exist/rest"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OutputStream
name|os
init|=
name|connect
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|request
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|is
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|is
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|PostExample
name|client
init|=
operator|new
name|PostExample
argument_list|()
decl_stmt|;
try|try
block|{
name|client
operator|.
name|query
argument_list|(
literal|"declare namespace rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\";\ndeclare namespace dc=\"http://purl.org/dc/elements/1.1/\";\n//rdf:Description[dc:subject&amp;= 'umw*']"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An exception occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

