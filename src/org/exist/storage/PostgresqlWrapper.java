begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

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
name|IOException
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  * @author     wolf  * @created    3. Juni 2002  */
end_comment

begin_class
specifier|public
class|class
name|PostgresqlWrapper
extends|extends
name|DBWrapper
block|{
comment|/**      *  Constructor for the PostgresqlWrapper object      *      * @param  config  Description of the Parameter      * @param  pool    Description of the Parameter      */
specifier|public
name|PostgresqlWrapper
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|DBConnectionPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|,
name|pool
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      * @param  fname            Description of the Parameter      * @param  table            Description of the Parameter      * @exception  IOException  Description of the Exception      */
specifier|public
name|void
name|loadFromFile
parameter_list|(
name|String
name|fname
parameter_list|,
name|String
name|table
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|checkFile
argument_list|(
name|fname
argument_list|)
condition|)
return|return;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|String
name|absolutePath
init|=
name|f
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
comment|// fix for windows
if|if
condition|(
name|File
operator|.
name|separator
operator|.
name|equals
argument_list|(
literal|"\\"
argument_list|)
condition|)
block|{
name|String
name|newPath
init|=
name|escapeChars
argument_list|(
name|absolutePath
argument_list|)
decl_stmt|;
comment|//System.out.println("old: "+absolutePath+"   new:"+newPath);
name|absolutePath
operator|=
name|newPath
expr_stmt|;
block|}
name|Connection
name|con
init|=
name|pool
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|sql
init|=
literal|"COPY "
operator|+
name|table
operator|+
literal|" FROM '"
operator|+
name|absolutePath
operator|+
literal|"'"
operator|+
literal|" USING DELIMITERS '|'"
decl_stmt|;
try|try
block|{
name|con
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Statement
name|stmt
init|=
name|con
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|getConnection
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\n"
operator|+
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
name|se
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|se
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|pool
operator|.
name|release
argument_list|(
name|con
argument_list|)
expr_stmt|;
comment|//removeFile(absolutePath);
block|}
specifier|private
name|String
name|escapeChars
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|str
argument_list|,
literal|"\\"
argument_list|)
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|token
init|=
name|tok
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|tok
operator|.
name|hasMoreElements
argument_list|()
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

