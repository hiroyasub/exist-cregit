begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org) * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *  *  $Id$ */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|exist
operator|.
name|dom
operator|.
name|BinaryDocument
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
name|QName
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
name|XMLUtil
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
name|Permission
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
name|security
operator|.
name|xacml
operator|.
name|AccessContext
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
name|DBSource
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
name|StringSource
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
name|XQueryPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|AttrList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|Receiver
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
name|CompiledXQuery
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
name|Constants
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
name|Expression
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
name|XQuery
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
name|util
operator|.
name|ExpressionDumper
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
name|SequenceIterator
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
name|Type
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
comment|/**  * A filter that listens for XInclude elements in the stream  * of events generated by the {@link org.exist.storage.serializers.Serializer}.  *   * XInclude elements are expanded at the position where they were found.  */
end_comment

begin_class
specifier|public
class|class
name|XIncludeFilter
implements|implements
name|Receiver
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XIncludeFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XINCLUDE_NS
init|=
literal|"http://www.w3.org/2001/XInclude"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|HREF_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"href"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|XPOINTER_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"xpointer"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
name|Receiver
name|receiver
decl_stmt|;
specifier|private
name|Serializer
name|serializer
decl_stmt|;
specifier|private
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
specifier|private
name|HashMap
name|namespaces
init|=
operator|new
name|HashMap
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|public
name|XIncludeFilter
parameter_list|(
name|Serializer
name|serializer
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
block|{
name|this
operator|.
name|receiver
operator|=
name|receiver
expr_stmt|;
name|this
operator|.
name|serializer
operator|=
name|serializer
expr_stmt|;
block|}
specifier|public
name|XIncludeFilter
parameter_list|(
name|Serializer
name|serializer
parameter_list|)
block|{
name|this
argument_list|(
name|serializer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setReceiver
parameter_list|(
name|Receiver
name|handler
parameter_list|)
block|{
name|this
operator|.
name|receiver
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|Receiver
name|getReceiver
parameter_list|()
block|{
return|return
name|receiver
return|;
block|}
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|document
operator|=
name|doc
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#characters(java.lang.CharSequence) 	 */
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#comment(char[], int, int) 	 */
specifier|public
name|void
name|comment
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
name|receiver
operator|.
name|comment
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#endDocument() 	 */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.util.serializer.Receiver#endElement(org.exist.dom.QName) 	 */
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|XINCLUDE_NS
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
name|receiver
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
name|namespaces
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.exist.util.serializer.Receiver#cdataSection(char[], int, int)      */
specifier|public
name|void
name|cdataSection
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|cdataSection
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#startDocument() 	 */
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#attribute(org.exist.dom.QName, java.lang.String) 	 */
specifier|public
name|void
name|attribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#startElement(org.exist.dom.QName, org.exist.util.serializer.AttrList) 	 */
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|,
name|AttrList
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
operator|&&
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|XINCLUDE_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|qname
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"include"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"processing include ..."
argument_list|)
expr_stmt|;
name|processXInclude
argument_list|(
name|attribs
operator|.
name|getValue
argument_list|(
name|HREF_ATTRIB
argument_list|)
argument_list|,
name|attribs
operator|.
name|getValue
argument_list|(
name|XPOINTER_ATTRIB
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//LOG.debug("start: " + qName);
name|receiver
operator|.
name|startElement
argument_list|(
name|qname
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|documentType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|documentType
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|processXInclude
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|xpointer
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|href
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"No href attribute found in XInclude include element"
argument_list|)
throw|;
comment|// save some settings
name|DocumentImpl
name|prevDoc
init|=
name|document
decl_stmt|;
name|boolean
name|createContainerElements
init|=
name|serializer
operator|.
name|createContainerElements
decl_stmt|;
name|serializer
operator|.
name|createContainerElements
operator|=
literal|false
expr_stmt|;
comment|//The following comments are the basis for possible external documents
name|XmldbURI
name|docUri
init|=
literal|null
decl_stmt|;
comment|//URI externalUri = null;
try|try
block|{
name|docUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|href
argument_list|)
expr_stmt|;
comment|/*                if(!stylesheetUri.toCollectionPathURI().equals(stylesheetUri)) {                    externalUri = stylesheetUri.getXmldbURI();                }                */
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|//could be an external URI!
comment|/*                try {                    externalUri = new URI(href);                } catch (URISyntaxException ee) {                */
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Stylesheet URI could not be parsed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
comment|//}
block|}
comment|// parse the href attribute
name|LOG
operator|.
name|debug
argument_list|(
literal|"found href=\""
operator|+
name|href
operator|+
literal|"\""
argument_list|)
expr_stmt|;
comment|//String xpointer = null;
comment|//String docName = href;
name|String
name|fragment
init|=
name|docUri
operator|.
name|getFragment
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fragment
operator|==
literal|null
operator|||
name|fragment
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Fragment identifiers must not be used in an xinclude href attribute. To specify an "
operator|+
literal|"xpointer, use the xpointer attribute."
argument_list|)
throw|;
comment|//        if(xpointer!=null) {
comment|//            try {
comment|//                xpointer = XMLUtil.decodeAttrMarkup(URLDecoder.decode(xpointer, "UTF-8"));
comment|//            } catch (UnsupportedEncodingException e) {
comment|//            	LOG.warn(e);
comment|//            }
comment|//            // remove the fragment part from the URI for further processing
comment|//            URI u = docUri.getURI();
comment|//            try {
comment|//                u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u.getPort(), u.getPath(), u.getQuery(), null);
comment|//                docUri = XmldbURI.xmldbUriFor(u);
comment|//            } catch (URISyntaxException e) {
comment|//                throw new IllegalArgumentException("Stylesheet URI could not be parsed: " + e.getMessage());
comment|//            }
comment|//        }
comment|// extract possible parameters in the URI
name|Map
name|params
init|=
literal|null
decl_stmt|;
name|String
name|paramStr
init|=
name|docUri
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|paramStr
operator|!=
literal|null
condition|)
block|{
name|params
operator|=
name|processParameters
argument_list|(
name|paramStr
argument_list|)
expr_stmt|;
block|}
comment|// if docName has no collection specified, assume
comment|// current collection
comment|// Patch 1520454 start
if|if
condition|(
operator|!
name|docUri
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|String
name|base
init|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
operator|+
literal|"/"
decl_stmt|;
name|String
name|child
init|=
literal|"./"
operator|+
name|docUri
operator|.
name|toString
argument_list|()
decl_stmt|;
name|URI
name|baseUri
init|=
name|URI
operator|.
name|create
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|URI
name|childUri
init|=
name|URI
operator|.
name|create
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|URI
name|uri
init|=
name|baseUri
operator|.
name|resolve
argument_list|(
name|childUri
argument_list|)
decl_stmt|;
name|docUri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
comment|// Patch 1520454 end
comment|// retrieve the document
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|serializer
operator|.
name|broker
operator|.
name|getXMLResource
argument_list|(
name|docUri
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
operator|&&
operator|!
name|doc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|serializer
operator|.
name|broker
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Permission denied to read xincluded resource"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"permission denied"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|/* if document has not been found and xpointer is                * null, throw an exception. If xpointer != null                * we retry below and interpret docName as                * a collection.                */
if|if
condition|(
name|doc
operator|==
literal|null
operator|&&
name|xpointer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"document "
operator|+
name|docUri
operator|+
literal|" not found"
argument_list|)
throw|;
comment|/* Check if the document is a stored XQuery */
name|boolean
name|xqueryDoc
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
operator|&&
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
name|xqueryDoc
operator|=
literal|"application/xquery"
operator|.
name|equals
argument_list|(
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xpointer
operator|==
literal|null
operator|&&
operator|!
name|xqueryDoc
condition|)
comment|// no xpointer found - just serialize the doc
name|serializer
operator|.
name|serializeToReceiver
argument_list|(
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
else|else
block|{
comment|// process the xpointer or the stored XQuery
try|try
block|{
name|Source
name|source
decl_stmt|;
if|if
condition|(
name|xpointer
operator|==
literal|null
condition|)
name|source
operator|=
operator|new
name|DBSource
argument_list|(
name|serializer
operator|.
name|broker
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|,
literal|true
argument_list|)
expr_stmt|;
else|else
block|{
name|xpointer
operator|=
name|checkNamespaces
argument_list|(
name|xpointer
argument_list|)
expr_stmt|;
name|source
operator|=
operator|new
name|StringSource
argument_list|(
name|xpointer
argument_list|)
expr_stmt|;
block|}
name|XQuery
name|xquery
init|=
name|serializer
operator|.
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryPool
name|pool
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|serializer
operator|.
name|broker
argument_list|,
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|!=
literal|null
condition|)
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
else|else
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|XINCLUDE
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareNamespaces
argument_list|(
name|namespaces
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareNamespace
argument_list|(
literal|"xinclude"
argument_list|,
name|XINCLUDE_NS
argument_list|)
expr_stmt|;
comment|//TODO: change these to putting the XmldbURI in, but we need to warn users!
name|context
operator|.
name|declareVariable
argument_list|(
literal|"xinclude:current-doc"
argument_list|,
name|document
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"xinclude:current-collection"
argument_list|,
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|xpointer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|XmldbURI
index|[]
block|{
name|doc
operator|.
name|getURI
argument_list|()
block|}
argument_list|)
expr_stmt|;
else|else
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|XmldbURI
index|[]
block|{
name|docUri
block|}
argument_list|)
expr_stmt|;
block|}
comment|// pass parameters as variables
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|params
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|,
name|xpointer
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"I/O error while reading query for xinclude: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"xpointer query: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
operator|(
name|Expression
operator|)
name|compiled
argument_list|)
argument_list|)
expr_stmt|;
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|seq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"xpointer found: "
operator|+
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeValue
name|node
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|serializeToReceiver
argument_list|(
name|node
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|val
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
name|seq
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|val
operator|=
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|characters
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"xpointer error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Error while processing XInclude expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// restore settings
name|document
operator|=
name|prevDoc
expr_stmt|;
name|serializer
operator|.
name|createContainerElements
operator|=
name|createContainerElements
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Process xmlns() schema. We process these here, because namespace mappings should 	 * already been known when parsing the xpointer() expression. 	 *  	 * @param xpointer 	 * @return 	 * @throws XPathException 	 */
specifier|private
name|String
name|checkNamespaces
parameter_list|(
name|String
name|xpointer
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|p0
decl_stmt|;
while|while
condition|(
operator|(
name|p0
operator|=
name|xpointer
operator|.
name|indexOf
argument_list|(
literal|"xmlns("
argument_list|)
operator|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
if|if
condition|(
name|p0
operator|<
literal|0
condition|)
return|return
name|xpointer
return|;
name|int
name|p1
init|=
name|xpointer
operator|.
name|indexOf
argument_list|(
literal|')'
argument_list|,
name|p0
operator|+
literal|6
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|<
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"expected ) for xmlns()"
argument_list|)
throw|;
name|String
name|mapping
init|=
name|xpointer
operator|.
name|substring
argument_list|(
name|p0
operator|+
literal|6
argument_list|,
name|p1
argument_list|)
decl_stmt|;
name|xpointer
operator|=
name|xpointer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p0
argument_list|)
operator|+
name|xpointer
operator|.
name|substring
argument_list|(
name|p1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|mapping
argument_list|,
literal|"= \t\n"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tok
operator|.
name|countTokens
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"expected prefix=namespace mapping in "
operator|+
name|mapping
argument_list|)
throw|;
name|String
name|prefix
init|=
name|tok
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|String
name|namespaceURI
init|=
name|tok
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
return|return
name|xpointer
return|;
block|}
specifier|protected
name|HashMap
name|processParameters
parameter_list|(
name|String
name|args
parameter_list|)
block|{
name|HashMap
name|parameters
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|String
name|param
decl_stmt|;
name|String
name|value
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
literal|0
decl_stmt|;
name|int
name|l
init|=
name|args
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|start
operator|<
name|l
operator|)
operator|&&
operator|(
name|end
operator|<
name|l
operator|)
condition|)
block|{
while|while
condition|(
operator|(
name|end
operator|<
name|l
operator|)
operator|&&
operator|(
name|args
operator|.
name|charAt
argument_list|(
name|end
operator|++
argument_list|)
operator|!=
literal|'='
operator|)
condition|)
empty_stmt|;
if|if
condition|(
name|end
operator|==
name|l
condition|)
break|break;
name|param
operator|=
name|args
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
operator|-
literal|1
argument_list|)
expr_stmt|;
name|start
operator|=
name|end
expr_stmt|;
while|while
condition|(
operator|(
name|end
operator|<
name|l
operator|)
operator|&&
operator|(
name|args
operator|.
name|charAt
argument_list|(
name|end
operator|++
argument_list|)
operator|!=
literal|'&'
operator|)
condition|)
empty_stmt|;
if|if
condition|(
name|end
operator|==
name|l
condition|)
name|value
operator|=
name|args
operator|.
name|substring
argument_list|(
name|start
argument_list|)
expr_stmt|;
else|else
name|value
operator|=
name|args
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
operator|-
literal|1
argument_list|)
expr_stmt|;
name|start
operator|=
name|end
expr_stmt|;
try|try
block|{
name|param
operator|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|param
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|value
operator|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|value
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"parameter: "
operator|+
name|param
operator|+
literal|" = "
operator|+
name|value
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|put
argument_list|(
name|param
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parameters
return|;
block|}
block|}
end_class

end_unit

