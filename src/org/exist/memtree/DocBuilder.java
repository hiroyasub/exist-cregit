begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|memtree
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_comment
comment|/**  * Interface to create a new in-memory document using a {@link MemTreeBuilder}.  *   * @see XQueryContext#createDocument(DocBuilder)  * @author wolf  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|DocBuilder
block|{
specifier|public
name|void
name|build
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

