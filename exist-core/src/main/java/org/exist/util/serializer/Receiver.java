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
name|util
operator|.
name|serializer
package|;
end_package

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
name|dom
operator|.
name|INodeHandle
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * A receiver is similar to the SAX content handler and lexical handler interfaces, but  * uses some higher level types as arguments. For example, element names are internally  * stored as QName objects, so startElement and endElement expect a QName. This way,  * we avoid copying objects.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Receiver
parameter_list|<
name|T
extends|extends
name|INodeHandle
parameter_list|>
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|MATCH_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"match"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
argument_list|)
decl_stmt|;
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
function_decl|;
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
function_decl|;
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespaceURI
parameter_list|)
throws|throws
name|SAXException
function_decl|;
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
function_decl|;
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
function_decl|;
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
function_decl|;
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
function_decl|;
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
function_decl|;
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
function_decl|;
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
function_decl|;
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
function_decl|;
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
function_decl|;
specifier|public
name|void
name|highlightText
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
function_decl|;
specifier|public
name|void
name|setCurrentNode
parameter_list|(
name|T
name|node
parameter_list|)
function_decl|;
specifier|public
name|Document
name|getDocument
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

