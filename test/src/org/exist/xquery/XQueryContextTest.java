begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|DBBroker
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
name|Subject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|BinaryValue
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
name|easymock
operator|.
name|EasyMock
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|*
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
comment|/**  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|XQueryContextTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|prepareForExecution_setsUserFromSession
parameter_list|()
block|{
comment|//partial mock context
name|XQueryContext
name|context
init|=
name|EasyMock
operator|.
name|createMockBuilder
argument_list|(
name|XQueryContext
operator|.
name|class
argument_list|)
operator|.
name|withConstructor
argument_list|()
operator|.
name|withArgs
argument_list|()
operator|.
name|addMockedMethod
argument_list|(
literal|"getUserFromHttpSession"
argument_list|)
operator|.
name|addMockedMethod
argument_list|(
literal|"getBroker"
argument_list|)
operator|.
name|createMock
argument_list|()
decl_stmt|;
name|DBBroker
name|mockBroker
init|=
name|createMock
argument_list|(
name|DBBroker
operator|.
name|class
argument_list|)
decl_stmt|;
name|Subject
name|mockSubject
init|=
name|createMock
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//expectations
name|expect
argument_list|(
name|context
operator|.
name|getUserFromHttpSession
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockSubject
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockBroker
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|mockBroker
operator|.
name|pushSubject
argument_list|(
name|mockSubject
argument_list|)
expr_stmt|;
comment|//test
name|replay
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|prepareForExecution
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test to ensure that BinaryValueInstances are      * correctly cleaned up by the XQueryContext      * between reuse of the context      */
annotation|@
name|Test
specifier|public
name|void
name|cleanUp_BinaryValueInstances
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|()
decl_stmt|;
specifier|final
name|XQueryWatchDog
name|mockWatchdog
init|=
name|createMock
argument_list|(
name|XQueryWatchDog
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|.
name|setWatchDog
argument_list|(
name|mockWatchdog
argument_list|)
expr_stmt|;
specifier|final
name|BinaryValue
name|mockBin1
init|=
name|createMock
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BinaryValue
name|mockBin2
init|=
name|createMock
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BinaryValue
name|mockBin3
init|=
name|createMock
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BinaryValue
name|mockBin4
init|=
name|createMock
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BinaryValue
name|mockBin5
init|=
name|createMock
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BinaryValue
name|mockBin6
init|=
name|createMock
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BinaryValue
name|mockBin7
init|=
name|createMock
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// expectations on our mocks
name|mockBin1
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockBin2
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockBin3
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockBin4
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockBin5
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockBin6
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockBin7
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockWatchdog
operator|.
name|reset
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// prepare our mocks for our test
name|replay
argument_list|(
name|mockBin1
argument_list|,
name|mockBin2
argument_list|,
name|mockBin3
argument_list|,
name|mockBin4
argument_list|,
name|mockBin5
argument_list|,
name|mockBin6
argument_list|,
name|mockBin7
argument_list|,
name|mockWatchdog
argument_list|)
expr_stmt|;
comment|/* round 1 */
comment|// use some binary streams
name|context
operator|.
name|registerBinaryValueInstance
argument_list|(
name|mockBin1
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerBinaryValueInstance
argument_list|(
name|mockBin2
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerBinaryValueInstance
argument_list|(
name|mockBin3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|countBinaryValueInstances
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countCleanupTasks
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|// cleanup those streams
name|context
operator|.
name|runCleanupTasks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countBinaryValueInstances
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|//reset the context (for reuse(), just as XQueryPool#returnCompiledXQuery(org.exist.source.Source, CompiledXQuery) would do)
name|context
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countCleanupTasks
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|/* round 2, let's reuse the context... */
comment|// use some more binary streams
name|context
operator|.
name|registerBinaryValueInstance
argument_list|(
name|mockBin4
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerBinaryValueInstance
argument_list|(
name|mockBin5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|countBinaryValueInstances
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countCleanupTasks
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|// cleanup those streams
name|context
operator|.
name|runCleanupTasks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countBinaryValueInstances
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|//reset the context (for reuse(), just as XQueryPool#returnCompiledXQuery(org.exist.source.Source, CompiledXQuery) would do)
name|context
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countCleanupTasks
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|/* round 3, let's reuse the context a second time... */
comment|// again, use some more binary streams
name|context
operator|.
name|registerBinaryValueInstance
argument_list|(
name|mockBin6
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerBinaryValueInstance
argument_list|(
name|mockBin7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|countBinaryValueInstances
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countCleanupTasks
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|// cleanup those streams
name|context
operator|.
name|runCleanupTasks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countBinaryValueInstances
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|//reset the context (for reuse(), just as XQueryPool#returnCompiledXQuery(org.exist.source.Source, CompiledXQuery) would do)
name|context
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countCleanupTasks
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify the expectations of our mocks
name|verify
argument_list|(
name|mockBin1
argument_list|,
name|mockBin2
argument_list|,
name|mockBin3
argument_list|,
name|mockBin4
argument_list|,
name|mockBin5
argument_list|,
name|mockBin6
argument_list|,
name|mockBin7
argument_list|,
name|mockWatchdog
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|countBinaryValueInstances
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
specifier|final
name|Field
name|fldBinaryValueInstances
init|=
name|context
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"binaryValueInstances"
argument_list|)
decl_stmt|;
name|fldBinaryValueInstances
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|BinaryValue
argument_list|>
name|binaryValueInstances
init|=
operator|(
name|List
argument_list|<
name|BinaryValue
argument_list|>
operator|)
name|fldBinaryValueInstances
operator|.
name|get
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|binaryValueInstances
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|int
name|countCleanupTasks
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
specifier|final
name|Field
name|fldCleanupTasks
init|=
name|context
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"cleanupTasks"
argument_list|)
decl_stmt|;
name|fldCleanupTasks
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|XQueryContext
operator|.
name|CleanupTask
argument_list|>
name|cleanupTasks
init|=
operator|(
name|List
argument_list|<
name|XQueryContext
operator|.
name|CleanupTask
argument_list|>
operator|)
name|fldCleanupTasks
operator|.
name|get
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|cleanupTasks
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

