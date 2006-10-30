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
operator|.
name|journal
package|;
end_package

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
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|ClusterEvent
import|;
end_import

begin_comment
comment|/**  * Manage the generation of the unique journal Id  * Created by Nicola Breda.  *  * @author Nicola Breda aka maiale  * @author David Frontini aka spider  *         Date: 05-aug-2005  *         Time: 18.09.08  *         Revision $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JournalIdGenerator
block|{
specifier|public
specifier|static
name|int
name|MAX_STORED_INDEX
init|=
literal|65000
decl_stmt|;
specifier|private
name|int
name|lastId
init|=
name|ClusterEvent
operator|.
name|NO_EVENT
decl_stmt|;
specifier|private
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|private
name|HashMap
name|idInUse
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|public
name|JournalIdGenerator
parameter_list|(
name|JournalManager
name|journal
parameter_list|,
name|int
name|maxItem
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MAX STORE IN ID GENERATOR = "
operator|+
name|maxItem
argument_list|)
expr_stmt|;
name|lastId
operator|=
name|journal
operator|.
name|getMaxIdSaved
argument_list|()
expr_stmt|;
name|counter
operator|=
name|journal
operator|.
name|getCounter
argument_list|()
expr_stmt|;
name|MAX_STORED_INDEX
operator|=
name|maxItem
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|int
index|[]
name|getNextData
parameter_list|(
name|String
name|address
parameter_list|)
block|{
name|lastId
operator|=
name|lastId
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|lastId
operator|>
name|MAX_STORED_INDEX
condition|)
block|{
name|lastId
operator|=
literal|0
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
name|idInUse
operator|.
name|put
argument_list|(
literal|""
operator|+
name|lastId
argument_list|,
name|address
argument_list|)
expr_stmt|;
return|return
operator|new
name|int
index|[]
block|{
name|lastId
block|,
name|counter
block|}
return|;
block|}
specifier|public
name|void
name|setLastId
parameter_list|(
name|int
name|lastId
parameter_list|)
block|{
name|this
operator|.
name|lastId
operator|=
name|lastId
expr_stmt|;
block|}
specifier|public
name|void
name|setCounter
parameter_list|(
name|int
name|counter
parameter_list|)
block|{
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
block|}
specifier|public
name|void
name|releaseId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|idInUse
operator|.
name|remove
argument_list|(
literal|""
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|increaseId
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|counter
parameter_list|)
block|{
comment|//TODO pensare meglio questa parte -- rimane il problema della rotazione
if|if
condition|(
operator|(
name|id
operator|>
name|lastId
operator|)
operator|||
operator|(
name|this
operator|.
name|counter
operator|!=
name|counter
operator|)
condition|)
block|{
name|lastId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
block|}
block|}
specifier|public
name|int
index|[]
name|getData
parameter_list|()
block|{
return|return
operator|new
name|int
index|[]
block|{
name|lastId
block|,
name|counter
block|}
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|shiftId
parameter_list|(
name|int
name|shift
parameter_list|)
block|{
name|lastId
operator|+=
name|shift
expr_stmt|;
if|if
condition|(
name|lastId
operator|>
name|MAX_STORED_INDEX
condition|)
block|{
name|lastId
operator|-=
name|MAX_STORED_INDEX
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

