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
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  *  Do a query on the root-Collection.  *  To run this example enter:   *   *  bin/run.sh examples.xmldb.SearchExample xpath-query  *    *  in the root directory of the distribution.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    20. September 2002  */
end_comment

begin_class
specifier|public
class|class
name|SearchExample
block|{
specifier|protected
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://localhost:8080/exist/xmlrpc"
decl_stmt|;
comment|//protected static String URI = "xmldb:exist://";
specifier|protected
specifier|static
name|String
name|driver
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
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
name|collection
init|=
literal|"/db"
decl_stmt|;
name|String
name|query
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
comment|// if collection does not start with "/" add it
name|collection
operator|=
operator|(
name|args
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|?
name|args
index|[
literal|0
index|]
else|:
literal|"/"
operator|+
name|args
index|[
literal|0
index|]
expr_stmt|;
name|query
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
name|query
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
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
name|collection
argument_list|)
decl_stmt|;
comment|// get query-service
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// set pretty-printing on
name|service
operator|.
name|setProperty
argument_list|(
literal|"pretty"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
literal|"encoding"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
expr_stmt|;
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
name|query
argument_list|(
name|query
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|resource
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|"query:         "
operator|+
name|query
argument_list|)
expr_stmt|;
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
literal|"usage: examples.xmldb.ExampleSearch [ collection ] xpath-query"
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

