begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_comment
comment|/**  * Implementations of this interface can be passed to method  * {@link org.exist.storage.TextSearchEngine#getNodes(DocumentSet, NodeSet, TermMatcher, CharSequence)}   * to check if an index entry matches a given search term.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|TermMatcher
block|{
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|term
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

