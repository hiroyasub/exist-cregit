begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
package|;
end_package

begin_import
import|import
name|bmsi
operator|.
name|util
operator|.
name|Diff
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|ElementImpl
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|EmbeddedXMLStreamReader
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
name|util
operator|.
name|serializer
operator|.
name|Receiver
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
name|SAXSerializer
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
name|SerializerPool
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
name|IOException
import|;
end_import

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
name|ArrayList
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
name|List
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

begin_class
specifier|public
class|class
name|XMLDiff
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XMLDiff
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE
init|=
literal|"http://exist-db.org/versioning"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"v"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|DIFF_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"diff"
argument_list|,
name|NAMESPACE
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|List
name|changes
init|=
literal|null
decl_stmt|;
specifier|public
name|XMLDiff
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|void
name|diff
parameter_list|(
name|DocumentImpl
name|docA
parameter_list|,
name|DocumentImpl
name|docB
parameter_list|)
throws|throws
name|DiffException
block|{
try|try
block|{
name|DiffNode
index|[]
name|nodesA
init|=
name|getNodes
argument_list|(
name|broker
argument_list|,
name|docA
argument_list|)
decl_stmt|;
name|DiffNode
index|[]
name|nodesB
init|=
name|getNodes
argument_list|(
name|broker
argument_list|,
name|docB
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Source:"
argument_list|)
expr_stmt|;
name|debugNodes
argument_list|(
name|nodesA
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Modified:"
argument_list|)
expr_stmt|;
name|debugNodes
argument_list|(
name|nodesB
argument_list|)
expr_stmt|;
block|}
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|(
name|nodesA
argument_list|,
name|nodesB
argument_list|)
decl_stmt|;
name|Diff
operator|.
name|change
name|script
init|=
name|diff
operator|.
name|diff_2
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|changes
operator|=
name|getChanges
argument_list|(
name|script
argument_list|,
name|docA
argument_list|,
name|docB
argument_list|,
name|nodesA
argument_list|,
name|nodesB
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiffException
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiffException
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
block|}
specifier|private
name|void
name|debugNodes
parameter_list|(
name|DiffNode
index|[]
name|nodes
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DiffNode
name|node
init|=
name|nodes
index|[
name|i
index|]
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|diff2XML
parameter_list|()
throws|throws
name|DiffException
block|{
try|try
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|SAXSerializer
name|sax
init|=
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
decl_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
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
name|sax
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|diff2XML
argument_list|(
name|sax
argument_list|)
expr_stmt|;
name|sax
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiffException
argument_list|(
literal|"error while serializing diff: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|diff2XML
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|DiffException
block|{
try|try
block|{
name|receiver
operator|.
name|startElement
argument_list|(
name|DIFF_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|changes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Difference
name|diff
init|=
operator|(
name|Difference
operator|)
name|changes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|diff
operator|.
name|serialize
argument_list|(
name|broker
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|endElement
argument_list|(
name|DIFF_ELEMENT
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiffException
argument_list|(
literal|"error while serializing diff: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|List
name|getChanges
parameter_list|(
name|Diff
operator|.
name|change
name|script
parameter_list|,
name|DocumentImpl
name|docA
parameter_list|,
name|DocumentImpl
name|docB
parameter_list|,
name|DiffNode
index|[]
name|nodesA
parameter_list|,
name|DiffNode
index|[]
name|nodesB
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|List
name|changes
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Diff
operator|.
name|change
name|next
init|=
name|script
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|int
name|start0
init|=
name|next
operator|.
name|line0
decl_stmt|;
name|int
name|start
init|=
name|next
operator|.
name|line1
decl_stmt|;
name|int
name|last
init|=
name|start
operator|+
name|next
operator|.
name|inserted
decl_stmt|;
name|int
name|lastDeleted
init|=
name|start0
operator|+
name|next
operator|.
name|deleted
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|inserted
operator|>
literal|0
condition|)
block|{
name|Difference
operator|.
name|Insert
name|diff
decl_stmt|;
if|if
condition|(
name|nodesA
index|[
name|start0
index|]
operator|.
name|nodeType
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
condition|)
name|diff
operator|=
operator|new
name|Difference
operator|.
name|Append
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|docA
argument_list|,
name|nodesA
index|[
name|start0
index|]
operator|.
name|nodeId
argument_list|)
argument_list|,
name|docB
argument_list|)
expr_stmt|;
else|else
name|diff
operator|=
operator|new
name|Difference
operator|.
name|Insert
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|docA
argument_list|,
name|nodesA
index|[
name|start0
index|]
operator|.
name|nodeId
argument_list|)
argument_list|,
name|docB
argument_list|)
expr_stmt|;
comment|// now scan the chunk and collect the nodes into a node set
name|DiffNode
index|[]
name|nodes
init|=
operator|new
name|DiffNode
index|[
name|last
operator|-
name|start
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|last
condition|;
name|i
operator|++
operator|,
name|j
operator|++
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
operator|+
literal|" "
operator|+
name|nodesB
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|nodes
index|[
name|j
index|]
operator|=
name|nodesB
index|[
name|i
index|]
expr_stmt|;
block|}
name|diff
operator|.
name|setNodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
name|changes
operator|.
name|add
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|next
operator|.
name|deleted
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"Deleted: "
operator|+
name|start0
operator|+
literal|" last: "
operator|+
name|lastDeleted
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start0
init|;
name|i
operator|<
name|lastDeleted
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|elementDeleted
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|nodesA
index|[
name|i
index|]
operator|.
name|nodeType
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|lastDeleted
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|nodesA
index|[
name|j
index|]
operator|.
name|nodeType
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
operator|&&
name|nodesA
index|[
name|j
index|]
operator|.
name|nodeId
operator|.
name|equals
argument_list|(
name|nodesA
index|[
name|i
index|]
operator|.
name|nodeId
argument_list|)
condition|)
block|{
name|Difference
operator|.
name|Delete
name|diff
init|=
operator|new
name|Difference
operator|.
name|Delete
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|docA
argument_list|,
name|nodesA
index|[
name|i
index|]
operator|.
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|changes
operator|.
name|add
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|i
operator|=
name|j
expr_stmt|;
name|elementDeleted
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|elementDeleted
condition|)
block|{
name|Difference
operator|.
name|Delete
name|diff
init|=
operator|new
name|Difference
operator|.
name|Delete
argument_list|(
name|nodesA
index|[
name|i
index|]
operator|.
name|nodeType
argument_list|,
operator|new
name|NodeProxy
argument_list|(
name|docA
argument_list|,
name|nodesA
index|[
name|i
index|]
operator|.
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|changes
operator|.
name|add
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|next
operator|=
name|next
operator|.
name|link
expr_stmt|;
block|}
return|return
name|changes
return|;
block|}
specifier|protected
name|DiffNode
index|[]
name|getNodes
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|root
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|IOException
block|{
name|EmbeddedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|newXMLStreamReader
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|root
argument_list|,
name|NodeId
operator|.
name|DOCUMENT_NODE
argument_list|,
name|root
operator|.
name|getFirstChildAddress
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|List
name|nodes
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|DiffNode
name|node
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
name|NodeId
name|nodeId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|EmbeddedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|XMLStreamReader
operator|.
name|START_ELEMENT
case|:
name|node
operator|=
operator|new
name|DiffNode
argument_list|(
name|nodeId
argument_list|,
name|status
argument_list|,
name|reader
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|reader
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|nodeId
operator|=
name|reader
operator|.
name|getAttributeId
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|reader
operator|.
name|getAttributeQName
argument_list|(
name|i
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|+
literal|'='
operator|+
name|reader
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|node
operator|=
operator|new
name|DiffNode
argument_list|(
name|nodeId
argument_list|,
name|XMLStreamReader
operator|.
name|ATTRIBUTE
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamReader
operator|.
name|END_ELEMENT
case|:
name|node
operator|=
operator|new
name|DiffNode
argument_list|(
name|nodeId
argument_list|,
name|status
argument_list|,
name|reader
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|CHARACTERS
case|:
case|case
name|XMLStreamReader
operator|.
name|COMMENT
case|:
name|node
operator|=
operator|new
name|DiffNode
argument_list|(
name|nodeId
argument_list|,
name|status
argument_list|,
name|reader
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|PROCESSING_INSTRUCTION
case|:
name|String
name|value
init|=
name|reader
operator|.
name|getPITarget
argument_list|()
operator|+
literal|" "
operator|+
name|reader
operator|.
name|getPIData
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
operator|new
name|DiffNode
argument_list|(
name|nodeId
argument_list|,
name|status
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|DiffNode
index|[]
name|array
init|=
operator|new
name|DiffNode
index|[
name|nodes
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
operator|(
name|DiffNode
index|[]
operator|)
name|nodes
operator|.
name|toArray
argument_list|(
name|array
argument_list|)
return|;
block|}
block|}
end_class

end_unit

