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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
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
comment|/**  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|ManagedCollectionLock
extends|extends
name|ManagedLock
argument_list|<
name|Either
argument_list|<
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
argument_list|,
name|Tuple2
argument_list|<
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
argument_list|,
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
argument_list|>
argument_list|>
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
name|Either
argument_list|<
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
argument_list|,
name|Tuple2
argument_list|<
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
argument_list|,
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
argument_list|>
argument_list|>
name|lock
parameter_list|,
specifier|final
name|Runnable
name|closer
parameter_list|)
block|{
name|super
argument_list|(
name|lock
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
block|}
end_class

end_unit

