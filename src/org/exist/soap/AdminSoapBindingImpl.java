begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  AdminSoapBindingImpl.java This file was auto-generated from WSDL by the  *  Apache Axis Wsdl2java emitter.  */
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
name|rmi
operator|.
name|RemoteException
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
name|Parser
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
name|User
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
name|BrokerPool
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    August 2, 2002  */
end_comment

begin_class
specifier|public
class|class
name|AdminSoapBindingImpl
implements|implements
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Admin
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|Admin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|/**  Constructor for the AdminSoapBindingImpl object */
specifier|public
name|AdminSoapBindingImpl
parameter_list|()
block|{
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failed to initialize broker pool"
argument_list|)
throw|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  collection           Description of the Parameter      *@return                      Description of the Return Value      *@exception  RemoteException  Description of the Exception      */
specifier|public
name|boolean
name|createCollection
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|RemoteException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating collection "
operator|+
name|collection
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|Collection
name|coll
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|coll
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"failed to create collection"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  collection           Description of the Parameter      *@return                      Description of the Return Value      *@exception  RemoteException  Description of the Exception      */
specifier|public
name|boolean
name|removeCollection
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|RemoteException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getCollection
argument_list|(
name|collection
argument_list|)
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|broker
operator|.
name|removeCollection
argument_list|(
name|collection
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  path                 Description of the Parameter      *@return                      Description of the Return Value      *@exception  RemoteException  Description of the Exception      */
specifier|public
name|boolean
name|removeDocument
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RemoteException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getDocument
argument_list|(
name|path
argument_list|)
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|broker
operator|.
name|removeDocument
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  data                 Description of the Parameter      *@param  encoding             Description of the Parameter      *@param  path                 Description of the Parameter      *@param  replace              Description of the Parameter      *@exception  RemoteException  Description of the Exception      */
specifier|public
name|void
name|store
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|encoding
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|,
name|boolean
name|replace
parameter_list|)
throws|throws
name|RemoteException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getDocument
argument_list|(
name|path
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|replace
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing document "
operator|+
name|path
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeDocument
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"document "
operator|+
name|path
operator|+
literal|" already exists in the database."
argument_list|)
throw|;
block|}
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|xml
decl_stmt|;
try|try
block|{
name|xml
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Parser
name|parser
init|=
operator|new
name|Parser
argument_list|(
name|broker
argument_list|,
operator|new
name|User
argument_list|(
literal|"admin"
argument_list|,
literal|null
argument_list|,
literal|"dba"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|parser
operator|.
name|parse
argument_list|(
name|xml
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"flushing data files"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"sync"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"parsing "
operator|+
name|path
operator|+
literal|" took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|+
literal|"ms."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

