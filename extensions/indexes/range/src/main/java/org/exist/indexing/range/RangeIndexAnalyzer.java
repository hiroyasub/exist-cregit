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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
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
name|TokenFilter
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
name|TokenStream
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
name|Tokenizer
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
name|KeywordTokenizer
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
name|ICUCollationAttributeFactory
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
name|AttributeFactory
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
name|invoke
operator|.
name|LambdaMetafactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
operator|.
name|methodType
import|;
end_import

begin_comment
comment|/**  * Lucene analyzer used by the range index. Based on {@link KeywordTokenizer}, it allows additional  * filters to be added to the pipeline through the collection.xconf configuration. A collation may be  * specified as well.  */
end_comment

begin_class
specifier|public
class|class
name|RangeIndexAnalyzer
extends|extends
name|Analyzer
block|{
specifier|private
specifier|static
class|class
name|FilterConfig
block|{
specifier|private
name|Function
argument_list|<
name|TokenStream
argument_list|,
name|TokenStream
argument_list|>
name|constructor
decl_stmt|;
name|FilterConfig
parameter_list|(
specifier|final
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
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"No class specified for filter"
argument_list|)
throw|;
block|}
try|try
block|{
specifier|final
name|Class
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
name|TokenFilter
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Filter "
operator|+
name|className
operator|+
literal|" is not a subclass of "
operator|+
name|TokenFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|MethodHandles
operator|.
name|Lookup
name|lookup
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
decl_stmt|;
specifier|final
name|MethodHandle
name|methodHandle
init|=
name|lookup
operator|.
name|findConstructor
argument_list|(
name|clazz
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|TokenStream
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|constructor
operator|=
operator|(
name|Function
argument_list|<
name|TokenStream
argument_list|,
name|TokenStream
argument_list|>
operator|)
name|LambdaMetafactory
operator|.
name|metafactory
argument_list|(
name|lookup
argument_list|,
literal|"apply"
argument_list|,
name|methodType
argument_list|(
name|Function
operator|.
name|class
argument_list|)
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
operator|.
name|erase
argument_list|()
argument_list|,
name|methodHandle
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|invokeExact
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
comment|// NOTE: must set interrupted flag
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Filter not found: "
operator|+
name|className
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|List
argument_list|<
name|FilterConfig
argument_list|>
name|filterConfigs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Collator
name|collator
init|=
literal|null
decl_stmt|;
specifier|public
name|RangeIndexAnalyzer
parameter_list|()
block|{
block|}
specifier|public
name|void
name|addFilter
parameter_list|(
name|Element
name|filter
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|filterConfigs
operator|.
name|add
argument_list|(
operator|new
name|FilterConfig
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addCollation
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
try|try
block|{
name|collator
operator|=
name|Collations
operator|.
name|getCollationFromURI
argument_list|(
name|uri
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
name|DatabaseConfigurationException
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
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
name|AttributeFactory
name|factory
init|=
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
decl_stmt|;
if|if
condition|(
name|collator
operator|!=
literal|null
condition|)
block|{
name|factory
operator|=
operator|new
name|ICUCollationAttributeFactory
argument_list|(
name|collator
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Tokenizer
name|src
init|=
operator|new
name|KeywordTokenizer
argument_list|(
name|factory
argument_list|,
name|reader
argument_list|,
literal|256
argument_list|)
decl_stmt|;
name|TokenStream
name|tok
init|=
name|src
decl_stmt|;
for|for
control|(
specifier|final
name|FilterConfig
name|filter
range|:
name|filterConfigs
control|)
block|{
name|tok
operator|=
name|filter
operator|.
name|constructor
operator|.
name|apply
argument_list|(
name|tok
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
name|tok
argument_list|)
return|;
block|}
block|}
end_class

end_unit

