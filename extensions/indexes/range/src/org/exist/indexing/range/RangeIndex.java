begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
operator|.
name|KeywordAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexWorker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndex
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
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * Main implementation class for the new range index. This extends the existing LuceneIndex.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|RangeIndex
extends|extends
name|LuceneIndex
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|RangeIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
name|RangeIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**      * Enumeration of supported operators and optimized functions.      */
specifier|public
enum|enum
name|Operator
block|{
name|GT
argument_list|(
literal|"gt"
argument_list|)
block|,
name|LT
argument_list|(
literal|"lt"
argument_list|)
block|,
name|EQ
argument_list|(
literal|"eq"
argument_list|)
block|,
name|GE
argument_list|(
literal|"ge"
argument_list|)
block|,
name|LE
argument_list|(
literal|"le"
argument_list|)
block|,
name|NE
argument_list|(
literal|"ne"
argument_list|)
block|,
name|ENDS_WITH
argument_list|(
literal|"ends-with"
argument_list|)
block|,
name|STARTS_WITH
argument_list|(
literal|"starts-with"
argument_list|)
block|,
name|CONTAINS
argument_list|(
literal|"contains"
argument_list|)
block|,
name|MATCH
argument_list|(
literal|"matches"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|Operator
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
empty_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DIR_NAME
init|=
literal|"range"
decl_stmt|;
specifier|private
name|Analyzer
name|defaultAnalyzer
init|=
operator|new
name|KeywordAnalyzer
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getDirName
parameter_list|()
block|{
return|return
name|DIR_NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
operator|new
name|RangeIndexWorker
argument_list|(
name|this
argument_list|,
name|broker
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
specifier|public
name|Analyzer
name|getDefaultAnalyzer
parameter_list|()
block|{
return|return
name|defaultAnalyzer
return|;
block|}
block|}
end_class

end_unit

