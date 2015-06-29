begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|ACLPermission
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
name|Permission
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
name|PermissionDeniedException
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
name|internal
operator|.
name|aider
operator|.
name|ACEAider
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
name|internal
operator|.
name|aider
operator|.
name|PermissionAiderFactory
import|;
end_import

begin_comment
comment|/**  * Base class for Remote XMLDB classes  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRemote
block|{
specifier|protected
name|RemoteCollection
name|collection
decl_stmt|;
name|AbstractRemote
parameter_list|(
specifier|final
name|RemoteCollection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
specifier|protected
name|XmldbURI
name|resolve
parameter_list|(
specifier|final
name|XmldbURI
name|name
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
return|return
name|collection
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
specifier|protected
name|Stream
argument_list|<
name|ACEAider
argument_list|>
name|extractAces
parameter_list|(
specifier|final
name|Object
name|aclParameter
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|aclParameter
argument_list|)
operator|.
name|map
argument_list|(
name|Arrays
operator|::
name|stream
argument_list|)
operator|.
name|map
argument_list|(
name|stream
lambda|->
name|stream
operator|.
name|map
argument_list|(
name|o
lambda|->
operator|(
name|ACEAider
operator|)
name|o
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
name|Stream
operator|.
expr|<
name|ACEAider
operator|>
name|empty
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Permission
name|getPermission
parameter_list|(
specifier|final
name|String
name|owner
parameter_list|,
specifier|final
name|String
name|group
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|,
specifier|final
name|Stream
argument_list|<
name|ACEAider
argument_list|>
name|aces
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
name|Permission
name|perm
init|=
name|PermissionAiderFactory
operator|.
name|getPermission
argument_list|(
name|owner
argument_list|,
name|group
argument_list|,
name|mode
argument_list|)
decl_stmt|;
if|if
condition|(
name|perm
operator|instanceof
name|ACLPermission
condition|)
block|{
specifier|final
name|ACLPermission
name|aclPermission
init|=
operator|(
name|ACLPermission
operator|)
name|perm
decl_stmt|;
for|for
control|(
specifier|final
name|ACEAider
name|ace
range|:
name|aces
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
control|)
block|{
name|aclPermission
operator|.
name|addACE
argument_list|(
name|ace
operator|.
name|getAccessType
argument_list|()
argument_list|,
name|ace
operator|.
name|getTarget
argument_list|()
argument_list|,
name|ace
operator|.
name|getWho
argument_list|()
argument_list|,
name|ace
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|perm
return|;
block|}
block|}
end_class

end_unit

