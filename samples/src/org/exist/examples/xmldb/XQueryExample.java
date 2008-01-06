begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|examples
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|util
operator|.
name|serializer
operator|.
name|SerializerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XQueryService
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
name|DatabaseManager
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
name|Collection
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
name|Database
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
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_comment
comment|/**  *  Reads an XQuery file and executes it. To run this example enter:   *   *  bin/run.sh org.exist.examples.xmldb.XQueryExample<xqueryfile>  *    *  in the root directory of the distribution.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    20. September 2002  */
end_comment

begin_class
specifier|public
class|class
name|XQueryExample
block|{
specifier|protected
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
decl_stmt|;
specifier|protected
specifier|static
name|String
name|driver
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
comment|/**      * Read the xquery file and return as string.      */
specifier|protected
specifier|static
name|String
name|readFile
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|f
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|xml
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|f
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
name|xml
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|xml
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
name|usage
argument_list|()
expr_stmt|;
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|String
name|query
init|=
name|readFile
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// get root-collection
name|Collection
name|col
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
decl_stmt|;
comment|// get query-service
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// set pretty-printing on
name|service
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
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
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
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// execute query and get results in ResourceSet
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
name|long
name|qtime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
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
name|SAXSerializer
name|serializer
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|setOutput
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|int
operator|)
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
operator|(
name|long
operator|)
name|i
argument_list|)
decl_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
block|}
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|long
name|rtime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"hits:          "
operator|+
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"query time:    "
operator|+
name|qtime
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"retrieve time: "
operator|+
name|rtime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"usage: examples.xmldb.XQueryExample xquery-file"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

