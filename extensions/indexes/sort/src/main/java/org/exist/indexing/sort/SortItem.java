begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|sort
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|NodeProxy
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
name|AtomicValue
import|;
end_import

begin_interface
specifier|public
interface|interface
name|SortItem
extends|extends
name|Comparable
argument_list|<
name|SortItem
argument_list|>
block|{
name|AtomicValue
name|getValue
parameter_list|()
function_decl|;
name|void
name|setValue
parameter_list|(
name|AtomicValue
name|value
parameter_list|)
function_decl|;
name|NodeProxy
name|getNode
parameter_list|()
function_decl|;
name|int
name|compareTo
parameter_list|(
name|SortItem
name|sortItem
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

