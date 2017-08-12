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
name|QName
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
name|xquery
operator|.
name|functions
operator|.
name|util
operator|.
name|UtilModule
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
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
specifier|public
class|class
name|InspectFunction
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|SIGNATURE_DEPRECATED
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"inspect-function"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns an XML fragment describing the function referenced by the passed function item."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The function item to inspect"
argument_list|)
block|,             }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the signature of the function"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|SIGNATURE
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"inspect-function"
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
literal|"Returns an XML fragment describing the function referenced by the passed function item."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The function item to inspect"
argument_list|)
block|,             }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the signature of the function"
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|ARGUMENT_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"argument"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|DEPRECATED_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"deprecated"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|DESCRIPTION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"description"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|RETURN_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"returns"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|FUNCTION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"function"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|ANNOTATION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"annotation"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|ANNOTATION_VALUE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"value"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|QName
name|VERSION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"version"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|QName
name|AUTHOR_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"author"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|QName
name|CALLS_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"calls"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|public
name|InspectFunction
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
specifier|final
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|FunctionSignature
name|sig
init|=
name|ref
operator|.
name|getSignature
argument_list|()
decl_stmt|;
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
name|int
name|nodeNr
init|=
name|generateDocs
argument_list|(
name|sig
argument_list|,
literal|null
argument_list|,
name|builder
argument_list|)
decl_stmt|;
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
comment|/**      * Generate an XML fragment containing information about the function identified by its signature.      *      * @param sig the signature of the function to describe      * @param func the function implementation. If provided, the method will also inspect the function body      *             and list all functions called from the current function.      * @param builder builder used to create the XML      * @return nodeNr of the generated element      * @throws XPathException      */
specifier|public
specifier|static
name|int
name|generateDocs
parameter_list|(
name|FunctionSignature
name|sig
parameter_list|,
name|UserDefinedFunction
name|func
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
throws|throws
name|XPathException
block|{
name|XQDocHelper
operator|.
name|parse
argument_list|(
name|sig
argument_list|)
expr_stmt|;
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
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|sig
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"module"
argument_list|,
literal|"module"
argument_list|,
literal|"CDATA"
argument_list|,
name|sig
operator|.
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|FUNCTION_QNAME
argument_list|,
name|attribs
argument_list|)
decl_stmt|;
name|writeParameters
argument_list|(
name|sig
argument_list|,
name|builder
argument_list|)
expr_stmt|;
specifier|final
name|SequenceType
name|returnType
init|=
name|sig
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
if|if
condition|(
name|returnType
operator|!=
literal|null
condition|)
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
name|returnType
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
name|returnType
operator|.
name|getCardinality
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|RETURN_QNAME
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
if|if
condition|(
name|returnType
operator|instanceof
name|FunctionReturnSequenceType
condition|)
block|{
specifier|final
name|FunctionReturnSequenceType
name|type
init|=
operator|(
name|FunctionReturnSequenceType
operator|)
name|returnType
decl_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|type
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|writeAnnotations
argument_list|(
name|sig
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|sig
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
name|DESCRIPTION_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|sig
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
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
name|sig
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|metadata
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
name|meta
range|:
name|metadata
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
name|meta
operator|.
name|getKey
argument_list|()
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|meta
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
if|if
condition|(
name|sig
operator|.
name|isDeprecated
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|DEPRECATED_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|sig
operator|.
name|getDeprecated
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
name|func
operator|!=
literal|null
condition|)
block|{
name|generateDependencies
argument_list|(
name|func
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|private
specifier|static
name|void
name|writeParameters
parameter_list|(
name|FunctionSignature
name|sig
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
specifier|final
name|SequenceType
index|[]
name|arguments
init|=
name|sig
operator|.
name|getArgumentTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|arguments
operator|!=
literal|null
condition|)
block|{
specifier|final
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceType
name|type
range|:
name|arguments
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
if|if
condition|(
name|type
operator|instanceof
name|FunctionParameterSequenceType
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"var"
argument_list|,
literal|"var"
argument_list|,
literal|"CDATA"
argument_list|,
operator|(
operator|(
name|FunctionParameterSequenceType
operator|)
name|type
operator|)
operator|.
name|getAttributeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|ARGUMENT_QNAME
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|FunctionParameterSequenceType
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|FunctionParameterSequenceType
operator|)
name|type
operator|)
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|writeAnnotations
parameter_list|(
name|FunctionSignature
name|signature
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
specifier|final
name|Annotation
index|[]
name|annots
init|=
name|signature
operator|.
name|getAnnotations
argument_list|()
decl_stmt|;
if|if
condition|(
name|annots
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Annotation
name|annot
range|:
name|annots
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
literal|null
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|annot
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"namespace"
argument_list|,
literal|"namespace"
argument_list|,
literal|"CDATA"
argument_list|,
name|annot
operator|.
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|ANNOTATION_QNAME
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
specifier|final
name|LiteralValue
index|[]
name|value
init|=
name|annot
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|LiteralValue
name|literal
range|:
name|value
control|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|ANNOTATION_VALUE_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
operator|.
name|getStringValue
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
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Inspect the provided function implementation and return an XML fragment listing all      * functions called from the function.      *      * @param function      * @param builder      */
specifier|public
specifier|static
name|void
name|generateDependencies
parameter_list|(
name|UserDefinedFunction
name|function
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|FunctionCallVisitor
name|visitor
init|=
operator|new
name|FunctionCallVisitor
argument_list|()
decl_stmt|;
name|function
operator|.
name|getFunctionBody
argument_list|()
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|FunctionSignature
argument_list|>
name|signatures
init|=
name|visitor
operator|.
name|getFunctionCalls
argument_list|()
decl_stmt|;
if|if
condition|(
name|signatures
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|CALLS_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
for|for
control|(
name|FunctionSignature
name|signature
range|:
name|signatures
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
literal|null
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|signature
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"module"
argument_list|,
literal|"module"
argument_list|,
literal|"CDATA"
argument_list|,
name|signature
operator|.
name|getName
argument_list|()
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
literal|"arity"
argument_list|,
literal|"arity"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|signature
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|FUNCTION_QNAME
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
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

