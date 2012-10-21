begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2012 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_annotation_defn
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|FIELD
argument_list|)
specifier|public
annotation_defn|@interface
name|ConfigurationFieldSettings
block|{
specifier|public
specifier|final
specifier|static
name|String
name|OCTAL_STRING_KEY
init|=
literal|"octalString"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RADIX_KEY
init|=
literal|"radix"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|KEY_VALUE_SEP
init|=
literal|"="
decl_stmt|;
name|String
name|value
parameter_list|()
function_decl|;
specifier|public
enum|enum
name|SettingKey
block|{
name|OCTAL_STRING
argument_list|(
name|OCTAL_STRING_KEY
argument_list|)
block|,
name|RADIX
argument_list|(
name|RADIX_KEY
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
name|SettingKey
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|String
name|extractValueFromSettings
parameter_list|(
specifier|final
name|String
name|settings
parameter_list|)
block|{
return|return
name|settings
operator|.
name|substring
argument_list|(
name|getKey
argument_list|()
operator|.
name|length
argument_list|()
operator|+
name|KEY_VALUE_SEP
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|SettingKey
name|forSettings
parameter_list|(
specifier|final
name|String
name|settings
parameter_list|)
block|{
if|if
condition|(
name|settings
operator|.
name|contains
argument_list|(
name|KEY_VALUE_SEP
argument_list|)
condition|)
block|{
return|return
name|forKey
argument_list|(
name|settings
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|settings
operator|.
name|indexOf
argument_list|(
name|KEY_VALUE_SEP
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|forKey
argument_list|(
name|settings
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|SettingKey
name|forKey
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
for|for
control|(
specifier|final
name|SettingKey
name|settingKey
range|:
name|SettingKey
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|settingKey
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|settingKey
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No such Setting for key: "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
block|}
end_annotation_defn

end_unit

