begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|inspect
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
name|BinaryDocument
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
name|DocumentImpl
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
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|AccessContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|DBSource
import|;
end_import

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
name|exist
operator|.
name|source
operator|.
name|SourceFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|Lock
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|xqdoc
operator|.
name|XQDocHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|InspectModule
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_INSPECT_MODULE
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"inspect-module"
argument_list|,
name|InspectionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|InspectionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Compiles a module from source (without importing it) and returns an XML fragment describing the "
operator|+
literal|"module and the functions/variables contained in it."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"location"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The location URI of the module to inspect"
argument_list|)
block|,             }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"An XML fragment describing the module and all functions contained in it."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_INSPECT_MODULE_URI
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"inspect-module-uri"
argument_list|,
name|InspectionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|InspectionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns an XML fragment describing the "
operator|+
literal|"module identified by the given URI and the functions/variables contained in it."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"uri"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The location URI of the module to inspect"
argument_list|)
block|,             }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"An XML fragment describing the module and all functions contained in it."
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"module"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|VARIABLE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"variable"
argument_list|)
decl_stmt|;
specifier|public
name|InspectModule
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
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
name|Module
name|module
decl_stmt|;
specifier|final
name|XQueryContext
name|tempContext
init|=
operator|new
name|XQueryContext
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|AccessContext
operator|.
name|XMLDB
argument_list|)
decl_stmt|;
name|tempContext
operator|.
name|setModuleLoadPath
argument_list|(
name|context
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"inspect-module"
argument_list|)
condition|)
block|{
name|module
operator|=
name|tempContext
operator|.
name|importModule
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|module
operator|=
name|tempContext
operator|.
name|importModule
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"CDATA"
argument_list|,
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"prefix"
argument_list|,
literal|"prefix"
argument_list|,
literal|"CDATA"
argument_list|,
name|module
operator|.
name|getDefaultPrefix
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"location"
argument_list|,
literal|"location"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"java:"
operator|+
name|module
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"inspect-module"
argument_list|)
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"location"
argument_list|,
literal|"location"
argument_list|,
literal|"CDATA"
argument_list|,
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|MODULE_QNAME
argument_list|,
name|attribs
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|XQDocHelper
operator|.
name|parse
argument_list|(
operator|(
name|ExternalModule
operator|)
name|module
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|module
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|InspectFunction
operator|.
name|DESCRIPTION_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|module
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
specifier|final
name|ExternalModule
name|externalModule
init|=
operator|(
name|ExternalModule
operator|)
name|module
decl_stmt|;
if|if
condition|(
name|externalModule
operator|.
name|getMetadata
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|externalModule
operator|.
name|getMetadata
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
comment|// variables
for|for
control|(
specifier|final
name|VariableDeclaration
name|var
range|:
name|externalModule
operator|.
name|getVariableDeclarations
argument_list|()
control|)
block|{
name|attribs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|var
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SequenceType
name|type
init|=
name|var
operator|.
name|getSequenceType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"cardinality"
argument_list|,
literal|"cardinality"
argument_list|,
literal|"CDATA"
argument_list|,
name|Cardinality
operator|.
name|getDescription
argument_list|(
name|type
operator|.
name|getCardinality
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|VARIABLE_QNAME
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
comment|// functions
for|for
control|(
specifier|final
name|FunctionSignature
name|sig
range|:
name|module
operator|.
name|listFunctions
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|sig
operator|.
name|isPrivate
argument_list|()
condition|)
block|{
name|UserDefinedFunction
name|func
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|func
operator|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getFunction
argument_list|(
name|sig
operator|.
name|getName
argument_list|()
argument_list|,
name|sig
operator|.
name|getArgumentCount
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|InspectFunction
operator|.
name|generateDocs
argument_list|(
name|sig
argument_list|,
name|func
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
block|}
end_class

end_unit

