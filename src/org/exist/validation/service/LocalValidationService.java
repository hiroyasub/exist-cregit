begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|service
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|security
operator|.
name|User
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
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|LocalCollection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|Validator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|ValidationReport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
operator|.
name|DatabaseResources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
operator|.
name|ResourceInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  *  XML validation service for LocalMode of eXist database.  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|LocalValidationService
implements|implements
name|ValidationService
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LocalValidationService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|private
name|User
name|user
decl_stmt|;
specifier|private
name|LocalCollection
name|localCollection
decl_stmt|;
specifier|private
name|Validator
name|validator
decl_stmt|;
specifier|private
name|DatabaseResources
name|grammaraccess
decl_stmt|;
specifier|public
name|LocalValidationService
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|collection
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Starting LocalValidationService"
argument_list|)
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|localCollection
operator|=
name|collection
expr_stmt|;
name|validator
operator|=
operator|new
name|Validator
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|grammaraccess
operator|=
name|validator
operator|.
name|getDatabaseResources
argument_list|()
expr_stmt|;
block|}
comment|/**      * Validate specified resource.      */
specifier|public
name|boolean
name|validateResource
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Validating resource '"
operator|+
name|id
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|// Write resource contents into stream, using Thread
name|InputStream
name|is
init|=
operator|new
name|ResourceInputStream
argument_list|(
name|brokerPool
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"resource not found"
argument_list|)
expr_stmt|;
block|}
comment|// Perform validation
name|ValidationReport
name|report
init|=
name|validator
operator|.
name|validate
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|// Return validation result
name|logger
operator|.
name|info
argument_list|(
literal|"Validation done."
argument_list|)
expr_stmt|;
return|return
operator|(
operator|!
name|report
operator|.
name|hasErrorsAndWarnings
argument_list|()
operator|)
return|;
block|}
comment|//    /**
comment|//     * Validates a resource given its contents
comment|//     */
comment|//    public boolean validateContents(String contents) throws XMLDBException {
comment|//        Reader rd = new StringReader(contents);
comment|//        ValidationReport report = validator.validate(rd);
comment|//        return !( report.hasErrors() || report.hasWarnings() );
comment|//    }
comment|//    /**
comment|//     * find the whole schema as an XMLResource
comment|//     */
comment|//    public XMLResource getSchema(String targetNamespace) throws XMLDBException {
comment|//        String path = grammaraccess.getGrammarPath(DatabaseResources.GRAMMAR_XSD , targetNamespace);
comment|//        grammaraccess.getGrammar(DatabaseResources.GRAMMAR_XSD, path);
comment|//        return null;
comment|//    }
comment|//    /**
comment|//     *  Is a schema defining this namespace/id known
comment|//     * @param namespaceURI
comment|//     * @return
comment|//     * @throws XMLDBException
comment|//     */
comment|//    public boolean isKnownNamespace(String namespaceURI) throws XMLDBException {
comment|//        return grammaraccess.hasGrammar(DatabaseResources.GRAMMAR_XSD, namespaceURI);
comment|//    }
comment|//    /**
comment|//     * Stores a new schema given its contents
comment|//     */
comment|//    public void putSchema(String schemaContents) throws XMLDBException {
comment|//        //
comment|//    }
comment|//
comment|// ----------------------------------------------------------
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// left empty
block|}
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"ValidationService"
return|;
block|}
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"1.0"
return|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|str1
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// left empty
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// left empty
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

