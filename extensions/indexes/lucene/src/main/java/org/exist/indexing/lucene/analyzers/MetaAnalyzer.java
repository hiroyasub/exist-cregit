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
name|indexing
operator|.
name|lucene
operator|.
name|analyzers
package|;
end_package

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
name|DelegatingAnalyzerWrapper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Delegates to different analyzers configured by field.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|MetaAnalyzer
extends|extends
name|DelegatingAnalyzerWrapper
block|{
specifier|private
specifier|final
name|Analyzer
name|defaultAnalyzer
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|perFieldAnalyzers
decl_stmt|;
specifier|public
name|MetaAnalyzer
parameter_list|(
annotation|@
name|Nonnull
name|Analyzer
name|defaultAnalyzer
parameter_list|)
block|{
name|super
argument_list|(
name|PER_FIELD_REUSE_STRATEGY
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultAnalyzer
operator|=
name|defaultAnalyzer
expr_stmt|;
name|perFieldAnalyzers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|addAnalyzer
parameter_list|(
annotation|@
name|Nonnull
name|String
name|fieldName
parameter_list|,
annotation|@
name|Nonnull
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|perFieldAnalyzers
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
annotation|@
name|Nullable
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
return|return
name|defaultAnalyzer
return|;
block|}
return|return
name|perFieldAnalyzers
operator|.
name|getOrDefault
argument_list|(
name|fieldName
argument_list|,
name|defaultAnalyzer
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MetaAnalyzer("
operator|+
name|this
operator|.
name|perFieldAnalyzers
operator|+
literal|", default="
operator|+
name|this
operator|.
name|defaultAnalyzer
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

