begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2006 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
comment|/**  * Class to represent a User's Job  * Should be extended by all classes wishing to  * schedule as a Job that perform user defined functions  *   * Classes extending UserJob may have multiple  * instances executing within the scheduler at once  *   * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|UserJob
implements|implements
name|JobDescription
implements|,
name|org
operator|.
name|quartz
operator|.
name|Job
block|{
specifier|public
specifier|final
specifier|static
name|String
name|JOB_GROUP
init|=
literal|"eXist.User"
decl_stmt|;
specifier|public
specifier|final
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|JOB_GROUP
return|;
block|}
block|}
end_class

end_unit

