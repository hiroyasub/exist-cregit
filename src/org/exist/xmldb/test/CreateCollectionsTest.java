begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|IOException
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Iterator
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|XMLUtil
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
name|XMLFilenameFilter
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
name|*
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
name|*
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|CreateCollectionsTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist:///db"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|public
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|public
name|CreateCollectionsTest
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
comment|// initialize driver
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
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
comment|// try to get collection
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
specifier|public
name|void
name|testCreateCollection
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Created Collection: "
operator|+
name|root
operator|.
name|getName
argument_list|()
operator|+
literal|"( "
operator|+
name|root
operator|.
name|getClass
argument_list|()
operator|+
literal|" )"
argument_list|)
expr_stmt|;
name|Service
index|[]
name|services
init|=
name|root
operator|.
name|getServices
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"services array: "
operator|+
name|services
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Collection must provide at least one Service"
argument_list|,
name|services
operator|!=
literal|null
operator|&&
name|services
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  number of services: "
operator|+
name|services
operator|.
name|length
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
name|services
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Service: "
operator|+
name|services
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|"( "
operator|+
name|services
index|[
name|i
index|]
operator|.
name|getClass
argument_list|()
operator|+
literal|" )"
argument_list|)
expr_stmt|;
block|}
name|Collection
name|parentCollection
init|=
name|root
operator|.
name|getParentCollection
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"root parentCollection: "
operator|+
name|parentCollection
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"root collection has no parent"
argument_list|,
name|parentCollection
argument_list|)
expr_stmt|;
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|int
name|ccc
init|=
name|testCollection
operator|.
name|getChildCollectionCount
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Collection just created: ChildCollectionCount==0"
argument_list|,
name|ccc
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Collection state should be Open after creation"
argument_list|,
name|testCollection
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|directory
init|=
literal|"samples/shakespeare"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"storing all XML files in directory "
operator|+
name|directory
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|File
name|files
index|[]
init|=
name|f
operator|.
name|listFiles
argument_list|(
operator|new
name|XMLFilenameFilter
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|storeResourceFromFile
argument_list|(
name|files
index|[
name|i
index|]
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
block|}
name|HashSet
name|fileNamesJustStored
init|=
operator|new
name|HashSet
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|file
init|=
name|files
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|lastSeparator
init|=
name|file
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
decl_stmt|;
name|fileNamesJustStored
operator|.
name|add
argument_list|(
name|file
operator|.
name|substring
argument_list|(
name|lastSeparator
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fileNames stored: "
operator|+
name|fileNamesJustStored
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|resourcesNames
init|=
name|testCollection
operator|.
name|listResources
argument_list|()
decl_stmt|;
name|int
name|resourceCount
init|=
name|testCollection
operator|.
name|getResourceCount
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testCollection.getResourceCount()="
operator|+
name|resourceCount
argument_list|)
expr_stmt|;
name|ArrayList
name|fileNamesPresentInDatabase
init|=
operator|new
name|ArrayList
argument_list|()
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
name|resourcesNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fileNamesPresentInDatabase
operator|.
name|add
argument_list|(
name|resourcesNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"resourcesNames must contain fileNames just stored"
argument_list|,
name|fileNamesPresentInDatabase
operator|.
name|containsAll
argument_list|(
name|fileNamesJustStored
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|fileToRemove
init|=
literal|"macbeth.xml"
decl_stmt|;
name|Resource
name|resMacbeth
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
name|fileToRemove
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"getResource("
operator|+
name|fileToRemove
operator|+
literal|"\")"
argument_list|,
name|resMacbeth
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|removeResource
argument_list|(
name|resMacbeth
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"After removal resource count must decrease"
argument_list|,
name|testCollection
operator|.
name|getResourceCount
argument_list|()
operator|==
name|resourceCount
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// restore the resource just removed :
name|storeResourceFromFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|fileToRemove
argument_list|)
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|storeBinaryResourceFromFile
argument_list|(
operator|new
name|File
argument_list|(
literal|"../webapp/logo.jpg"
argument_list|)
argument_list|,
name|testCollection
argument_list|)
decl_stmt|;
name|Object
name|content
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"logo.jpg"
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|byte
index|[]
name|dataStored
init|=
operator|(
name|byte
index|[]
operator|)
name|content
decl_stmt|;
name|assertTrue
argument_list|(
literal|"After storing binary resource, data out==data in"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|dataStored
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|XMLResource
name|storeResourceFromFile
parameter_list|(
name|File
name|file
parameter_list|,
name|Collection
name|testCollection
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"storing "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|XMLResource
name|res
decl_stmt|;
name|String
name|xml
decl_stmt|;
name|res
operator|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"storeResourceFromFile"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|xml
operator|=
name|XMLUtil
operator|.
name|readFile
argument_list|(
name|file
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|xml
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"stored "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
specifier|private
name|byte
index|[]
name|storeBinaryResourceFromFile
parameter_list|(
name|File
name|file
parameter_list|,
name|Collection
name|testCollection
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"storing "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Resource
name|res
init|=
operator|(
name|BinaryResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"store binary Resource From File"
argument_list|,
name|res
argument_list|)
expr_stmt|;
comment|// Get an array of bytes from the file:
name|FileInputStream
name|istr
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedInputStream
name|bstr
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|istr
argument_list|)
decl_stmt|;
comment|// promote
name|int
name|size
init|=
operator|(
name|int
operator|)
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// get the file size (in bytes)
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
comment|// allocate byte array of right size
name|bstr
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
comment|// read into byte array
name|bstr
operator|.
name|close
argument_list|()
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"stored "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|public
name|void
name|testMultipleCreates
parameter_list|()
block|{
try|try
block|{
name|Collection
name|rootColl
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db"
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|rootColl
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cms
argument_list|)
expr_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"dummy1"
argument_list|)
expr_stmt|;
name|printChildren
argument_list|(
name|rootColl
argument_list|)
expr_stmt|;
name|Collection
name|c1
init|=
name|rootColl
operator|.
name|getChildCollection
argument_list|(
literal|"dummy1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|cms
operator|.
name|setCollection
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"dummy2"
argument_list|)
expr_stmt|;
name|Collection
name|c2
init|=
name|c1
operator|.
name|getChildCollection
argument_list|(
literal|"dummy2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|cms
operator|.
name|setCollection
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"dummy3"
argument_list|)
expr_stmt|;
name|Collection
name|c3
init|=
name|c2
operator|.
name|getChildCollection
argument_list|(
literal|"dummy3"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c3
argument_list|)
expr_stmt|;
name|cms
operator|.
name|setCollection
argument_list|(
name|rootColl
argument_list|)
expr_stmt|;
name|cms
operator|.
name|removeCollection
argument_list|(
literal|"dummy1"
argument_list|)
expr_stmt|;
name|printChildren
argument_list|(
name|rootColl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"number of child collections should be 2"
argument_list|,
name|rootColl
operator|.
name|getChildCollectionCount
argument_list|()
operator|==
literal|2
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
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|printChildren
parameter_list|(
name|Collection
name|c
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Children of "
operator|+
name|c
operator|.
name|getName
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|String
index|[]
name|names
init|=
name|c
operator|.
name|listChildCollections
argument_list|()
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
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|CreateCollectionsTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//junit.swingui.TestRunner.run(LexerTest.class);
block|}
block|}
end_class

end_unit

