begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id: MailSessionFunctions.java 9745 2009-08-09 21:37:29Z ixitar $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|xmpp
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
name|StringReader
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
name|QName
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
name|memtree
operator|.
name|SAXAdapter
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionCall
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
name|FunctionSignature
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
name|FunctionParameterSequenceType
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
name|FunctionReference
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
name|FunctionReturnSequenceType
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
name|IntegerValue
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
name|SequenceType
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
name|jivesoftware
operator|.
name|smack
operator|.
name|Chat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|XMPPConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|packet
operator|.
name|Message
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

begin_class
specifier|public
class|class
name|XMPPChatFunction
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XMPPChatFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"create-chat"
argument_list|,
name|XMPPModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMPPModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"create a new XMPP chat."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"connection"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The connection handle for chat."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"JID"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The user JID this chat with is."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"listener"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Listener is the function takes 3 parameters e.g. "
operator|+
literal|"user:chat-listener($chat as xs:integer, $message as xs:item?, $param as item()*) as empty(). "
operator|+
literal|"$message is incomming message from the listened $chat."
operator|+
literal|"$param is an any additional parameters sequence."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"param"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The sequense of any additional parameters for listener."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"an xs:long representing the chat handle."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|XMPPChatFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
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
name|long
name|connectionHandle
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|XMPPConnection
name|connection
init|=
name|XMPPModule
operator|.
name|retrieveConnection
argument_list|(
name|connectionHandle
argument_list|)
decl_stmt|;
name|String
name|jid
init|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|FunctionReference
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"No chat listener function provided."
argument_list|)
throw|;
name|FunctionReference
name|chatListenerFunctionRef
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FunctionCall
name|chatListenerFunction
init|=
name|chatListenerFunctionRef
operator|.
name|getFunctionCall
argument_list|()
decl_stmt|;
name|FunctionSignature
name|chatListenerFunctionSig
init|=
name|chatListenerFunction
operator|.
name|getSignature
argument_list|()
decl_stmt|;
if|if
condition|(
name|chatListenerFunctionSig
operator|.
name|getArgumentCount
argument_list|()
operator|<
literal|3
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Chat listener function must take at least 3 arguments."
argument_list|)
throw|;
name|chatListenerFunction
operator|.
name|setContext
argument_list|(
name|context
operator|.
name|copyContext
argument_list|()
argument_list|)
expr_stmt|;
name|Sequence
name|listenerParam
init|=
name|args
index|[
literal|3
index|]
decl_stmt|;
name|long
name|chatHandle
init|=
name|XMPPModule
operator|.
name|getHandle
argument_list|()
decl_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|(
name|chatHandle
argument_list|,
name|contextSequence
argument_list|,
name|chatListenerFunction
argument_list|,
name|listenerParam
argument_list|)
decl_stmt|;
name|Chat
name|chat
init|=
name|connection
operator|.
name|getChatManager
argument_list|()
operator|.
name|createChat
argument_list|(
name|jid
argument_list|,
name|listener
argument_list|)
decl_stmt|;
comment|// store the chat and return the handle of the chat
name|IntegerValue
name|integerValue
init|=
operator|new
name|IntegerValue
argument_list|(
name|XMPPModule
operator|.
name|storeChat
argument_list|(
name|chat
argument_list|,
name|chatHandle
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|integerValue
return|;
block|}
specifier|private
class|class
name|Listener
implements|implements
name|MessageListener
block|{
specifier|private
name|Sequence
name|contextSequence
decl_stmt|;
specifier|private
name|long
name|chatHandle
decl_stmt|;
specifier|private
name|FunctionCall
name|chatListenerFunction
decl_stmt|;
specifier|private
name|Sequence
name|listenerParam
decl_stmt|;
specifier|public
name|Listener
parameter_list|(
name|long
name|chatHandle
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|FunctionCall
name|chatListenerFunction
parameter_list|,
name|Sequence
name|listenerParam
parameter_list|)
block|{
name|this
operator|.
name|chatHandle
operator|=
name|chatHandle
expr_stmt|;
name|this
operator|.
name|contextSequence
operator|=
name|contextSequence
expr_stmt|;
name|this
operator|.
name|chatListenerFunction
operator|=
name|chatListenerFunction
expr_stmt|;
name|this
operator|.
name|listenerParam
operator|=
name|listenerParam
expr_stmt|;
block|}
specifier|public
name|void
name|processMessage
parameter_list|(
name|Chat
name|caht
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|message
operator|.
name|toXML
argument_list|()
argument_list|)
decl_stmt|;
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
name|reader
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|xr
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
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|xr
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Sequence
name|listenerParams
index|[]
init|=
operator|new
name|Sequence
index|[
literal|3
index|]
decl_stmt|;
name|listenerParams
index|[
literal|0
index|]
operator|=
operator|new
name|IntegerValue
argument_list|(
name|chatHandle
argument_list|)
expr_stmt|;
name|listenerParams
index|[
literal|1
index|]
operator|=
operator|(
name|DocumentImpl
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|listenerParams
index|[
literal|2
index|]
operator|=
name|listenerParam
expr_stmt|;
name|chatListenerFunction
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
name|listenerParams
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while constructing XML parser: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while parsing XML parser: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while parsing XML parser: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Chat listener function runtime error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

