begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
package|;
end_package

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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
operator|.
name|KeywordAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
operator|.
name|CollationKeyAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|LongField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|TextField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
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
name|NodeListImpl
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
name|storage
operator|.
name|NodePath
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
name|Collations
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
name|DatabaseConfigurationException
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
name|Element
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|TreeMap
import|;
end_import

begin_class
specifier|public
class|class
name|RangeIndexConfig
block|{
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_ROOT
init|=
literal|"range"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_ELEM
init|=
literal|"create"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_ELEM
init|=
literal|"field"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|RangeIndexConfig
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|RangeIndexConfigElement
argument_list|>
name|paths
init|=
operator|new
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|RangeIndexConfigElement
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
specifier|private
name|PathIterator
name|iterator
init|=
operator|new
name|PathIterator
argument_list|()
decl_stmt|;
specifier|public
name|RangeIndexConfig
parameter_list|(
name|NodeList
name|configNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
block|{
name|parse
argument_list|(
name|configNodes
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RangeIndexConfig
parameter_list|(
name|RangeIndexConfig
name|other
parameter_list|)
block|{
name|this
operator|.
name|paths
operator|=
name|other
operator|.
name|paths
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|other
operator|.
name|analyzer
expr_stmt|;
block|}
specifier|public
name|RangeIndexConfigElement
name|find
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
for|for
control|(
name|RangeIndexConfigElement
name|rice
range|:
name|paths
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|rice
operator|.
name|find
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|rice
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|NodeList
name|configNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
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
name|configNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|configNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|CONFIG_ROOT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|parseChildren
argument_list|(
name|node
operator|.
name|getChildNodes
argument_list|()
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|parseChildren
parameter_list|(
name|NodeList
name|configNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
block|{
name|Node
name|node
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|configNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
name|configNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|CREATE_ELEM
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|NodeList
name|fields
init|=
name|getFields
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|)
decl_stmt|;
name|RangeIndexConfigElement
name|newConfig
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|newConfig
operator|=
operator|new
name|ComplexRangeIndexConfigElement
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|,
name|fields
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newConfig
operator|=
operator|new
name|RangeIndexConfigElement
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
block|}
name|RangeIndexConfigElement
name|idxConf
init|=
name|paths
operator|.
name|get
argument_list|(
name|newConfig
operator|.
name|getNodePath
argument_list|()
operator|.
name|getLastComponent
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxConf
operator|==
literal|null
condition|)
block|{
name|paths
operator|.
name|put
argument_list|(
name|newConfig
operator|.
name|getNodePath
argument_list|()
operator|.
name|getLastComponent
argument_list|()
argument_list|,
name|newConfig
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|idxConf
operator|.
name|add
argument_list|(
name|newConfig
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid range index configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|analyzer
operator|=
operator|new
name|KeywordAnalyzer
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|RangeIndexConfigElement
argument_list|>
name|getConfig
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
name|iterator
operator|.
name|reset
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|iterator
return|;
block|}
specifier|private
name|NodeList
name|getFields
parameter_list|(
name|Element
name|root
parameter_list|)
block|{
name|NodeListImpl
name|fields
init|=
operator|new
name|NodeListImpl
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
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
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|FIELD_ELEM
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
name|RangeIndexConfigElement
name|idxConf
init|=
name|paths
operator|.
name|get
argument_list|(
name|path
operator|.
name|getLastComponent
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|idxConf
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|idxConf
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|true
return|;
name|idxConf
operator|=
name|idxConf
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
class|class
name|PathIterator
implements|implements
name|Iterator
argument_list|<
name|RangeIndexConfigElement
argument_list|>
block|{
specifier|private
name|RangeIndexConfigElement
name|nextConfig
decl_stmt|;
specifier|private
name|NodePath
name|path
decl_stmt|;
specifier|private
name|boolean
name|atLast
init|=
literal|false
decl_stmt|;
specifier|protected
name|void
name|reset
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
name|this
operator|.
name|atLast
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|nextConfig
operator|=
name|paths
operator|.
name|get
argument_list|(
name|path
operator|.
name|getLastComponent
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextConfig
operator|==
literal|null
condition|)
block|{
name|atLast
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|nextConfig
operator|!=
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RangeIndexConfigElement
name|next
parameter_list|()
block|{
if|if
condition|(
name|nextConfig
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|RangeIndexConfigElement
name|currentConfig
init|=
name|nextConfig
decl_stmt|;
name|nextConfig
operator|=
name|nextConfig
operator|.
name|getNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextConfig
operator|==
literal|null
operator|&&
operator|!
name|atLast
condition|)
block|{
name|atLast
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|currentConfig
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|//Nothing to do
block|}
block|}
block|}
end_class

end_unit

