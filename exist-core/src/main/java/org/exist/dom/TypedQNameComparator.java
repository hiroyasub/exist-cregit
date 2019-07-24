begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2014 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

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
name|xquery
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Comparator for comparing two QNames which takes their  * nameType into account  *  * Should be able to be removed in future when we further refactor  * to decouple QName from nameType.  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|TypedQNameComparator
implements|implements
name|Comparator
argument_list|<
name|QName
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|QName
name|q1
parameter_list|,
specifier|final
name|QName
name|q2
parameter_list|)
block|{
if|if
condition|(
name|q1
operator|.
name|getNameType
argument_list|()
operator|!=
name|q2
operator|.
name|getNameType
argument_list|()
condition|)
block|{
return|return
name|q1
operator|.
name|getNameType
argument_list|()
operator|<
name|q2
operator|.
name|getNameType
argument_list|()
condition|?
name|Constants
operator|.
name|INFERIOR
else|:
name|Constants
operator|.
name|SUPERIOR
return|;
block|}
specifier|final
name|int
name|c
init|=
name|q1
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|compareTo
argument_list|(
name|q2
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|c
operator|==
name|Constants
operator|.
name|EQUAL
condition|?
name|q1
operator|.
name|getLocalPart
argument_list|()
operator|.
name|compareTo
argument_list|(
name|q2
operator|.
name|getLocalPart
argument_list|()
argument_list|)
else|:
name|c
return|;
block|}
block|}
end_class

end_unit

