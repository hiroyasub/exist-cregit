begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|UserAttributes
block|{
specifier|public
specifier|static
name|String
name|_FIRTSNAME
init|=
literal|"FirstName"
decl_stmt|;
specifier|public
specifier|static
name|String
name|_LASTNAME
init|=
literal|"LastName"
decl_stmt|;
specifier|public
specifier|static
name|String
name|_FULLNAME
init|=
literal|"FullName"
decl_stmt|;
specifier|public
specifier|static
name|String
name|_EMAIL
init|=
literal|"Email"
decl_stmt|;
specifier|public
specifier|static
name|String
name|_COUNTRY
init|=
literal|"Country"
decl_stmt|;
specifier|public
specifier|static
name|String
name|_LANGUAGE
init|=
literal|"Language"
decl_stmt|;
specifier|public
specifier|static
name|String
name|FIRTSNAME
init|=
literal|"http://axschema.org/namePerson/first"
decl_stmt|;
specifier|public
specifier|static
name|String
name|LASTNAME
init|=
literal|"http://axschema.org/namePerson/last"
decl_stmt|;
specifier|public
specifier|static
name|String
name|FULLNAME
init|=
literal|"http://axschema.org/namePerson"
decl_stmt|;
specifier|public
specifier|static
name|String
name|EMAIL
init|=
literal|"http://axschema.org/contact/email"
decl_stmt|;
specifier|public
specifier|static
name|String
name|COUNTRY
init|=
literal|"http://axschema.org/contact/country/home"
decl_stmt|;
specifier|public
specifier|static
name|String
name|LANGUAGE
init|=
literal|"http://axschema.org/pref/language"
decl_stmt|;
comment|//alias -> axschema url
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|alias
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|addAlias
argument_list|(
name|_FIRTSNAME
argument_list|,
name|FIRTSNAME
argument_list|)
expr_stmt|;
name|addAlias
argument_list|(
name|_LASTNAME
argument_list|,
name|LASTNAME
argument_list|)
expr_stmt|;
name|addAlias
argument_list|(
name|_FULLNAME
argument_list|,
name|FULLNAME
argument_list|)
expr_stmt|;
name|addAlias
argument_list|(
name|_EMAIL
argument_list|,
name|EMAIL
argument_list|)
expr_stmt|;
name|addAlias
argument_list|(
name|_COUNTRY
argument_list|,
name|COUNTRY
argument_list|)
expr_stmt|;
name|addAlias
argument_list|(
name|_LANGUAGE
argument_list|,
name|LANGUAGE
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
specifier|private
specifier|static
name|void
name|addAlias
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|alias
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|alias
operator|.
name|put
argument_list|(
name|key
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

