begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
specifier|public
class|class
name|HTML5WriterTest
block|{
specifier|private
name|HTML5Writer
name|writer
decl_stmt|;
specifier|private
name|StringWriter
name|targetWriter
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|targetWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|HTML5Writer
argument_list|(
name|targetWriter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAttributeWithBooleanValue
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|expected
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html>\n<input checked>"
decl_stmt|;
specifier|final
name|QName
name|elQName
init|=
operator|new
name|QName
argument_list|(
literal|"input"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|startElement
argument_list|(
name|elQName
argument_list|)
expr_stmt|;
name|writer
operator|.
name|attribute
argument_list|(
literal|"checked"
argument_list|,
literal|"checked"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|closeStartTag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|actual
init|=
name|targetWriter
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAttributeWithNonBooleanValue
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|expected
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html>\n<input name=\"name\">"
decl_stmt|;
specifier|final
name|QName
name|elQName
init|=
operator|new
name|QName
argument_list|(
literal|"input"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|startElement
argument_list|(
name|elQName
argument_list|)
expr_stmt|;
name|writer
operator|.
name|attribute
argument_list|(
literal|"name"
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|closeStartTag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|actual
init|=
name|targetWriter
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAttributeQNameWithBooleanValue
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|expected
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html>\n<input checked>"
decl_stmt|;
specifier|final
name|QName
name|elQName
init|=
operator|new
name|QName
argument_list|(
literal|"input"
argument_list|)
decl_stmt|;
specifier|final
name|QName
name|attrQName
init|=
operator|new
name|QName
argument_list|(
literal|"checked"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|startElement
argument_list|(
name|elQName
argument_list|)
expr_stmt|;
name|writer
operator|.
name|attribute
argument_list|(
name|attrQName
argument_list|,
name|attrQName
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|closeStartTag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|actual
init|=
name|targetWriter
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAttributeQNameWithNonBooleanValue
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|expected
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html>\n<input name=\"name\">"
decl_stmt|;
specifier|final
name|QName
name|elQName
init|=
operator|new
name|QName
argument_list|(
literal|"input"
argument_list|)
decl_stmt|;
specifier|final
name|QName
name|attrQName
init|=
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|startElement
argument_list|(
name|elQName
argument_list|)
expr_stmt|;
name|writer
operator|.
name|attribute
argument_list|(
name|attrQName
argument_list|,
name|attrQName
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|closeStartTag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|actual
init|=
name|targetWriter
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

