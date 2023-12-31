begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
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
name|XPathException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ComputableValue
extends|extends
name|AtomicValue
block|{
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#getStringValue()      */
specifier|public
specifier|abstract
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#convertTo(int)      */
specifier|public
specifier|abstract
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#compareTo(org.exist.xquery.value.AtomicValue)      */
specifier|public
specifier|abstract
name|int
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#max(org.exist.xquery.value.AtomicValue)      */
specifier|public
specifier|abstract
name|AtomicValue
name|max
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#min(org.exist.xquery.value.AtomicValue)      */
specifier|public
specifier|abstract
name|AtomicValue
name|min
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|ComputableValue
name|minus
parameter_list|(
name|ComputableValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|ComputableValue
name|plus
parameter_list|(
name|ComputableValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|ComputableValue
name|mult
parameter_list|(
name|ComputableValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|ComputableValue
name|div
parameter_list|(
name|ComputableValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
block|}
end_class

end_unit

