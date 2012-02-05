begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|LockedDocumentMap
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
name|LockException
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|MutableDocumentSet
extends|extends
name|DocumentSet
block|{
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|checkDuplicates
parameter_list|)
function_decl|;
name|void
name|addAll
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
function_decl|;
name|void
name|addAll
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|String
index|[]
name|paths
parameter_list|)
function_decl|;
name|void
name|addAll
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|String
index|[]
name|paths
parameter_list|,
name|LockedDocumentMap
name|lockMap
parameter_list|,
name|int
name|lockType
parameter_list|)
throws|throws
name|LockException
function_decl|;
name|void
name|addCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

