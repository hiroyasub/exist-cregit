begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * QueryServiceLocator.java  *  * This file was auto-generated from WSDL  * by the Apache Axis WSDL2Java emitter.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

begin_class
specifier|public
class|class
name|QueryServiceLocator
extends|extends
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|client
operator|.
name|Service
implements|implements
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QueryService
block|{
comment|// Use to get a proxy class for Query
specifier|private
specifier|final
name|java
operator|.
name|lang
operator|.
name|String
name|Query_address
init|=
literal|"http://localhost:8080/exist/services/Query"
decl_stmt|;
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|getQueryAddress
parameter_list|()
block|{
return|return
name|Query_address
return|;
block|}
comment|// The WSDD service name defaults to the port name.
specifier|private
name|java
operator|.
name|lang
operator|.
name|String
name|QueryWSDDServiceName
init|=
literal|"Query"
decl_stmt|;
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|getQueryWSDDServiceName
parameter_list|()
block|{
return|return
name|QueryWSDDServiceName
return|;
block|}
specifier|public
name|void
name|setQueryWSDDServiceName
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|name
parameter_list|)
block|{
name|QueryWSDDServiceName
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Query
name|getQuery
parameter_list|()
throws|throws
name|javax
operator|.
name|xml
operator|.
name|rpc
operator|.
name|ServiceException
block|{
name|java
operator|.
name|net
operator|.
name|URL
name|endpoint
decl_stmt|;
try|try
block|{
name|endpoint
operator|=
operator|new
name|java
operator|.
name|net
operator|.
name|URL
argument_list|(
name|Query_address
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|net
operator|.
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|javax
operator|.
name|xml
operator|.
name|rpc
operator|.
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|getQuery
argument_list|(
name|endpoint
argument_list|)
return|;
block|}
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Query
name|getQuery
parameter_list|(
name|java
operator|.
name|net
operator|.
name|URL
name|portAddress
parameter_list|)
throws|throws
name|javax
operator|.
name|xml
operator|.
name|rpc
operator|.
name|ServiceException
block|{
try|try
block|{
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QuerySoapBindingStub
name|_stub
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QuerySoapBindingStub
argument_list|(
name|portAddress
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|_stub
operator|.
name|setPortName
argument_list|(
name|getQueryWSDDServiceName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|_stub
return|;
block|}
catch|catch
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|AxisFault
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * For the given interface, get the stub implementation.      * If this service has no port for the given interface,      * then ServiceException is thrown.      */
specifier|public
name|java
operator|.
name|rmi
operator|.
name|Remote
name|getPort
parameter_list|(
name|Class
name|serviceEndpointInterface
parameter_list|)
throws|throws
name|javax
operator|.
name|xml
operator|.
name|rpc
operator|.
name|ServiceException
block|{
try|try
block|{
if|if
condition|(
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Query
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|serviceEndpointInterface
argument_list|)
condition|)
block|{
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QuerySoapBindingStub
name|_stub
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QuerySoapBindingStub
argument_list|(
operator|new
name|java
operator|.
name|net
operator|.
name|URL
argument_list|(
name|Query_address
argument_list|)
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|_stub
operator|.
name|setPortName
argument_list|(
name|getQueryWSDDServiceName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|_stub
return|;
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|javax
operator|.
name|xml
operator|.
name|rpc
operator|.
name|ServiceException
argument_list|(
name|t
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|javax
operator|.
name|xml
operator|.
name|rpc
operator|.
name|ServiceException
argument_list|(
literal|"There is no stub implementation for the interface:  "
operator|+
operator|(
name|serviceEndpointInterface
operator|==
literal|null
condition|?
literal|"null"
else|:
name|serviceEndpointInterface
operator|.
name|getName
argument_list|()
operator|)
argument_list|)
throw|;
block|}
comment|/**      * For the given interface, get the stub implementation.      * If this service has no port for the given interface,      * then ServiceException is thrown.      */
specifier|public
name|java
operator|.
name|rmi
operator|.
name|Remote
name|getPort
parameter_list|(
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|portName
parameter_list|,
name|Class
name|serviceEndpointInterface
parameter_list|)
throws|throws
name|javax
operator|.
name|xml
operator|.
name|rpc
operator|.
name|ServiceException
block|{
if|if
condition|(
name|portName
operator|==
literal|null
condition|)
block|{
return|return
name|getPort
argument_list|(
name|serviceEndpointInterface
argument_list|)
return|;
block|}
name|String
name|inputPortName
init|=
name|portName
operator|.
name|getLocalPart
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"Query"
operator|.
name|equals
argument_list|(
name|inputPortName
argument_list|)
condition|)
block|{
return|return
name|getQuery
argument_list|()
return|;
block|}
else|else
block|{
name|java
operator|.
name|rmi
operator|.
name|Remote
name|_stub
init|=
name|getPort
argument_list|(
name|serviceEndpointInterface
argument_list|)
decl_stmt|;
operator|(
operator|(
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|client
operator|.
name|Stub
operator|)
name|_stub
operator|)
operator|.
name|setPortName
argument_list|(
name|portName
argument_list|)
expr_stmt|;
return|return
name|_stub
return|;
block|}
block|}
specifier|public
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|getServiceName
parameter_list|()
block|{
return|return
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"QueryService"
argument_list|)
return|;
block|}
specifier|private
name|java
operator|.
name|util
operator|.
name|HashSet
name|ports
init|=
literal|null
decl_stmt|;
specifier|public
name|java
operator|.
name|util
operator|.
name|Iterator
name|getPorts
parameter_list|()
block|{
if|if
condition|(
name|ports
operator|==
literal|null
condition|)
block|{
name|ports
operator|=
operator|new
name|java
operator|.
name|util
operator|.
name|HashSet
argument_list|()
expr_stmt|;
name|ports
operator|.
name|add
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"Query"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ports
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

