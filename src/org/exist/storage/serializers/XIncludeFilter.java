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
name|StringReader
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
name|NodeProxy
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
name|NodeSet
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
name|xquery
operator|.
name|PathExpr
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
name|parser
operator|.
name|XQueryLexer
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
name|parser
operator|.
name|XQueryParser
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
name|parser
operator|.
name|XQueryTreeParser
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

begin_import
import|import
name|antlr
operator|.
name|RecognitionException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenStreamException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|collections
operator|.
name|AST
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
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#endElement(org.exist.dom.QName) 	 */
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
specifier|protected
name|void
name|processXInclude
parameter_list|(
name|String
name|href
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
comment|// parse the href attribute
if|if
condition|(
name|href
operator|!=
literal|null
condition|)
block|{
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
name|String
name|xpointer
init|=
literal|null
decl_stmt|;
name|String
name|docName
init|=
name|href
decl_stmt|;
comment|// try to find xpointer part
name|int
name|p
init|=
name|href
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|<
name|p
condition|)
block|{
name|docName
operator|=
name|href
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|xpointer
operator|=
name|XMLUtil
operator|.
name|decodeAttrMarkup
argument_list|(
name|href
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found xpointer: "
operator|+
name|xpointer
argument_list|)
expr_stmt|;
block|}
comment|// if docName has no collection specified, assume
comment|// current collection
name|p
operator|=
name|docName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|<
literal|0
operator|&&
name|document
operator|!=
literal|null
condition|)
name|docName
operator|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|docName
expr_stmt|;
comment|// retrieve the document
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading "
operator|+
name|docName
argument_list|)
expr_stmt|;
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
name|getDocument
argument_list|(
name|docName
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
comment|/* if document has not been found and xpointer is 			 * null, throw an exception. If xpointer != null 			 * we retry below and interpret docName as 			 * a collection. 			 */
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
name|docName
operator|+
literal|" not found"
argument_list|)
throw|;
if|if
condition|(
name|xpointer
operator|==
literal|null
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
comment|// process the xpointer
try|try
block|{
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|serializer
operator|.
name|broker
argument_list|)
decl_stmt|;
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
name|String
index|[]
block|{
name|doc
operator|.
name|getName
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
name|String
index|[]
block|{
name|docName
block|}
argument_list|)
expr_stmt|;
name|xpointer
operator|=
name|checkNamespaces
argument_list|(
name|context
argument_list|,
name|xpointer
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareNamespaces
argument_list|(
name|namespaces
argument_list|)
expr_stmt|;
name|XQueryLexer
name|lexer
init|=
operator|new
name|XQueryLexer
argument_list|(
name|context
argument_list|,
operator|new
name|StringReader
argument_list|(
name|xpointer
argument_list|)
argument_list|)
decl_stmt|;
name|XQueryParser
name|parser
init|=
operator|new
name|XQueryParser
argument_list|(
name|lexer
argument_list|)
decl_stmt|;
name|XQueryTreeParser
name|treeParser
init|=
operator|new
name|XQueryTreeParser
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|parser
operator|.
name|xpointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
block|}
name|AST
name|ast
init|=
name|parser
operator|.
name|getAST
argument_list|()
decl_stmt|;
name|PathExpr
name|expr
init|=
operator|new
name|PathExpr
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|treeParser
operator|.
name|xpointer
argument_list|(
name|ast
argument_list|,
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
name|treeParser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|treeParser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
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
name|expr
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|expr
operator|.
name|analyze
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|expr
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Sequence
name|seq
init|=
name|expr
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|seq
operator|.
name|getItemType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|NODE
case|:
name|NodeSet
name|set
init|=
operator|(
name|NodeSet
operator|)
name|seq
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"xpointer found: "
operator|+
name|set
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|proxy
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|set
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
name|proxy
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|serializeToReceiver
argument_list|(
name|proxy
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
break|break;
default|default :
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
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|RecognitionException
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
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TokenStreamException
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
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
comment|/** 	 * Process xmlns() schema. We process these here, because namespace mappings should 	 * already been known when parsing the xpointer() expression. 	 *  	 * @param context 	 * @param xpointer 	 * @return 	 * @throws XPathException 	 */
specifier|private
name|String
name|checkNamespaces
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|xpointer
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|p0
init|=
operator|-
literal|1
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
operator|>
operator|-
literal|1
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
block|}
end_class

end_unit

