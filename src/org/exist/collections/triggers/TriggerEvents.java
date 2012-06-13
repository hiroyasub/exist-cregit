begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|TriggerEvents
block|{
comment|//3 bits?
specifier|public
specifier|static
name|short
name|CREATE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
name|short
name|UPDATE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
name|short
name|COPY
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
name|short
name|MOVE
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
name|short
name|DELETE
init|=
literal|5
decl_stmt|;
comment|//1 bit
specifier|public
specifier|static
name|short
name|BEFORE
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER
init|=
literal|1
decl_stmt|;
comment|//2 bits (because of metadata?)
specifier|public
specifier|static
name|short
name|COLLECTION
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
name|short
name|DOCUMENT
init|=
literal|10
decl_stmt|;
comment|// -1 (BEFORE) * 1 (CREATE) + 1 (COLLECTION) //TODO: bit it
specifier|public
specifier|static
name|short
name|BEFORE_CREATE_COLLECTION
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_CREATE_COLLECTION
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_UPDATE_COLLECTION
init|=
operator|-
literal|2
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_UPDATE_COLLECTION
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_COPY_COLLECTION
init|=
operator|-
literal|3
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_COPY_COLLECTION
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_MOVE_COLLECTION
init|=
operator|-
literal|4
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_MOVE_COLLECTION
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_DELETE_COLLECTION
init|=
operator|-
literal|5
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_DELETE_COLLECTION
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_CREATE_DOCUMENT
init|=
operator|-
literal|11
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_CREATE_DOCUMENT
init|=
literal|11
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_UPDATE_DOCUMENT
init|=
operator|-
literal|12
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_UPDATE_DOCUMENT
init|=
literal|12
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_COPY_DOCUMENT
init|=
operator|-
literal|13
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_COPY_DOCUMENT
init|=
literal|13
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_MOVE_DOCUMENT
init|=
operator|-
literal|14
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_MOVE_DOCUMENT
init|=
literal|14
decl_stmt|;
specifier|public
specifier|static
name|short
name|BEFORE_DELETE_DOCUMENT
init|=
operator|-
literal|15
decl_stmt|;
specifier|public
specifier|static
name|short
name|AFTER_DELETE_DOCUMENT
init|=
literal|15
decl_stmt|;
specifier|public
enum|enum
name|EVENTS
block|{
name|CREATE_COLLECTION
block|,
name|UPDATE_COLLECTION
block|,
name|COPY_COLLECTION
block|,
name|MOVE_COLLECTION
block|,
name|DELETE_COLLECTION
block|,
name|CREATE_DOCUMENT
block|,
name|UPDATE_DOCUMENT
block|,
name|COPY_DOCUMENT
block|,
name|MOVE_DOCUMENT
block|,
name|DELETE_DOCUMENT
block|}
specifier|private
specifier|final
specifier|static
name|String
name|EVENTS_STRING
index|[]
init|=
block|{
literal|"CREATE-COLLECTION"
block|,
literal|"UPDATE-COLLECTION"
block|,
literal|"COPY-COLLECTION"
block|,
literal|"MOVE-COLLECTION"
block|,
literal|"DELETE-COLLECTION"
block|,
literal|"CREATE-DOCUMENT"
block|,
literal|"UPDATE-DOCUMENT"
block|,
literal|"COPY-DOCUMENT"
block|,
literal|"MOVE-DOCUMENT"
block|,
literal|"DELETE-DOCUMENT"
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|EVENTS
name|_EVENTS_
index|[]
init|=
block|{
name|EVENTS
operator|.
name|CREATE_COLLECTION
block|,
name|EVENTS
operator|.
name|UPDATE_COLLECTION
block|,
name|EVENTS
operator|.
name|COPY_COLLECTION
block|,
name|EVENTS
operator|.
name|MOVE_COLLECTION
block|,
name|EVENTS
operator|.
name|DELETE_COLLECTION
block|,
name|EVENTS
operator|.
name|CREATE_DOCUMENT
block|,
name|EVENTS
operator|.
name|UPDATE_DOCUMENT
block|,
name|EVENTS
operator|.
name|COPY_DOCUMENT
block|,
name|EVENTS
operator|.
name|MOVE_DOCUMENT
block|,
name|EVENTS
operator|.
name|DELETE_DOCUMENT
block|}
decl_stmt|;
specifier|public
specifier|static
name|Set
argument_list|<
name|EVENTS
argument_list|>
name|convertFromString
parameter_list|(
name|String
name|events
parameter_list|)
throws|throws
name|TriggerException
block|{
name|Set
argument_list|<
name|EVENTS
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|EVENTS
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|events
argument_list|,
literal|", "
argument_list|)
decl_stmt|;
name|String
name|event
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|event
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|EVENTS_STRING
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
name|EVENTS_STRING
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|_EVENTS_
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
name|EVENTS_STRING
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Unknown event type '"
operator|+
name|event
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|OLD_EVENTS_STRING
index|[]
init|=
block|{
literal|"STORE"
block|,
literal|"UPDATE"
block|,
literal|"REMOVE"
block|, 	}
decl_stmt|;
specifier|public
specifier|static
name|Set
argument_list|<
name|EVENTS
argument_list|>
name|convertFromOldDesign
parameter_list|(
name|String
name|events
parameter_list|)
throws|throws
name|TriggerException
block|{
name|Set
argument_list|<
name|EVENTS
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|EVENTS
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|events
argument_list|,
literal|", "
argument_list|)
decl_stmt|;
name|String
name|event
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|event
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|OLD_EVENTS_STRING
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
name|OLD_EVENTS_STRING
index|[
name|i
index|]
argument_list|)
condition|)
block|{
break|break;
block|}
name|i
operator|++
expr_stmt|;
block|}
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
name|result
operator|.
name|add
argument_list|(
name|EVENTS
operator|.
name|CREATE_DOCUMENT
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|result
operator|.
name|add
argument_list|(
name|EVENTS
operator|.
name|UPDATE_DOCUMENT
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|result
operator|.
name|add
argument_list|(
name|EVENTS
operator|.
name|DELETE_DOCUMENT
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|//	        	throw new CollectionConfigurationException(
comment|//		    			"Unknown event type '" + event);
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

