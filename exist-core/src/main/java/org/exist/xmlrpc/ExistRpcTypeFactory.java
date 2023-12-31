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
name|xmlrpc
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ws
operator|.
name|commons
operator|.
name|util
operator|.
name|NamespaceContextImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|common
operator|.
name|TypeFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|common
operator|.
name|XmlRpcController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|common
operator|.
name|XmlRpcStreamConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|parser
operator|.
name|TypeParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|serializer
operator|.
name|TypeSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Custom XML-RPC type factory to enable the use  * of extended types in XML-RPC with eXist-db.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|ExistRpcTypeFactory
extends|extends
name|TypeFactoryImpl
block|{
specifier|public
name|ExistRpcTypeFactory
parameter_list|(
specifier|final
name|XmlRpcController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|controller
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TypeParser
name|getParser
parameter_list|(
specifier|final
name|XmlRpcStreamConfig
name|config
parameter_list|,
specifier|final
name|NamespaceContextImpl
name|context
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|)
block|{
if|if
condition|(
name|TupleSerializer
operator|.
name|TUPLE_TAG
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return
operator|new
name|TupleParser
argument_list|(
name|config
argument_list|,
name|context
argument_list|,
name|this
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getParser
argument_list|(
name|config
argument_list|,
name|context
argument_list|,
name|uri
argument_list|,
name|localName
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|TypeSerializer
name|getSerializer
parameter_list|(
specifier|final
name|XmlRpcStreamConfig
name|config
parameter_list|,
specifier|final
name|Object
name|object
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|object
operator|instanceof
name|Tuple
condition|)
block|{
return|return
operator|new
name|TupleSerializer
argument_list|(
name|this
argument_list|,
name|config
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getSerializer
argument_list|(
name|config
argument_list|,
name|object
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

