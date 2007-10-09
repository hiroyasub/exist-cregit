begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

begin_comment
comment|/**  * Indexes that store their values in a determinist way (whatever it is) should implement this interface.  *   * @author brihaye  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|OrderedValuesIndex
extends|extends
name|IndexWorker
block|{
comment|/**      * A key to the value "hint" to start from when the index scans its index entries      */
specifier|public
specifier|static
specifier|final
name|String
name|START_VALUE
init|=
literal|"start_value"
decl_stmt|;
comment|/**      * A key to the value "hint" to end with when the index scans its index entries      */
specifier|public
specifier|static
specifier|final
name|String
name|END_VALUE
init|=
literal|"end_value"
decl_stmt|;
block|}
end_interface

end_unit

