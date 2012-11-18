begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|FileInputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|XMLUtilTest
block|{
specifier|private
name|String
name|thisTestFileRelativePath
init|=
literal|"test/src/org/exist/dom/"
decl_stmt|;
specifier|private
name|String
name|utf8TestFileName
init|=
literal|"utf8.xml"
decl_stmt|;
specifier|private
name|String
name|utf16TestFileName
init|=
literal|"utf16.xml"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetXMLDeclWithUTF8
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|existDir
decl_stmt|;
name|File
name|thisTestFileDir
decl_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|existDir
operator|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
expr_stmt|;
name|thisTestFileDir
operator|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
name|thisTestFileRelativePath
argument_list|)
expr_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|thisTestFileDir
argument_list|,
name|utf8TestFileName
argument_list|)
decl_stmt|;
name|InputStream
name|in
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test file not found"
argument_list|)
expr_stmt|;
return|return;
block|}
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|l
decl_stmt|;
do|do
block|{
name|l
operator|=
name|in
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
if|if
condition|(
name|l
operator|>
literal|0
condition|)
name|out
operator|.
name|write
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|l
operator|>
operator|-
literal|1
condition|)
do|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|String
name|expectedDecl
init|=
literal|"<?xml version=\"1.0\"?>"
decl_stmt|;
name|String
name|decl
init|=
name|XMLUtil
operator|.
name|getXMLDecl
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"XML Declaration for the UTF-8 encode example file wasn't resolved properly"
argument_list|,
name|expectedDecl
argument_list|,
name|decl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetXMLDeclWithUTF16
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|existDir
decl_stmt|;
name|File
name|thisTestFileDir
decl_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|existDir
operator|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
expr_stmt|;
name|thisTestFileDir
operator|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
name|thisTestFileRelativePath
argument_list|)
expr_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|thisTestFileDir
argument_list|,
name|utf16TestFileName
argument_list|)
decl_stmt|;
name|InputStream
name|in
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test file not found"
argument_list|)
expr_stmt|;
return|return;
block|}
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|l
decl_stmt|;
do|do
block|{
name|l
operator|=
name|in
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
if|if
condition|(
name|l
operator|>
literal|0
condition|)
name|out
operator|.
name|write
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|l
operator|>
operator|-
literal|1
condition|)
do|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|String
name|expectedDecl
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"no\"?>"
decl_stmt|;
name|String
name|decl
init|=
name|XMLUtil
operator|.
name|getXMLDecl
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"XML Declaration for the UTF-16 encode example file wasn't resolved properly"
argument_list|,
name|expectedDecl
argument_list|,
name|decl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

