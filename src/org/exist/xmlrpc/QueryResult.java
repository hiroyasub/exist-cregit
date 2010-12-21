begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|xquery
operator|.
name|value
operator|.
name|BinaryValue
import|;
end_import

begin_comment
comment|/**  * Simple container for the results of a query. Used to cache  * query results that may be retrieved later by the client.  *   * @author wolf  * @author jmfernandez  */
end_comment

begin_class
specifier|public
class|class
name|QueryResult
extends|extends
name|AbstractCachedResult
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|QueryResult
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Sequence
name|result
decl_stmt|;
specifier|protected
name|Properties
name|serialization
init|=
literal|null
decl_stmt|;
comment|// set upon failure
specifier|protected
name|XPathException
name|exception
init|=
literal|null
decl_stmt|;
specifier|public
name|QueryResult
parameter_list|(
name|Sequence
name|result
parameter_list|,
name|Properties
name|outputProperties
parameter_list|)
block|{
name|this
argument_list|(
name|result
argument_list|,
name|outputProperties
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QueryResult
parameter_list|(
name|Sequence
name|result
parameter_list|,
name|Properties
name|outputProperties
parameter_list|,
name|long
name|queryTime
parameter_list|)
block|{
name|super
argument_list|(
name|queryTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|serialization
operator|=
name|outputProperties
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
specifier|public
name|QueryResult
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasErrors
parameter_list|()
block|{
return|return
name|exception
operator|!=
literal|null
return|;
block|}
specifier|public
name|XPathException
name|getException
parameter_list|()
block|{
return|return
name|exception
return|;
block|}
comment|/**      * @return Returns the result.      */
annotation|@
name|Override
specifier|public
name|Sequence
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|free
parameter_list|()
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|//cleanup any binary values
if|if
condition|(
name|result
operator|instanceof
name|BinaryValue
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|BinaryValue
operator|)
name|result
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to cleanup BinaryValue: "
operator|+
name|result
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

