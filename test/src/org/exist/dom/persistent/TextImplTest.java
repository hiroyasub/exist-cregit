begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|DLN
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
name|w3c
operator|.
name|dom
operator|.
name|DOMException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
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
name|EasyMock
operator|.
name|verify
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
name|assertFalse
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

begin_comment
comment|/**  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|TextImplTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|isSameNode_sameText
parameter_list|()
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|DocumentImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|21
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|text
operator|.
name|setNodeId
argument_list|(
operator|new
name|DLN
argument_list|(
literal|"1.2.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|text
operator|.
name|isSameNode
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isSameNode_differentText
parameter_list|()
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|DocumentImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|text
operator|.
name|setNodeId
argument_list|(
operator|new
name|DLN
argument_list|(
literal|"1.2.1"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|TextImpl
name|text2
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|text2
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|text2
operator|.
name|setNodeId
argument_list|(
operator|new
name|DLN
argument_list|(
literal|"1.7.9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|text
operator|.
name|isSameNode
argument_list|(
name|text2
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isSameNode_differentTextDifferentDoc
parameter_list|()
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|DocumentImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|21
argument_list|)
expr_stmt|;
specifier|final
name|DocumentImpl
name|doc2
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|DocumentImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|doc2
operator|.
name|getDocId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|67
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|doc
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|text
operator|.
name|setNodeId
argument_list|(
operator|new
name|DLN
argument_list|(
literal|"1.2.1"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|TextImpl
name|text2
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|text2
operator|.
name|setOwnerDocument
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|text2
operator|.
name|setNodeId
argument_list|(
operator|new
name|DLN
argument_list|(
literal|"1.2.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|text
operator|.
name|isSameNode
argument_list|(
name|text2
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|doc
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isSameNode_nonText
parameter_list|()
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|DocumentImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|text
operator|.
name|setNodeId
argument_list|(
operator|new
name|DLN
argument_list|(
literal|"1.2.1"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ElementImpl
name|elem
init|=
operator|new
name|ElementImpl
argument_list|()
decl_stmt|;
name|elem
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setNodeId
argument_list|(
operator|new
name|DLN
argument_list|(
literal|"1.2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|text
operator|.
name|isSameNode
argument_list|(
name|elem
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setData
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"helloworld"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"helloworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|setData
argument_list|(
literal|"worldhello"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"worldhello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setData_empty
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"helloworld"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"helloworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|setData
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setData_shrink
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"helloworld"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"helloworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|setData
argument_list|(
literal|"goodbye"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"goodbye"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setData_expand
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"helloworld"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"helloworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|setData
argument_list|(
literal|"thanksandgoodbye"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"thanksandgoodbye"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|appendData
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|appendData
argument_list|(
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"helloworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|appendData_empty
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|appendData
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertData_start
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|insertData
argument_list|(
literal|0
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"worldhello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertData_middle
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|insertData
argument_list|(
literal|3
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"helworldlo"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertData_end
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|insertData
argument_list|(
literal|5
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"helloworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DOMException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|insertData_pastEnd
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|insertData
argument_list|(
literal|10
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertData_empty
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|insertData
argument_list|(
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|replaceData_shrink
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"helloworld"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"helloworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|replaceData
argument_list|(
literal|1
argument_list|,
literal|7
argument_list|,
literal|"ok"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hokld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|replaceData_start
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|replaceData
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"worldello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|replaceData_middle
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|replaceData
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"helworldo"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|replaceData_end
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|replaceData
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hellworld"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DOMException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|replaceData_pastEnd
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|insertData
argument_list|(
literal|10
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|replaceData_empty
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|replaceData
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"heo"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|replaceData_longArg
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"1230 North Ave. Dallas, Texas 98551"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1230 North Ave. Dallas, Texas 98551"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|replaceData
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|,
literal|"260030"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"260030 North Ave. Dallas, Texas 98551"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|replaceData_untilEnd
parameter_list|()
block|{
specifier|final
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|(
literal|"1230 North Ave. Dallas, Texas 98551"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1230 North Ave. Dallas, Texas 98551"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|replaceData
argument_list|(
literal|0
argument_list|,
literal|50
argument_list|,
literal|"2600"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2600"
argument_list|,
name|text
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

