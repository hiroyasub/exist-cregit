begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|search
operator|.
name|Query
import|;
end_import

begin_class
specifier|public
class|class
name|TermInPhrase
implements|implements
name|Comparable
argument_list|<
name|TermInPhrase
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
specifier|private
specifier|final
name|String
name|term
decl_stmt|;
specifier|public
name|TermInPhrase
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|term
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
comment|// DW: missing hashCode() ?
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// DW: parameter 'obj' is not checked for type.
return|return
name|term
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|TermInPhrase
operator|)
name|obj
operator|)
operator|.
name|term
argument_list|)
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|TermInPhrase
name|obj
parameter_list|)
block|{
return|return
name|term
operator|.
name|compareTo
argument_list|(
name|obj
operator|.
name|term
argument_list|)
return|;
block|}
block|}
end_class

end_unit

