begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|xacml
package|;
end_package

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|ParsingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|attr
operator|.
name|AttributeDesignator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|attr
operator|.
name|AttributeValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|xacml
operator|.
name|XACMLConstants
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
name|Function
import|;
end_import

begin_comment
comment|//TODO give user more help through this class
end_comment

begin_comment
comment|//
end_comment

begin_class
specifier|public
class|class
name|ModuleAttributeHandler
implements|implements
name|AttributeHandler
block|{
specifier|public
name|ModuleAttributeHandler
parameter_list|()
block|{
block|}
specifier|public
name|void
name|filterFunctions
parameter_list|(
name|Set
name|functions
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
block|{
name|URI
name|id
init|=
name|attribute
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|MODULE_CATEGORY_ATTRIBUTE
argument_list|)
condition|)
block|{
name|List
name|retain
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|retain
operator|.
name|add
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|retain
operator|.
name|add
argument_list|(
literal|"equals"
argument_list|)
expr_stmt|;
name|functions
operator|.
name|retainAll
argument_list|(
name|retain
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SOURCE_KEY_ATTRIBUTE
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|MODULE_NS_ATTRIBUTE
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SOURCE_TYPE_ATTRIBUTE
argument_list|)
condition|)
block|{
comment|//empty filter
block|}
block|}
specifier|public
name|boolean
name|getAllowedValues
parameter_list|(
name|Set
name|values
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
block|{
name|URI
name|id
init|=
name|attribute
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|MODULE_CATEGORY_ATTRIBUTE
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|MAIN_MODULE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|EXTERNAL_LIBRARY_MODULE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|INTERNAL_LIBRARY_MODULE
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SOURCE_KEY_ATTRIBUTE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SOURCE_TYPE_ATTRIBUTE
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|FILE_SOURCE_TYPE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|DB_SOURCE_TYPE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|CLASS_SOURCE_TYPE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|CLASSLOADER_SOURCE_TYPE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|URL_SOURCE_TYPE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|STRING_SOURCE_TYPE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|COCOON_SOURCE_TYPE
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/*if(id.equals(XACMLConstants.CLASS_ATTRIBUTE)) 		{ 			values.add("org.exist.xquery.functions.ModuleImpl"); 			addInternal(values, 1); 			return true; 		}*/
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|MODULE_NS_ATTRIBUTE
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
expr_stmt|;
name|addInternal
argument_list|(
name|values
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|true
return|;
block|}
comment|//TODO: because BrokerPool and thus Configuration are not
comment|//	available remotely, this is commented until a remote
comment|//	solution is written
comment|//index = 0 for namespaces, 1 for the class name
specifier|private
name|void
name|addInternal
parameter_list|(
name|Set
name|values
parameter_list|,
name|int
name|index
parameter_list|)
block|{
comment|/*String modules[][] = (String[][])config.getProperty("xquery.modules"); 		if(modules == null) 			return; 		for(int i = 0; i< modules.length; i++) 			values.add(modules[i][index]);*/
block|}
specifier|public
name|void
name|checkUserValue
parameter_list|(
name|AttributeValue
name|value
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
throws|throws
name|ParsingException
block|{
block|}
block|}
end_class

end_unit

