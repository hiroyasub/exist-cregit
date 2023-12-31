begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

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
name|uk
operator|.
name|ac
operator|.
name|ic
operator|.
name|doc
operator|.
name|slurp
operator|.
name|multilock
operator|.
name|MultiLock
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|ManagedCollectionLock
extends|extends
name|ManagedLock
argument_list|<
name|MultiLock
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|XmldbURI
name|collectionUri
decl_stmt|;
specifier|public
name|ManagedCollectionLock
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|,
specifier|final
name|MultiLock
index|[]
name|locks
parameter_list|,
specifier|final
name|Runnable
name|closer
parameter_list|)
block|{
name|super
argument_list|(
name|locks
argument_list|,
name|closer
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionUri
operator|=
name|collectionUri
expr_stmt|;
block|}
specifier|public
name|XmldbURI
name|getPath
parameter_list|()
block|{
return|return
name|collectionUri
return|;
block|}
specifier|public
specifier|static
name|ManagedCollectionLock
name|notLocked
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
block|{
return|return
operator|new
name|ManagedCollectionLock
argument_list|(
name|collectionUri
argument_list|,
literal|null
argument_list|,
parameter_list|()
lambda|->
block|{
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

