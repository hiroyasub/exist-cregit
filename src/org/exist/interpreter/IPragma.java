begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|interpreter
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
name|AnalyzeContextInfo
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
name|Expression
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
name|XPathException
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
name|Item
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
name|Sequence
import|;
end_import

begin_interface
specifier|public
interface|interface
name|IPragma
block|{
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|before
parameter_list|(
name|Context
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|after
parameter_list|(
name|Context
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
function_decl|;
specifier|public
name|String
name|getContents
parameter_list|()
function_decl|;
specifier|public
name|QName
name|getQName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

