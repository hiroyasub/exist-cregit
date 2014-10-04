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
name|repo
operator|.
name|Deployment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|*
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

begin_class
specifier|public
class|class
name|GetAppRoot
extends|extends
name|BasicFunction
block|{
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
literal|"get-root"
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
literal|"Returns the root collection into which applications are installed. Corresponds to the "
operator|+
literal|"collection path defined in conf.xml (<repository root=\"...\"/>) or /db if not configured."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The application root collection"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetAppRoot
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
annotation|@
name|Override
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
name|String
name|configured
init|=
operator|(
name|String
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|Deployment
operator|.
name|PROPERTY_APP_ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|configured
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|configured
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

