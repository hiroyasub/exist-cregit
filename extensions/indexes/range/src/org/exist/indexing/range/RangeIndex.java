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
name|exist
operator|.
name|indexing
operator|.
name|IndexWorker
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
name|LuceneIndex
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

begin_class
specifier|public
class|class
name|RangeIndex
extends|extends
name|LuceneIndex
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
name|RangeIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
name|RangeIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|public
enum|enum
name|Operator
block|{
name|GT
block|,
name|LT
block|,
name|EQ
block|,
name|GE
block|,
name|LE
block|,
name|ENDS_WITH
block|,
name|STARTS_WITH
block|,
name|CONTAINS
block|}
empty_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DIR_NAME
init|=
literal|"range"
decl_stmt|;
specifier|private
name|Analyzer
name|defaultAnalyzer
init|=
operator|new
name|KeywordAnalyzer
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getDirName
parameter_list|()
block|{
return|return
name|DIR_NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
operator|new
name|RangeIndexWorker
argument_list|(
name|this
argument_list|,
name|broker
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|ID
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
block|}
end_class

end_unit

