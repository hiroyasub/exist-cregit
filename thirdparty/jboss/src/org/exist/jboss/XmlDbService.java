begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|jboss
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|naming
operator|.
name|NonSerializableFactory
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
name|Database
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
name|modules
operator|.
name|CollectionManagementService
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * This serice depends on eXists and exposes the XML:DB api  * We bind the service to JNDI for convenience reasons only  * we could just as well go through the jmx spine instead.  *  * @author Per Nyfelt  */
end_comment

begin_class
specifier|public
class|class
name|XmlDbService
extends|extends
name|ServiceMBeanSupport
implements|implements
name|XmlDbServiceMBean
block|{
specifier|private
name|String
name|baseCollectionURI
decl_stmt|;
specifier|private
name|String
name|driver
decl_stmt|;
specifier|private
name|Collection
name|baseCollection
decl_stmt|;
specifier|public
name|String
name|getDriver
parameter_list|()
block|{
return|return
name|driver
return|;
block|}
specifier|public
name|void
name|setDriver
parameter_list|(
name|String
name|driver
parameter_list|)
block|{
name|this
operator|.
name|driver
operator|=
name|driver
expr_stmt|;
block|}
specifier|public
name|String
name|getBaseCollectionURI
parameter_list|()
block|{
return|return
name|baseCollectionURI
return|;
block|}
specifier|public
name|void
name|setBaseCollectionURI
parameter_list|(
name|String
name|baseCollectionURI
parameter_list|)
block|{
name|this
operator|.
name|baseCollectionURI
operator|=
name|baseCollectionURI
expr_stmt|;
block|}
specifier|public
name|Collection
name|getBaseCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|baseCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseCollectionURI
argument_list|)
expr_stmt|;
return|return
name|baseCollection
return|;
block|}
specifier|protected
name|void
name|startService
parameter_list|()
throws|throws
name|Exception
block|{
name|Context
name|context
init|=
operator|new
name|InitialContext
argument_list|()
decl_stmt|;
name|Class
name|c
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|c
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
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
name|baseCollection
operator|=
name|getBaseCollection
argument_list|()
expr_stmt|;
name|baseCollection
operator|.
name|setProperty
argument_list|(
literal|"encoding"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Got base Collection"
argument_list|)
expr_stmt|;
name|NonSerializableFactory
operator|.
name|rebind
argument_list|(
name|context
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|String
index|[]
name|collections
init|=
name|baseCollection
operator|.
name|listChildCollections
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"ChildCollections "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|collections
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|stopService
parameter_list|()
throws|throws
name|Exception
block|{
name|NonSerializableFactory
operator|.
name|unbind
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseCollection
operator|!=
literal|null
condition|)
block|{
name|baseCollection
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Closed base (db) collection"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|CollectionManagementService
name|getCollectionManagementService
parameter_list|(
name|Collection
name|parentCollection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|parentCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|service
return|;
block|}
block|}
end_class

end_unit

