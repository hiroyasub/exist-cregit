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
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|io
operator|.
name|VariableByteInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|Capture
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
name|io
operator|.
name|VariableByteOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|capture
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
name|EXistException
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

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|SymbolTableTest
block|{
specifier|private
specifier|final
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|getName_returns_empty_string_when_id_is_zero
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|symbolTable
operator|.
name|getName
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNameSpace_returns_empty_string_when_id_is_zero
parameter_list|()
throws|throws
name|EXistException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|symbolTable
operator|.
name|getNamespace
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|geMimeType_returns_empty_string_when_id_is_zero
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|symbolTable
operator|.
name|getMimeType
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|getSymbol_for_localName_throws_exception_when_name_is_empty_string
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|getSymbol
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNSSymbol_returns_zero_when_namespace_is_null
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|symbolTable
operator|.
name|getNSSymbol
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNSSymbol_returns_zero_when_namespace_is_empty_string
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|symbolTable
operator|.
name|getNSSymbol
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|localName_ids_are_stable
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
specifier|final
name|String
name|localName
init|=
literal|"some-name"
decl_stmt|;
specifier|final
name|short
name|localNameId
init|=
name|symbolTable
operator|.
name|getSymbol
argument_list|(
name|localName
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
name|symbolTable
operator|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
specifier|final
name|String
name|roundTrippedLocalName
init|=
name|symbolTable
operator|.
name|getName
argument_list|(
name|localNameId
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|localName
argument_list|,
name|roundTrippedLocalName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|namespace_ids_are_stable
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
specifier|final
name|String
name|namespace
init|=
literal|"http://something/or/other"
decl_stmt|;
specifier|final
name|short
name|namespaceId
init|=
name|symbolTable
operator|.
name|getNSSymbol
argument_list|(
name|namespace
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
name|symbolTable
operator|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
specifier|final
name|String
name|roundTrippedNamespace
init|=
name|symbolTable
operator|.
name|getNamespace
argument_list|(
name|namespaceId
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|namespace
argument_list|,
name|roundTrippedNamespace
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mimetype_ids_are_stable
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
specifier|final
name|String
name|mimetype
init|=
literal|"something/other"
decl_stmt|;
specifier|final
name|int
name|mimetypeId
init|=
name|symbolTable
operator|.
name|getMimeTypeId
argument_list|(
name|mimetype
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
name|symbolTable
operator|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
specifier|final
name|String
name|roundTrippedMimetype
init|=
name|symbolTable
operator|.
name|getMimeType
argument_list|(
name|mimetypeId
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|mimetype
argument_list|,
name|roundTrippedMimetype
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|write_and_read_are_balanced
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|symbolTable
operator|.
name|getSymbol
argument_list|(
literal|"some-name"
argument_list|)
expr_stmt|;
name|VariableByteOutputStream
name|mockOs
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|VariableByteOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|VariableByteInput
name|mockIs
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|VariableByteInput
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Capture
argument_list|<
name|Byte
argument_list|>
name|byteCapture
init|=
operator|new
name|Capture
argument_list|<
name|Byte
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Capture
argument_list|<
name|Integer
argument_list|>
name|intCapture
init|=
operator|new
name|Capture
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Capture
argument_list|<
name|String
argument_list|>
name|strCapture
init|=
operator|new
name|Capture
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//write expectations
name|mockOs
operator|.
name|writeByte
argument_list|(
name|capture
argument_list|(
name|byteCapture
argument_list|)
argument_list|)
expr_stmt|;
name|mockOs
operator|.
name|writeInt
argument_list|(
name|capture
argument_list|(
name|intCapture
argument_list|)
argument_list|)
expr_stmt|;
name|mockOs
operator|.
name|writeUTF
argument_list|(
name|capture
argument_list|(
name|strCapture
argument_list|)
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockOs
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|localNameSymbols
operator|.
name|write
argument_list|(
name|mockOs
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockOs
argument_list|)
expr_stmt|;
comment|//read expectations
name|expect
argument_list|(
name|mockIs
operator|.
name|available
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readByte
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|byteCapture
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readInt
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|intCapture
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readUTF
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|strCapture
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|available
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockIs
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|read
argument_list|(
name|mockIs
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockIs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readLegacyFormat
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|SymbolTable
name|symbolTable
init|=
operator|new
name|SymbolTable
argument_list|(
literal|null
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|VariableByteInput
name|mockIs
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|VariableByteInput
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/* readLegacy expectations */
comment|//max and nsMax
name|expect
argument_list|(
name|mockIs
operator|.
name|readShort
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readShort
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|//localnames
name|expect
argument_list|(
name|mockIs
operator|.
name|readInt
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readUTF
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"local-name"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readShort
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|(
name|short
operator|)
literal|67
argument_list|)
expr_stmt|;
comment|//namespaces
name|expect
argument_list|(
name|mockIs
operator|.
name|readInt
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readUTF
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"http://some/or/other"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readShort
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|(
name|short
operator|)
literal|77
argument_list|)
expr_stmt|;
comment|//default mappings
name|expect
argument_list|(
name|mockIs
operator|.
name|readInt
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readUTF
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"mapping"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readShort
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|(
name|short
operator|)
literal|87
argument_list|)
expr_stmt|;
comment|//mimetypes
name|expect
argument_list|(
name|mockIs
operator|.
name|readInt
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readUTF
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"some/other"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockIs
operator|.
name|readInt
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|97
argument_list|)
expr_stmt|;
comment|//replay
name|replay
argument_list|(
name|mockIs
argument_list|)
expr_stmt|;
comment|//action
name|symbolTable
operator|.
name|readLegacy
argument_list|(
name|mockIs
argument_list|)
expr_stmt|;
comment|//verify
name|verify
argument_list|(
name|mockIs
argument_list|)
expr_stmt|;
name|symbolTable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

