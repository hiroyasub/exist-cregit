begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Parser.java - eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
name|SAXParserFactory
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
name|Category
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
name|AttrImpl
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
name|CommentImpl
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
name|DocumentTypeImpl
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
name|ElementImpl
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
name|ProcessingInstructionImpl
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
name|TextImpl
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
name|util
operator|.
name|Configuration
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
name|FastStringBuffer
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
name|ProgressIndicator
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
name|XMLString
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
name|ContentHandler
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
name|ErrorHandler
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
name|Locator
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
name|SAXNotRecognizedException
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
name|SAXNotSupportedException
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
name|SAXParseException
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
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_comment
comment|/**  * Parses a given input document via SAX, stores it to  * the database and handles index-creation.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|Indexer
extends|extends
name|Observable
implements|implements
name|ContentHandler
implements|,
name|LexicalHandler
implements|,
name|ErrorHandler
block|{
specifier|private
specifier|final
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|Parser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
specifier|protected
name|XMLString
name|charBuf
init|=
operator|new
name|XMLString
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|currentLine
init|=
literal|0
decl_stmt|;
specifier|protected
name|StringBuffer
name|currentPath
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
specifier|protected
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|insideDTD
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|validate
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|level
init|=
literal|0
decl_stmt|;
specifier|protected
name|Locator
name|locator
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|normalize
init|=
name|XMLString
operator|.
name|SUPPRESS_BOTH
decl_stmt|;
specifier|protected
name|Map
name|nsMappings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Element
name|rootNode
decl_stmt|;
specifier|protected
name|Stack
name|stack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|privileged
init|=
literal|false
decl_stmt|;
specifier|protected
name|String
name|ignorePrefix
init|=
literal|null
decl_stmt|;
specifier|protected
name|ProgressIndicator
name|progress
decl_stmt|;
comment|// reusable fields
specifier|private
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|()
decl_stmt|;
specifier|private
name|Stack
name|usedElements
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
name|FastStringBuffer
name|temp
init|=
operator|new
name|FastStringBuffer
argument_list|()
decl_stmt|;
comment|/** 	 *  Create a new parser using the given database broker and 	 * user to store the document. 	 * 	 *@param  broker               	 *@param  user                user identity 	 *@param  replace             replace existing documents? 	 *@exception  EXistException   	 */
specifier|public
name|Indexer
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Create a new parser using the given database broker and 	 * user to store the document. 	 * 	 *@param  broker               	 *@param  user                user identity 	 *@param  replace             replace existing documents? 	 *@param  privileged		  used by the security manager to 	 *							  indicate that it needs privileged 	 *                            access to the db. 	 *@exception  EXistException   	 */
specifier|public
name|Indexer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|boolean
name|priv
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|privileged
operator|=
name|priv
expr_stmt|;
name|Configuration
name|config
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|String
name|suppressWS
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"indexer.suppress-whitespace"
argument_list|)
decl_stmt|;
if|if
condition|(
name|suppressWS
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|suppressWS
operator|.
name|equals
argument_list|(
literal|"leading"
argument_list|)
condition|)
name|normalize
operator|=
name|XMLString
operator|.
name|SUPPRESS_LEADING_WS
expr_stmt|;
if|else if
condition|(
name|suppressWS
operator|.
name|equals
argument_list|(
literal|"trailing"
argument_list|)
condition|)
name|normalize
operator|=
name|XMLString
operator|.
name|SUPPRESS_TRAILING_WS
expr_stmt|;
if|else if
condition|(
name|suppressWS
operator|.
name|equals
argument_list|(
literal|"none"
argument_list|)
condition|)
name|normalize
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setBroker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|void
name|setValidating
parameter_list|(
name|boolean
name|validate
parameter_list|)
block|{
name|this
operator|.
name|validate
operator|=
name|validate
expr_stmt|;
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
comment|// reset internal fields
name|level
operator|=
literal|0
expr_stmt|;
name|currentPath
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|stack
operator|=
operator|new
name|Stack
argument_list|()
expr_stmt|;
name|nsMappings
operator|.
name|clear
argument_list|()
expr_stmt|;
name|rootNode
operator|=
literal|null
expr_stmt|;
block|}
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
block|{
if|if
condition|(
name|length
operator|<=
literal|0
condition|)
return|return;
if|if
condition|(
name|charBuf
operator|!=
literal|null
condition|)
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
else|else
block|{
name|charBuf
operator|=
operator|new
name|XMLString
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
block|{
if|if
condition|(
name|insideDTD
condition|)
return|return;
name|CommentImpl
name|comment
init|=
operator|new
name|CommentImpl
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|comment
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|stack
operator|.
name|empty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|comment
argument_list|,
name|currentPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|document
operator|.
name|appendChild
argument_list|(
name|comment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|charBuf
operator|!=
literal|null
operator|&&
name|charBuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|XMLString
name|normalized
init|=
name|charBuf
operator|.
name|normalize
argument_list|(
name|normalize
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalized
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//TextImpl text =
comment|//    new TextImpl( normalized );
name|text
operator|.
name|setData
argument_list|(
name|normalized
argument_list|)
expr_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|text
argument_list|,
name|currentPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|charBuf
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|last
operator|.
name|appendChildInternal
argument_list|(
name|comment
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|comment
argument_list|,
name|currentPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endCDATA
parameter_list|()
block|{
block|}
specifier|public
name|void
name|endDTD
parameter_list|()
block|{
name|insideDTD
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
if|if
condition|(
operator|!
name|validate
condition|)
block|{
name|progress
operator|.
name|finish
argument_list|()
expr_stmt|;
name|setChanged
argument_list|()
expr_stmt|;
name|notifyObservers
argument_list|(
name|progress
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|qname
parameter_list|)
block|{
comment|//		if(namespace != null&& namespace.length()> 0&&
comment|//			qname.indexOf(':')< 0)
comment|//			qname = '#' + namespace + ':' + qname;
specifier|final
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
condition|)
block|{
if|if
condition|(
name|charBuf
operator|!=
literal|null
operator|&&
name|charBuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|XMLString
name|normalized
init|=
name|charBuf
operator|.
name|normalize
argument_list|(
name|normalize
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalized
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|text
operator|.
name|setData
argument_list|(
name|normalized
argument_list|)
expr_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|text
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|text
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|charBuf
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|currentPath
operator|.
name|delete
argument_list|(
name|currentPath
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|currentPath
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//				currentPath.substring(0, currentPath.lastIndexOf('/'));
if|if
condition|(
name|validate
condition|)
block|{
if|if
condition|(
name|document
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|<
name|last
operator|.
name|getChildCount
argument_list|()
condition|)
block|{
name|document
operator|.
name|setTreeLevelOrder
argument_list|(
name|level
argument_list|,
name|last
operator|.
name|getChildCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|document
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getDatabaseType
argument_list|()
operator|==
name|DBBroker
operator|.
name|DBM
operator|||
name|broker
operator|.
name|getDatabaseType
argument_list|()
operator|==
name|DBBroker
operator|.
name|NATIVE
condition|)
block|{
if|if
condition|(
name|last
operator|.
name|getChildCount
argument_list|()
operator|>
literal|0
condition|)
name|broker
operator|.
name|update
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
else|else
name|broker
operator|.
name|store
argument_list|(
name|last
argument_list|,
name|currentPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|level
operator|--
expr_stmt|;
if|if
condition|(
name|last
operator|!=
name|rootNode
condition|)
block|{
name|last
operator|.
name|clear
argument_list|()
expr_stmt|;
name|usedElements
operator|.
name|push
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|endEntity
parameter_list|(
name|String
name|name
parameter_list|)
block|{
block|}
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|ignorePrefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|equals
argument_list|(
name|ignorePrefix
argument_list|)
condition|)
block|{
name|ignorePrefix
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|nsMappings
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|error
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
throws|throws
name|SAXException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"error at line "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"error at line "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|": "
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
specifier|public
name|void
name|fatalError
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
throws|throws
name|SAXException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"fatal error at line "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"fatal error at line "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|": "
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
specifier|public
name|void
name|ignorableWhitespace
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
block|{
block|}
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
block|{
name|ProcessingInstructionImpl
name|pi
init|=
operator|new
name|ProcessingInstructionImpl
argument_list|(
literal|0
argument_list|,
name|target
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|pi
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|pi
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|document
operator|.
name|appendChild
argument_list|(
name|pi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|charBuf
operator|!=
literal|null
operator|&&
name|charBuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|XMLString
name|normalized
init|=
name|charBuf
operator|.
name|normalize
argument_list|(
name|normalize
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalized
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//TextImpl text =
comment|//    new TextImpl( normalized );
name|text
operator|.
name|setData
argument_list|(
name|normalized
argument_list|)
expr_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|text
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|text
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|charBuf
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|last
operator|.
name|appendChildInternal
argument_list|(
name|pi
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|pi
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
name|this
operator|.
name|locator
operator|=
name|locator
expr_stmt|;
block|}
comment|/** 	 *  set SAX parser feature. This method will catch (and ignore) exceptions 	 *  if the used parser does not support a feature. 	 * 	 *@param  factory   	 *@param  feature   	 *@param  value     	 */
specifier|private
name|void
name|setFeature
parameter_list|(
name|SAXParserFactory
name|factory
parameter_list|,
name|String
name|feature
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
try|try
block|{
name|factory
operator|.
name|setFeature
argument_list|(
name|feature
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotRecognizedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
name|snse
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|snse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|pce
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|pce
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|name
parameter_list|)
block|{
block|}
specifier|public
name|void
name|startCDATA
parameter_list|()
block|{
block|}
comment|// Methods of interface LexicalHandler
comment|// used to determine Doctype
specifier|public
name|void
name|startDTD
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
block|{
name|DocumentTypeImpl
name|docType
init|=
operator|new
name|DocumentTypeImpl
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
decl_stmt|;
name|document
operator|.
name|setDocumentType
argument_list|(
name|docType
argument_list|)
expr_stmt|;
name|insideDTD
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
block|{
if|if
condition|(
operator|!
name|validate
condition|)
block|{
name|progress
operator|=
operator|new
name|ProgressIndicator
argument_list|(
name|currentLine
argument_list|,
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|getDoctype
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// we don't know the doctype
comment|// set it to the root node's tag name
specifier|final
name|DocumentTypeImpl
name|dt
init|=
operator|new
name|DocumentTypeImpl
argument_list|(
name|rootNode
operator|.
name|getTagName
argument_list|()
argument_list|,
literal|null
argument_list|,
name|document
operator|.
name|getFileName
argument_list|()
argument_list|)
decl_stmt|;
name|document
operator|.
name|setDocumentType
argument_list|(
name|dt
argument_list|)
expr_stmt|;
block|}
name|document
operator|.
name|setChildCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|qname
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
block|{
comment|// calculate number of real attributes:
comment|// don't store namespace declarations
name|int
name|attrLength
init|=
name|attributes
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|String
name|attrQName
decl_stmt|;
name|String
name|attrNS
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
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrNS
operator|=
name|attributes
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrQName
operator|=
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|attrQName
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
operator|||
name|attrNS
operator|.
name|equals
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|)
condition|)
operator|--
name|attrLength
expr_stmt|;
block|}
name|ElementImpl
name|last
init|=
literal|null
decl_stmt|;
name|ElementImpl
name|node
init|=
literal|null
decl_stmt|;
name|int
name|p
init|=
name|qname
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|p
operator|>
operator|-
literal|1
condition|?
name|qname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
else|:
literal|""
decl_stmt|;
name|QName
name|qn
init|=
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|namespace
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stack
operator|.
name|empty
argument_list|()
condition|)
block|{
name|last
operator|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
expr_stmt|;
if|if
condition|(
name|charBuf
operator|!=
literal|null
operator|&&
name|charBuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// mixed element content: don't normalize the text node, just check
comment|// if there is any text at all
specifier|final
name|XMLString
name|normalized
init|=
name|charBuf
operator|.
name|normalize
argument_list|(
name|normalize
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalized
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|text
operator|.
name|setData
argument_list|(
name|charBuf
argument_list|)
expr_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|text
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|text
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|charBuf
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|usedElements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|node
operator|=
operator|(
name|ElementImpl
operator|)
name|usedElements
operator|.
name|pop
argument_list|()
expr_stmt|;
name|node
operator|.
name|setNodeName
argument_list|(
name|qn
argument_list|)
expr_stmt|;
block|}
else|else
name|node
operator|=
operator|new
name|ElementImpl
argument_list|(
name|qn
argument_list|)
expr_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|node
operator|.
name|setAttributes
argument_list|(
operator|(
name|short
operator|)
name|attrLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|nsMappings
operator|!=
literal|null
operator|&&
name|nsMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|node
operator|.
name|setNamespaceMappings
argument_list|(
name|nsMappings
argument_list|)
expr_stmt|;
name|nsMappings
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|stack
operator|.
name|push
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|currentPath
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|qname
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
block|{
name|broker
operator|.
name|store
argument_list|(
name|node
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|validate
condition|)
name|node
operator|=
operator|new
name|ElementImpl
argument_list|(
literal|0
argument_list|,
name|qn
argument_list|)
expr_stmt|;
else|else
name|node
operator|=
operator|new
name|ElementImpl
argument_list|(
literal|1
argument_list|,
name|qn
argument_list|)
expr_stmt|;
name|rootNode
operator|=
name|node
expr_stmt|;
name|node
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|node
operator|.
name|setAttributes
argument_list|(
operator|(
name|short
operator|)
name|attrLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|nsMappings
operator|!=
literal|null
operator|&&
name|nsMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|node
operator|.
name|setNamespaceMappings
argument_list|(
name|nsMappings
argument_list|)
expr_stmt|;
name|nsMappings
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|stack
operator|.
name|push
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|currentPath
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|qname
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
block|{
name|broker
operator|.
name|store
argument_list|(
name|node
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
name|document
operator|.
name|appendChild
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|level
operator|++
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|getMaxDepth
argument_list|()
operator|<
name|level
condition|)
name|document
operator|.
name|setMaxDepth
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|String
name|attrPrefix
decl_stmt|;
name|String
name|attrLocalName
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
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrNS
operator|=
name|attributes
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrLocalName
operator|=
name|attributes
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrQName
operator|=
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// skip xmlns-attributes and attributes in eXist's namespace
if|if
condition|(
name|attrQName
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
operator|||
name|attrNS
operator|.
name|equals
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|)
condition|)
operator|--
name|attrLength
expr_stmt|;
else|else
block|{
name|p
operator|=
name|attrQName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|attrPrefix
operator|=
operator|(
name|p
operator|>
operator|-
literal|1
operator|)
condition|?
name|attrQName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
else|:
literal|null
expr_stmt|;
specifier|final
name|AttrImpl
name|attr
init|=
operator|new
name|AttrImpl
argument_list|(
operator|new
name|QName
argument_list|(
name|attrLocalName
argument_list|,
name|attrNS
argument_list|,
name|attrPrefix
argument_list|)
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|attr
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|attributes
operator|.
name|getType
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
literal|"ID"
argument_list|)
condition|)
name|attr
operator|.
name|setType
argument_list|(
name|AttrImpl
operator|.
name|ID
argument_list|)
expr_stmt|;
name|node
operator|.
name|appendChildInternal
argument_list|(
name|attr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
name|broker
operator|.
name|store
argument_list|(
name|attr
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|attrLength
operator|>
literal|0
condition|)
name|node
operator|.
name|setAttributes
argument_list|(
operator|(
name|short
operator|)
name|attrLength
argument_list|)
expr_stmt|;
comment|// notify observers about progress every 100 lines
if|if
condition|(
name|locator
operator|!=
literal|null
condition|)
block|{
name|currentLine
operator|=
name|locator
operator|.
name|getLineNumber
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|validate
condition|)
block|{
name|progress
operator|.
name|setValue
argument_list|(
name|currentLine
argument_list|)
expr_stmt|;
if|if
condition|(
name|progress
operator|.
name|changed
argument_list|()
condition|)
block|{
name|setChanged
argument_list|()
expr_stmt|;
name|notifyObservers
argument_list|(
name|progress
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|startEntity
parameter_list|(
name|String
name|name
parameter_list|)
block|{
block|}
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
block|{
comment|// skip the eXist namespace
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|)
condition|)
block|{
name|ignorePrefix
operator|=
name|prefix
expr_stmt|;
return|return;
block|}
name|nsMappings
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|prepareForStore
parameter_list|()
block|{
block|}
specifier|public
name|void
name|warning
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
throws|throws
name|SAXException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"warning at line "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"warning at line "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|": "
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
end_class

end_unit

