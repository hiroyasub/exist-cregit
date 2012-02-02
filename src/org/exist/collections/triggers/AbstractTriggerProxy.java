begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTriggerProxy
parameter_list|<
name|T
extends|extends
name|Trigger
parameter_list|>
implements|implements
name|TriggerProxy
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
decl_stmt|;
comment|/**      * The database Collection URI of where the configuration for this Trigger came from      * typically somewhere under /db/system/config/db/      */
specifier|private
specifier|final
name|XmldbURI
name|collectionConfigurationURI
decl_stmt|;
specifier|public
name|AbstractTriggerProxy
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|XmldbURI
name|collectionConfigurationURI
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|collectionConfigurationURI
operator|=
name|collectionConfigurationURI
expr_stmt|;
block|}
specifier|public
name|AbstractTriggerProxy
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|XmldbURI
name|collectionConfigurationURI
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|collectionConfigurationURI
operator|=
name|collectionConfigurationURI
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
specifier|protected
name|Class
argument_list|<
name|T
argument_list|>
name|getClazz
parameter_list|()
block|{
return|return
name|clazz
return|;
block|}
specifier|protected
name|XmldbURI
name|getCollectionConfigurationURI
parameter_list|()
block|{
return|return
name|collectionConfigurationURI
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setParameters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
specifier|protected
name|T
name|newInstance
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|TriggerException
block|{
try|try
block|{
name|T
name|trigger
init|=
name|getClazz
argument_list|()
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|XmldbURI
name|collectionForTrigger
init|=
name|getCollectionConfigurationURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|collectionForTrigger
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|CONFIG_COLLECTION_URI
argument_list|)
condition|)
block|{
name|collectionForTrigger
operator|=
name|collectionForTrigger
operator|.
name|trimFromBeginning
argument_list|(
name|XmldbURI
operator|.
name|CONFIG_COLLECTION_URI
argument_list|)
expr_stmt|;
block|}
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|collectionForTrigger
argument_list|)
decl_stmt|;
name|trigger
operator|.
name|configure
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|,
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|trigger
return|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Unable to instantiate Trigger '"
operator|+
name|getClazz
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"': "
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ie
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Unable to instantiate Trigger '"
operator|+
name|getClazz
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"': "
operator|+
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|iae
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|TriggerProxy
argument_list|>
name|newInstance
parameter_list|(
name|Class
name|c
parameter_list|,
name|XmldbURI
name|collectionConfigurationURI
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|TriggerException
block|{
specifier|final
name|List
argument_list|<
name|TriggerProxy
argument_list|>
name|proxies
init|=
operator|new
name|ArrayList
argument_list|<
name|TriggerProxy
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|DocumentTrigger
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|proxies
operator|.
name|add
argument_list|(
operator|new
name|DocumentTriggerProxy
argument_list|(
operator|(
name|Class
argument_list|<
name|DocumentTrigger
argument_list|>
operator|)
name|c
argument_list|,
name|collectionConfigurationURI
argument_list|,
name|parameters
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|CollectionTrigger
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|proxies
operator|.
name|add
argument_list|(
operator|new
name|CollectionTriggerProxy
argument_list|(
operator|(
name|Class
argument_list|<
name|CollectionTrigger
argument_list|>
operator|)
name|c
argument_list|,
name|collectionConfigurationURI
argument_list|,
name|parameters
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|proxies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Unknown Trigger class type: "
operator|+
name|c
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|proxies
return|;
block|}
block|}
end_class

end_unit
