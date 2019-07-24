begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:ohumbel@gmail.com">Otmar Humbel</a>  */
end_comment

begin_class
specifier|public
class|class
name|BrokerPoolNoRecoveryTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
name|createConfigProperties
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSync_Recovery_Disabled
parameter_list|()
block|{
comment|// For this test it is sufficient to have startDb() called in ExistEmbeddedServer.
comment|// With disabled recovery, this used to fail with a java.util.NoSuchElementException: No value present
name|assertNotNull
argument_list|(
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
expr_stmt|;
comment|// for Codacy alone
block|}
specifier|private
specifier|static
name|Properties
name|createConfigProperties
parameter_list|()
block|{
name|Properties
name|configProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|configProperties
operator|.
name|put
argument_list|(
name|BrokerPoolConstants
operator|.
name|PROPERTY_RECOVERY_ENABLED
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
return|return
name|configProperties
return|;
block|}
block|}
end_class

end_unit

