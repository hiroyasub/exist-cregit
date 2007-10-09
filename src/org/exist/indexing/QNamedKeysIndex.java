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
comment|/**  * Indexes that store their values with an information about the QName of their nodes   * should implement this interface.  *   * @author brihaye  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|QNamedKeysIndex
extends|extends
name|IndexWorker
block|{
comment|/**      * A key to a QName {@link java.util.List} "hint" to be used when the index scans its index entries      */
specifier|public
specifier|static
specifier|final
name|String
name|QNAMES_KEY
init|=
literal|"qnames_key"
decl_stmt|;
block|}
end_interface

end_unit

