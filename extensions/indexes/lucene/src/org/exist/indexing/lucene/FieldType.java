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

begin_comment
comment|/**  * Configures a field type: analyzers etc. used for indexing  * a field.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|FieldType
block|{
specifier|private
specifier|final
specifier|static
name|String
name|ID_ATTR
init|=
literal|"id"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ANALYZER_ID_ATTR
init|=
literal|"analyzer"
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
specifier|final
specifier|static
name|String
name|STORE_ATTRIB
init|=
literal|"store"
decl_stmt|;
specifier|private
name|String
name|id
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|analyzerId
init|=
literal|null
decl_stmt|;
comment|// save Analyzer for later use in LuceneMatchListener
specifier|private
name|Analyzer
name|analyzer
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
specifier|private
name|Field
operator|.
name|Store
name|store
init|=
literal|null
decl_stmt|;
specifier|public
name|FieldType
parameter_list|(
name|Element
name|config
parameter_list|,
name|AnalyzerConfig
name|analyzers
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
if|if
condition|(
name|LuceneConfig
operator|.
name|FIELD_TYPE_ELEMENT
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|id
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
name|ID_ATTR
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|id
argument_list|)
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"fieldType needs an attribute 'id'"
argument_list|)
throw|;
block|}
name|String
name|aId
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|ANALYZER_ID_ATTR
argument_list|)
decl_stmt|;
comment|// save Analyzer for later use in LuceneMatchListener
if|if
condition|(
name|aId
operator|!=
literal|null
operator|&&
name|aId
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|analyzer
operator|=
name|analyzers
operator|.
name|getAnalyzerById
argument_list|(
name|aId
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"No analyzer configured for id "
operator|+
name|aId
argument_list|)
throw|;
name|analyzerId
operator|=
name|aId
expr_stmt|;
block|}
else|else
block|{
name|analyzer
operator|=
name|analyzers
operator|.
name|getDefaultAnalyzer
argument_list|()
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
name|String
name|storeAttr
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|STORE_ATTRIB
argument_list|)
decl_stmt|;
if|if
condition|(
name|storeAttr
operator|!=
literal|null
operator|&&
name|storeAttr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|store
operator|=
name|storeAttr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
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
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
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
name|Field
operator|.
name|Store
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

