begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xqdoc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xqdoc
operator|.
name|conversion
operator|.
name|XQDocController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xqdoc
operator|.
name|conversion
operator|.
name|XQDocException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xqdoc
operator|.
name|conversion
operator|.
name|XQDocPayload
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|XQDocHelper
block|{
specifier|private
name|XQDocController
name|controller
decl_stmt|;
specifier|public
name|XQDocHelper
parameter_list|()
throws|throws
name|XQDocException
block|{
name|controller
operator|=
operator|new
name|XQDocController
argument_list|(
name|XQDocController
operator|.
name|JAN2007
argument_list|)
expr_stmt|;
name|controller
operator|.
name|setEncodeURIs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|scan
parameter_list|(
name|Source
name|source
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|XQDocException
throws|,
name|IOException
block|{
name|XQDocPayload
name|payload
init|=
name|controller
operator|.
name|process
argument_list|(
name|source
operator|.
name|getReader
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|payload
operator|.
name|getXQDocXML
argument_list|()
return|;
block|}
block|}
end_class

end_unit
