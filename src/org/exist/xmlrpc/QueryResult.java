begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * QueryResult.java - Mar 28, 2003  *   * @author wolf  */
end_comment

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
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|Value
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  * To change this generated comment go to   * Window>Preferences>Java>Code Generation>Code and Comments  */
end_comment

begin_class
specifier|public
class|class
name|QueryResult
block|{
name|long
name|queryTime
init|=
literal|0
decl_stmt|;
name|Value
name|result
decl_stmt|;
name|long
name|timestamp
init|=
literal|0
decl_stmt|;
specifier|public
name|QueryResult
parameter_list|(
name|Value
name|result
parameter_list|,
name|long
name|queryTime
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|queryTime
operator|=
name|queryTime
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

