begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|samples
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|jboss
operator|.
name|XmlDbService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|jboss
operator|.
name|exist
operator|.
name|EXistService
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
name|Collection
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
name|XMLDBException
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
name|Resource
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
name|org
operator|.
name|jboss
operator|.
name|system
operator|.
name|ServiceMBeanSupport
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
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * This class represent a server component living on the JBoss server e.g a Servler  * an EJB, another JMX MBean or whatever. It might also be possible to access the database  * remotely but this has not been tested at all  *  * @author Per Nyfelt  */
end_comment

begin_class
specifier|public
class|class
name|XmlDbClientService
extends|extends
name|ServiceMBeanSupport
implements|implements
name|XmlDbClientServiceMBean
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
name|XmlDbClientService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INVENTORY_NAME
init|=
literal|"inventory"
decl_stmt|;
specifier|private
name|Collection
name|inventory
decl_stmt|;
specifier|public
name|XmlDbClientService
parameter_list|()
block|{
block|}
specifier|protected
name|void
name|startService
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|startService
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|stopService
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stopService
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|useXmlDbService
parameter_list|()
block|{
try|try
block|{
name|verifyInventory
argument_list|()
expr_stmt|;
name|String
name|result
init|=
literal|"ChildCollection: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|inventory
operator|.
name|listChildCollections
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|+=
literal|"Resources: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|inventory
operator|.
name|listResources
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|inventory
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Whoa!!! No Inventory Collection found, this should have been created by the inventory service"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
name|msg
return|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
name|e
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|public
name|String
name|addXMLforResourceName
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|resourceName
parameter_list|)
block|{
try|try
block|{
name|verifyInventory
argument_list|()
expr_stmt|;
name|Resource
name|res
init|=
name|inventory
operator|.
name|getResource
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"creating resource"
argument_list|)
expr_stmt|;
name|res
operator|=
name|inventory
operator|.
name|createResource
argument_list|(
name|resourceName
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing xml content"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|xml
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing resource to eXist"
argument_list|)
expr_stmt|;
name|inventory
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|inventory
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|resourceName
operator|+
literal|" stored successfully!"
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
name|e
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|public
name|String
name|fetchXMLforResurceName
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
try|try
block|{
name|verifyInventory
argument_list|()
expr_stmt|;
name|Resource
name|res
init|=
name|inventory
operator|.
name|getResource
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
return|return
literal|"resource "
operator|+
name|resourceName
operator|+
literal|" was not found"
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Getting xml content"
argument_list|)
expr_stmt|;
return|return
operator|(
name|String
operator|)
name|res
operator|.
name|getContent
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
name|e
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
name|void
name|verifyInventory
parameter_list|()
throws|throws
name|NamingException
throws|,
name|XMLDBException
block|{
name|Context
name|ctx
init|=
operator|new
name|InitialContext
argument_list|()
decl_stmt|;
name|XmlDbService
name|xmlDbService
init|=
operator|(
name|XmlDbService
operator|)
name|ctx
operator|.
name|lookup
argument_list|(
name|XmlDbService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
name|baseCol
init|=
name|xmlDbService
operator|.
name|getBaseCollection
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got base Collection "
operator|+
name|baseCol
argument_list|)
expr_stmt|;
name|inventory
operator|=
name|baseCol
operator|.
name|getChildCollection
argument_list|(
name|INVENTORY_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|inventory
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating a new Collection for "
operator|+
name|INVENTORY_NAME
argument_list|)
expr_stmt|;
name|CollectionManagementService
name|mgtService
init|=
name|XmlDbService
operator|.
name|getCollectionManagementService
argument_list|(
name|baseCol
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got CollectionManagementService"
argument_list|)
expr_stmt|;
name|inventory
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|INVENTORY_NAME
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Collection "
operator|+
name|INVENTORY_NAME
operator|+
literal|" created."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Found existing inventory collection "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

