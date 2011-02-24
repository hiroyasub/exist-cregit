begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
import|;
end_import

begin_class
specifier|public
class|class
name|TaskStatus
implements|implements
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8405783622910875893L
decl_stmt|;
specifier|public
enum|enum
name|Status
block|{
name|NA
block|,
name|NEVER_RUN
block|,
name|INIT
block|,
name|PAUSED
block|,
name|STOPPED_OK
block|,
name|STOPPED_ERROR
block|,
name|RUNNING_CHECK
block|,
name|RUNNING_BACKUP
block|,
name|PING_OK
block|,
name|PING_ERROR
block|,
name|PING_WAIT
block|}
specifier|private
name|Status
name|status
init|=
name|Status
operator|.
name|NA
decl_stmt|;
specifier|private
name|Date
name|_statusChangeTime
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
specifier|private
name|Object
name|_reason
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|_percentageDone
init|=
literal|0
decl_stmt|;
specifier|public
name|TaskStatus
parameter_list|(
name|Status
name|newStatus
parameter_list|)
block|{
name|setStatus
argument_list|(
name|newStatus
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|getReason
parameter_list|()
block|{
return|return
name|_reason
return|;
block|}
specifier|public
name|void
name|setReason
parameter_list|(
name|Object
name|reason
parameter_list|)
block|{
if|if
condition|(
name|reason
operator|!=
literal|null
condition|)
block|{
name|_reason
operator|=
name|reason
expr_stmt|;
block|}
block|}
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
specifier|public
name|void
name|setStatus
parameter_list|(
name|Status
name|newStatus
parameter_list|)
block|{
name|status
operator|=
name|newStatus
expr_stmt|;
block|}
specifier|public
name|String
name|getStatusString
parameter_list|()
block|{
name|String
name|percentageInfo
init|=
literal|""
decl_stmt|;
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|INIT
case|:
case|case
name|NA
case|:
case|case
name|NEVER_RUN
case|:
case|case
name|STOPPED_OK
case|:
case|case
name|PING_ERROR
case|:
case|case
name|PING_OK
case|:
case|case
name|PING_WAIT
case|:
break|break;
default|default:
name|percentageInfo
operator|=
literal|" - "
operator|+
name|_percentageDone
operator|+
literal|"% done"
expr_stmt|;
break|break;
block|}
return|return
name|toString
argument_list|()
operator|+
name|percentageInfo
return|;
block|}
specifier|public
name|Date
name|getStatusChangeTime
parameter_list|()
block|{
return|return
name|_statusChangeTime
return|;
block|}
specifier|public
name|void
name|setStatusChangeTime
parameter_list|()
block|{
name|_statusChangeTime
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setPercentage
parameter_list|(
name|int
name|percentage
parameter_list|)
block|{
if|if
condition|(
name|percentage
operator|>
literal|0
operator|&&
name|percentage
operator|<
literal|101
condition|)
block|{
name|_percentageDone
operator|=
name|percentage
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getPercentage
parameter_list|()
block|{
return|return
name|_percentageDone
return|;
block|}
specifier|public
name|CompositeDataSupport
name|getCompositeData
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
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
name|CompositeDataSupport
name|compositeData
init|=
literal|null
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"statusChangeTime"
argument_list|,
name|_statusChangeTime
argument_list|)
expr_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"reason"
argument_list|,
name|_reason
argument_list|)
expr_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"percentage"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|_percentageDone
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|compositeData
operator|=
operator|new
name|CompositeDataSupport
argument_list|(
operator|new
name|CompositeType
argument_list|(
literal|"TaskStatus"
argument_list|,
literal|"Status of the task"
argument_list|,
comment|//
operator|new
name|String
index|[]
block|{
literal|"status"
block|,
literal|"statusChangeTime"
block|,
literal|"reason"
block|,
literal|"percentage"
block|}
argument_list|,
comment|//
operator|new
name|String
index|[]
block|{
literal|"status of the task"
block|,
literal|"reason for this status"
block|,
literal|"time when the status has changed"
block|,
literal|"percentage of work"
block|}
argument_list|,
comment|//
operator|new
name|SimpleType
index|[]
block|{
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|DATE
block|,
name|SimpleType
operator|.
name|OBJECTNAME
block|,
name|SimpleType
operator|.
name|INTEGER
block|}
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
name|e
parameter_list|)
block|{
comment|// TODO TI: Make correct error handling
block|}
return|return
name|compositeData
return|;
block|}
specifier|public
specifier|static
name|TaskStatus
name|getTaskStatus
parameter_list|(
name|CompositeDataSupport
name|compositeData
parameter_list|)
block|{
name|TaskStatus
name|status
init|=
operator|new
name|TaskStatus
argument_list|(
operator|(
name|Status
operator|)
name|compositeData
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
decl_stmt|;
name|status
operator|.
name|_reason
operator|=
name|compositeData
operator|.
name|get
argument_list|(
literal|"reason"
argument_list|)
expr_stmt|;
name|status
operator|.
name|_statusChangeTime
operator|=
operator|(
name|Date
operator|)
name|compositeData
operator|.
name|get
argument_list|(
literal|"statusChangeTime"
argument_list|)
expr_stmt|;
name|status
operator|.
name|_percentageDone
operator|=
operator|(
operator|(
name|Integer
operator|)
name|compositeData
operator|.
name|get
argument_list|(
literal|"percentage"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
name|status
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|status
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

