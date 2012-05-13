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
name|util
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
name|memtree
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

begin_comment
comment|/**  * Created with IntelliJ IDEA.  * User: wolf  * Date: 5/12/12  * Time: 9:48 PM  * To change this template use File | Settings | File Templates.  */
end_comment

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
name|signature
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
specifier|private
specifier|final
specifier|static
name|QName
name|ARGUMENT_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"argument"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|DEPRECATED_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"deprecated"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|DESCRIPTION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|RETURN_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"returns"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|FUNCTION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"function"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|ANNOTATION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"annotation"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|ANNOTATION_VALUE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
specifier|public
name|InspectFunction
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
name|FunctionSignature
name|sig
init|=
name|ref
operator|.
name|getSignature
argument_list|()
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
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
name|SequenceType
name|returnType
init|=
name|signature
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
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
specifier|private
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
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
for|for
control|(
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
name|FunctionParameterSequenceType
name|ftype
init|=
operator|(
name|FunctionParameterSequenceType
operator|)
name|type
decl_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|ftype
operator|.
name|getAttributeName
argument_list|()
operator|+
literal|" "
operator|+
name|ftype
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
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
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
block|}
end_class

end_unit

