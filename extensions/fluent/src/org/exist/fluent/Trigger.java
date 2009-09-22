begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

begin_comment
comment|/**  * An action being undertaken on the database, used to characterize an event.  * Note that for folders,<code>UPDATE</code> means rename.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_enum
specifier|public
enum|enum
name|Trigger
block|{
name|BEFORE_STORE
block|,
name|AFTER_STORE
block|,
name|BEFORE_CREATE
block|,
name|AFTER_CREATE
block|,
name|BEFORE_UPDATE
block|,
name|AFTER_UPDATE
block|,
name|BEFORE_RENAME
block|,
name|AFTER_RENAME
block|,
name|BEFORE_MOVE
block|,
name|AFTER_MOVE
block|,
name|BEFORE_REMOVE
block|,
name|AFTER_REMOVE
block|}
end_enum

end_unit

