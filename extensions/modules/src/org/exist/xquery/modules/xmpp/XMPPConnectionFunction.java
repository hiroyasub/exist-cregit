begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|util
operator|.
name|Properties
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
name|proxy
operator|.
name|ProxyInfo
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
name|proxy
operator|.
name|ProxyInfo
operator|.
name|ProxyType
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
name|ConnectionConfiguration
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
name|persistent
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
name|modules
operator|.
name|ModuleUtils
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

begin_class
specifier|public
class|class
name|XMPPConnectionFunction
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
name|XMPPConnectionFunction
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
literal|"get-xmpp-connection"
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
literal|"Create a XMPP connection."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"properties"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"An optional connection properties in the form<properties><property name=\"\" value=\"\"/></properties>."
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
name|EXACTLY_ONE
argument_list|,
literal|"an xs:long representing the connection handle."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|XMPPConnectionFunction
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
name|Properties
name|props
init|=
name|ModuleUtils
operator|.
name|parseProperties
argument_list|(
operator|(
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
operator|)
operator|.
name|getNode
argument_list|()
argument_list|)
decl_stmt|;
name|ProxyInfo
name|proxy
decl_stmt|;
name|ConnectionConfiguration
name|config
decl_stmt|;
if|if
condition|(
name|props
operator|.
name|containsKey
argument_list|(
literal|"proxy.type"
argument_list|)
condition|)
block|{
name|ProxyType
name|type
init|=
name|ProxyType
operator|.
name|valueOf
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"proxy.type"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"proxy.host"
argument_list|)
decl_stmt|;
name|int
name|port
init|=
operator|new
name|Integer
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"proxy.port"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"proxy.user"
argument_list|)
decl_stmt|;
name|String
name|passwd
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"proxy.password"
argument_list|)
decl_stmt|;
name|proxy
operator|=
operator|new
name|ProxyInfo
argument_list|(
name|type
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
name|user
argument_list|,
name|passwd
argument_list|)
expr_stmt|;
block|}
else|else
name|proxy
operator|=
literal|null
expr_stmt|;
name|String
name|service
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"xmpp.service"
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"xmpp.host"
argument_list|)
decl_stmt|;
name|String
name|tmp
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"xmpp.port"
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|tmp
operator|==
literal|null
condition|?
literal|5222
else|:
operator|new
name|Integer
argument_list|(
name|tmp
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|host
operator|==
literal|null
condition|)
name|config
operator|=
operator|new
name|ConnectionConfiguration
argument_list|(
name|service
argument_list|)
expr_stmt|;
if|else if
condition|(
name|service
operator|==
literal|null
condition|)
name|config
operator|=
operator|new
name|ConnectionConfiguration
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
else|else
name|config
operator|=
operator|new
name|ConnectionConfiguration
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|host
operator|==
literal|null
condition|)
name|config
operator|=
operator|new
name|ConnectionConfiguration
argument_list|(
name|service
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
if|else if
condition|(
name|service
operator|==
literal|null
condition|)
name|config
operator|=
operator|new
name|ConnectionConfiguration
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
else|else
name|config
operator|=
operator|new
name|ConnectionConfiguration
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|service
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
name|XMPPConnection
name|connection
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
comment|// store the connection and return the handle of the connection
name|IntegerValue
name|integerValue
init|=
operator|new
name|IntegerValue
argument_list|(
name|XMPPModule
operator|.
name|storeConnection
argument_list|(
name|connection
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|integerValue
return|;
block|}
block|}
end_class

end_unit

