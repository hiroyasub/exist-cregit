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
name|cluster
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|ChannelException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|JChannel
import|;
end_import

begin_comment
comment|/**  * Created by Francesco Mondora.  *  * @author Francesco Mondora aka Makkina  * @author Michele Danieli aka mdanieli  *         Date: 13-dic-2004  *         Time: 18.09.08  *         Revision $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ClusterChannel
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ClusterChannel
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Channel
name|channel
init|=
name|initChannel
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXIST_GROUP
init|=
literal|"exist-replication-group"
decl_stmt|;
specifier|public
specifier|static
name|Vector
name|incomingEvents
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|Channel
name|getChannel
parameter_list|()
throws|throws
name|ClusterException
block|{
return|return
name|channel
return|;
block|}
specifier|private
specifier|static
specifier|final
name|Channel
name|initChannel
parameter_list|()
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Javagroups: connecting to channel"
argument_list|)
expr_stmt|;
name|Channel
name|channel
init|=
operator|new
name|JChannel
argument_list|(
name|ClusterConfiguration
operator|.
name|getProtocolStack
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|connect
argument_list|(
name|ClusterChannel
operator|.
name|EXIST_GROUP
argument_list|)
expr_stmt|;
return|return
name|channel
return|;
block|}
catch|catch
parameter_list|(
name|ChannelException
name|e
parameter_list|)
block|{
name|log
operator|.
name|fatal
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|boolean
name|hasToBePublished
parameter_list|(
name|String
name|event
parameter_list|)
block|{
comment|//TODO fix event management
return|return
operator|!
name|incomingEvents
operator|.
name|contains
argument_list|(
name|event
argument_list|)
return|;
comment|//return true;
block|}
specifier|public
specifier|static
name|void
name|accountEvent
parameter_list|(
name|String
name|event
parameter_list|)
block|{
name|incomingEvents
operator|.
name|addElement
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

