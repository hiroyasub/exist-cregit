begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|cocoon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|ResourceSet
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * XSPHelper.java enclosing_type  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|XSPHelper
block|{
specifier|private
name|TreeMap
name|collections
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|TreeMap
name|documents
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|ResourceSet
name|result
decl_stmt|;
specifier|public
name|XSPHelper
parameter_list|(
name|ResourceSet
name|result
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
return|return;
name|ArrayList
name|hitsByDoc
decl_stmt|;
name|XMLResource
name|resource
decl_stmt|;
name|Collection
name|currentCollection
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
operator|(
name|long
operator|)
name|i
argument_list|)
expr_stmt|;
name|currentCollection
operator|=
name|resource
operator|.
name|getParentCollection
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|documents
operator|=
operator|(
name|TreeMap
operator|)
name|collections
operator|.
name|get
argument_list|(
name|currentCollection
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|documents
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
name|collections
operator|.
name|put
argument_list|(
name|currentCollection
operator|.
name|getName
argument_list|()
argument_list|,
name|documents
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|hitsByDoc
operator|=
operator|(
name|ArrayList
operator|)
name|documents
operator|.
name|get
argument_list|(
name|resource
operator|.
name|getDocumentId
argument_list|()
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|hitsByDoc
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|documents
operator|.
name|put
argument_list|(
name|resource
operator|.
name|getDocumentId
argument_list|()
argument_list|,
name|hitsByDoc
argument_list|)
expr_stmt|;
block|}
name|hitsByDoc
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getHits
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|result
operator|==
literal|null
condition|?
literal|0
else|:
operator|(
name|int
operator|)
name|result
operator|.
name|getSize
argument_list|()
return|;
block|}
specifier|public
name|ResourceSet
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

