begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|serializers
operator|.
name|Serializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|json
operator|.
name|JSONSerializer
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
name|SAXException
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
name|SAXNotRecognizedException
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
name|SAXNotSupportedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
comment|/**  * Utility class for writing out XQuery results. It is an abstraction around  * eXist's internal serializers specialized on writing XQuery sequences.  *  * @author Wolf  */
end_comment

begin_class
specifier|public
class|class
name|XQuerySerializer
block|{
specifier|private
specifier|final
name|Properties
name|outputProperties
decl_stmt|;
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|Writer
name|writer
decl_stmt|;
specifier|public
name|XQuerySerializer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Properties
name|outputProperties
parameter_list|,
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|outputProperties
operator|=
name|outputProperties
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
comment|// ALWAYS enforce XDM serialization rules
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|XDM_SERIALIZATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
name|serialize
argument_list|(
name|sequence
argument_list|,
literal|1
argument_list|,
name|sequence
operator|.
name|getItemCount
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|howmany
parameter_list|,
specifier|final
name|boolean
name|wrap
parameter_list|,
specifier|final
name|boolean
name|typed
parameter_list|,
specifier|final
name|long
name|compilationTime
parameter_list|,
specifier|final
name|long
name|executionTime
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
specifier|final
name|String
name|method
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|method
condition|)
block|{
case|case
literal|"adaptive"
case|:
name|serializeAdaptive
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"json"
case|:
name|serializeJSON
argument_list|(
name|sequence
argument_list|,
name|compilationTime
argument_list|,
name|executionTime
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"xml"
case|:
default|default:
name|serializeXML
argument_list|(
name|sequence
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|,
name|wrap
argument_list|,
name|typed
argument_list|,
name|compilationTime
argument_list|,
name|executionTime
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|public
name|boolean
name|normalize
parameter_list|()
block|{
specifier|final
name|String
name|method
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
return|return
operator|!
operator|(
literal|"json"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
operator|||
literal|"adaptive"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
operator|)
return|;
block|}
specifier|private
name|void
name|serializeXML
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|howmany
parameter_list|,
specifier|final
name|boolean
name|wrap
parameter_list|,
specifier|final
name|boolean
name|typed
parameter_list|,
specifier|final
name|long
name|compilationTime
parameter_list|,
specifier|final
name|long
name|executionTime
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
specifier|final
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|SAXSerializer
name|sax
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sax
operator|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
expr_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|sequence
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|,
name|wrap
argument_list|,
name|typed
argument_list|,
name|compilationTime
argument_list|,
name|executionTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
decl||
name|SAXNotRecognizedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|sax
operator|!=
literal|null
condition|)
block|{
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|serializeJSON
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|,
specifier|final
name|long
name|compilationTime
parameter_list|,
specifier|final
name|long
name|executionTime
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
comment|// backwards compatibility: if the sequence contains a single element, we assume
comment|// it should be transformed to JSON following the rules of the old JSON writer
if|if
condition|(
name|sequence
operator|.
name|hasOne
argument_list|()
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|sequence
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|)
condition|)
block|{
name|serializeXML
argument_list|(
name|sequence
argument_list|,
literal|1
argument_list|,
name|sequence
operator|.
name|getItemCount
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|compilationTime
argument_list|,
name|executionTime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JSONSerializer
name|serializer
init|=
operator|new
name|JSONSerializer
argument_list|(
name|broker
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|sequence
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|serializeAdaptive
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
specifier|final
name|AdaptiveSerializer
name|serializer
init|=
operator|new
name|AdaptiveSerializer
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
