begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|nio
operator|.
name|CharBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|DatabaseInstanceManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSLTS_case
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XSLT_COLLECTION
init|=
literal|"xslt_tests"
decl_stmt|;
specifier|private
name|Collection
name|col
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|col
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/"
operator|+
name|XSLT_COLLECTION
argument_list|)
expr_stmt|;
if|if
condition|(
name|col
operator|==
literal|null
condition|)
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|mgtService
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|col
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|XSLT_COLLECTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"collection created."
argument_list|)
expr_stmt|;
block|}
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|TransformerFactoryAllocator
operator|.
name|PROPERTY_TRANSFORMER_CLASS
argument_list|,
literal|"org.exist.xslt.TransformerFactoryImpl"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|testCase
parameter_list|(
name|String
name|inputURL
parameter_list|,
name|String
name|xslURL
parameter_list|,
name|String
name|outputURL
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|input
init|=
name|loadFile
argument_list|(
literal|"test/external/XSLTS_1_1_0/TestInputs/"
operator|+
name|inputURL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|stylesheet
init|=
name|loadFile
argument_list|(
literal|"test/external/XSLTS_1_1_0/TestInputs/"
operator|+
name|xslURL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"xquery version \"1.0\";\n"
operator|+
literal|"declare namespace transform=\"http://exist-db.org/xquery/transform\";\n"
operator|+
literal|"declare variable $xml {"
operator|+
name|input
operator|+
literal|"};\n"
operator|+
literal|"declare variable $xslt {"
operator|+
name|stylesheet
operator|+
literal|"};\n"
operator|+
literal|"transform:transform($xml, $xslt, ())\n"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|StringBuffer
name|content
init|=
operator|new
name|StringBuffer
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
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
name|content
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checkResult
argument_list|(
name|outputURL
argument_list|,
name|content
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|checkResult
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|tokenCount
init|=
literal|0
decl_stmt|;
name|String
name|ref
init|=
name|loadFile
argument_list|(
literal|"test/external/XSLTS_1_1_0/ExpectedTestResults/"
operator|+
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ref
operator|=
name|ref
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|ref
operator|=
name|ref
operator|.
name|replaceAll
argument_list|(
literal|"<dgnorm_document>"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ref
operator|=
name|ref
operator|.
name|replaceAll
argument_list|(
literal|"</dgnorm_document>"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
name|delim
init|=
literal|" \t\n\r\f<>"
decl_stmt|;
name|StringTokenizer
name|refTokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|ref
argument_list|,
name|delim
argument_list|)
decl_stmt|;
name|StringTokenizer
name|resTokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|result
argument_list|,
name|delim
argument_list|)
decl_stmt|;
while|while
condition|(
name|refTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|tokenCount
operator|++
expr_stmt|;
name|String
name|refToken
init|=
name|refTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|resTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|result
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"result should have: "
operator|+
name|refToken
operator|+
literal|", but get EOF (at "
operator|+
name|tokenCount
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|String
name|resToken
init|=
name|resTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|refToken
operator|.
name|equals
argument_list|(
name|resToken
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|result
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"result should have: "
operator|+
name|refToken
operator|+
literal|", but get "
operator|+
name|resToken
operator|+
literal|" (at "
operator|+
name|tokenCount
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|resTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|resToken
init|=
name|resTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ref
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"result should have nothing, but get "
operator|+
name|resToken
operator|+
literal|" (at "
operator|+
name|tokenCount
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|String
name|loadFile
parameter_list|(
name|String
name|fileURL
parameter_list|,
name|boolean
name|incapsulate
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileURL
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"can load information."
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Open the file and then get a channel from the stream
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FileChannel
name|fc
init|=
name|fis
operator|.
name|getChannel
argument_list|()
decl_stmt|;
comment|// Get the file's size and then map it into memory
name|int
name|sz
init|=
operator|(
name|int
operator|)
name|fc
operator|.
name|size
argument_list|()
decl_stmt|;
name|MappedByteBuffer
name|bb
init|=
name|fc
operator|.
name|map
argument_list|(
name|FileChannel
operator|.
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|sz
argument_list|)
decl_stmt|;
comment|// Charset and decoder for ISO-8859-15
name|Charset
name|charset
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"ISO-8859-15"
argument_list|)
decl_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|charset
operator|.
name|newDecoder
argument_list|()
decl_stmt|;
comment|// Decode the file into a char buffer
name|CharBuffer
name|cb
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|bb
argument_list|)
decl_stmt|;
name|result
operator|=
name|cb
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//TODO: rewrite to handle<?xml*?>
if|if
condition|(
name|result
operator|.
name|startsWith
argument_list|(
literal|"<?xml "
argument_list|)
condition|)
block|{
name|int
name|endAt
init|=
name|result
operator|.
name|indexOf
argument_list|(
literal|"?>"
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|substring
argument_list|(
name|endAt
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
comment|//XXX: rethink: prexslt query processing
if|if
condition|(
name|incapsulate
condition|)
block|{
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"\\{"
argument_list|,
literal|"\\{\\{"
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replaceAll
argument_list|(
literal|"\\}"
argument_list|,
literal|"\\}\\}"
argument_list|)
expr_stmt|;
block|}
comment|// Close the channel and the stream
name|fc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

