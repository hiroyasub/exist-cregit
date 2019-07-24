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
name|config
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|PermissionDeniedException
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  * Configuration interface provide methods to read settings.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Configuration
block|{
name|String
name|NS
init|=
literal|"http://exist-db.org/Configuration"
decl_stmt|;
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
comment|/**      * Return sub configuration by name.      * @param name of the configuration      * @return Configuration      */
name|Configuration
name|getConfiguration
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Return list of sub configurations by name.      *       * @param name of the sub configuration      * @return the selected sub configuration      */
name|List
argument_list|<
name|Configuration
argument_list|>
name|getConfigurations
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set of properties configuration have      * @return set of properties of the configuration      *       */
name|Set
argument_list|<
name|String
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Check presents of setting by name      * @param name of the property      * @return true if the property is in the configuration  otherwise false      */
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Return property string value.      *       * @param property to get the value for      * @return String value of the requested property      */
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
comment|/**      * Return property map value.      *       * @param property name of the property map      * @return property map      */
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPropertyMap
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
comment|/**      * Return property integer value.      *       * @param property name      * @return property integer value      *      */
name|Integer
name|getPropertyInteger
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
comment|/**      * Return property long value.      *      * @param property name      * @return property long value      *      */
name|Long
name|getPropertyLong
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
comment|/**      * Return property boolean value.      *      * @param property name      * @return property boolean value      *       */
name|Boolean
name|getPropertyBoolean
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
comment|/**      * Keep at internal map object associated with key.      *       * @param name of the object      * @param object to add      * @return the created object      */
name|Object
name|putObject
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|object
parameter_list|)
function_decl|;
comment|/**      * Get object associated by key from internal map.      *       * @param name of the object      * @return the according object      */
name|Object
name|getObject
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Configuration name.      * @return name of the Configuration      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Return configuration's String value.      * @return configuration's string value      */
name|String
name|getValue
parameter_list|()
function_decl|;
comment|/**      * Return element associated with configuration.      * @return element associated with configuration.      */
name|Element
name|getElement
parameter_list|()
function_decl|;
comment|/**      * Perform check for changers.      *       * @param document to check for changes      */
name|void
name|checkForUpdates
parameter_list|(
name|Element
name|document
parameter_list|)
function_decl|;
comment|/**      * Save configuration.      *       * @throws PermissionDeniedException if permission to save the configuration is denied      * @throws ConfigurationException if there is an error in the configuration      */
name|void
name|save
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
function_decl|;
comment|/**      * Save configuration.      *       * @param broker eXist-db DBBroker      * @throws PermissionDeniedException if permission to save the configuration is denied      * @throws ConfigurationException if there is an error in the configuration      */
name|void
name|save
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
function_decl|;
comment|/**      * Determines equality based on a property value of the configuration      *      * @param obj The Configured instance      * @param property The name of the property to use for comparison, or      *                 if empty, the {@link ConfigurationImpl#ID} is used.      * @return true if obj equals property otherwise false      */
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|,
name|Optional
argument_list|<
name|String
argument_list|>
name|property
parameter_list|)
function_decl|;
comment|/**      * Free up memory allocated for cache.      */
name|void
name|clearCache
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

