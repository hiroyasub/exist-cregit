begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|DefaultDocumentSet
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
name|DocumentSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
operator|.
name|Serializer
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
name|FileUtils
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
name|LockException
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
name|serializer
operator|.
name|SAXSerializer
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
name|CompiledXQuery
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
name|XQuery
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
name|XQueryContext
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
name|*
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|CompiledExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XQueryService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Helper class to generate test documents from a given XQuery.  */
end_comment

begin_class
specifier|public
class|class
name|TestDataGenerator
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|TestDataGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|outputProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|outputProps
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|String
name|IMPORT
init|=
literal|"import module namespace pt='http://exist-db.org/xquery/test/performance' "
operator|+
literal|"at 'java:org.exist.performance.xquery.PerfTestModule';\n"
operator|+
literal|"declare variable $filename external;\n"
operator|+
literal|"declare variable $count external;\n"
decl_stmt|;
specifier|private
name|String
name|prefix
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|private
name|Path
index|[]
name|generatedFiles
decl_stmt|;
specifier|public
name|TestDataGenerator
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|generatedFiles
operator|=
operator|new
name|Path
index|[
name|count
index|]
expr_stmt|;
block|}
specifier|public
name|Path
index|[]
name|generate
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|xqueryContent
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
specifier|final
name|DocumentSet
name|docs
init|=
name|collection
operator|.
name|allDocs
argument_list|(
name|broker
argument_list|,
operator|new
name|DefaultDocumentSet
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|XQuery
name|service
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"filename"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"count"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query
init|=
name|IMPORT
operator|+
name|xqueryContent
decl_stmt|;
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|service
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|query
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|generatedFiles
index|[
name|i
index|]
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
literal|".xml"
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"filename"
argument_list|,
name|generatedFiles
index|[
name|i
index|]
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"count"
argument_list|,
operator|new
name|Integer
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|results
init|=
name|service
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
specifier|final
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|Writer
name|out
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|generatedFiles
index|[
name|i
index|]
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
specifier|final
name|SAXSerializer
name|sax
init|=
operator|new
name|SAXSerializer
argument_list|(
name|out
argument_list|,
name|outputProps
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|iter
init|=
name|results
operator|.
name|iterate
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|item
init|=
name|iter
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|serializer
operator|.
name|toSAX
argument_list|(
operator|(
name|NodeValue
operator|)
name|item
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
decl||
name|PermissionDeniedException
decl||
name|LockException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
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
return|return
name|generatedFiles
return|;
block|}
specifier|public
name|Path
index|[]
name|generate
parameter_list|(
specifier|final
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|xqueryContent
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|String
name|query
init|=
name|IMPORT
operator|+
name|xqueryContent
decl_stmt|;
try|try
block|{
specifier|final
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"filename"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"count"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
specifier|final
name|CompiledExpression
name|compiled
init|=
name|service
operator|.
name|compile
argument_list|(
name|query
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|generatedFiles
index|[
name|i
index|]
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
literal|".xml"
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"filename"
argument_list|,
name|generatedFiles
index|[
name|i
index|]
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"count"
argument_list|,
operator|new
name|Integer
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
name|compiled
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Writer
name|out
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|generatedFiles
index|[
name|i
index|]
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
specifier|final
name|SAXSerializer
name|sax
init|=
operator|new
name|SAXSerializer
argument_list|(
name|out
argument_list|,
name|outputProps
argument_list|)
decl_stmt|;
for|for
control|(
name|ResourceIterator
name|iter
init|=
name|result
operator|.
name|getIterator
argument_list|()
init|;
name|iter
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|XMLResource
name|r
init|=
operator|(
name|XMLResource
operator|)
name|iter
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|r
operator|.
name|getContentAsSAX
argument_list|(
name|sax
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
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
return|return
name|generatedFiles
return|;
block|}
specifier|public
name|void
name|releaseAll
parameter_list|()
block|{
for|for
control|(
specifier|final
name|Path
name|generatedFile
range|:
name|generatedFiles
control|)
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|generatedFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
