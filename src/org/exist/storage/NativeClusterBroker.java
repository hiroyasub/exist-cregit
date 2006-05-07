begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|//$Id$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|ClusterCollection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|ClusterComunication
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|ClusterException
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
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|Configuration
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
comment|/**  * Created by Francesco Mondora.  *  * @author Francesco Mondora aka Makkina  * @author Michele Danieli aka mdanieli  *         Date: 13-dic-2004  *         Time: 17.12.51  *         Revision $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|NativeClusterBroker
extends|extends
name|NativeBroker
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|NativeClusterBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|NativeClusterBroker
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get collection object. If the collection does not exist, null is      * returned.      *      * Wraps for cluster the resultant collection in a ClusterCollection      *      * @param name Description of the Parameter      * @return The collection value      */
specifier|public
name|Collection
name|openCollection
parameter_list|(
name|XmldbURI
name|name
parameter_list|,
name|long
name|addr
parameter_list|,
name|int
name|lockMode
parameter_list|)
block|{
name|Collection
name|c
init|=
name|super
operator|.
name|openCollection
argument_list|(
name|name
argument_list|,
name|addr
argument_list|,
name|lockMode
argument_list|)
decl_stmt|;
return|return
name|c
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|ClusterCollection
argument_list|(
name|c
argument_list|)
return|;
block|}
specifier|public
name|void
name|saveCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|super
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
operator|new
name|ClusterCollection
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * get collection object If the collection does not yet exists, it is      * created automatically.      *      * Wraps for cluster the resultant collection in a ClusterCollection      *      * @param name the collection's name      * @return The orCreateCollection value      * @throws org.exist.security.PermissionDeniedException      *          Description of the Exception      */
specifier|public
name|Collection
name|getOrCreateCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|Collection
name|c
init|=
name|super
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|c
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|ClusterCollection
argument_list|(
name|c
argument_list|)
return|;
block|}
specifier|public
name|void
name|sync
parameter_list|(
name|int
name|syncEvent
parameter_list|)
block|{
name|super
operator|.
name|sync
argument_list|(
name|syncEvent
argument_list|)
expr_stmt|;
try|try
block|{
name|ClusterComunication
name|cm
init|=
name|ClusterComunication
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|cm
operator|!=
literal|null
condition|)
comment|//waiting initialize CLusterCommunication
name|cm
operator|.
name|synch
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterException
name|e
parameter_list|)
block|{
comment|//TODO verify if DB must be declared disaligned
name|LOG
operator|.
name|warn
argument_list|(
literal|"ERROR IN JOURNAL SYNCHRONIZATION"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getBackendType
parameter_list|()
block|{
return|return
name|NATIVE_CLUSTER
return|;
block|}
block|}
end_class

end_unit

