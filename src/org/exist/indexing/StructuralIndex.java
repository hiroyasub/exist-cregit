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
name|exist
operator|.
name|dom
operator|.
name|DocumentSet
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
name|ExtNodeSet
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
name|NodeSet
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
name|QName
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
name|NodeSelector
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
name|NodeTest
import|;
end_import

begin_comment
comment|/**  * Core interface for structural indexes. The structural index provides access to elements and attributes  * through their name and relation.  */
end_comment

begin_interface
specifier|public
interface|interface
name|StructuralIndex
block|{
specifier|public
specifier|final
specifier|static
name|String
name|STRUCTURAL_INDEX_ID
init|=
literal|"structural-index"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_CLASS
init|=
literal|"org.exist.storage.structural.NativeStructuralIndex"
decl_stmt|;
specifier|public
name|boolean
name|matchElementsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|qname
parameter_list|,
name|NodeSelector
name|selector
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|matchDescendantsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|QName
name|qname
parameter_list|,
name|int
name|axis
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|ExtNodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
function_decl|;
specifier|public
name|NodeSet
name|findElementsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|qname
parameter_list|,
name|NodeSelector
name|selector
parameter_list|)
function_decl|;
specifier|public
name|NodeSet
name|findDescendantsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|QName
name|qname
parameter_list|,
name|int
name|axis
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
function_decl|;
specifier|public
name|NodeSet
name|findAncestorsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|QName
name|qname
parameter_list|,
name|int
name|axis
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
function_decl|;
comment|/**      * Find all nodes matching a given node test, axis and type. Used to evaluate wildcard      * expressions like //*, //pfx:*.      */
specifier|public
name|NodeSet
name|scanByType
parameter_list|(
name|byte
name|type
parameter_list|,
name|int
name|axis
parameter_list|,
name|NodeTest
name|test
parameter_list|,
name|boolean
name|useSelfAsContext
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

