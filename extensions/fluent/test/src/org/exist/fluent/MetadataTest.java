begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
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
name|util
operator|.
name|Date
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
name|MetadataTest
extends|extends
name|DatabaseTestCase
block|{
annotation|@
name|Test
specifier|public
name|void
name|binaryDocumentCreationDate
parameter_list|()
block|{
name|Date
name|before
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
decl_stmt|;
name|Date
name|after
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xmlLoadDocumentCreationDate
parameter_list|()
block|{
name|Date
name|before
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo/>"
argument_list|)
argument_list|)
decl_stmt|;
name|Date
name|after
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xmlBuildDocumentCreationDate
parameter_list|()
block|{
name|Date
name|before
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|build
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|)
operator|.
name|elem
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|end
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|commit
argument_list|()
decl_stmt|;
name|Date
name|after
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|folderCreationDate
parameter_list|()
block|{
name|Date
name|before
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Folder
name|folder
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|Date
name|after
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|compareTo
argument_list|(
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xmlDocumentAppendLastModificationDate
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo/>"
argument_list|)
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Date
name|before
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|doc
operator|.
name|root
argument_list|()
operator|.
name|append
argument_list|()
operator|.
name|elem
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|end
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Date
name|after
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xmlDocumentReplaceLastModificationDate
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo><bar/></foo>"
argument_list|)
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Date
name|before
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|doc
operator|.
name|query
argument_list|()
operator|.
name|single
argument_list|(
literal|"//bar"
argument_list|)
operator|.
name|node
argument_list|()
operator|.
name|replace
argument_list|()
operator|.
name|elem
argument_list|(
literal|"baz"
argument_list|)
operator|.
name|end
argument_list|(
literal|"baz"
argument_list|)
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Date
name|after
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xmlDocumentUpdateLastModificationDate
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo/>"
argument_list|)
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Date
name|before
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|doc
operator|.
name|root
argument_list|()
operator|.
name|update
argument_list|()
operator|.
name|attr
argument_list|(
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Date
name|after
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|creationDate
argument_list|()
operator|.
name|compareTo
argument_list|(
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|lastModificationDate
argument_list|()
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|documentOwner
parameter_list|()
block|{
name|Document
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo/>"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"admin"
argument_list|,
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"guest"
argument_list|,
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|documentGroup
parameter_list|()
block|{
name|Document
name|doc
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|()
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo/>"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dba"
argument_list|,
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"guest"
argument_list|,
name|doc
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|folderOwner
parameter_list|()
block|{
name|Folder
name|folder
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"admin"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|()
argument_list|)
expr_stmt|;
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"guest"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|folderGroup
parameter_list|()
block|{
name|Folder
name|folder
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dba"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"guest"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|topFolderOwner
parameter_list|()
block|{
name|Folder
name|folder
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"admin"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|()
argument_list|)
expr_stmt|;
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"guest"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|owner
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|topFolderGroup
parameter_list|()
block|{
name|Folder
name|folder
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dba"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"guest"
argument_list|,
name|folder
operator|.
name|metadata
argument_list|()
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO: test permissions stuff!
block|}
end_class

end_unit

