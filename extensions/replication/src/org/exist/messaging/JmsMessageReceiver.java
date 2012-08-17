begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|messaging
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
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
name|messaging
operator|.
name|configuration
operator|.
name|JmsMessagingConfiguration
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
name|*
import|;
end_import

begin_comment
comment|/**  *  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|JmsMessageReceiver
implements|implements
name|MessageReceiver
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
name|JmsMessageReceiver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|XQueryContext
name|xqcontext
decl_stmt|;
specifier|public
name|JmsMessageReceiver
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|xqcontext
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeImpl
name|receive
parameter_list|(
name|JmsMessagingConfiguration
name|jmc
parameter_list|,
name|FunctionReference
name|ref
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// JMS specific checks
name|jmc
operator|.
name|validateContent
argument_list|()
expr_stmt|;
comment|// Retrieve relevant values
name|String
name|initialContextFactory
init|=
name|jmc
operator|.
name|getInitalContextProperty
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|)
decl_stmt|;
name|String
name|providerURL
init|=
name|jmc
operator|.
name|getInitalContextProperty
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|)
decl_stmt|;
name|String
name|connectionFactory
init|=
name|jmc
operator|.
name|getConnectionFactory
argument_list|()
decl_stmt|;
name|String
name|destination
init|=
name|jmc
operator|.
name|getDestination
argument_list|()
decl_stmt|;
name|MyListener
name|myListener
init|=
operator|new
name|MyListener
argument_list|(
name|ref
argument_list|,
name|xqcontext
argument_list|)
decl_stmt|;
comment|// TODO split up, use more exceptions, add better reporting
try|try
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
name|initialContextFactory
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|providerURL
argument_list|)
expr_stmt|;
name|javax
operator|.
name|naming
operator|.
name|Context
name|context
init|=
operator|new
name|InitialContext
argument_list|(
name|props
argument_list|)
decl_stmt|;
comment|// Setup connection
name|ConnectionFactory
name|cf
init|=
operator|(
name|ConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
name|connectionFactory
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
operator|(
name|Destination
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"dynamicQueues/Dannes"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|messageConsumer
operator|.
name|setMessageListener
argument_list|(
name|myListener
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//
comment|//            // Close connection
comment|//            // TODO keep connection open for re-use, efficiency
comment|//            connection.close();
return|return
literal|null
return|;
comment|// createReport(message, xqcontext);
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|//    /*
comment|//     *
comment|//     */
comment|//    private Message createMessage(Session session, Item item, MessagingMetadata mdd, XQueryContext xqcontext) throws JMSException, XPathException {
comment|//
comment|//
comment|//        Message message = null;
comment|//
comment|//        mdd.add("exist.datatype", Type.getTypeName(item.getType()));
comment|//
comment|//        if (item.getType() == Type.ELEMENT || item.getType() == Type.DOCUMENT) {
comment|//            LOG.debug("Streaming element or document node");
comment|//
comment|//            if (item instanceof NodeProxy) {
comment|//                NodeProxy np = (NodeProxy) item;
comment|//                String uri = np.getDocument().getBaseURI();
comment|//                LOG.debug("Document detected, adding URL " + uri);
comment|//                mdd.add("exist.document-uri", uri);
comment|//            }
comment|//
comment|//            // Node provided
comment|//            Serializer serializer = xqcontext.getBroker().newSerializer();
comment|//
comment|//            NodeValue node = (NodeValue) item;
comment|//            InputStream is = new NodeInputStream(serializer, node);
comment|//
comment|//            ByteArrayOutputStream baos=new ByteArrayOutputStream();
comment|//            try {
comment|//                IOUtils.copy(is, baos);
comment|//            } catch (IOException ex) {
comment|//                LOG.error(ex);
comment|//                throw new XPathException(ex);
comment|//            }
comment|//            IOUtils.closeQuietly(is);
comment|//            IOUtils.closeQuietly(baos);
comment|//
comment|//            BytesMessage bytesMessage = session.createBytesMessage();
comment|//            bytesMessage.writeBytes(baos.toByteArray());
comment|//
comment|//            message=bytesMessage;
comment|//
comment|//
comment|//        } else if (item.getType() == Type.BASE64_BINARY || item.getType() == Type.HEX_BINARY) {
comment|//            LOG.debug("Streaming base64 binary");
comment|//
comment|//            if (item instanceof Base64BinaryDocument) {
comment|//                Base64BinaryDocument b64doc = (Base64BinaryDocument) item;
comment|//                String uri =  b64doc.getUrl();
comment|//                LOG.debug("Base64BinaryDocument detected, adding URL " + uri);
comment|//                mdd.add("exist.document-uri", uri);
comment|//            }
comment|//
comment|//            BinaryValue binary = (BinaryValue) item;
comment|//
comment|//            ByteArrayOutputStream baos=new ByteArrayOutputStream();
comment|//            InputStream is = binary.getInputStream();
comment|//
comment|//            //TODO consider using BinaryValue.getInputStream()
comment|//            //byte[] data = (byte[]) binary.toJavaObject(byte[].class);
comment|//
comment|//            try {
comment|//                IOUtils.copy(is, baos);
comment|//            } catch (IOException ex) {
comment|//                LOG.error(ex);
comment|//                throw new XPathException(ex);
comment|//            }
comment|//            IOUtils.closeQuietly(is);
comment|//            IOUtils.closeQuietly(baos);
comment|//
comment|//            BytesMessage bytesMessage = session.createBytesMessage();
comment|//            bytesMessage.writeBytes(baos.toByteArray());
comment|//
comment|//            message=bytesMessage;
comment|//
comment|//
comment|//        } else {
comment|//
comment|//            TextMessage textMessage = session.createTextMessage();
comment|//            textMessage.setText(item.getStringValue());
comment|//            message=textMessage;
comment|//        }
comment|//
comment|//        return message;
comment|//    }
comment|//    /**
comment|//     * Create messaging results report
comment|//     */
comment|//    private NodeImpl createReport(Message message, XQueryContext xqcontext) {
comment|//
comment|//        MemTreeBuilder builder = xqcontext.getDocumentBuilder();
comment|//
comment|//        // start root element
comment|//        int nodeNr = builder.startElement("", "JMS", "JMS", null);
comment|//
comment|//        try {
comment|//            String txt = message.getJMSMessageID();
comment|//            if (txt != null) {
comment|//                builder.startElement("", "MessageID", "MessageID", null);
comment|//                builder.characters(message.getJMSMessageID());
comment|//                builder.endElement();
comment|//            }
comment|//        } catch (JMSException ex) {
comment|//            LOG.error(ex);
comment|//        }
comment|//
comment|//        try {
comment|//            String txt = message.getJMSCorrelationID();
comment|//            if (txt != null) {
comment|//                builder.startElement("", "CorrelationID", "CorrelationID", null);
comment|//                builder.characters(message.getJMSCorrelationID());
comment|//                builder.endElement();
comment|//            }
comment|//        } catch (JMSException ex) {
comment|//            LOG.error(ex);
comment|//        }
comment|//
comment|//        try {
comment|//            String txt = message.getJMSType();
comment|//            if (txt != null) {
comment|//                builder.startElement("", "Type", "Type", null);
comment|//                builder.characters(message.getJMSType());
comment|//                builder.endElement();
comment|//            }
comment|//        } catch (JMSException ex) {
comment|//            LOG.error(ex);
comment|//        }
comment|//
comment|//        // finish root element
comment|//        builder.endElement();
comment|//
comment|//        // return result
comment|//        return ((DocumentImpl) builder.getDocument()).getNode(nodeNr);
comment|//
comment|//
comment|//    }
block|}
end_class

end_unit

