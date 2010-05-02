begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Templates
import|;
end_import

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
name|TemplatesHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|xquery
operator|.
name|XPathException
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
name|ElementAtExist
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|SAXAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|TemplatesHandlerImpl
extends|extends
name|SAXAdapter
implements|implements
name|TemplatesHandler
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TemplatesHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|systemId
init|=
literal|null
decl_stmt|;
specifier|private
name|Templates
name|templates
init|=
literal|null
decl_stmt|;
specifier|protected
name|TemplatesHandlerImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.sax.TemplatesHandler#getSystemId() 	 */
specifier|public
name|String
name|getSystemId
parameter_list|()
block|{
return|return
name|systemId
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.sax.TemplatesHandler#getTemplates() 	 */
specifier|public
name|Templates
name|getTemplates
parameter_list|()
block|{
if|if
condition|(
name|templates
operator|==
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|getDocument
argument_list|()
decl_stmt|;
name|ElementAtExist
name|xsl
init|=
operator|(
name|ElementAtExist
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
try|try
block|{
name|templates
operator|=
name|XSL
operator|.
name|compile
argument_list|(
name|xsl
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|getDetailMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO: remove
block|}
block|}
return|return
name|templates
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.sax.TemplatesHandler#setSystemId(java.lang.String) 	 */
specifier|public
name|void
name|setSystemId
parameter_list|(
name|String
name|systemId
parameter_list|)
block|{
name|this
operator|.
name|systemId
operator|=
name|systemId
expr_stmt|;
block|}
block|}
end_class

end_unit

