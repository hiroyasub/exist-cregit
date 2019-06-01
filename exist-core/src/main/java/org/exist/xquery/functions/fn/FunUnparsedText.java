begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

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
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
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
name|source
operator|.
name|SourceFactory
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
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|StandardCharsets
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_class
specifier|public
class|class
name|FunUnparsedText
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|PARAM_HREF
init|=
name|optParam
argument_list|(
literal|"href"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"the URI to load text from"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|PARAM_ENCODING
init|=
name|param
argument_list|(
literal|"encoding"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"character encoding of the resource"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|FunctionSignature
index|[]
name|FS_UNPARSED_TEXT
init|=
name|functionSignatures
argument_list|(
operator|new
name|QName
argument_list|(
literal|"unparsed-text"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"reads an external resource (for example, a file) and returns a string representation of the resource"
argument_list|,
name|returnsOpt
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
name|arities
argument_list|(
name|arity
argument_list|(
name|PARAM_HREF
argument_list|)
argument_list|,
name|arity
argument_list|(
name|PARAM_HREF
argument_list|,
name|PARAM_ENCODING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|FunctionSignature
index|[]
name|FS_UNPARSED_TEXT_LINES
init|=
name|functionSignatures
argument_list|(
operator|new
name|QName
argument_list|(
literal|"unparsed-text-lines"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"reads an external resource (for example, a file) and returns its contents as a sequence of strings, one for each line of text in the string representation of the resource"
argument_list|,
name|returnsOptMany
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
name|arities
argument_list|(
name|arity
argument_list|(
name|PARAM_HREF
argument_list|)
argument_list|,
name|arity
argument_list|(
name|PARAM_HREF
argument_list|,
name|PARAM_ENCODING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|FunctionSignature
index|[]
name|FS_UNPARSED_TEXT_AVAILABLE
init|=
name|functionSignatures
argument_list|(
operator|new
name|QName
argument_list|(
literal|"unparsed-text-available"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"determines whether a call on the fn:unparsed-text function with identical arguments would return a string"
argument_list|,
name|returns
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
argument_list|,
name|arities
argument_list|(
name|arity
argument_list|(
name|PARAM_HREF
argument_list|)
argument_list|,
name|arity
argument_list|(
name|PARAM_HREF
argument_list|,
name|PARAM_ENCODING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunUnparsedText
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|encoding
init|=
name|args
operator|.
name|length
operator|==
literal|2
condition|?
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"unparsed-text-lines"
argument_list|)
condition|)
block|{
return|return
name|readLines
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|encoding
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"unparsed-text-available"
argument_list|)
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|contentAvailable
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|encoding
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|readContent
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|encoding
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|boolean
name|contentAvailable
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|String
name|encoding
parameter_list|)
block|{
specifier|final
name|Charset
name|charset
decl_stmt|;
try|try
block|{
name|charset
operator|=
name|encoding
operator|!=
literal|null
condition|?
name|Charset
operator|.
name|forName
argument_list|(
name|encoding
argument_list|)
else|:
name|UTF_8
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
try|try
init|(
specifier|final
name|Reader
name|dynamicTextResource
init|=
name|context
operator|.
name|getDynamicallyAvailableTextResource
argument_list|(
name|toUri
argument_list|(
name|uri
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|charset
argument_list|)
init|)
block|{
if|if
condition|(
name|dynamicTextResource
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
try|try
block|{
name|readContent
argument_list|(
name|getSource
argument_list|(
name|uri
argument_list|)
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
decl||
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|String
name|readContent
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|String
name|encoding
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Charset
name|charset
decl_stmt|;
try|try
block|{
name|charset
operator|=
name|encoding
operator|!=
literal|null
condition|?
name|Charset
operator|.
name|forName
argument_list|(
name|encoding
argument_list|)
else|:
name|UTF_8
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1190
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
try|try
init|(
specifier|final
name|Reader
name|dynamicTextResource
init|=
name|context
operator|.
name|getDynamicallyAvailableTextResource
argument_list|(
name|toUri
argument_list|(
name|uri
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|charset
argument_list|)
init|)
block|{
if|if
condition|(
name|dynamicTextResource
operator|!=
literal|null
condition|)
block|{
return|return
name|readAll
argument_list|(
name|dynamicTextResource
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|readContent
argument_list|(
name|getSource
argument_list|(
name|uri
argument_list|)
argument_list|,
name|encoding
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
literal|"Cannot read text resource"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
name|readAll
parameter_list|(
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|char
name|buf
index|[]
init|=
operator|new
name|char
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|read
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|reader
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
name|builder
operator|.
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|String
name|readContent
parameter_list|(
specifier|final
name|Source
name|source
parameter_list|,
specifier|final
name|String
name|encoding
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|Charset
name|charset
init|=
name|getCharset
argument_list|(
name|encoding
argument_list|,
name|source
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|output
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|source
operator|.
name|getInputStream
argument_list|()
init|)
block|{
comment|// InputStream can have value NULL for data retrieved from URL
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|output
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
return|return
name|output
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|NullPointerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Sequence
name|readLines
parameter_list|(
specifier|final
name|String
name|uriParam
parameter_list|,
specifier|final
name|String
name|encoding
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|getSource
argument_list|(
name|uriParam
argument_list|)
decl_stmt|;
specifier|final
name|Charset
name|charset
init|=
name|getCharset
argument_list|(
name|encoding
argument_list|,
name|source
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|source
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
argument_list|)
init|)
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Charset
name|getCharset
parameter_list|(
specifier|final
name|String
name|encoding
parameter_list|,
specifier|final
name|Source
name|source
parameter_list|)
throws|throws
name|XPathException
block|{
name|Charset
name|charset
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|charset
operator|=
name|source
operator|.
name|getEncoding
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|charset
operator|==
literal|null
condition|)
block|{
name|charset
operator|=
name|StandardCharsets
operator|.
name|UTF_8
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|charset
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1190
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|charset
return|;
block|}
specifier|private
name|Source
name|getSource
parameter_list|(
specifier|final
name|String
name|uriParam
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getCurrentSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
literal|"non-dba user not allowed to read from file system"
argument_list|)
throw|;
block|}
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|uriParam
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
operator|+
name|uriParam
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uri
operator|.
name|getFragment
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
literal|"href argument may not contain fragment identifier"
argument_list|)
throw|;
block|}
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|""
argument_list|,
name|uri
operator|.
name|toASCIIString
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
literal|"Could not find source for: "
operator|+
name|uriParam
argument_list|)
throw|;
block|}
return|return
name|source
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|PermissionDeniedException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|URI
name|toUri
parameter_list|(
specifier|final
name|String
name|uriStr
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|uriStr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|uri
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
specifier|final
name|AnyURIValue
name|baseXdmUri
init|=
name|context
operator|.
name|getBaseURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseXdmUri
operator|!=
literal|null
operator|&&
operator|!
name|baseXdmUri
operator|.
name|equals
argument_list|(
name|AnyURIValue
operator|.
name|EMPTY_URI
argument_list|)
condition|)
block|{
name|URI
name|baseUri
init|=
name|baseXdmUri
operator|.
name|toURI
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|baseUri
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|baseUri
operator|=
operator|new
name|URI
argument_list|(
name|baseUri
operator|.
name|toString
argument_list|()
operator|+
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|uri
operator|=
name|baseUri
operator|.
name|resolve
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
literal|"$uri is a relative URI but there is no base-URI set"
argument_list|)
throw|;
block|}
block|}
return|return
name|uri
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|context
operator|.
name|getRootExpression
argument_list|()
argument_list|,
name|ErrorCodes
operator|.
name|FODC0005
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
