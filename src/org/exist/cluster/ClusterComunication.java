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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|exist
operator|.
name|cluster
operator|.
name|cocoon
operator|.
name|ConsoleInfo
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
name|journal
operator|.
name|JournalIdGenerator
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
name|journal
operator|.
name|JournalManager
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
name|jgroups
operator|.
name|Address
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

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|MembershipListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|SuspectedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|View
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|blocks
operator|.
name|GroupRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|blocks
operator|.
name|RpcDispatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|util
operator|.
name|RspList
import|;
end_import

begin_comment
comment|/**  * Manage the Cluster communication via RPC JGroups  * Created by Nicola Breda.  *  * @author Nicola Breda aka maiale  * @author David Frontini aka spider  *         Date: 05-aug-2005  *         Time: 18.09.08  *         Revision $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ClusterComunication
implements|implements
name|MembershipListener
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"cluster"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_PROTOCOL_ATTRIBUTE
init|=
literal|"protocol"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_USER_ATTRIBUTE
init|=
literal|"dbaUser"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_PWD_ATTRIBUTE
init|=
literal|"dbaPassword"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_EXCLUDED_COLLECTIONS_ATTRIBUTE
init|=
literal|"exclude"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_CLUSTER_PROTOCOL
init|=
literal|"cluster.protocol"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_CLUSTER_USER
init|=
literal|"cluster.user"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_CLUSTER_PWD
init|=
literal|"cluster.pwd"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_CLUSTER_EXCLUDED_COLLECTIONS
init|=
literal|"cluster.exclude"
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ClusterComunication
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|JChannel
name|channel
decl_stmt|;
specifier|private
specifier|static
name|RpcDispatcher
name|disp
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|banner
init|=
literal|" #####  #       #     #  #####  ####### ####### ######\n"
operator|+
literal|"#     # #       #     # #     #    #    #       #     #\n"
operator|+
literal|"#       #       #     # #          #    #       #     #\n"
operator|+
literal|"#       #       #     #  #####     #    #####   ######\n"
operator|+
literal|"#       #       #     #       #    #    #       #   #\n"
operator|+
literal|"#     # #       #     # #     #    #    #       #    #\n"
operator|+
literal|" #####  #######  #####   #####     #    ####### #     #\n"
operator|+
literal|"\n"
operator|+
literal|"\n"
operator|+
literal|" ######  #    #     #     ####    #####\n"
operator|+
literal|" #        #  #      #    #          #\n"
operator|+
literal|" #####     ##       #     ####      #\n"
operator|+
literal|" #         ##       #         #     #\n"
operator|+
literal|" #        #  #      #    #    #     #\n"
operator|+
literal|" ######  #    #     #     ####      #"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PROTOCOL_STACK
init|=
literal|"UDP(mcast_addr=228.1.2.3;mcast_port=45566;ip_ttl=32;loopback=true):"
operator|+
literal|"PING(timeout=3000;num_initial_members=6):"
operator|+
literal|"FD(timeout=3000):"
operator|+
literal|"VERIFY_SUSPECT(timeout=1500):"
operator|+
literal|"pbcast.NAKACK(gc_lag=10;retransmit_timeout=600,1200,2400,4800):"
operator|+
literal|"UNICAST(timeout=600,1200,2400,4800):"
operator|+
literal|"pbcast.STABLE(desired_avg_gossip=10000):"
operator|+
literal|"FRAG:"
operator|+
literal|"pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;"
operator|+
literal|"shun=true;print_local_addr=true)"
decl_stmt|;
specifier|private
specifier|static
name|ClusterComunication
name|instance
decl_stmt|;
specifier|private
name|Vector
argument_list|<
name|Address
argument_list|>
name|membersNoSender
init|=
operator|new
name|Vector
argument_list|<
name|Address
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Address
name|localAddress
decl_stmt|;
specifier|private
name|Address
name|coordinatorAddress
decl_stmt|;
specifier|private
specifier|static
name|String
name|dbaUser
decl_stmt|;
specifier|private
specifier|static
name|String
name|dbaPwd
decl_stmt|;
specifier|private
specifier|static
name|ArrayList
name|excludedCollection
decl_stmt|;
specifier|private
name|JournalManager
name|journalManager
decl_stmt|;
specifier|private
name|JournalIdGenerator
name|journalIdGenerator
decl_stmt|;
specifier|private
name|boolean
name|coordinator
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isRealign
init|=
literal|true
decl_stmt|;
specifier|private
name|ArrayList
name|realignQueue
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|viewConfigured
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|shift
decl_stmt|;
specifier|private
name|Configuration
name|configuration
decl_stmt|;
specifier|public
specifier|static
name|String
name|getDbaUser
parameter_list|()
block|{
return|return
name|dbaUser
return|;
block|}
specifier|public
specifier|static
name|String
name|getDbaPwd
parameter_list|()
block|{
return|return
name|dbaPwd
return|;
block|}
specifier|private
specifier|static
name|void
name|createInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|ClusterException
block|{
name|ClusterComunication
name|c
init|=
operator|new
name|ClusterComunication
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|banner
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|protocol
init|=
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_CLUSTER_PROTOCOL
argument_list|)
decl_stmt|;
name|dbaUser
operator|=
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_CLUSTER_USER
argument_list|)
expr_stmt|;
name|dbaPwd
operator|=
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_CLUSTER_PWD
argument_list|)
expr_stmt|;
name|excludedCollection
operator|=
operator|(
name|ArrayList
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_CLUSTER_EXCLUDED_COLLECTIONS
argument_list|)
expr_stmt|;
if|if
condition|(
name|protocol
operator|==
literal|null
condition|)
name|protocol
operator|=
name|DEFAULT_PROTOCOL_STACK
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"PROTOCOL \n"
operator|+
name|protocol
argument_list|)
expr_stmt|;
name|channel
operator|=
operator|new
name|JChannel
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
name|disp
operator|=
operator|new
name|RpcDispatcher
argument_list|(
name|channel
argument_list|,
literal|null
argument_list|,
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|disp
operator|.
name|setDeadlockDetection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|c
operator|.
name|configuration
operator|=
name|conf
expr_stmt|;
name|c
operator|.
name|journalManager
operator|=
operator|new
name|JournalManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|c
operator|.
name|journalIdGenerator
operator|=
operator|new
name|JournalIdGenerator
argument_list|(
name|c
operator|.
name|journalManager
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|JournalManager
operator|.
name|PROPERTY_CLUSTER_JOURNAL_MAXSTORE
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|shift
operator|=
operator|(
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|JournalManager
operator|.
name|PROPERTY_CLUSTER_JOURNAL_SHIFT
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|instance
operator|=
name|c
expr_stmt|;
name|channel
operator|.
name|connect
argument_list|(
literal|"eXist-cluster"
argument_list|)
expr_stmt|;
name|c
operator|.
name|localAddress
operator|=
name|channel
operator|.
name|getLocalAddress
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|c
operator|.
name|viewConfigured
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SLEEPING - WAITING TO CONFIGURE THE CLUSTER"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|isRealign
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"TRY TO REALIGNING "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|realign
argument_list|()
expr_stmt|;
name|c
operator|.
name|isRealign
operator|=
literal|false
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"REALIGNED ... "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
name|log
operator|.
name|error
argument_list|(
literal|"Error during cluster JGroups environment configuration "
operator|+
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ClusterException
argument_list|(
literal|"ERROR CREATING CLUSTER ..."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|ClusterComunication
parameter_list|()
block|{
block|}
comment|/**      * ----------------   MEMBERSHIP LISTENER METHODS   ------------------------------   ****      */
specifier|public
name|void
name|viewAccepted
parameter_list|(
name|View
name|view
parameter_list|)
block|{
name|this
operator|.
name|coordinatorAddress
operator|=
name|view
operator|.
name|getCreator
argument_list|()
expr_stmt|;
comment|// The master address of the cluster
name|boolean
name|coordinator
init|=
name|coordinatorAddress
operator|.
name|equals
argument_list|(
name|localAddress
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"COordinator : "
operator|+
name|coordinator
operator|+
literal|" localAddress : "
operator|+
name|localAddress
argument_list|)
expr_stmt|;
if|if
condition|(
name|coordinator
condition|)
name|log
operator|.
name|info
argument_list|(
literal|"***************** I'M MASTER!!!!!!!!!"
argument_list|)
expr_stmt|;
comment|//Per evitare problematiche di sincronizzazione in caso di failure - il nuovo master sposta in avanti i suoi indici
comment|//in modo da compensare possibili disallineamenti.
if|if
condition|(
name|coordinator
operator|&&
operator|!
name|this
operator|.
name|coordinator
operator|&&
name|journalIdGenerator
operator|!=
literal|null
condition|)
block|{
name|journalIdGenerator
operator|.
name|shiftId
argument_list|(
name|shift
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|coordinator
operator|=
name|coordinatorAddress
operator|.
name|equals
argument_list|(
name|localAddress
argument_list|)
expr_stmt|;
comment|//check if this node is a master
name|Vector
name|members
init|=
operator|(
name|Vector
operator|)
name|view
operator|.
name|getMembers
argument_list|()
operator|.
name|clone
argument_list|()
decl_stmt|;
name|members
operator|.
name|removeElement
argument_list|(
name|channel
operator|.
name|getLocalAddress
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|membersNoSender
operator|=
name|members
expr_stmt|;
comment|//all members into the cluster
name|viewConfigured
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|suspect
parameter_list|(
name|Address
name|address
parameter_list|)
block|{
if|if
condition|(
name|coordinatorAddress
operator|.
name|equals
argument_list|(
name|address
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"MASTER IS DEAD"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|block
parameter_list|()
block|{
block|}
comment|/**      * ********** ---------------------------------------------------------  **********      */
specifier|public
specifier|static
name|ClusterComunication
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
comment|/**      * **************  --------- CONSOLE METHODS ---------- *******************************      */
specifier|public
name|boolean
name|isCoordinator
parameter_list|()
block|{
return|return
name|coordinator
return|;
block|}
specifier|public
name|Address
name|getCoordinator
parameter_list|()
block|{
return|return
name|coordinatorAddress
return|;
block|}
specifier|public
name|Address
name|getAddress
parameter_list|()
block|{
return|return
name|localAddress
return|;
block|}
specifier|public
name|Vector
argument_list|<
name|Address
argument_list|>
name|getMembersNoCoordinator
parameter_list|()
block|{
name|Vector
argument_list|<
name|Address
argument_list|>
name|members
init|=
operator|(
name|Vector
argument_list|<
name|Address
argument_list|>
operator|)
name|membersNoSender
operator|.
name|clone
argument_list|()
decl_stmt|;
name|members
operator|.
name|remove
argument_list|(
name|coordinatorAddress
argument_list|)
expr_stmt|;
return|return
name|members
return|;
block|}
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getConsoleInfos
parameter_list|(
name|Vector
argument_list|<
name|Address
argument_list|>
name|address
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|RspList
name|list
init|=
name|disp
operator|.
name|callRemoteMethods
argument_list|(
name|address
argument_list|,
literal|"getConsoleProperties"
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|,
name|GroupRequest
operator|.
name|GET_ALL
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Address
name|addr
range|:
name|address
control|)
block|{
name|response
operator|.
name|put
argument_list|(
name|addr
operator|.
name|toString
argument_list|()
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
specifier|public
name|int
index|[]
index|[]
name|getHeaders
parameter_list|()
throws|throws
name|ClusterException
block|{
name|int
index|[]
index|[]
name|data
init|=
operator|new
name|int
index|[
literal|2
index|]
index|[]
decl_stmt|;
name|data
index|[
literal|0
index|]
operator|=
operator|new
name|int
index|[]
block|{
name|journalManager
operator|.
name|getLastIdSaved
argument_list|()
block|,
name|journalManager
operator|.
name|getMaxIdSaved
argument_list|()
block|,
name|journalManager
operator|.
name|getCounter
argument_list|()
block|}
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|coordinator
condition|)
name|data
index|[
literal|1
index|]
operator|=
operator|(
name|int
index|[]
operator|)
name|disp
operator|.
name|callRemoteMethod
argument_list|(
name|coordinatorAddress
argument_list|,
literal|"getRemoteHeader"
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|,
name|GroupRequest
operator|.
name|GET_FIRST
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ClusterException
argument_list|(
literal|"Error retrieving ..."
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|data
return|;
block|}
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
comment|/**      * **************  ---------------------------------------- *******************************      */
comment|/**      * Configure the cluster communication      *      * @param c      */
specifier|public
specifier|static
name|void
name|configure
parameter_list|(
name|Configuration
name|c
parameter_list|)
throws|throws
name|ClusterException
block|{
name|createInstance
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|synch
parameter_list|()
throws|throws
name|ClusterException
block|{
name|journalManager
operator|.
name|squeueEvent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|removeDocument
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|documentName
parameter_list|)
throws|throws
name|ClusterException
block|{
if|if
condition|(
name|excludedCollection
operator|.
name|contains
argument_list|(
name|collection
argument_list|)
condition|)
return|return;
name|remoteInvocation
argument_list|(
operator|new
name|RemoveClusterEvent
argument_list|(
name|documentName
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|storeDocument
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|documentName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|ClusterException
block|{
if|if
condition|(
name|excludedCollection
operator|.
name|contains
argument_list|(
name|collection
argument_list|)
condition|)
return|return;
name|remoteInvocation
argument_list|(
operator|new
name|StoreClusterEvent
argument_list|(
name|content
argument_list|,
name|collection
argument_list|,
name|documentName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addCollection
parameter_list|(
name|String
name|parent
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|ClusterException
block|{
if|if
condition|(
name|excludedCollection
operator|.
name|contains
argument_list|(
name|parent
argument_list|)
operator|||
name|excludedCollection
operator|.
name|contains
argument_list|(
name|parent
operator|+
literal|"/"
operator|+
name|collectionName
argument_list|)
condition|)
return|return;
name|remoteInvocation
argument_list|(
operator|new
name|CreateCollectionClusterEvent
argument_list|(
name|parent
argument_list|,
name|collectionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|update
parameter_list|(
name|String
name|resource
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|xupdate
parameter_list|)
throws|throws
name|ClusterException
block|{
if|if
condition|(
name|excludedCollection
operator|.
name|contains
argument_list|(
name|resource
argument_list|)
condition|)
return|return;
comment|//avoid to propagate the internal collection for example temp.
name|remoteInvocation
argument_list|(
operator|new
name|UpdateClusterEvent
argument_list|(
name|resource
argument_list|,
name|name
argument_list|,
name|xupdate
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|String
name|parent
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|ClusterException
block|{
if|if
condition|(
name|excludedCollection
operator|.
name|contains
argument_list|(
name|collection
argument_list|)
operator|||
name|excludedCollection
operator|.
name|contains
argument_list|(
name|parent
operator|+
literal|"/"
operator|+
name|collection
argument_list|)
condition|)
return|return;
comment|//avoid to propagate the internal collection for example temp.
name|remoteInvocation
argument_list|(
operator|new
name|RemoveCollectionClusterEvent
argument_list|(
name|parent
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|remoteInvocation
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
throws|throws
name|ClusterException
block|{
name|String
name|code
init|=
literal|""
operator|+
name|event
operator|.
name|hashCode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ClusterChannel
operator|.
name|hasToBePublished
argument_list|(
name|code
argument_list|)
condition|)
block|{
name|ClusterChannel
operator|.
name|removeEvent
argument_list|(
name|code
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
index|[]
name|data
init|=
name|getId
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|event
operator|.
name|setId
argument_list|(
name|data
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|event
operator|.
name|setCounter
argument_list|(
name|data
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|journalManager
operator|.
name|enqueEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
comment|//add event to the journal queue
name|disp
operator|.
name|callRemoteMethods
argument_list|(
name|membersNoSender
argument_list|,
literal|"invoke"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|event
block|}
argument_list|,
operator|new
name|Class
index|[]
block|{
name|ClusterEvent
operator|.
name|class
block|}
argument_list|,
name|GroupRequest
operator|.
name|GET_NONE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|coordinator
condition|)
name|journalIdGenerator
operator|.
name|increaseId
argument_list|(
name|event
operator|.
name|getId
argument_list|()
argument_list|,
name|event
operator|.
name|getCounter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Retrieve the id for the journal      *      * @return the unique id      * @throws ClusterException      * @param firstRequest      */
specifier|private
name|int
index|[]
name|getId
parameter_list|(
name|boolean
name|firstRequest
parameter_list|)
throws|throws
name|ClusterException
block|{
try|try
block|{
name|int
index|[]
name|id
decl_stmt|;
if|if
condition|(
name|coordinator
condition|)
block|{
comment|//if I'am a master - create next id
name|log
operator|.
name|info
argument_list|(
literal|"GENERATING LOCAL ID..."
argument_list|)
expr_stmt|;
name|id
operator|=
name|journalIdGenerator
operator|.
name|getNextData
argument_list|(
name|localAddress
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// ask to the master the next id --> rpc to getNextDataRemote
name|log
operator|.
name|info
argument_list|(
literal|"RETRIEVING ID FROM "
operator|+
name|coordinatorAddress
argument_list|)
expr_stmt|;
name|Object
name|idObj
init|=
name|disp
operator|.
name|callRemoteMethod
argument_list|(
name|coordinatorAddress
argument_list|,
literal|"getNextDataRemote"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|localAddress
operator|.
name|toString
argument_list|()
block|}
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
argument_list|,
name|GroupRequest
operator|.
name|GET_FIRST
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|id
operator|=
operator|(
operator|(
name|int
index|[]
operator|)
name|idObj
operator|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
catch|catch
parameter_list|(
name|SuspectedException
name|se
parameter_list|)
block|{
if|if
condition|(
operator|!
name|firstRequest
condition|)
throw|throw
operator|new
name|ClusterException
argument_list|(
literal|"unable to retrieve the journal id... master down ... no more retry "
argument_list|,
name|se
argument_list|)
throw|;
name|log
operator|.
name|info
argument_list|(
literal|"SUSPECTED MASTER SHUTDOWN .... RETRY..."
argument_list|)
expr_stmt|;
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"WAITING FOR NEW MASTER"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
return|return
name|getId
argument_list|(
literal|false
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ClusterException
argument_list|(
literal|"unable to retrieve the journal id "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|realign
parameter_list|()
throws|throws
name|ClusterException
block|{
if|if
condition|(
name|coordinator
condition|)
return|return;
comment|//TODO: per ora assumiamo che il master (o chi diventa master) sia allineato.
name|int
name|last
init|=
name|ClusterEvent
operator|.
name|NO_EVENT
decl_stmt|;
try|try
block|{
name|ArrayList
name|events
init|=
literal|null
decl_stmt|;
name|int
index|[]
name|header
init|=
operator|new
name|int
index|[]
block|{
name|journalManager
operator|.
name|getLastIdSaved
argument_list|()
block|,
name|journalManager
operator|.
name|getMaxIdSaved
argument_list|()
block|,
name|journalManager
operator|.
name|getCounter
argument_list|()
block|}
decl_stmt|;
name|int
index|[]
name|remoteHeader
init|=
operator|(
name|int
index|[]
operator|)
name|disp
operator|.
name|callRemoteMethod
argument_list|(
name|coordinatorAddress
argument_list|,
literal|"getRemoteHeader"
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|,
name|GroupRequest
operator|.
name|GET_FIRST
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|counterDiff
init|=
name|Math
operator|.
name|abs
argument_list|(
name|header
index|[
literal|2
index|]
operator|-
name|remoteHeader
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|counterDiff
operator|>
literal|1
condition|)
name|killNoRealign
argument_list|()
expr_stmt|;
if|if
condition|(
name|counterDiff
operator|==
literal|1
operator|&&
name|remoteHeader
index|[
literal|1
index|]
operator|>
name|header
index|[
literal|1
index|]
condition|)
name|killNoRealign
argument_list|()
expr_stmt|;
if|if
condition|(
name|counterDiff
operator|==
literal|0
operator|&&
name|header
index|[
literal|1
index|]
operator|>
name|remoteHeader
index|[
literal|1
index|]
condition|)
name|killClusterMasterDisaligned
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Call remote method getNextEvents: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|idObj
init|=
name|disp
operator|.
name|callRemoteMethod
argument_list|(
name|coordinatorAddress
argument_list|,
literal|"getNextEvents"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|header
block|,
name|remoteHeader
block|,
operator|new
name|Integer
argument_list|(
name|last
argument_list|)
block|}
argument_list|,
operator|new
name|Class
index|[]
block|{
name|int
index|[]
operator|.
expr|class
block|,
name|int
index|[]
operator|.
expr|class
block|,
name|Integer
operator|.
name|class
block|}
argument_list|,
name|GroupRequest
operator|.
name|GET_FIRST
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|events
operator|=
operator|(
operator|(
name|ArrayList
operator|)
name|idObj
operator|)
expr_stmt|;
if|if
condition|(
name|events
operator|==
literal|null
operator|||
name|events
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
break|break;
name|last
operator|=
name|manageEvents
argument_list|(
name|events
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Last id managed : "
operator|+
name|last
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|realignQueue
init|)
block|{
while|while
condition|(
name|realignQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//execute the queue ....
name|ClusterEvent
name|event
init|=
operator|(
name|ClusterEvent
operator|)
name|realignQueue
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Execute the event "
operator|+
name|event
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterChannel
operator|.
name|accountEvent
argument_list|(
literal|""
operator|+
name|event
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|journalManager
operator|.
name|isProcessed
argument_list|(
name|event
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Event  processed .........."
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|manageEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
name|isRealign
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"No align done successfully ..."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ClusterException
argument_list|(
literal|"No align done successfully ..."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|killClusterMasterDisaligned
parameter_list|()
block|{
name|log
operator|.
name|fatal
argument_list|(
literal|"MASTER DISALIGNED... CLUSTER DATA MAY BE CORRUPTED"
argument_list|)
expr_stmt|;
name|log
operator|.
name|fatal
argument_list|(
literal|"PLEASE STOP CLUSTER AND FIX COLLECTION AND JOURNAL DATA"
argument_list|)
expr_stmt|;
comment|//TODO ... to be implemented... MUSTER DISALIGNED
block|}
specifier|private
name|void
name|killNoRealign
parameter_list|()
throws|throws
name|ClusterException
block|{
name|log
operator|.
name|fatal
argument_list|(
literal|"NODE DISALIGNED... no hot realignement available.... please fix node collection and journal data"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ClusterException
argument_list|(
literal|"NODE DISALIGNED"
argument_list|)
throw|;
block|}
specifier|private
name|int
name|manageEvents
parameter_list|(
name|ArrayList
argument_list|<
name|ClusterEvent
argument_list|>
name|events
parameter_list|)
throws|throws
name|ClusterException
block|{
for|for
control|(
name|ClusterEvent
name|event
range|:
name|events
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Manage event id "
operator|+
name|event
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|journalManager
operator|.
name|isProcessed
argument_list|(
name|event
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"event already processed ........."
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|ClusterChannel
operator|.
name|accountEvent
argument_list|(
literal|""
operator|+
name|event
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|manageEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
return|return
name|events
operator|.
name|get
argument_list|(
name|events
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
return|;
block|}
specifier|private
name|void
name|manageEvent
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
throws|throws
name|ClusterException
block|{
name|event
operator|.
name|execute
argument_list|()
expr_stmt|;
name|journalManager
operator|.
name|enqueEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
if|if
condition|(
name|coordinator
condition|)
name|journalIdGenerator
operator|.
name|releaseId
argument_list|(
name|event
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|journalIdGenerator
operator|.
name|increaseId
argument_list|(
name|event
operator|.
name|getId
argument_list|()
argument_list|,
name|event
operator|.
name|getCounter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* -------------- REMOTE METHODS --------------------- */
specifier|public
name|ArrayList
name|getNextEvents
parameter_list|(
name|int
index|[]
name|header
parameter_list|,
name|int
index|[]
name|myHeader
parameter_list|,
name|Integer
name|start
parameter_list|)
block|{
return|return
name|journalManager
operator|.
name|getNextEvents
argument_list|(
name|header
argument_list|,
name|myHeader
argument_list|,
name|start
argument_list|)
return|;
block|}
specifier|public
name|int
index|[]
name|getNextDataRemote
parameter_list|(
name|String
name|address
parameter_list|)
block|{
return|return
name|journalIdGenerator
operator|.
name|getNextData
argument_list|(
name|address
argument_list|)
return|;
block|}
specifier|public
name|void
name|invoke
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
throws|throws
name|ClusterException
block|{
name|String
name|code
init|=
literal|""
operator|+
name|event
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|ClusterChannel
operator|.
name|accountEvent
argument_list|(
name|code
argument_list|)
expr_stmt|;
comment|//reentrant fix
synchronized|synchronized
init|(
name|realignQueue
init|)
block|{
if|if
condition|(
name|isRealign
condition|)
block|{
name|realignQueue
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|manageEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
index|[]
name|getRemoteHeader
parameter_list|()
throws|throws
name|ClusterException
block|{
return|return
operator|new
name|int
index|[]
block|{
name|journalManager
operator|.
name|getLastIdSaved
argument_list|()
block|,
name|journalManager
operator|.
name|getMaxIdSaved
argument_list|()
block|,
name|journalManager
operator|.
name|getCounter
argument_list|()
block|}
return|;
block|}
specifier|public
name|ConsoleInfo
name|getConsoleProperties
parameter_list|()
throws|throws
name|ClusterException
block|{
name|String
name|port
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|)
decl_stmt|;
if|if
condition|(
name|port
operator|==
literal|null
condition|)
name|port
operator|=
literal|"8080"
expr_stmt|;
comment|//TODO ... verify how to retrieve default port
name|ConsoleInfo
name|info
init|=
operator|new
name|ConsoleInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setProperty
argument_list|(
literal|"port"
argument_list|,
name|port
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|disp
operator|.
name|stop
argument_list|()
expr_stmt|;
name|channel
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|instance
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

