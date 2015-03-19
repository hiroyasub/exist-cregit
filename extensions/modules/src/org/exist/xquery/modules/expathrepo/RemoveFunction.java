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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|repo
operator|.
name|ExistRepository
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XQueryContext
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|PackageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|UserInteractionStrategy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|tui
operator|.
name|BatchUserInteraction
import|;
end_import

begin_comment
comment|/**  * Remove Function: Remove package from repository  *  * @author James Fuller<jim.fuller@exist-db.org>  * @author cutlass  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|RemoveFunction
extends|extends
name|BasicFunction
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|RemoveFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"remove"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Remove package from repository."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"text"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"package name"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true if successful, false otherwise"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|RemoveFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|removed
init|=
name|BooleanValue
operator|.
name|TRUE
decl_stmt|;
name|boolean
name|force
init|=
literal|false
decl_stmt|;
name|UserInteractionStrategy
name|interact
init|=
operator|new
name|BatchUserInteraction
argument_list|()
decl_stmt|;
name|String
name|pkg
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|pkg
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Package name required"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ExistRepository
name|repo
init|=
name|getContext
argument_list|()
operator|.
name|getRepository
argument_list|()
decl_stmt|;
name|Repository
name|parent_repo
init|=
name|repo
operator|.
name|getParentRepo
argument_list|()
decl_stmt|;
name|parent_repo
operator|.
name|removePackage
argument_list|(
name|pkg
argument_list|,
name|force
argument_list|,
name|interact
argument_list|)
expr_stmt|;
name|repo
operator|.
name|reportAction
argument_list|(
name|ExistRepository
operator|.
name|Action
operator|.
name|UNINSTALL
argument_list|,
name|pkg
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryPool
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PackageException
name|ex
parameter_list|)
block|{
return|return
name|removed
return|;
comment|// /TODO: _repo.removePackage seems to throw PackageException
comment|// throw new XPathException("Problem removing package " + pkg + " in expath repository, check that eXist-db has access permissions to expath repository file directory  ", ex);
block|}
return|return
name|removed
return|;
block|}
block|}
end_class

end_unit

