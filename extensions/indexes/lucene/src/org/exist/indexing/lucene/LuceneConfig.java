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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParserBase
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
name|indexing
operator|.
name|lucene
operator|.
name|analyzers
operator|.
name|NoDiacriticsStandardAnalyzer
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

begin_class
specifier|public
class|class
name|LuceneConfig
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
name|LuceneConfig
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CONFIG_ROOT
init|=
literal|"lucene"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INDEX_ELEMENT
init|=
literal|"text"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ANALYZER_ELEMENT
init|=
literal|"analyzer"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PARSER_ELEMENT
init|=
literal|"parser"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|FIELD_TYPE_ELEMENT
init|=
literal|"fieldType"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INLINE_ELEMENT
init|=
literal|"inline"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|IGNORE_ELEMENT
init|=
literal|"ignore"
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
specifier|static
specifier|final
name|String
name|DIACRITICS
init|=
literal|"diacritics"
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|LuceneIndexConfig
argument_list|>
name|paths
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|LuceneIndexConfig
argument_list|>
name|wildcardPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|LuceneIndexConfig
argument_list|>
name|namedIndexes
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
name|fieldTypes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|QName
argument_list|>
name|inlineNodes
init|=
literal|null
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|QName
argument_list|>
name|ignoreNodes
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|PathIterator
name|iterator
init|=
operator|new
name|PathIterator
argument_list|()
decl_stmt|;
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|AnalyzerConfig
name|analyzers
init|=
operator|new
name|AnalyzerConfig
argument_list|()
decl_stmt|;
specifier|private
name|String
name|queryParser
init|=
literal|null
decl_stmt|;
specifier|public
name|LuceneConfig
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
throws|throws
name|DatabaseConfigurationException
block|{
name|parseConfig
argument_list|(
name|configNodes
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
block|}
comment|/**      * Copy constructor. LuceneConfig is only configured once by database instance,      * so to avoid concurrency issues when using e.g. iterator, we create a copy.      *       * @param other      */
specifier|public
name|LuceneConfig
parameter_list|(
name|LuceneConfig
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
name|wildcardPaths
operator|=
name|other
operator|.
name|wildcardPaths
expr_stmt|;
name|this
operator|.
name|namedIndexes
operator|=
name|other
operator|.
name|namedIndexes
expr_stmt|;
name|this
operator|.
name|fieldTypes
operator|=
name|other
operator|.
name|fieldTypes
expr_stmt|;
name|this
operator|.
name|inlineNodes
operator|=
name|other
operator|.
name|inlineNodes
expr_stmt|;
name|this
operator|.
name|ignoreNodes
operator|=
name|other
operator|.
name|ignoreNodes
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|other
operator|.
name|boost
expr_stmt|;
name|this
operator|.
name|analyzers
operator|=
name|other
operator|.
name|analyzers
expr_stmt|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
name|LuceneIndexConfig
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
for|for
control|(
name|LuceneIndexConfig
name|config
range|:
name|wildcardPaths
control|)
block|{
if|if
condition|(
name|config
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|LuceneIndexConfig
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
specifier|protected
name|LuceneIndexConfig
name|getWildcardConfig
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
name|LuceneIndexConfig
name|config
decl_stmt|;
for|for
control|(
name|LuceneIndexConfig
name|wildcardPath
range|:
name|wildcardPaths
control|)
block|{
name|config
operator|=
name|wildcardPath
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
name|config
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|LuceneIndexConfig
name|idxConf
init|=
name|paths
operator|.
name|get
argument_list|(
name|qname
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
operator|!
name|idxConf
operator|.
name|isNamed
argument_list|()
operator|&&
name|idxConf
operator|.
name|getNodePath
argument_list|()
operator|.
name|match
argument_list|(
name|qname
argument_list|)
condition|)
break|break;
name|idxConf
operator|=
name|idxConf
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|idxConf
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|idxConf
operator|.
name|getAnalyzerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
return|return
name|analyzers
operator|.
name|getAnalyzerById
argument_list|(
name|idxConf
operator|.
name|getAnalyzerId
argument_list|()
argument_list|)
return|;
block|}
return|return
name|analyzers
operator|.
name|getDefaultAnalyzer
argument_list|()
return|;
block|}
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|(
name|NodePath
name|nodePath
parameter_list|)
block|{
if|if
condition|(
name|nodePath
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
name|LuceneIndexConfig
name|idxConf
init|=
name|paths
operator|.
name|get
argument_list|(
name|nodePath
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
operator|!
name|idxConf
operator|.
name|isNamed
argument_list|()
operator|&&
name|idxConf
operator|.
name|match
argument_list|(
name|nodePath
argument_list|)
condition|)
break|break;
name|idxConf
operator|=
name|idxConf
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|idxConf
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|LuceneIndexConfig
name|config
range|:
name|wildcardPaths
control|)
block|{
if|if
condition|(
name|config
operator|.
name|match
argument_list|(
name|nodePath
argument_list|)
condition|)
return|return
name|config
operator|.
name|getAnalyzer
argument_list|()
return|;
block|}
block|}
if|if
condition|(
name|idxConf
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|idxConf
operator|.
name|getAnalyzerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
return|return
name|analyzers
operator|.
name|getAnalyzerById
argument_list|(
name|idxConf
operator|.
name|getAnalyzerId
argument_list|()
argument_list|)
return|;
block|}
return|return
name|analyzers
operator|.
name|getDefaultAnalyzer
argument_list|()
return|;
block|}
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|LuceneIndexConfig
name|config
init|=
name|namedIndexes
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|config
operator|.
name|getAnalyzerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
return|return
name|analyzers
operator|.
name|getAnalyzerById
argument_list|(
name|config
operator|.
name|getAnalyzerId
argument_list|()
argument_list|)
return|;
block|}
return|return
name|analyzers
operator|.
name|getDefaultAnalyzer
argument_list|()
return|;
block|}
specifier|public
name|Analyzer
name|getAnalyzerById
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|analyzers
operator|.
name|getAnalyzerById
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Try to instantiate the configured Lucene query parser. Lucene's parsers      * do not all have a common base class, so we need to wrap around the implementation      * details.      *      * @param field the default field to query      * @param analyzer analyzer to use for query parsing      * @return a query wrapper      */
specifier|public
name|QueryParserWrapper
name|getQueryParser
parameter_list|(
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|QueryParserWrapper
name|parser
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|queryParser
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|queryParser
argument_list|)
decl_stmt|;
if|if
condition|(
name|QueryParserBase
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|parser
operator|=
operator|new
name|ClassicQueryParserWrapper
argument_list|(
name|queryParser
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
name|QueryParserWrapper
operator|.
name|create
argument_list|(
name|queryParser
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to instantiate lucene query parser class: "
operator|+
name|queryParser
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
comment|// use default parser
name|parser
operator|=
operator|new
name|ClassicQueryParserWrapper
argument_list|(
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
return|return
name|parser
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
name|inlineNodes
operator|!=
literal|null
operator|&&
name|inlineNodes
operator|.
name|contains
argument_list|(
name|qname
argument_list|)
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
name|ignoreNodes
operator|!=
literal|null
operator|&&
name|ignoreNodes
operator|.
name|contains
argument_list|(
name|qname
argument_list|)
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
specifier|public
name|FieldType
name|getFieldType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fieldTypes
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Parse a configuration entry. The main configuration entries for this index      * are the&lt;text&gt; elements. They may be enclosed by a&lt;lucene&gt; element.      *      * @param configNodes      * @param namespaces      * @throws org.exist.util.DatabaseConfigurationException      */
specifier|protected
name|void
name|parseConfig
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
throws|throws
name|DatabaseConfigurationException
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
condition|)
block|{
try|try
block|{
specifier|final
name|String
name|localName
init|=
name|node
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
name|CONFIG_ROOT
case|:
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|elem
operator|.
name|hasAttribute
argument_list|(
name|BOOST_ATTRIB
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|BOOST_ATTRIB
argument_list|)
decl_stmt|;
try|try
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
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
literal|"Invalid value for 'boost' attribute in "
operator|+
literal|"lucene index config: float expected, got "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|elem
operator|.
name|hasAttribute
argument_list|(
name|DIACRITICS
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|DIACRITICS
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
name|analyzers
operator|.
name|setDefaultAnalyzer
argument_list|(
operator|new
name|NoDiacriticsStandardAnalyzer
argument_list|(
name|LuceneIndex
operator|.
name|LUCENE_VERSION_IN_USE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|parseConfig
argument_list|(
name|node
operator|.
name|getChildNodes
argument_list|()
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|ANALYZER_ELEMENT
case|:
name|analyzers
operator|.
name|addAnalyzer
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
break|break;
case|case
name|PARSER_ELEMENT
case|:
name|queryParser
operator|=
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIELD_TYPE_ELEMENT
case|:
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|,
name|analyzers
argument_list|)
decl_stmt|;
name|fieldTypes
operator|.
name|put
argument_list|(
name|type
operator|.
name|getId
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
break|break;
case|case
name|INDEX_ELEMENT
case|:
block|{
comment|// found an index definition
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|LuceneIndexConfig
name|config
init|=
operator|new
name|LuceneIndexConfig
argument_list|(
name|elem
argument_list|,
name|namespaces
argument_list|,
name|analyzers
argument_list|,
name|fieldTypes
argument_list|)
decl_stmt|;
comment|// if it is a named index, add it to the namedIndexes map
if|if
condition|(
name|config
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|namedIndexes
operator|.
name|put
argument_list|(
name|config
operator|.
name|getName
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|// register index either by QName or path
if|if
condition|(
name|config
operator|.
name|getNodePath
argument_list|()
operator|.
name|hasWildcard
argument_list|()
condition|)
block|{
name|wildcardPaths
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LuceneIndexConfig
name|idxConf
init|=
name|paths
operator|.
name|get
argument_list|(
name|config
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
name|config
operator|.
name|getNodePath
argument_list|()
operator|.
name|getLastComponent
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|idxConf
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
case|case
name|INLINE_ELEMENT
case|:
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|QName
name|qname
init|=
name|LuceneIndexConfig
operator|.
name|parseQName
argument_list|(
name|elem
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
if|if
condition|(
name|inlineNodes
operator|==
literal|null
condition|)
block|{
name|inlineNodes
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|inlineNodes
operator|.
name|add
argument_list|(
name|qname
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|IGNORE_ELEMENT
case|:
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|QName
name|qname
init|=
name|LuceneIndexConfig
operator|.
name|parseQName
argument_list|(
name|elem
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
if|if
condition|(
name|ignoreNodes
operator|==
literal|null
condition|)
block|{
name|ignoreNodes
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|ignoreNodes
operator|.
name|add
argument_list|(
name|qname
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
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
name|warn
argument_list|(
literal|"Invalid lucene configuration element: "
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
block|}
specifier|private
class|class
name|PathIterator
implements|implements
name|Iterator
argument_list|<
name|LuceneIndexConfig
argument_list|>
block|{
specifier|private
name|LuceneIndexConfig
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
name|nextConfig
operator|=
name|getWildcardConfig
argument_list|(
name|path
argument_list|)
expr_stmt|;
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
name|LuceneIndexConfig
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
name|LuceneIndexConfig
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
name|nextConfig
operator|=
name|getWildcardConfig
argument_list|(
name|path
argument_list|)
expr_stmt|;
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

