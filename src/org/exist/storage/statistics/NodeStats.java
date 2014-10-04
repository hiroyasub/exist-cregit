begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|statistics
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
name|dom
operator|.
name|persistent
operator|.
name|SymbolTable
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
name|NodePath
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
name|ContentHandler
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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

begin_comment
comment|/**  * Collects statistics for a single node in the data guide.  */
end_comment

begin_class
class|class
name|NodeStats
block|{
specifier|private
name|QName
name|qname
decl_stmt|;
specifier|private
name|int
name|nodeCount
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|maxDepth
init|=
literal|0
decl_stmt|;
specifier|transient
specifier|private
name|int
name|depth
init|=
literal|0
decl_stmt|;
specifier|protected
name|NodeStats
name|parent
init|=
literal|null
decl_stmt|;
specifier|protected
name|NodeStats
index|[]
name|children
init|=
literal|null
decl_stmt|;
specifier|protected
name|NodeStats
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|qname
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|NodeStats
parameter_list|(
name|NodeStats
name|parent
parameter_list|,
name|QName
name|qname
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
block|}
specifier|public
name|void
name|incDepth
parameter_list|()
block|{
name|this
operator|.
name|depth
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|updateMaxDepth
parameter_list|()
block|{
if|if
condition|(
name|depth
operator|>
name|maxDepth
condition|)
block|{
name|maxDepth
operator|=
name|depth
expr_stmt|;
block|}
name|depth
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxDepth
parameter_list|()
block|{
return|return
name|maxDepth
return|;
block|}
specifier|protected
name|void
name|addOccurrence
parameter_list|()
block|{
name|nodeCount
operator|++
expr_stmt|;
block|}
specifier|protected
name|NodeStats
name|addChild
parameter_list|(
name|QName
name|qn
parameter_list|)
block|{
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|NodeStats
name|child
init|=
name|children
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|qname
operator|.
name|equalsSimple
argument_list|(
name|qn
argument_list|)
condition|)
block|{
return|return
name|child
return|;
block|}
block|}
block|}
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
name|children
operator|=
operator|new
name|NodeStats
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|NodeStats
index|[]
name|tc
init|=
operator|new
name|NodeStats
index|[
name|children
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|children
argument_list|,
literal|0
argument_list|,
name|tc
argument_list|,
literal|0
argument_list|,
name|children
operator|.
name|length
argument_list|)
expr_stmt|;
name|children
operator|=
name|tc
expr_stmt|;
block|}
name|children
index|[
name|children
operator|.
name|length
operator|-
literal|1
index|]
operator|=
operator|new
name|NodeStats
argument_list|(
name|this
argument_list|,
name|qn
argument_list|)
expr_stmt|;
return|return
name|children
index|[
name|children
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
specifier|protected
name|void
name|mergeInto
parameter_list|(
name|DataGuide
name|other
parameter_list|,
name|NodePath
name|currentPath
parameter_list|)
block|{
name|NodePath
name|newPath
decl_stmt|;
if|if
condition|(
name|qname
operator|==
literal|null
condition|)
block|{
name|newPath
operator|=
name|currentPath
expr_stmt|;
block|}
else|else
block|{
name|newPath
operator|=
operator|new
name|NodePath
argument_list|(
name|currentPath
argument_list|)
expr_stmt|;
name|newPath
operator|.
name|addComponent
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|other
operator|.
name|add
argument_list|(
name|newPath
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|NodeStats
name|child
init|=
name|children
index|[
name|i
index|]
decl_stmt|;
name|child
operator|.
name|mergeInto
argument_list|(
name|other
argument_list|,
name|newPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|mergeStats
parameter_list|(
name|NodeStats
name|other
parameter_list|)
block|{
name|nodeCount
operator|+=
name|other
operator|.
name|nodeCount
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|maxDepth
operator|>
name|maxDepth
condition|)
block|{
name|maxDepth
operator|=
name|other
operator|.
name|maxDepth
expr_stmt|;
block|}
block|}
specifier|protected
name|int
name|getSize
parameter_list|()
block|{
name|int
name|s
init|=
name|qname
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|s
operator|+=
name|children
index|[
name|i
index|]
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|s
return|;
block|}
specifier|protected
name|void
name|getMaxParentDepth
parameter_list|(
name|QName
name|name
parameter_list|,
name|NodeStats
name|max
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|qname
operator|!=
literal|null
operator|&&
name|qname
operator|.
name|equalsSimple
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|max
operator|.
name|maxDepth
operator|=
name|Math
operator|.
name|max
argument_list|(
name|parent
operator|.
name|maxDepth
argument_list|,
name|max
operator|.
name|maxDepth
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|.
name|getMaxParentDepth
argument_list|(
name|name
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|write
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
block|{
name|buffer
operator|.
name|putShort
argument_list|(
name|symbols
operator|.
name|getNSSymbol
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putShort
argument_list|(
name|symbols
operator|.
name|getSymbol
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|nodeCount
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|maxDepth
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|children
operator|==
literal|null
condition|?
literal|0
else|:
name|children
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|read
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
block|{
specifier|final
name|short
name|nsid
init|=
name|buffer
operator|.
name|getShort
argument_list|()
decl_stmt|;
specifier|final
name|short
name|localid
init|=
name|buffer
operator|.
name|getShort
argument_list|()
decl_stmt|;
specifier|final
name|String
name|namespaceURI
init|=
name|symbols
operator|.
name|getNamespace
argument_list|(
name|nsid
argument_list|)
decl_stmt|;
specifier|final
name|String
name|localName
init|=
name|symbols
operator|.
name|getName
argument_list|(
name|localid
argument_list|)
decl_stmt|;
name|qname
operator|=
name|symbols
operator|.
name|getQName
argument_list|(
name|Node
operator|.
name|ELEMENT_NODE
argument_list|,
name|namespaceURI
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|nodeCount
operator|=
name|buffer
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|maxDepth
operator|=
name|buffer
operator|.
name|getInt
argument_list|()
expr_stmt|;
specifier|final
name|int
name|childCount
init|=
name|buffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|childCount
operator|>
literal|0
condition|)
block|{
name|children
operator|=
operator|new
name|NodeStats
index|[
name|childCount
index|]
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
name|childCount
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|=
operator|new
name|NodeStats
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|children
index|[
name|i
index|]
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|dump
parameter_list|(
name|StringBuilder
name|currentPath
parameter_list|,
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|paths
parameter_list|)
block|{
name|StringBuilder
name|newPath
decl_stmt|;
if|if
condition|(
name|qname
operator|==
literal|null
condition|)
block|{
name|newPath
operator|=
name|currentPath
expr_stmt|;
block|}
else|else
block|{
name|newPath
operator|=
operator|new
name|StringBuilder
argument_list|(
name|currentPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|newPath
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|newPath
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
block|}
name|newPath
operator|.
name|append
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|newPath
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|nodeCount
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|newPath
operator|.
name|append
argument_list|(
name|maxDepth
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
name|paths
operator|.
name|add
argument_list|(
name|newPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|NodeStats
name|child
init|=
name|children
index|[
name|i
index|]
decl_stmt|;
name|child
operator|.
name|dump
argument_list|(
name|newPath
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|toSAX
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
block|{
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
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"namespace"
argument_list|,
literal|"namespace"
argument_list|,
literal|"CDATA"
argument_list|,
name|qname
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
literal|"node-count"
argument_list|,
literal|"node-count"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nodeCount
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"max-depth"
argument_list|,
literal|"max-depth"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|maxDepth
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"node"
argument_list|,
literal|"node"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|.
name|toSAX
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
name|handler
operator|.
name|endElement
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"node"
argument_list|,
literal|"node"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

