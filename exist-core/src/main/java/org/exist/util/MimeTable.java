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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|logging
operator|.
name|log4j
operator|.
name|LogManager
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
name|xml
operator|.
name|sax
operator|.
name|Attributes
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
name|InputSource
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
import|;
end_import

begin_comment
comment|/**  * Global table of mime types. This singleton class maintains a list  * of mime types known to the system. It is used to look up the  * mime type for a specific file extension and to check if a file  * is an XML or binary resource.  *   * The mime type table is read from a file "mime-types.xml",  * which should reside in the directory identified in the exist home  * directory. If no such file is found, the class tries  * to load the default map from the org.exist.util package via the   * class loader.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|MimeTable
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|MimeTable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FILE_LOAD_FAILED_ERR
init|=
literal|"Failed to load mime-type table from "
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LOAD_FAILED_ERR
init|=
literal|"Failed to load mime-type table from class loader"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MIME_TYPES_XML
init|=
literal|"mime-types.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MIME_TYPES_XML_DEFAULT
init|=
literal|"org/exist/util/"
operator|+
name|MIME_TYPES_XML
decl_stmt|;
specifier|private
specifier|static
name|MimeTable
name|instance
init|=
literal|null
decl_stmt|;
comment|/** From where the mime table is loaded for message purpose */
specifier|private
name|String
name|src
decl_stmt|;
comment|/**      * Returns the singleton.      */
specifier|public
specifier|static
name|MimeTable
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|MimeTable
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
comment|/**      * Returns the singleton, using a custom mime-types.xml file      */
specifier|public
specifier|static
name|MimeTable
name|getInstance
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|MimeTable
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
comment|/**      * Returns the singleton, using a custom mime-types.xml stream,      * like for instance an internal database resource.      */
specifier|public
specifier|static
name|MimeTable
name|getInstance
parameter_list|(
specifier|final
name|InputStream
name|stream
parameter_list|,
specifier|final
name|String
name|src
parameter_list|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|MimeTable
argument_list|(
name|stream
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
specifier|private
name|MimeType
name|defaultMime
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MimeType
argument_list|>
name|mimeTypes
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MimeType
argument_list|>
name|extensions
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|preferredExtension
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|MimeTable
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
block|}
specifier|public
name|MimeTable
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|path
argument_list|)
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading mime table from file "
operator|+
name|path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
init|)
block|{
name|loadMimeTypes
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|src
operator|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
decl||
name|SAXException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|FILE_LOAD_FAILED_ERR
operator|+
name|path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|MimeTable
parameter_list|(
specifier|final
name|InputStream
name|stream
parameter_list|,
specifier|final
name|String
name|src
parameter_list|)
block|{
name|load
argument_list|(
name|stream
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
comment|/**      * Inform from where a mime-table is loaded      */
specifier|public
name|String
name|getSrc
parameter_list|()
block|{
return|return
name|this
operator|.
name|src
return|;
block|}
comment|//TODO: deprecate?
specifier|public
name|MimeType
name|getContentTypeFor
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
specifier|final
name|String
name|ext
init|=
name|getExtension
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
specifier|final
name|MimeType
name|mt
init|=
operator|(
name|ext
operator|==
literal|null
operator|)
condition|?
name|defaultMime
else|:
name|extensions
operator|.
name|get
argument_list|(
name|ext
argument_list|)
decl_stmt|;
return|return
operator|(
name|mt
operator|==
literal|null
operator|)
condition|?
name|defaultMime
else|:
name|mt
return|;
block|}
specifier|public
name|MimeType
name|getContentTypeFor
parameter_list|(
name|XmldbURI
name|fileName
parameter_list|)
block|{
return|return
name|getContentTypeFor
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|MimeType
name|getContentType
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
return|return
name|mimeTypes
operator|.
name|get
argument_list|(
name|mimeType
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllExtensions
parameter_list|(
name|MimeType
name|mimeType
parameter_list|)
block|{
return|return
name|getAllExtensions
argument_list|(
name|mimeType
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllExtensions
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|extns
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MimeType
argument_list|>
name|extension
range|:
name|extensions
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|MimeType
name|mt
init|=
name|extension
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|mt
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|mimeType
argument_list|)
condition|)
block|{
name|extns
operator|.
name|add
argument_list|(
name|extension
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|String
name|preferred
init|=
name|preferredExtension
operator|.
name|get
argument_list|(
name|mimeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|preferred
operator|!=
literal|null
operator|&&
operator|!
name|extns
operator|.
name|contains
argument_list|(
name|preferred
argument_list|)
condition|)
block|{
name|extns
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|preferred
argument_list|)
expr_stmt|;
block|}
return|return
name|extns
return|;
block|}
specifier|public
name|String
name|getPreferredExtension
parameter_list|(
name|MimeType
name|mimeType
parameter_list|)
block|{
return|return
name|getPreferredExtension
argument_list|(
name|mimeType
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPreferredExtension
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
return|return
name|preferredExtension
operator|.
name|get
argument_list|(
name|mimeType
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isXMLContent
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
specifier|final
name|String
name|ext
init|=
name|getExtension
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ext
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|MimeType
name|type
init|=
name|extensions
operator|.
name|get
argument_list|(
name|ext
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|type
operator|.
name|getType
argument_list|()
operator|==
name|MimeType
operator|.
name|XML
return|;
block|}
comment|/**      * Determine if the passed mime type is text, i.e. may require a charset      * declaration.      *       * @param mimeType      * @return TRUE if mimetype is for text content else FALSE      */
specifier|public
name|boolean
name|isTextContent
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
specifier|final
name|MimeType
name|mime
init|=
name|getContentType
argument_list|(
name|mimeType
argument_list|)
decl_stmt|;
return|return
name|mimeType
operator|.
name|startsWith
argument_list|(
literal|"text/"
argument_list|)
operator|||
name|mimeType
operator|.
name|endsWith
argument_list|(
literal|"xquery"
argument_list|)
operator|||
name|mime
operator|.
name|isXMLType
argument_list|()
return|;
block|}
specifier|private
name|String
name|getExtension
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
specifier|final
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|fileName
operator|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
expr_stmt|;
specifier|final
name|int
name|p
init|=
name|fileName
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|<
literal|0
operator|||
name|p
operator|+
literal|1
operator|==
name|fileName
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fileName
operator|.
name|substring
argument_list|(
name|p
argument_list|)
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
specifier|private
name|void
name|load
parameter_list|()
block|{
specifier|final
name|ClassLoader
name|cl
init|=
name|MimeTable
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
name|cl
operator|.
name|getResourceAsStream
argument_list|(
name|MIME_TYPES_XML_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|LOAD_FAILED_ERR
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|loadMimeTypes
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|this
operator|.
name|src
operator|=
literal|"resource://"
operator|+
name|MIME_TYPES_XML_DEFAULT
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
decl||
name|SAXException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|LOAD_FAILED_ERR
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|load
parameter_list|(
specifier|final
name|InputStream
name|stream
parameter_list|,
specifier|final
name|String
name|src
parameter_list|)
block|{
name|boolean
name|loaded
init|=
literal|false
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Loading mime table from stream "
operator|+
name|src
argument_list|)
expr_stmt|;
try|try
block|{
name|loadMimeTypes
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|src
operator|=
name|src
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
decl||
name|SAXException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|LOAD_FAILED_ERR
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|loaded
condition|)
block|{
specifier|final
name|ClassLoader
name|cl
init|=
name|MimeTable
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
name|cl
operator|.
name|getResourceAsStream
argument_list|(
name|MIME_TYPES_XML_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|LOAD_FAILED_ERR
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|loadMimeTypes
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|this
operator|.
name|src
operator|=
literal|"resource://"
operator|+
name|MIME_TYPES_XML_DEFAULT
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
decl||
name|SAXException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|LOAD_FAILED_ERR
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @param stream      * @throws SAXException       * @throws ParserConfigurationException       * @throws IOException       */
specifier|private
name|void
name|loadMimeTypes
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
specifier|final
name|SAXParserFactory
name|factory
init|=
name|ExistSAXParserFactory
operator|.
name|getSAXParserFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|stream
argument_list|)
decl_stmt|;
specifier|final
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
specifier|final
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setFeature
argument_list|(
literal|"http://xml.org/sax/features/external-general-entities"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setFeature
argument_list|(
literal|"http://xml.org/sax/features/external-parameter-entities"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setFeature
argument_list|(
name|FEATURE_SECURE_PROCESSING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
operator|new
name|MimeTableHandler
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|MimeTableHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|static
specifier|final
name|String
name|EXTENSIONS
init|=
literal|"extensions"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"description"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MIME_TYPE
init|=
literal|"mime-type"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MIME_TYPES
init|=
literal|"mime-types"
decl_stmt|;
specifier|private
name|MimeType
name|mime
init|=
literal|null
decl_stmt|;
specifier|private
name|FastStringBuffer
name|charBuf
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|64
argument_list|)
decl_stmt|;
comment|/* (non-Javadoc)          * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)          */
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|MIME_TYPES
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
comment|// Check for a default mime type settings
specifier|final
name|String
name|defaultMimeAttr
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"default-mime-type"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|defaultTypeAttr
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"default-resource-type"
argument_list|)
decl_stmt|;
comment|// Resource type default is XML
name|int
name|type
init|=
name|MimeType
operator|.
name|XML
decl_stmt|;
if|if
condition|(
name|defaultTypeAttr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"binary"
operator|.
name|equals
argument_list|(
name|defaultTypeAttr
argument_list|)
condition|)
block|{
name|type
operator|=
name|MimeType
operator|.
name|BINARY
expr_stmt|;
block|}
block|}
comment|// If a default-mime-type is specified, create a new default mime type
if|if
condition|(
name|defaultMimeAttr
operator|!=
literal|null
operator|&&
name|defaultMimeAttr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|defaultMime
operator|=
operator|new
name|MimeType
argument_list|(
name|defaultMimeAttr
argument_list|,
name|type
argument_list|)
expr_stmt|;
comment|// If the default-resource-type is specified, and the default-mime-type is unspecified, use a predefined type
block|}
if|else if
condition|(
name|defaultTypeAttr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|MimeType
operator|.
name|XML
condition|)
block|{
name|defaultMime
operator|=
name|MimeType
operator|.
name|XML_TYPE
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|==
name|MimeType
operator|.
name|BINARY
condition|)
block|{
name|defaultMime
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// the defaultMime is left to null, for backward compatibility with 1.2
block|}
comment|// Put the default mime into the mime map
if|if
condition|(
name|defaultMime
operator|!=
literal|null
condition|)
block|{
name|mimeTypes
operator|.
name|put
argument_list|(
name|defaultMime
operator|.
name|getName
argument_list|()
argument_list|,
name|defaultMime
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|MIME_TYPE
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No name specified for mime-type"
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|type
init|=
name|MimeType
operator|.
name|BINARY
decl_stmt|;
specifier|final
name|String
name|typeAttr
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeAttr
operator|!=
literal|null
operator|&&
literal|"xml"
operator|.
name|equals
argument_list|(
name|typeAttr
argument_list|)
condition|)
block|{
name|type
operator|=
name|MimeType
operator|.
name|XML
expr_stmt|;
block|}
name|mime
operator|=
operator|new
name|MimeType
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|mime
argument_list|)
expr_stmt|;
block|}
name|charBuf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)          */
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|MIME_TYPE
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|mime
operator|=
literal|null
expr_stmt|;
block|}
if|else if
condition|(
name|DESCRIPTION
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|description
init|=
name|charBuf
operator|.
name|getNormalizedString
argument_list|(
name|FastStringBuffer
operator|.
name|SUPPRESS_BOTH
argument_list|)
decl_stmt|;
name|mime
operator|.
name|setDescription
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|EXTENSIONS
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|extList
init|=
name|charBuf
operator|.
name|getNormalizedString
argument_list|(
name|FastStringBuffer
operator|.
name|SUPPRESS_BOTH
argument_list|)
decl_stmt|;
specifier|final
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|extList
argument_list|,
literal|", "
argument_list|)
decl_stmt|;
name|String
name|preferred
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|ext
init|=
name|tok
operator|.
name|nextToken
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|extensions
operator|.
name|containsKey
argument_list|(
name|ext
argument_list|)
condition|)
block|{
name|extensions
operator|.
name|put
argument_list|(
name|ext
argument_list|,
name|mime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|preferred
operator|==
literal|null
condition|)
block|{
name|preferred
operator|=
name|ext
expr_stmt|;
block|}
block|}
name|preferredExtension
operator|.
name|put
argument_list|(
name|mime
operator|.
name|getName
argument_list|()
argument_list|,
name|preferred
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* (non-Javadoc)          * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)          */
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|charBuf
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|MimeTable
name|table
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|MimeType
name|type
init|=
name|table
operator|.
name|getContentTypeFor
argument_list|(
literal|"samples/xquery/fibo.svg"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Not found!"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|type
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

