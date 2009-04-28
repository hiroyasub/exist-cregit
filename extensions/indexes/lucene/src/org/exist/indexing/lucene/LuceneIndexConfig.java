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
name|LuceneIndexConfig
block|{
specifier|private
specifier|final
specifier|static
name|String
name|ANALYZER_ID_ATTR
init|=
literal|"analyzer"
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
name|BOOST_ATTRIB
init|=
literal|"boost"
decl_stmt|;
specifier|private
name|String
name|analyzerId
init|=
literal|null
decl_stmt|;
specifier|private
name|QName
name|qname
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
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|LuceneIndexConfig
parameter_list|(
name|Element
name|config
parameter_list|,
name|Map
name|namespaces
parameter_list|,
name|AnalyzerConfig
name|analyzers
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
name|qname
operator|=
name|parseQName
argument_list|(
name|config
argument_list|,
name|namespaces
argument_list|)
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
name|qname
operator|=
name|path
operator|.
name|getComponent
argument_list|(
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|id
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|ANALYZER_ID_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|analyzers
operator|.
name|getAnalyzerById
argument_list|(
name|id
argument_list|)
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"No analyzer configured for id "
operator|+
name|id
argument_list|)
throw|;
name|analyzerId
operator|=
name|id
expr_stmt|;
block|}
name|String
name|boostAttr
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|BOOST_ATTRIB
argument_list|)
decl_stmt|;
if|if
condition|(
name|boostAttr
operator|!=
literal|null
operator|&&
name|boostAttr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|boostAttr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Invalid value for attribute 'boost'. Expected float, "
operator|+
literal|"got: "
operator|+
name|boostAttr
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|String
name|getAnalyzerId
parameter_list|()
block|{
return|return
name|analyzerId
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|qname
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
name|boost
return|;
block|}
specifier|protected
specifier|static
name|QName
name|parseQName
parameter_list|(
name|Element
name|config
parameter_list|,
name|Map
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
name|name
operator|==
literal|null
operator|||
name|name
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
operator|(
name|String
operator|)
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
return|return
name|path
operator|.
name|match
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
end_class

end_unit

