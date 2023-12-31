begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|Subject
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

begin_comment
comment|/**  * A simple source object wrapping a single query string, but associating it with a specific  * map (e.g., of namespace bindings).  This prevents two textually equal queries with different  * maps from getting aliased in the query pool.  *   * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|StringSourceWithMapKey
extends|extends
name|AbstractSource
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
comment|/** 	 * Create a new source for the given content and namespace map (string to string). 	 * The map will be taken over and modified by the source, so make a copy first if 	 * you're passing a shared one. 	 * 	 * @param content the content of the query 	 * @param map the map of prefixes to namespace URIs 	 */
specifier|public
name|StringSourceWithMapKey
parameter_list|(
name|String
name|content
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
name|this
operator|.
name|map
operator|.
name|put
argument_list|(
literal|"<query>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|path
parameter_list|()
block|{
return|return
name|type
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
literal|"StringWithMapKey"
return|;
block|}
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|map
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validity
name|isValid
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|Validity
operator|.
name|VALID
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validity
name|isValid
parameter_list|(
specifier|final
name|Source
name|other
parameter_list|)
block|{
return|return
name|Validity
operator|.
name|VALID
return|;
block|}
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|StringReader
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"<query>"
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
comment|// not implemented
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|map
operator|.
name|get
argument_list|(
literal|"<query>"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|validate
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|int
name|perm
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|// TODO protected?
block|}
block|}
end_class

end_unit

