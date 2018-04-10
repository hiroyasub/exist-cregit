begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
package|;
end_package

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Paths
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|SourceFactoryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromFile_contextAbsoluteFileUrl_locationAbsoluteUrl
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|URL
name|mainUrl
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"main.xq"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|contextPath
init|=
name|mainUrl
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|URL
name|libraryUrl
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|location
init|=
name|libraryUrl
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|FileSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|File
argument_list|(
name|libraryUrl
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromFile_contextAbsoluteFile_locationAbsoluteFile
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|URL
name|mainUrl
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"main.xq"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|contextPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|mainUrl
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|URL
name|libraryUrl
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|location
init|=
name|Paths
operator|.
name|get
argument_list|(
name|libraryUrl
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|FileSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|File
argument_list|(
name|libraryUrl
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromFile_contextAbsoluteFileUrl_locationRelative
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|URL
name|mainUrl
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"main.xq"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|contextPath
init|=
name|mainUrl
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|location
init|=
literal|"library.xqm"
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|FileSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|File
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromFile_contextAbsoluteFile_locationRelative
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|URL
name|mainUrl
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"main.xq"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|contextPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|mainUrl
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|location
init|=
literal|"library.xqm"
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|FileSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|File
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromFile_contextAbsoluteDir_locationRelative
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|URL
name|mainUrl
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"main.xq"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|contextPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|mainUrl
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//final String contextPath = mainParent.substring(0, mainParent.lastIndexOf('/'));
specifier|final
name|String
name|location
init|=
literal|"library.xqm"
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|FileSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromResource_contextAbsoluteFileUrl_locationRelative
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|String
name|contextPath
init|=
literal|"resource:org/exist/source/main.xq"
decl_stmt|;
specifier|final
name|String
name|location
init|=
literal|"library.xqm"
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|ClassLoaderSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromResource_contextAbsoluteFileUrl_locationAbsoluteUrl
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|String
name|contextPath
init|=
literal|"resource:org/exist/source/main.xq"
decl_stmt|;
specifier|final
name|String
name|location
init|=
literal|"resource:org/exist/source/library.xqm"
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|ClassLoaderSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromResource_contextAbsoluteFileUrl_locationRelativeUrl
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|String
name|contextPath
init|=
literal|"resource:org/exist/source/main.xq"
decl_stmt|;
specifier|final
name|String
name|location
init|=
literal|"library.xqm"
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|instanceof
name|ClassLoaderSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"library.xqm"
argument_list|)
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSourceFromResource_contextAbsoluteFileUrl_locationRelativeUrl_basedOnSource
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|String
name|contextPath
init|=
literal|"resource:org/exist/source/main.xq"
decl_stmt|;
specifier|final
name|String
name|location
init|=
literal|"library.xqm"
decl_stmt|;
specifier|final
name|Source
name|mainSource
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
name|contextPath
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mainSource
operator|instanceof
name|ClassLoaderSource
argument_list|)
expr_stmt|;
specifier|final
name|Source
name|relativeSource
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
literal|null
argument_list|,
operator|(
operator|(
name|ClassLoaderSource
operator|)
name|mainSource
operator|)
operator|.
name|getSource
argument_list|()
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|relativeSource
operator|instanceof
name|ClassLoaderSource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|location
argument_list|)
argument_list|,
name|relativeSource
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

