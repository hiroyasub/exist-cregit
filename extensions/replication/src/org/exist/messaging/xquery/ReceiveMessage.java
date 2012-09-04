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
operator|.
name|xquery
package|;
end_package

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
name|JmsMessageReceiver
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

begin_comment
comment|/**  *  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|ReceiveMessage
extends|extends
name|BasicFunction
block|{
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
literal|"receive"
argument_list|,
name|MessagingModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MessagingModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Text1"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"configuration"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"text"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"callback function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"text"
argument_list|)
block|,
comment|//                new FunctionParameterSequenceType("content", Type.ITEM, Cardinality.ZERO_OR_ONE,
comment|//                        "Send message to remote server")
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Confirmation message, if present"
argument_list|)
argument_list|)
block|,               }
decl_stmt|;
specifier|public
name|ReceiveMessage
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
comment|/*             xquery version "1.0";              import module namespace messaging="http://exist-db.org/xquery/messaging"             at "java:org.exist.messaging.xquery.MessagingModule";               declare function local:index-callback($configuration as element(), $properties as element(), $content as item()) {                 util:log("INFO", $content)             };              let $config :=<jms><InitialContext><java.naming.factory.initial>org.apache.activemq.jndi.ActiveMQInitialContextFactory</java.naming.factory.initial><java.naming.provider.url>tcp://localhost:61616</java.naming.provider.url></InitialContext><ConnectionFactory>ConnectionFactory</ConnectionFactory><Destination>dynamicQueues/MyTestQ</Destination></jms>              let $callback := util:function(xs:QName("local:index-callback"), 3)              return             messaging:receive($config, $callback)         */
comment|// Get configuration
name|NodeValue
name|configNode
init|=
operator|(
name|NodeValue
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
decl_stmt|;
name|JmsMessagingConfiguration
name|jmc
init|=
operator|new
name|JmsMessagingConfiguration
argument_list|()
decl_stmt|;
name|jmc
operator|.
name|parseDocument
argument_list|(
name|configNode
argument_list|)
expr_stmt|;
comment|//        // Get additional header
comment|//	    NodeValue headersNode = (NodeValue) args[1].itemAt(0);
comment|//        MessagingMetadata mmd = new MessagingMetadata();
comment|//        mmd.parseDocument(headersNode);
comment|// Get function reference
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Get content
comment|//        Item content = args[2].itemAt(0);
comment|//        if(content instanceof NodeProxy){
comment|//            NodeProxy np = (NodeProxy) content;
comment|//            mmd.add( "url" , np.getDocument().getBaseURI() );
comment|//        }
comment|//
comment|//
comment|//        mmd.add("exist.type", Type.getTypeName( content.getType() ));
comment|// Send content
name|JmsMessageReceiver
name|sender
init|=
operator|new
name|JmsMessageReceiver
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|NodeImpl
name|result
init|=
name|sender
operator|.
name|receive
argument_list|(
name|jmc
argument_list|,
name|ref
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

