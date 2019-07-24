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
name|scheduler
package|;
end_package

begin_comment
comment|/**  * Interface defined requirements for a Scheduleable job.  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|JobDescription
block|{
name|String
name|EXIST_INTERNAL_GROUP
init|=
literal|"eXist.internal"
decl_stmt|;
name|String
name|DATABASE
init|=
literal|"database"
decl_stmt|;
name|String
name|SYSTEM_TASK
init|=
literal|"systemtask"
decl_stmt|;
name|String
name|XQUERY_SOURCE
init|=
literal|"xqueryresource"
decl_stmt|;
name|String
name|ACCOUNT
init|=
literal|"account"
decl_stmt|;
name|String
name|PARAMS
init|=
literal|"params"
decl_stmt|;
name|String
name|UNSCHEDULE
init|=
literal|"unschedule"
decl_stmt|;
comment|/**      * Get the name of the job.      *      * @return  The job's name      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Set the name of the job.      *      * @param  name  The job's new name      */
name|void
name|setName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Get the name group for the job.      *      * @return  The job's group name      */
name|String
name|getGroup
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

