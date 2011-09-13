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
specifier|final
specifier|static
name|String
name|ID_ATTRIBUTE
init|=
literal|"id"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CLASS_ATTRIBUTE
init|=
literal|"class"
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
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|id
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|defaultAnalyzer
operator|=
name|analyzer
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
if|if
condition|(
name|className
operator|!=
literal|null
operator|&&
name|className
operator|.
name|length
argument_list|()
operator|!=
literal|0
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
name|className
argument_list|)
decl_stmt|;
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
name|warn
argument_list|(
literal|"Lucene index: analyzer class has to be a subclass of "
operator|+
name|Analyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|KeyValue
argument_list|>
name|cParams
init|=
name|getConstructorParameters
argument_list|(
name|config
argument_list|)
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|cParamClasses
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
index|[]
name|cParamValues
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
name|cParamClasses
index|[
name|i
index|]
operator|=
name|cParams
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
expr_stmt|;
name|cParamValues
index|[
name|i
index|]
operator|=
name|cParams
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
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
name|cParamClasses
argument_list|)
decl_stmt|;
name|cstr
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Analyzer
operator|)
name|cstr
operator|.
name|newInstance
argument_list|(
name|cParamValues
argument_list|)
return|;
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
literal|"Lucene index: analyzer class "
operator|+
name|className
operator|+
literal|" not found."
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
literal|"Exception while instantiating analyzer class "
operator|+
name|className
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
literal|"Exception while instantiating analyzer class "
operator|+
name|className
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while instantiating analyzer class "
operator|+
name|className
operator|+
literal|": "
operator|+
name|nsme
operator|.
name|getMessage
argument_list|()
argument_list|,
name|nsme
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
literal|"Exception while instantiating analyzer class "
operator|+
name|className
operator|+
literal|": "
operator|+
name|ite
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ite
argument_list|)
expr_stmt|;
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
literal|"Exception while instantiating analyzer class "
operator|+
name|className
operator|+
literal|": "
operator|+
name|pe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pe
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|KeyValue
argument_list|>
name|getConstructorParameters
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
name|KeyValue
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<
name|KeyValue
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
literal|"param"
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
specifier|private
specifier|static
name|KeyValue
name|getConstructorParameter
parameter_list|(
name|Element
name|param
parameter_list|)
throws|throws
name|ParameterException
block|{
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|param
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
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
literal|"type"
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
literal|"type"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
literal|null
expr_stmt|;
block|}
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
literal|"value"
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
literal|"value"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|KeyValue
name|parameter
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|type
operator|.
name|equals
argument_list|(
literal|"java.lang.reflect.Field"
argument_list|)
condition|)
block|{
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
name|indexOf
argument_list|(
literal|"."
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
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
name|KeyValue
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
name|type
operator|!=
literal|null
operator|&&
name|type
operator|.
name|equals
argument_list|(
literal|"java.io.File"
argument_list|)
condition|)
block|{
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
name|KeyValue
argument_list|(
name|name
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|type
operator|.
name|equals
argument_list|(
literal|"java.util.Set"
argument_list|)
condition|)
block|{
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
name|KeyValue
argument_list|(
name|name
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|!=
literal|null
operator|&&
operator|(
name|type
operator|.
name|equals
argument_list|(
literal|"java.lang.Integer"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"int"
argument_list|)
operator|)
condition|)
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
name|KeyValue
argument_list|(
name|name
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|!=
literal|null
operator|&&
operator|(
name|type
operator|.
name|equals
argument_list|(
literal|"java.lang.Boolean"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
operator|)
condition|)
block|{
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
name|KeyValue
argument_list|(
name|name
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//assume java.lang.String
name|parameter
operator|=
operator|new
name|KeyValue
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|parameter
return|;
block|}
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
literal|"value"
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
specifier|private
specifier|static
class|class
name|KeyValue
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
specifier|public
name|KeyValue
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
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
block|}
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

