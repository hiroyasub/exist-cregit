begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|expathrepo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|ErrorCodes
operator|.
name|ErrorCode
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|EXPathErrorCode
extends|extends
name|ErrorCode
block|{
comment|/**      * EXPATH specific errors [EXP][DY|SE|ST][nnnn]      *       * EXP = EXPath      * DY = Dynamic      * DY = Dynamic      * SE = Serialization      * ST = Static      * nnnn = number      */
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXPDY001
init|=
operator|new
name|EXPathErrorCode
argument_list|(
literal|"EXPATH001"
argument_list|,
literal|"Package not found."
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXPDY002
init|=
operator|new
name|EXPathErrorCode
argument_list|(
literal|"EXPATH002"
argument_list|,
literal|"Bad collection URI."
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXPDY003
init|=
operator|new
name|EXPathErrorCode
argument_list|(
literal|"EXPATH003"
argument_list|,
literal|"Permission denied."
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXPDY004
init|=
operator|new
name|EXPathErrorCode
argument_list|(
literal|"EXPATH004"
argument_list|,
literal|"Error in descriptor found."
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXPATH_ERROR_NS
init|=
literal|"http://expath.org/ns/error"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXPATH_ERROR_PREFIX
init|=
literal|"experr"
decl_stmt|;
specifier|private
name|EXPathErrorCode
parameter_list|(
name|String
name|code
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|QName
argument_list|(
name|code
argument_list|,
name|EXPATH_ERROR_NS
argument_list|,
name|EXPATH_ERROR_PREFIX
argument_list|)
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

