begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|TestUtils
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|MimeTable
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
name|MimeType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|CollectionImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|DatabaseInstanceManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExternalResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
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
name|xmldb
operator|.
name|XQueryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|BinaryResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
import|import static
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|AutoDeploymentTrigger
operator|.
name|AUTODEPLOY_PROPERTY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Exist embedded XML:DB Server Rule for JUnit  */
end_comment

begin_class
specifier|public
class|class
name|ExistXmldbEmbeddedServer
extends|extends
name|ExternalResource
block|{
specifier|private
specifier|final
name|boolean
name|asGuest
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|disableAutoDeploy
decl_stmt|;
specifier|private
name|String
name|prevAutoDeploy
init|=
literal|"off"
decl_stmt|;
specifier|private
name|Database
name|database
init|=
literal|null
decl_stmt|;
specifier|private
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|private
name|XQueryService
name|xpathQueryService
init|=
literal|null
decl_stmt|;
specifier|public
name|ExistXmldbEmbeddedServer
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param asGuest Use the guest account, default is the admin account      */
specifier|public
name|ExistXmldbEmbeddedServer
parameter_list|(
specifier|final
name|boolean
name|asGuest
parameter_list|)
block|{
name|this
argument_list|(
name|asGuest
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param asGuest Use the guest account, default is the admin account      * @param disableAutoDeploy Whether auto-deployment of XARs should be disabled      */
specifier|public
name|ExistXmldbEmbeddedServer
parameter_list|(
specifier|final
name|boolean
name|asGuest
parameter_list|,
specifier|final
name|boolean
name|disableAutoDeploy
parameter_list|)
block|{
name|this
operator|.
name|asGuest
operator|=
name|asGuest
expr_stmt|;
name|this
operator|.
name|disableAutoDeploy
operator|=
name|disableAutoDeploy
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Throwable
block|{
name|startDb
argument_list|()
expr_stmt|;
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|startDb
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
block|{
if|if
condition|(
name|database
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|disableAutoDeploy
condition|)
block|{
name|this
operator|.
name|prevAutoDeploy
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|AUTODEPLOY_PROPERTY
argument_list|,
literal|"off"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|AUTODEPLOY_PROPERTY
argument_list|,
literal|"off"
argument_list|)
expr_stmt|;
block|}
comment|// initialize driver
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|database
operator|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
if|if
condition|(
name|asGuest
condition|)
block|{
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_PWD
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
expr_stmt|;
block|}
name|xpathQueryService
operator|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistXmldbEmbeddedServer already running"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ResourceSet
name|executeQuery
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|CompiledExpression
name|compiledQuery
init|=
name|xpathQueryService
operator|.
name|compile
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|xpathQueryService
operator|.
name|execute
argument_list|(
name|compiledQuery
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|ResourceSet
name|executeQuery
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|externalVariables
parameter_list|)
throws|throws
name|XMLDBException
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|externalVariable
range|:
name|externalVariables
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|xpathQueryService
operator|.
name|declareVariable
argument_list|(
name|externalVariable
operator|.
name|getKey
argument_list|()
argument_list|,
name|externalVariable
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CompiledExpression
name|compiledQuery
init|=
name|xpathQueryService
operator|.
name|compile
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|xpathQueryService
operator|.
name|execute
argument_list|(
name|compiledQuery
argument_list|)
decl_stmt|;
name|xpathQueryService
operator|.
name|clearVariables
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|String
name|executeOneValue
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|Collection
name|createCollection
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|collectionName
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|CollectionManagementService
name|collectionManagementService
init|=
operator|(
name|CollectionManagementService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|newCollection
init|=
name|collection
operator|.
name|getChildCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCollection
operator|==
literal|null
condition|)
block|{
name|collectionManagementService
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
specifier|final
name|XmldbURI
name|uri
init|=
name|XmldbURI
operator|.
name|LOCAL_DB_URI
operator|.
name|resolveCollectionPath
argument_list|(
operator|(
operator|(
name|CollectionImpl
operator|)
name|collection
operator|)
operator|.
name|getPathURI
argument_list|()
operator|.
name|append
argument_list|(
name|collectionName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|asGuest
condition|)
block|{
name|newCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_PWD
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
expr_stmt|;
block|}
return|return
name|newCollection
return|;
block|}
specifier|public
specifier|static
name|void
name|storeResource
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|documentName
parameter_list|,
specifier|final
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|documentName
argument_list|)
decl_stmt|;
specifier|final
name|String
name|type
init|=
name|mime
operator|.
name|isXMLType
argument_list|()
condition|?
name|XMLResource
operator|.
name|RESOURCE_TYPE
else|:
name|BinaryResource
operator|.
name|RESOURCE_TYPE
decl_stmt|;
specifier|final
name|Resource
name|resource
init|=
name|collection
operator|.
name|createResource
argument_list|(
name|documentName
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getXMLResource
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|resource
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collection
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
decl_stmt|;
return|return
name|res
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|Collection
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
specifier|public
name|void
name|restart
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
throws|,
name|IllegalAccessException
block|{
name|stopDb
argument_list|()
expr_stmt|;
name|startDb
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|after
parameter_list|()
block|{
try|try
block|{
name|stopDb
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|stopDb
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|database
operator|!=
literal|null
condition|)
block|{
name|root
operator|.
name|close
argument_list|()
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
specifier|final
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// clear instance variables
name|xpathQueryService
operator|=
literal|null
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
name|database
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|disableAutoDeploy
condition|)
block|{
comment|//set the autodeploy trigger enablement back to how it was before this test class
name|System
operator|.
name|setProperty
argument_list|(
name|AUTODEPLOY_PROPERTY
argument_list|,
name|this
operator|.
name|prevAutoDeploy
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistXmldbEmbeddedServer already stopped"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

