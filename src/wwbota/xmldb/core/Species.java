begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/** Java class "Species.java" generated from Poseidon for UML.  *  Poseidon for UML is developed by<A HREF="http://www.gentleware.com">Gentleware</A>.  *  Generated with<A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.  */
end_comment

begin_package
package|package
name|wwbota
operator|.
name|xmldb
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  *<p>  *   *</p>  */
end_comment

begin_class
specifier|public
class|class
name|Species
block|{
comment|///////////////////////////////////////
comment|// attributes
comment|/**  *<p>  * Represents ...  *</p>  */
specifier|private
name|Element
name|description
decl_stmt|;
comment|///////////////////////////////////////
comment|// associations
comment|/**  *<p>  *   *</p>  */
specifier|public
name|Collection
name|part
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// of type Organ
comment|///////////////////////////////////////
comment|// access methods for associations
specifier|public
name|Collection
name|getParts
parameter_list|()
block|{
return|return
name|part
return|;
block|}
specifier|public
name|void
name|addPart
parameter_list|(
name|Organ
name|organ
parameter_list|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|part
operator|.
name|contains
argument_list|(
name|organ
argument_list|)
condition|)
name|this
operator|.
name|part
operator|.
name|add
argument_list|(
name|organ
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removePart
parameter_list|(
name|Organ
name|organ
parameter_list|)
block|{
name|this
operator|.
name|part
operator|.
name|remove
argument_list|(
name|organ
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// end Species
end_comment

end_unit

