begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|NameValuePair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|methods
operator|.
name|PostMethod
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|HttpSession
implements|implements
name|Runnable
block|{
specifier|private
name|String
name|url
decl_stmt|;
specifier|protected
name|HttpSession
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Runnable#run() 	 */
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|//		HttpState initialState = new HttpState();
comment|//
comment|//		Cookie mycookie = new Cookie(".exist-db.org", "XDEBUG_SESSION", "default", "/", null, false);
comment|//
comment|//		initialState.addCookie(mycookie);
name|HttpClient
name|client
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
comment|//		client.setState(initialState);
name|PostMethod
name|method
init|=
operator|new
name|PostMethod
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|NameValuePair
index|[]
name|postData
init|=
operator|new
name|NameValuePair
index|[
literal|1
index|]
decl_stmt|;
name|postData
index|[
literal|0
index|]
operator|=
operator|new
name|NameValuePair
argument_list|(
literal|"XDEBUG_SESSION"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|method
operator|.
name|addParameters
argument_list|(
name|postData
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending http request with debugging flag"
argument_list|)
expr_stmt|;
name|client
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"get http response"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
block|}
block|}
end_class

end_unit

