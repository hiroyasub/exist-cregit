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
name|xslt
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXSource
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
name|InputSource
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
name|XMLReader
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
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
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
comment|/**  * {@link javax.xml.transform.sax.SAXSource} Supplying an XML document from the eXist database.  *  * @author<a href="mailto:Paul.L.Merchant.Jr@dartmouth.edu">Paul Merchant, Jr.</a>  */
end_comment

begin_class
specifier|public
class|class
name|EXistDbSource
extends|extends
name|SAXSource
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|EXistDbSource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|InputSource
name|source
decl_stmt|;
specifier|public
name|EXistDbSource
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
operator|new
name|EXistDbInputSource
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InputSource
name|getInputSource
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSystemId
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|source
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|this
operator|.
name|source
operator|.
name|getSystemId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|XMLReader
name|getXMLReader
parameter_list|()
block|{
comment|/* FIXME:  Should the reader be configured to read our InputSource before returning?          * Apparently Saxon configures it later with our InputSource, leaving this an open question. 	 */
return|return
operator|new
name|EXistDbXMLReader
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setInputSource
parameter_list|(
specifier|final
name|InputSource
name|inputSource
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|inputSource
operator|instanceof
name|EXistDbInputSource
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"EXistDbSource only accepts EXistDbInputSource"
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|=
name|inputSource
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSystemId
parameter_list|(
specifier|final
name|String
name|systemId
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|source
operator|==
literal|null
condition|)
block|{
comment|// This should not be possible
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"EXistDbSource cannot initialize InputSource from a systemId"
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|.
name|setSystemId
argument_list|(
name|systemId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setXMLReader
parameter_list|(
specifier|final
name|XMLReader
name|reader
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Setting external reader is not supported"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

