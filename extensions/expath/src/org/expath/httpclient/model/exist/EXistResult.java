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
name|IOException
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
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
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
name|modules
operator|.
name|ModuleUtils
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
name|Base64BinaryValueType
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
name|BinaryValueFromInputStream
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
name|NodeValue
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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
name|StringValue
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
name|HttpResponse
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
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|EXistResult
implements|implements
name|Result
block|{
specifier|private
specifier|final
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|EXistResult
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|HttpClientException
block|{
try|try
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|string
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"Unable to add string value to result:"
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
block|}
comment|//TODO would be better if the EXPath API provided a stream!
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|HttpClientException
block|{
try|try
block|{
name|result
operator|.
name|add
argument_list|(
name|BinaryValueFromInputStream
operator|.
name|getInstance
argument_list|(
name|context
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"Unable to add binary value to result:"
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|Source
name|src
parameter_list|)
throws|throws
name|HttpClientException
block|{
try|try
block|{
name|NodeValue
name|nodeValue
init|=
name|ModuleUtils
operator|.
name|sourceToXML
argument_list|(
name|context
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|nodeValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"Unable to add Source to result:"
operator|+
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|saxe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"Unable to add Source to result:"
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"Unable to add Source to result:"
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|HttpResponse
name|response
parameter_list|)
throws|throws
name|HttpClientException
block|{
name|EXistTreeBuilder
name|builder
init|=
operator|new
name|EXistTreeBuilder
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|response
operator|.
name|outputResponseElement
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|DocumentImpl
name|elem
init|=
name|builder
operator|.
name|close
argument_list|()
decl_stmt|;
try|try
block|{
name|result
operator|.
name|add
argument_list|(
name|elem
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"Unable to add HttpResponse to result:"
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Sequence
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

