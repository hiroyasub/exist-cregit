begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|XMLSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|NodeProxy
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
name|SortedNodeSet
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
name|User
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
name|BrokerPool
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
name|Serializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|SequenceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ErrorCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_class
specifier|public
class|class
name|LocalResourceSet
implements|implements
name|ResourceSet
block|{
specifier|protected
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
name|LocalCollection
name|collection
decl_stmt|;
specifier|protected
name|Vector
name|resources
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
specifier|protected
name|Properties
name|properties
decl_stmt|;
specifier|private
name|User
name|user
decl_stmt|;
specifier|public
name|LocalResourceSet
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|col
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|col
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
specifier|public
name|LocalResourceSet
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|col
parameter_list|,
name|Sequence
name|val
parameter_list|,
name|Properties
name|properties
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|col
expr_stmt|;
if|if
condition|(
name|val
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|val
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
name|sortExpr
operator|!=
literal|null
condition|)
block|{
name|SortedNodeSet
name|sorted
init|=
operator|new
name|SortedNodeSet
argument_list|(
name|brokerPool
argument_list|,
name|user
argument_list|,
name|sortExpr
argument_list|)
decl_stmt|;
try|try
block|{
name|sorted
operator|.
name|addAll
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|val
operator|=
name|sorted
expr_stmt|;
block|}
name|Item
name|item
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|val
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|item
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
name|resources
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
else|else
name|resources
operator|.
name|add
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|resources
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|resources
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ResourceIterator
name|getIterator
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|NewResourceIterator
argument_list|()
return|;
block|}
specifier|public
name|ResourceIterator
name|getIterator
parameter_list|(
name|long
name|start
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|NewResourceIterator
argument_list|(
name|start
argument_list|)
return|;
block|}
specifier|public
name|Resource
name|getMembersAsResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
operator|&&
name|properties
operator|.
name|containsKey
argument_list|(
literal|"encoding"
argument_list|)
condition|)
name|encoding
operator|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
literal|"encoding"
argument_list|)
expr_stmt|;
name|OutputFormat
name|format
init|=
operator|new
name|OutputFormat
argument_list|(
literal|"xml"
argument_list|,
name|encoding
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|XMLSerializer
name|handler
init|=
operator|new
name|XMLSerializer
argument_list|(
name|writer
argument_list|,
name|format
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// configure the serializer
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
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|GENERATE_DOC_EVENTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|//	serialize results
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|handler
operator|.
name|startPrefixMapping
argument_list|(
literal|"exist"
argument_list|,
name|Serializer
operator|.
name|EXIST_NS
argument_list|)
expr_stmt|;
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
literal|"hitCount"
argument_list|,
literal|"hitCount"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|resources
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|Serializer
operator|.
name|EXIST_NS
argument_list|,
literal|"result"
argument_list|,
literal|"exist:result"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|Object
name|current
decl_stmt|;
name|char
index|[]
name|value
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|resources
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|current
operator|=
operator|(
name|Object
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|instanceof
name|NodeProxy
condition|)
name|serializer
operator|.
name|toSAX
argument_list|(
operator|(
name|NodeProxy
operator|)
name|current
argument_list|)
expr_stmt|;
else|else
block|{
name|value
operator|=
name|current
operator|.
name|toString
argument_list|()
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
name|handler
operator|.
name|endElement
argument_list|(
name|Serializer
operator|.
name|EXIST_NS
argument_list|,
literal|"result"
argument_list|,
literal|"exist:result"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endPrefixMapping
argument_list|(
literal|"exist"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
literal|"serialization error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
literal|"serialization error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|Resource
name|res
init|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
specifier|public
name|Resource
name|getResource
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|pos
operator|>=
name|resources
operator|.
name|size
argument_list|()
condition|)
return|return
literal|null
return|;
name|Object
name|r
init|=
name|resources
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
decl_stmt|;
name|LocalXMLResource
name|res
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|r
operator|instanceof
name|NodeProxy
condition|)
block|{
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|r
decl_stmt|;
comment|// the resource might belong to a different collection
comment|// than the one by which this resource set has been
comment|// generated: adjust if necessary.
name|LocalCollection
name|coll
init|=
name|collection
decl_stmt|;
if|if
condition|(
name|coll
operator|==
literal|null
operator|||
name|p
operator|.
name|doc
operator|.
name|getCollection
argument_list|()
operator|==
literal|null
operator|||
name|coll
operator|.
name|collection
operator|.
name|getId
argument_list|()
operator|!=
name|p
operator|.
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getId
argument_list|()
condition|)
block|{
name|coll
operator|=
operator|new
name|LocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
literal|null
argument_list|,
name|p
operator|.
name|doc
operator|.
name|getCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|res
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|coll
argument_list|,
name|p
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|Node
condition|)
block|{
name|res
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
literal|""
argument_list|,
operator|-
literal|1
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContentAsDOM
argument_list|(
operator|(
name|Node
operator|)
name|r
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|String
condition|)
block|{
name|res
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
literal|""
argument_list|,
operator|-
literal|1
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|Resource
condition|)
return|return
operator|(
name|Resource
operator|)
name|r
return|;
return|return
name|res
return|;
block|}
comment|/** 	 *  Gets the size attribute of the LocalResourceSet object 	 * 	 *@return                     The size value 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|long
name|getSize
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|resources
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  pos                 Description of the Parameter 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|removeResource
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|resources
operator|.
name|removeElementAt
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Class 	 * 	 *@author     wolf 	 *@created    3. Juni 2002 	 */
class|class
name|NewResourceIterator
implements|implements
name|ResourceIterator
block|{
name|long
name|pos
init|=
literal|0
decl_stmt|;
comment|/**  Constructor for the NewResourceIterator object */
specifier|public
name|NewResourceIterator
parameter_list|()
block|{
block|}
comment|/** 		 *  Constructor for the NewResourceIterator object 		 * 		 *@param  start  Description of the Parameter 		 */
specifier|public
name|NewResourceIterator
parameter_list|(
name|long
name|start
parameter_list|)
block|{
name|pos
operator|=
name|start
expr_stmt|;
block|}
comment|/** 		 *  Description of the Method 		 * 		 *@return                     Description of the Return Value 		 *@exception  XMLDBException  Description of the Exception 		 */
specifier|public
name|boolean
name|hasMoreResources
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|pos
operator|<
name|getSize
argument_list|()
return|;
block|}
comment|/** 		 *  Description of the Method 		 * 		 *@return                     Description of the Return Value 		 *@exception  XMLDBException  Description of the Exception 		 */
specifier|public
name|Resource
name|nextResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getResource
argument_list|(
name|pos
operator|++
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

