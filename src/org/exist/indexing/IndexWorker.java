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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
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
name|DatabaseConfigurationException
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

begin_comment
comment|/**  * Provide concurrent access to an index structure. Implements the core operations on the index.  * The methods in this class are used in a multi-threaded environment.  * {@link org.exist.indexing.Index#getWorker()} should  * thus return a new IndexWorker whenever it is  called. Implementations of IndexWorker have  * to take care of synchronizing access to shared resources.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexWorker
block|{
name|String
name|getIndexId
parameter_list|()
function_decl|;
name|Object
name|configure
parameter_list|(
name|NodeList
name|configNodes
parameter_list|,
name|Map
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
function_decl|;
name|void
name|flush
parameter_list|()
function_decl|;
name|StreamListener
name|getListener
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

