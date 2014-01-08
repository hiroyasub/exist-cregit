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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|HashSet
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
name|logging
operator|.
name|Level
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
name|util
operator|.
name|CharArraySet
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
name|collections
operator|.
name|CollectionConfiguration
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
name|NamedNodeMap
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
name|AnalyzerConfig
block|{
comment|/*       Supported configurations<analyzer class="org.apache.lucene.analysis.standard.StandardAnalyzer"/><analyzer id="ws" class="org.apache.lucene.analysis.WhitespaceAnalyzer"/><analyzer id="stdstops" class="org.apache.lucene.analysis.standard.StandardAnalyzer">      ..<param name="stopwords" type="java.io.File" value="/tmp/stop.txt"/></analyzer><analyzer id="stdstops" class="org.apache.lucene.analysis.standard.StandardAnalyzer">      ..<param name="stopwords" type="java.util.Set">      ....<value>the</value>      ....<value>this</value>      ....<value>and</value>      ....<value>that</value>      ..</param></analyzer><analyzer id="sbstops" class="org.apache.lucene.analysis.snowball.SnowballAnalyzer">      ..<param name="name" value="English"/>      ..<param name="stopwords" type="java.util.Set">      ....<value>the</value>      ....<value>this</value>      ....<value>and</value>      ....<value>that</value>      ..</param></analyzer>       */
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
name|AnalyzerConfig
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID_ATTRIBUTE
init|=
literal|"id"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAME_ATTRIBUTE
init|=
literal|"name"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_ATTRIBUTE
init|=
literal|"type"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CLASS_ATTRIBUTE
init|=
literal|"class"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_VALUE_ENTRY
init|=
literal|"value"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_ELEMENT_NAME
init|=
literal|"param"
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|analyzers
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Analyzer
name|defaultAnalyzer
init|=
literal|null
decl_stmt|;
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
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|Analyzer
name|getDefaultAnalyzer
parameter_list|()
block|{
return|return
name|defaultAnalyzer
return|;
block|}
comment|/**      * Parse<analyzer/> element and register configured analyzer.      *      * @param config The analyzer element from .xconf file.      *      * @throws DatabaseConfigurationException Something unexpected happened.      */
specifier|public
name|void
name|addAnalyzer
parameter_list|(
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
comment|// Configure lucene analuzer with configuration
specifier|final
name|Analyzer
name|analyzer
init|=
name|configureAnalyzer
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// Get (optional) id-attribute of analyzer
specifier|final
name|String
name|id
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|ID_ATTRIBUTE
argument_list|)
decl_stmt|;
comment|// If no ID is provided, register as default analyzer
comment|// else register analyzer
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|setDefaultAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|analyzers
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Set default the analyzer.      *      * @param analyzer Lucene analyzer      */
specifier|public
name|void
name|setDefaultAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|defaultAnalyzer
operator|=
name|analyzer
expr_stmt|;
block|}
comment|/**      * Parse<analyzer/> element from xconf and initialize an analyzer with the      * parameters.      *      * @param config The analyzer element      * @return Initialized Analyzer object      *      * @throws DatabaseConfigurationException Something unexpected happened.      */
specifier|protected
specifier|static
name|Analyzer
name|configureAnalyzer
parameter_list|(
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
comment|// Get classname from attribute
specifier|final
name|String
name|className
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|CLASS_ATTRIBUTE
argument_list|)
decl_stmt|;
name|Analyzer
name|newAnalyzer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|className
argument_list|)
condition|)
block|{
comment|// No classname is defined.
name|LOG
operator|.
name|error
argument_list|(
literal|"Missing class attribute or attribute is empty."
argument_list|)
expr_stmt|;
comment|// DW: throw exception?
block|}
else|else
block|{
comment|// Classname is defined.
try|try
block|{
comment|// Probe class
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Lucene index: analyzer class %s not found. (%s)"
argument_list|,
name|className
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|newAnalyzer
return|;
block|}
comment|// CHeck if class is an Analyzer
if|if
condition|(
operator|!
name|Analyzer
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Lucene index: analyzer class has to be a subclass of %s"
argument_list|,
name|Analyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|newAnalyzer
return|;
block|}
comment|// Get list of parameters
specifier|final
name|List
argument_list|<
name|KeyTypedValue
argument_list|>
name|cParams
init|=
name|getAllConstructorParameters
argument_list|(
name|config
argument_list|)
decl_stmt|;
comment|// Iterate over all parameters, convert data to two arrays
comment|// that can be used in the reflection code
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|cParamClasses
index|[]
init|=
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[
name|cParams
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|Object
name|cParamValues
index|[]
init|=
operator|new
name|Object
index|[
name|cParams
operator|.
name|size
argument_list|()
index|]
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
name|cParams
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|KeyTypedValue
name|ktv
init|=
name|cParams
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|cParamClasses
index|[
name|i
index|]
operator|=
name|ktv
operator|.
name|getValueClass
argument_list|()
expr_stmt|;
name|cParamValues
index|[
name|i
index|]
operator|=
name|ktv
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cParamClasses
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// If no parameters are provided
name|LOG
operator|.
name|error
argument_list|(
literal|"No parameters provided to instantiate new analyzer."
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|cParamClasses
operator|.
name|length
operator|>
literal|0
operator|&&
name|cParamClasses
index|[
literal|0
index|]
operator|==
name|Version
operator|.
name|class
condition|)
block|{
comment|// A lucene Version object has been provided
name|newAnalyzer
operator|=
name|createInstance
argument_list|(
name|className
argument_list|,
name|clazz
argument_list|,
name|cParamClasses
argument_list|,
name|cParamValues
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Extend arrays with Version object info, add to front
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|vcParamClasses
init|=
name|addVersionToClasses
argument_list|(
name|cParamClasses
argument_list|)
decl_stmt|;
name|Object
index|[]
name|vcParamValues
init|=
name|addVersionValueToValues
argument_list|(
name|cParamValues
argument_list|)
decl_stmt|;
comment|// Finally invoke again
name|Analyzer
name|instance
init|=
name|createInstance
argument_list|(
name|className
argument_list|,
name|clazz
argument_list|,
name|vcParamClasses
argument_list|,
name|vcParamValues
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
comment|// Fallback, maybe a very special Analyzer has been specified
name|instance
operator|=
name|createInstance
argument_list|(
name|className
argument_list|,
name|clazz
argument_list|,
name|cParamClasses
argument_list|,
name|cParamValues
argument_list|)
expr_stmt|;
block|}
name|newAnalyzer
operator|=
name|instance
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParameterException
name|pe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while instantiating analyzer class %s: %s"
argument_list|,
name|className
argument_list|,
name|pe
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|pe
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newAnalyzer
return|;
block|}
specifier|private
specifier|static
name|Analyzer
name|createInstance
parameter_list|(
name|String
name|className
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|vcParamClasses
parameter_list|,
name|Object
index|[]
name|vcParamValues
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Constructor
argument_list|<
name|?
argument_list|>
name|cstr
init|=
name|clazz
operator|.
name|getDeclaredConstructor
argument_list|(
name|vcParamClasses
argument_list|)
decl_stmt|;
name|cstr
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Using analyzer %s"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|Analyzer
operator|)
name|cstr
operator|.
name|newInstance
argument_list|(
name|vcParamValues
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while instantiating analyzer class %s: %s"
argument_list|,
name|className
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while instantiating analyzer class %s: %s"
argument_list|,
name|className
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while instantiating analyzer class %s: %s"
argument_list|,
name|className
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while instantiating analyzer class %s: %s"
argument_list|,
name|className
argument_list|,
name|ite
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|ite
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not find matching analyzer class constructor%s: %s"
argument_list|,
name|className
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while instantiating analyzer class %s: %s"
argument_list|,
name|className
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Extend list of values, add version-value to front      */
specifier|private
specifier|static
name|Object
index|[]
name|addVersionValueToValues
parameter_list|(
specifier|final
name|Object
index|[]
name|cParamValues
parameter_list|)
block|{
specifier|final
name|Object
name|vcParamValues
index|[]
init|=
operator|new
name|Object
index|[
name|cParamValues
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|vcParamValues
index|[
literal|0
index|]
operator|=
name|LuceneIndex
operator|.
name|LUCENE_VERSION_IN_USE
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|cParamValues
argument_list|,
literal|0
argument_list|,
name|vcParamValues
argument_list|,
literal|1
argument_list|,
name|cParamValues
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|vcParamValues
return|;
block|}
comment|/**      * Extend list of classes, add version-class to front      */
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|addVersionToClasses
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|cParamClasses
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|vcParamClasses
index|[]
init|=
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[
name|cParamClasses
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|vcParamClasses
index|[
literal|0
index|]
operator|=
name|Version
operator|.
name|class
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|cParamClasses
argument_list|,
literal|0
argument_list|,
name|vcParamClasses
argument_list|,
literal|1
argument_list|,
name|cParamClasses
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|vcParamClasses
return|;
block|}
comment|/**      * Retrieve parameter info from all<param/> elements.      *      * @param config The<analyzer/> element from the provided configuration      * @return List of triples key-value-valueType      * @throws org.exist.indexing.lucene.AnalyzerConfig.ParameterException      */
specifier|private
specifier|static
name|List
argument_list|<
name|KeyTypedValue
argument_list|>
name|getAllConstructorParameters
parameter_list|(
name|Element
name|config
parameter_list|)
throws|throws
name|ParameterException
block|{
specifier|final
name|List
argument_list|<
name|KeyTypedValue
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<
name|KeyTypedValue
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|NodeList
name|params
init|=
name|config
operator|.
name|getElementsByTagNameNS
argument_list|(
name|CollectionConfiguration
operator|.
name|NAMESPACE
argument_list|,
name|PARAM_ELEMENT_NAME
argument_list|)
decl_stmt|;
comment|// iterate over all<param/> elements
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|parameters
operator|.
name|add
argument_list|(
name|getConstructorParameter
argument_list|(
operator|(
name|Element
operator|)
name|params
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|parameters
return|;
block|}
comment|/**      * Retrieve configuration information from one<param/> element. Type      * information is used to construct actual data containing objects.      *      * @param param Element that represents<param/>      * @return Triple key-value-value-type      * @throws org.exist.indexing.lucene.AnalyzerConfig.ParameterException      */
specifier|private
specifier|static
name|KeyTypedValue
name|getConstructorParameter
parameter_list|(
name|Element
name|param
parameter_list|)
throws|throws
name|ParameterException
block|{
comment|// Get attributes
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|param
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
comment|// Get name of parameter, NULL when no value is present
specifier|final
name|String
name|name
decl_stmt|;
if|if
condition|(
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|NAME_ATTRIBUTE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|NAME_ATTRIBUTE
argument_list|)
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// DW: TODO need to check if the NULL value is safe to use.
name|name
operator|=
literal|null
expr_stmt|;
block|}
comment|// Get value type information of parameter, NULL when not available
specifier|final
name|String
name|type
decl_stmt|;
if|if
condition|(
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|TYPE_ATTRIBUTE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|TYPE_ATTRIBUTE
argument_list|)
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Safe to use, NULL check done.
name|type
operator|=
literal|null
expr_stmt|;
block|}
comment|// Get actual value from attribute, or NULL when not available.
specifier|final
name|String
name|value
decl_stmt|;
if|if
condition|(
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|PARAM_VALUE_ENTRY
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|PARAM_VALUE_ENTRY
argument_list|)
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// DW: TODO need to check if the NULL value is safe to use.
comment|// This is dangerous, unless a Set is filled
name|value
operator|=
literal|null
expr_stmt|;
block|}
comment|// Place holder return value
name|KeyTypedValue
name|parameter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// No type is provided, assume string.
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"java.lang.reflect.Field"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParameterException
argument_list|(
literal|"The 'value' attribute must exist and must contain a full classname."
argument_list|)
throw|;
block|}
comment|// Use reflection
comment|// - retrieve classname from the value field
comment|// - retrieve fieldname from the value field
specifier|final
name|String
name|clazzName
init|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fieldName
init|=
name|value
operator|.
name|substring
argument_list|(
name|value
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Retrieve value from Field
specifier|final
name|Class
name|fieldClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|clazzName
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|field
init|=
name|fieldClazz
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|fValue
init|=
name|field
operator|.
name|get
argument_list|(
name|fieldClazz
operator|.
name|newInstance
argument_list|()
argument_list|)
decl_stmt|;
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|fValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
name|nsfe
parameter_list|)
block|{
throw|throw
operator|new
name|ParameterException
argument_list|(
name|nsfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|nsfe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|nsfe
parameter_list|)
block|{
throw|throw
operator|new
name|ParameterException
argument_list|(
name|nsfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|nsfe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|nsfe
parameter_list|)
block|{
throw|throw
operator|new
name|ParameterException
argument_list|(
name|nsfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|nsfe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|nsfe
parameter_list|)
block|{
throw|throw
operator|new
name|ParameterException
argument_list|(
name|nsfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|nsfe
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
literal|"java.io.File"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// This is actually decrecated now, "Reader" must be used
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|f
argument_list|,
name|File
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"java.io.FileReader"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// DW: Experimental
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|value
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Reader
name|r
init|=
operator|new
name|FileReader
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|r
argument_list|,
name|Reader
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"File %s could not be found."
argument_list|,
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
literal|"java.util.Set"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// This is actually deprecated now, Lucene4 requires CharArraySet
specifier|final
name|Set
name|s
init|=
name|getConstructorParameterSetValues
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|s
argument_list|,
name|Set
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"org.apache.lucene.analysis.util.CharArraySet"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// This is mandatory since Lucene4
specifier|final
name|CharArraySet
name|s
init|=
name|getConstructorParameterCharArraySetValues
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|s
argument_list|,
name|CharArraySet
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"java.lang.Integer"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"int"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|Integer
name|n
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Value %s could not be converted to an integer."
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
literal|"java.lang.Boolean"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"boolean"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// Straight forward
specifier|final
name|boolean
name|b
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// FallBack type == null or did not match
try|try
block|{
comment|//if the type is an Enum then use valueOf()
specifier|final
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isEnum
argument_list|()
condition|)
block|{
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|Enum
operator|.
name|valueOf
argument_list|(
name|clazz
argument_list|,
name|value
argument_list|)
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//default, assume java.lang.String
name|parameter
operator|=
operator|new
name|KeyTypedValue
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|ParameterException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Class for type: %s not found. %s"
argument_list|,
name|type
argument_list|,
name|cnfe
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|cnfe
argument_list|)
throw|;
block|}
block|}
return|return
name|parameter
return|;
block|}
comment|/**      * Get parameter configuration data as standard Java (Hash)Set.      *      * @param param The parameter-configuration element.      * @return Set of parameter values      */
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getConstructorParameterSetValues
parameter_list|(
name|Element
name|param
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|NodeList
name|values
init|=
name|param
operator|.
name|getElementsByTagNameNS
argument_list|(
name|CollectionConfiguration
operator|.
name|NAMESPACE
argument_list|,
name|PARAM_VALUE_ENTRY
argument_list|)
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
name|values
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Element
name|value
init|=
operator|(
name|Element
operator|)
name|values
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//TODO getNodeValue() on org.exist.dom.ElementImpl should return null according to W3C spec!
if|if
condition|(
name|value
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|ElementImpl
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|value
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|add
argument_list|(
name|value
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
comment|/**      * Get parameter configuration data as a Lucene CharArraySet.      *      * @param param The parameter-configuration element.      * @return Parameter data as Lucene CharArraySet      */
specifier|private
specifier|static
name|CharArraySet
name|getConstructorParameterCharArraySetValues
parameter_list|(
name|Element
name|param
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|getConstructorParameterSetValues
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|CharArraySet
operator|.
name|copy
argument_list|(
name|LuceneIndex
operator|.
name|LUCENE_VERSION_IN_USE
argument_list|,
name|set
argument_list|)
return|;
block|}
comment|/**      * CLass for containing the Triple : key (name), corresponding value and      * class type of value.      */
specifier|private
specifier|static
class|class
name|KeyTypedValue
block|{
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
specifier|private
specifier|final
name|Object
name|value
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|valueClass
decl_stmt|;
specifier|public
name|KeyTypedValue
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|KeyTypedValue
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|valueClass
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|valueClass
operator|=
name|valueClass
expr_stmt|;
block|}
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getValueClass
parameter_list|()
block|{
return|return
name|valueClass
return|;
block|}
block|}
comment|/**      * Exception class to for reporting problems with the parameters.      */
specifier|private
specifier|static
class|class
name|ParameterException
extends|extends
name|Exception
block|{
specifier|public
name|ParameterException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ParameterException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

