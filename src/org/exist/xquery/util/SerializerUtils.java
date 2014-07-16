begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|Namespaces
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
name|functions
operator|.
name|fn
operator|.
name|FnModule
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
name|NodeValue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Serializer utilities used by several XQuery functions.  */
end_comment

begin_class
specifier|public
class|class
name|SerializerUtils
block|{
comment|/**      * Parse output:serialization-parameters XML fragment into serialization      * properties as defined by the fn:serialize function.      *      * @param parent the parent expression calling this method      * @param parameters root node of the XML fragment      * @param properties parameters are added to the given properties      */
specifier|public
specifier|static
name|void
name|getSerializationOptions
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|NodeValue
name|parameters
parameter_list|,
name|Properties
name|properties
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|XMLStreamReader
name|reader
init|=
name|parent
operator|.
name|getContext
argument_list|()
operator|.
name|getXMLStreamReader
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
operator|&&
operator|(
name|reader
operator|.
name|next
argument_list|()
operator|!=
name|XMLStreamReader
operator|.
name|START_ELEMENT
operator|)
condition|)
block|{
block|}
if|if
condition|(
operator|!
name|reader
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XSLT_XQUERY_SERIALIZATION_NS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|FnModule
operator|.
name|SENR0001
argument_list|,
literal|"serialization parameter elements should be in the output namespace"
argument_list|)
throw|;
block|}
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
block|{
specifier|final
name|String
name|key
init|=
name|reader
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|properties
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|FnModule
operator|.
name|SEPM0019
argument_list|,
literal|"serialization parameter specified twice: "
operator|+
name|key
argument_list|)
throw|;
block|}
name|String
name|value
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// backwards compatibility: use element text as value
name|value
operator|=
name|reader
operator|.
name|getElementText
argument_list|()
expr_stmt|;
block|}
name|properties
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
