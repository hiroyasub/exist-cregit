begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Templates
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TransformerHandler
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
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
name|w3c
operator|.
name|tests
operator|.
name|TestCase
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
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
name|Element
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSLTS_case
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XSLT_COLLECTION
init|=
literal|"XSLTS"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XSLTS_folder
init|=
name|XSLT_COLLECTION
operator|+
literal|"_1_1_0"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|loadTS
parameter_list|()
throws|throws
name|Exception
block|{
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
parameter_list|,
name|String
name|expectedError
parameter_list|)
throws|throws
name|Exception
block|{
comment|//		String input = loadFile(XSLTS_folder+"TestInputs/"+inputURL, false);
comment|//		String stylesheet = loadFile(XSLTS_folder+"TestInputs/"+xslURL, true);
comment|//        String query = "xquery version \"1.0\";\n" +
comment|//                "declare namespace transform=\"http://exist-db.org/xquery/transform\";\n" +
comment|//                "declare variable $xml external;\n" +
comment|//                "declare variable $xslt external;\n" +
comment|//                "transform:transform($xml, $xslt, ())\n";
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|XQueryContext
name|context
decl_stmt|;
comment|//			XQuery xquery;
comment|//
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
name|db
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
comment|//				xquery = broker.getXQueryService();
comment|//
comment|//				broker.getConfiguration().setProperty( XQueryContext.PROPERTY_XQUERY_RAISE_ERROR_ON_FAILED_RETRIEVAL, true);
comment|//
comment|//				context = xquery.newContext(AccessContext.TEST);
name|context
operator|=
operator|new
name|XSLContext
argument_list|(
name|db
argument_list|)
expr_stmt|;
comment|//
comment|//			} catch (Exception e) {
comment|//				Assert.fail(e.getMessage());
comment|//				return;
comment|//			}
comment|//
comment|//			//declare variable
comment|//	        if (inputURL !=  null&& inputURL != "")
comment|//	        	context.declareVariable("xml", loadVarFromURI(context, testLocation+XSLTS_folder+"/TestInputs/"+inputURL));
comment|//	        else
comment|//	        	context.declareVariable("xml", loadVarFromString(context, "<empty/>"));
comment|//
comment|//			context.declareVariable("xslt", loadVarFromURI(context, testLocation+XSLTS_folder+"/TestInputs/"+xslURL));
comment|//
comment|//			//compile
comment|//			CompiledXQuery compiled = xquery.compile(context, query);
comment|//
comment|//			//execute
comment|//			Sequence result = xquery.execute(compiled, null);
name|TransformerFactoryImpl
name|factory
init|=
operator|new
name|TransformerFactoryImpl
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setBrokerPool
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|Templates
name|templates
init|=
name|factory
operator|.
name|newTemplates
argument_list|(
operator|new
name|SourceImpl
argument_list|(
name|loadVarFromURI
argument_list|(
name|context
argument_list|,
name|testLocation
operator|+
name|XSLTS_folder
operator|+
literal|"/TestInputs/"
operator|+
name|xslURL
argument_list|)
operator|.
name|getOwnerDocument
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|(
name|templates
argument_list|)
decl_stmt|;
comment|//	        TransformErrorListener errorListener = new TransformErrorListener();
comment|//	        handler.getTransformer().setErrorListener(errorListener);
name|NodeImpl
name|input
decl_stmt|;
if|if
condition|(
name|inputURL
operator|!=
literal|null
operator|&&
name|inputURL
operator|!=
literal|""
condition|)
name|input
operator|=
name|loadVarFromURI
argument_list|(
name|context
argument_list|,
name|testLocation
operator|+
name|XSLTS_folder
operator|+
literal|"/TestInputs/"
operator|+
name|inputURL
argument_list|)
expr_stmt|;
else|else
name|input
operator|=
name|loadVarFromString
argument_list|(
name|context
argument_list|,
literal|"<empty/>"
argument_list|)
expr_stmt|;
name|Transformer
name|transformer
init|=
name|handler
operator|.
name|getTransformer
argument_list|()
decl_stmt|;
name|Sequence
name|result
init|=
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|Transformer
operator|)
name|transformer
operator|)
operator|.
name|transform
argument_list|(
name|input
argument_list|)
decl_stmt|;
comment|//compare result with one provided by test case
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|outputURL
operator|==
literal|null
operator|||
name|outputURL
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected error: "
operator|+
name|expectedError
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//workaround
name|Document
name|doc
init|=
operator|new
name|DocumentImpl
argument_list|(
name|context
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Element
name|outputFile
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"outputFile"
argument_list|)
decl_stmt|;
name|outputFile
operator|.
name|setAttribute
argument_list|(
literal|"compare"
argument_list|,
literal|"Fragment"
argument_list|)
expr_stmt|;
name|outputFile
operator|.
name|setTextContent
argument_list|(
name|outputURL
argument_list|)
expr_stmt|;
if|if
condition|(
name|compareResult
argument_list|(
literal|""
argument_list|,
name|XSLTS_folder
operator|+
literal|"/ExpectedTestResults/"
argument_list|,
name|outputFile
argument_list|,
name|result
argument_list|)
condition|)
block|{
name|ok
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ok
condition|)
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected \n"
operator|+
literal|"["
operator|+
name|readFileAsString
argument_list|(
operator|new
name|File
argument_list|(
name|testLocation
operator|+
name|XSLTS_folder
operator|+
literal|"/ExpectedTestResults/"
argument_list|,
name|outputURL
argument_list|)
argument_list|)
operator|+
literal|"]\n"
operator|+
literal|", get \n["
operator|+
name|sequenceToString
argument_list|(
name|result
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|String
name|error
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|expectedError
operator|.
name|isEmpty
argument_list|()
condition|)
empty_stmt|;
else|else
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected error is "
operator|+
name|expectedError
operator|+
literal|", get "
operator|+
name|error
operator|+
literal|" ["
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
comment|//        StringBuilder content = new StringBuilder();
comment|//    	for (int i = 0; i< result.getSize(); i++)
comment|//    		content.append((String) result.getResource(i).getContent());
comment|//
comment|//        assertTrue(checkResult(outputURL, content.toString()));
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
literal|"expected:"
argument_list|)
expr_stmt|;
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
literal|"get:"
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
try|try
block|{
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
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|protected
name|XmldbURI
name|getCollection
parameter_list|()
block|{
return|return
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"/db/XSLTS"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

