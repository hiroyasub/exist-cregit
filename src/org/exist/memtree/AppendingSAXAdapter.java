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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|AppendingSAXAdapter
extends|extends
name|SAXAdapter
block|{
specifier|public
name|AppendingSAXAdapter
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|setBuilder
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
comment|//do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
comment|//do nothing
block|}
block|}
end_class

end_unit

