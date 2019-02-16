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
package|;
end_package

begin_comment
comment|/**  * Indexes that store their values with an information about the QName of their nodes   * should implement this interface.  *   * @author brihaye  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|QNamedKeysIndex
extends|extends
name|IndexWorker
block|{
comment|/**      * A key to a QName {@link java.util.List} "hint" to be used when the index scans its index entries      */
specifier|public
specifier|static
specifier|final
name|String
name|QNAMES_KEY
init|=
literal|"qnames_key"
decl_stmt|;
block|}
end_interface

end_unit

