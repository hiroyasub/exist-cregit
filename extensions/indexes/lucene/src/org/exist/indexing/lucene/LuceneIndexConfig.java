begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|ElementValue
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
name|DatabaseConfigurationException
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

begin_class
specifier|public
class|class
name|LuceneIndexConfig
block|{
specifier|private
specifier|final
specifier|static
name|String
name|N_INLINE
init|=
literal|"inline"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|N_IGNORE
init|=
literal|"ignore"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QNAME_ATTR
init|=
literal|"qname"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MATCH_ATTR
init|=
literal|"match"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|IGNORE_ELEMENT
init|=
literal|"ignore"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INLINE_ELEMENT
init|=
literal|"inline"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_ATTR
init|=
literal|"field"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_ATTR
init|=
literal|"type"
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|NodePath
name|path
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isQNameIndex
init|=
literal|false
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|String
argument_list|>
name|specialNodes
init|=
literal|null
decl_stmt|;
specifier|private
name|LuceneIndexConfig
name|nextConfig
init|=
literal|null
decl_stmt|;
specifier|private
name|FieldType
name|type
init|=
literal|null
decl_stmt|;
specifier|public
name|LuceneIndexConfig
parameter_list|(
name|Element
name|config
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
name|AnalyzerConfig
name|analyzers
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
name|fieldTypes
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
condition|)
block|{
name|QName
name|qname
init|=
name|parseQName
argument_list|(
name|config
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|isQNameIndex
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|String
name|matchPath
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|MATCH_ATTR
argument_list|)
decl_stmt|;
try|try
block|{
name|path
operator|=
operator|new
name|NodePath
argument_list|(
name|namespaces
argument_list|,
name|matchPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene module: Invalid match path in collection config: "
operator|+
name|matchPath
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene module: invalid qname in configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|String
name|name
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|FIELD_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|String
name|fieldType
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|TYPE_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
operator|&&
name|fieldType
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|type
operator|=
name|fieldTypes
operator|.
name|get
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
name|type
operator|=
operator|new
name|FieldType
argument_list|(
name|config
argument_list|,
name|analyzers
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|config
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|Element
name|root
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|Node
name|child
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
specifier|final
name|String
name|localName
init|=
name|child
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|localName
condition|)
block|{
switch|switch
condition|(
name|localName
condition|)
block|{
case|case
name|IGNORE_ELEMENT
case|:
block|{
name|String
name|qnameAttr
init|=
operator|(
operator|(
name|Element
operator|)
name|child
operator|)
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|qnameAttr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene configuration element 'ignore' needs an attribute 'qname'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|specialNodes
operator|==
literal|null
condition|)
block|{
name|specialNodes
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|specialNodes
operator|.
name|put
argument_list|(
name|parseQName
argument_list|(
name|qnameAttr
argument_list|,
name|namespaces
argument_list|)
argument_list|,
name|N_IGNORE
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|INLINE_ELEMENT
case|:
block|{
name|String
name|qnameAttr
init|=
operator|(
operator|(
name|Element
operator|)
name|child
operator|)
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|qnameAttr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene configuration element 'inline' needs an attribute 'qname'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|specialNodes
operator|==
literal|null
condition|)
block|{
name|specialNodes
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|specialNodes
operator|.
name|put
argument_list|(
name|parseQName
argument_list|(
name|qnameAttr
argument_list|,
name|namespaces
argument_list|)
argument_list|,
name|N_INLINE
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
comment|// return saved Analyzer for use in LuceneMatchListener
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|type
operator|.
name|getAnalyzer
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAnalyzerId
parameter_list|()
block|{
return|return
name|type
operator|.
name|getAnalyzerId
argument_list|()
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|path
operator|.
name|getLastComponent
argument_list|()
return|;
block|}
specifier|public
name|NodePath
name|getNodePath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|type
operator|.
name|getBoost
argument_list|()
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|LuceneIndexConfig
name|config
parameter_list|)
block|{
if|if
condition|(
name|nextConfig
operator|==
literal|null
condition|)
name|nextConfig
operator|=
name|config
expr_stmt|;
else|else
name|nextConfig
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneIndexConfig
name|getNext
parameter_list|()
block|{
return|return
name|nextConfig
return|;
block|}
comment|/** 	 * @return true if this index can be queried by name 	 */
specifier|public
name|boolean
name|isNamed
parameter_list|()
block|{
return|return
name|name
operator|!=
literal|null
return|;
block|}
specifier|public
name|boolean
name|isIgnoredNode
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
name|specialNodes
operator|!=
literal|null
operator|&&
name|specialNodes
operator|.
name|get
argument_list|(
name|qname
argument_list|)
operator|==
name|N_IGNORE
return|;
block|}
specifier|public
name|boolean
name|isInlineNode
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
name|specialNodes
operator|!=
literal|null
operator|&&
name|specialNodes
operator|.
name|get
argument_list|(
name|qname
argument_list|)
operator|==
name|N_INLINE
return|;
block|}
specifier|public
specifier|static
name|QName
name|parseQName
parameter_list|(
name|Element
name|config
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|String
name|name
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|name
argument_list|)
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene index configuration error: element "
operator|+
name|config
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" must have an attribute "
operator|+
name|QNAME_ATTR
argument_list|)
throw|;
return|return
name|parseQName
argument_list|(
name|name
argument_list|,
name|namespaces
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|QName
name|parseQName
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|boolean
name|isAttribute
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|isAttribute
operator|=
literal|true
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|String
name|prefix
init|=
name|QName
operator|.
name|extractPrefix
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|namespaceURI
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|namespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"No namespace defined for prefix: "
operator|+
name|prefix
operator|+
literal|" in index definition"
argument_list|)
throw|;
block|}
block|}
name|QName
name|qname
init|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAttribute
condition|)
name|qname
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
return|return
name|qname
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene index configuration error: "
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
name|boolean
name|match
parameter_list|(
name|NodePath
name|other
parameter_list|)
block|{
if|if
condition|(
name|isQNameIndex
condition|)
return|return
name|other
operator|.
name|getLastComponent
argument_list|()
operator|.
name|equalsSimple
argument_list|(
name|path
operator|.
name|getLastComponent
argument_list|()
argument_list|)
return|;
return|return
name|path
operator|.
name|match
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

