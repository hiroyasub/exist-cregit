begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * CollectionImpl.java - Aug 4, 2003  *   * @author wolf  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * This interface extends org.xmldb.api.base.Collection with extensions specific to eXist.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CollectionImpl
extends|extends
name|Collection
block|{
comment|/** 	 * Returns the time of creation of the collection. 	 * @return 	 */
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

