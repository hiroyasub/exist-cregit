begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|dbgp
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
name|mina
operator|.
name|core
operator|.
name|session
operator|.
name|IoSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|DebuggerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|Response
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
name|SAXAdapter
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|NodeList
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
name|Text
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|ResponseImpl
implements|implements
name|Response
block|{
specifier|private
name|IoSession
name|session
decl_stmt|;
specifier|private
name|Element
name|parsedResponse
init|=
literal|null
decl_stmt|;
specifier|public
name|ResponseImpl
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|InputStream
name|inputStream
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|factory
operator|.
name|newSAXParser
argument_list|()
expr_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|parsedResponse
operator|=
operator|(
name|Element
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ResponseImpl parsedResponse = "
operator|+
name|parsedResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
block|}
block|}
specifier|protected
name|boolean
name|isValid
parameter_list|()
block|{
return|return
operator|(
name|parsedResponse
operator|!=
literal|null
operator|)
return|;
block|}
specifier|protected
name|DebuggerImpl
name|getDebugger
parameter_list|()
block|{
return|return
operator|(
name|DebuggerImpl
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
literal|"debugger"
argument_list|)
return|;
block|}
specifier|public
name|IoSession
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
specifier|public
name|String
name|getTransactionID
parameter_list|()
block|{
if|if
condition|(
name|parsedResponse
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"init"
argument_list|)
condition|)
return|return
literal|"init"
return|;
return|return
name|getAttribute
argument_list|(
literal|"transaction_id"
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasAttribute
parameter_list|(
name|String
name|attr
parameter_list|)
block|{
return|return
name|parsedResponse
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
name|attr
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
name|String
name|getAttribute
parameter_list|(
name|String
name|attr
parameter_list|)
block|{
name|Node
name|item
init|=
name|parsedResponse
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
name|attr
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|//raise error?
return|return
name|item
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
block|{
name|Node
name|node
init|=
name|parsedResponse
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
return|return
operator|(
operator|(
name|Text
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|NodeList
name|getElemetsByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|parsedResponse
operator|.
name|getElementsByTagName
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

