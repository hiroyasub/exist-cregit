begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|examples
operator|.
name|xmlrpc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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

begin_comment
comment|/**  * Store a document to the database using  * XML-RPC.  */
end_comment

begin_class
specifier|public
class|class
name|Store
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|uri
init|=
literal|"http://localhost:8080/exist/xmlrpc"
decl_stmt|;
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
literal|"usage: org.exist.examples.xmlrpc.Store xmlFile [ docName ]"
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
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
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
name|String
name|docName
init|=
operator|(
name|args
operator|.
name|length
operator|==
literal|2
operator|)
condition|?
name|args
index|[
literal|1
index|]
else|:
name|args
index|[
literal|0
index|]
decl_stmt|;
name|XmlRpc
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
operator|new
name|XmlRpcClient
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|xmlrpc
operator|.
name|setBasicAuthentication
argument_list|(
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// read the file into a string
name|BufferedReader
name|f
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|args
index|[
literal|0
index|]
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
comment|// set parameters for XML-RPC call
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|xml
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// execute the call
name|Boolean
name|result
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
comment|// check result
if|if
condition|(
name|result
operator|.
name|booleanValue
argument_list|()
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"document stored."
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"could not store document."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

