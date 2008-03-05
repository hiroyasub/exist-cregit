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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  * An actual or virtual name for a document, augmented with instructions for processing  * in case of duplication.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Name
block|{
specifier|protected
specifier|static
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|givenName
decl_stmt|,
name|oldName
decl_stmt|;
specifier|protected
name|Collection
name|context
decl_stmt|;
specifier|private
name|Name
parameter_list|()
block|{
block|}
name|Folder
name|stripPathPrefix
parameter_list|(
name|Folder
name|base
parameter_list|)
block|{
return|return
name|base
return|;
block|}
specifier|private
specifier|static
specifier|abstract
class|class
name|SpecifiedName
extends|extends
name|Name
block|{
specifier|protected
name|String
name|specifiedName
decl_stmt|;
name|SpecifiedName
parameter_list|(
name|String
name|specifiedName
parameter_list|)
block|{
name|this
operator|.
name|specifiedName
operator|=
name|specifiedName
expr_stmt|;
block|}
name|Folder
name|stripPathPrefix
parameter_list|(
name|Folder
name|base
parameter_list|)
block|{
name|int
name|k
init|=
name|specifiedName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|base
return|;
block|}
else|else
block|{
name|Folder
name|target
init|=
name|base
operator|.
name|children
argument_list|()
operator|.
name|create
argument_list|(
name|specifiedName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
argument_list|)
decl_stmt|;
name|specifiedName
operator|=
name|specifiedName
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|target
return|;
block|}
block|}
block|}
name|void
name|setOldName
parameter_list|(
name|String
name|oldName
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|oldName
operator|==
literal|null
assert|;
name|this
operator|.
name|oldName
operator|=
name|oldName
expr_stmt|;
block|}
name|void
name|setContext
parameter_list|(
name|Collection
name|context
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|context
operator|==
literal|null
assert|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/** 	 * Get the computed value of this name.  Once computed, the value is cached. 	 * 	 * @return the value of this name 	 */
specifier|public
name|String
name|get
parameter_list|()
block|{
if|if
condition|(
name|givenName
operator|==
literal|null
condition|)
name|eval
argument_list|()
expr_stmt|;
return|return
name|givenName
return|;
block|}
specifier|protected
specifier|abstract
name|void
name|eval
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|String
name|def
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|givenName
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|givenName
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|def
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|boolean
name|existsInContext
parameter_list|(
name|String
name|proposedName
parameter_list|)
block|{
if|if
condition|(
name|proposedName
operator|==
literal|null
operator|||
name|proposedName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name null or empty"
argument_list|)
throw|;
name|XmldbURI
name|proposedUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|proposedName
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|hasDocument
argument_list|(
name|proposedUri
argument_list|)
operator|||
name|context
operator|.
name|hasSubcollection
argument_list|(
name|proposedUri
argument_list|)
return|;
block|}
specifier|protected
name|void
name|evalInsert
parameter_list|(
name|String
name|proposedName
parameter_list|)
block|{
if|if
condition|(
name|existsInContext
argument_list|(
name|proposedName
argument_list|)
condition|)
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"entry with name "
operator|+
name|proposedName
operator|+
literal|" already exists in destination"
argument_list|)
throw|;
name|givenName
operator|=
name|proposedName
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|Pattern
name|NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(.*)($[0-9a-z]+)?(\\..+)?"
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|evalDeconflict
parameter_list|(
name|String
name|proposedName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|existsInContext
argument_list|(
name|proposedName
argument_list|)
condition|)
block|{
name|givenName
operator|=
name|proposedName
expr_stmt|;
return|return;
block|}
name|Matcher
name|matcher
init|=
name|NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|proposedName
argument_list|)
decl_stmt|;
name|boolean
name|matchResult
init|=
name|matcher
operator|.
name|matches
argument_list|()
decl_stmt|;
assert|assert
name|matchResult
assert|;
name|String
name|baseName
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|baseName
operator|+=
literal|"$"
expr_stmt|;
name|String
name|suffix
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffix
operator|==
literal|null
condition|)
name|suffix
operator|=
literal|""
expr_stmt|;
name|evalGenerate
argument_list|(
name|baseName
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|evalGenerate
parameter_list|(
name|String
name|baseName
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
synchronized|synchronized
init|(
name|rand
init|)
block|{
do|do
block|{
name|givenName
operator|=
name|baseName
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|+
name|suffix
expr_stmt|;
block|}
do|while
condition|(
name|existsInContext
argument_list|(
name|givenName
argument_list|)
condition|)
do|;
block|}
block|}
comment|/** 	 * Generate a random name that will not conflict with anything else in the destination folder. 	 * 	 * @return a random name unique within the target folder 	 */
specifier|public
specifier|static
name|Name
name|generate
parameter_list|()
block|{
return|return
name|generate
argument_list|(
literal|""
argument_list|)
return|;
block|}
comment|/** 	 * Generate a random name with the given suffix that will not conflict with anything else in  	 * the destination folder. 	 * 	 * @param suffix the string to append to the random name, e.g. ".xml" 	 * @return a random name unique within the target folder and ending with the given suffix 	 */
specifier|public
specifier|static
name|Name
name|generate
parameter_list|(
specifier|final
name|String
name|suffix
parameter_list|)
block|{
return|return
operator|new
name|Name
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|eval
parameter_list|()
block|{
name|evalGenerate
argument_list|(
literal|""
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|def
parameter_list|()
block|{
return|return
literal|"generate"
operator|+
operator|(
name|suffix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|""
else|:
literal|" "
operator|+
name|suffix
operator|)
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * Keep the existing name of the source item if it is unique in the destination folder, otherwise 	 * adjust it as per the rules of {@link #adjust(String)}. 	 * 	 * @return the existing name if unique, otherwise a unique variation of the existing name 	 */
specifier|public
specifier|static
name|Name
name|keepAdjust
parameter_list|()
block|{
return|return
operator|new
name|Name
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|eval
parameter_list|()
block|{
name|evalDeconflict
argument_list|(
name|oldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|def
parameter_list|()
block|{
return|return
literal|"adjust "
operator|+
operator|(
name|oldName
operator|==
literal|null
condition|?
literal|"old name"
else|:
name|oldName
operator|)
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * Try to use the given name but, if it conflicts with anything in the destination folder, add 	 * a random component that will make it unique.  If a random component needs to be added, 	 * it is inserted between the main part of the name and its dotted suffix, separated by 	 * a '$' sign.  (If the name has no suffix, the random component is simply appended to the 	 * name.)  If the given name already has a random component in the format described (perhaps 	 * resulting from a previous 'adjustment'), it is first removed before a new one is selected. 	 * 	 * @param name the desired name 	 * @return if the given name is unique, the name; otherwise, a unique variation on the given name 	 */
specifier|public
specifier|static
name|Name
name|adjust
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SpecifiedName
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|eval
parameter_list|()
block|{
name|evalDeconflict
argument_list|(
name|specifiedName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|def
parameter_list|()
block|{
return|return
literal|"adjust "
operator|+
name|specifiedName
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * Keep the existing name of the source item, overwriting any document with the same name 	 * in the destination folder as per the rules for {@link #overwrite(String)}. 	 * 	 * @return the existing name that will be used whether it's unique or not 	 */
specifier|public
specifier|static
name|Name
name|keepOverwrite
parameter_list|()
block|{
return|return
operator|new
name|Name
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|eval
parameter_list|()
block|{
name|givenName
operator|=
name|oldName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|def
parameter_list|()
block|{
return|return
literal|"overwrite "
operator|+
operator|(
name|oldName
operator|==
literal|null
condition|?
literal|"old name"
else|:
name|oldName
operator|)
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * Use the given name whether it is unique or not.  If the name is already used for another 	 * document in the destination folder, that document will be overwritten.  If the name is already 	 * used for a child folder of the destination folder, the folder will not be affected and the 	 * operation will throw an exception. 	 * 	 * @param name the desired name 	 * @return the desired name that will be used whether it's unique or not 	 */
specifier|public
specifier|static
name|Name
name|overwrite
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SpecifiedName
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|eval
parameter_list|()
block|{
name|givenName
operator|=
name|specifiedName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|def
parameter_list|()
block|{
return|return
literal|"overwrite "
operator|+
name|specifiedName
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * Keep the existing name of the source item, believed to be unique in the destination folder. 	 * The name follows the rules given in {@link #create(String)}. 	 * 	 * @return the existing name, with a stipulation that any operation using it will fail if it's a duplicate 	 */
specifier|public
specifier|static
name|Name
name|keepCreate
parameter_list|()
block|{
return|return
operator|new
name|Name
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|eval
parameter_list|()
block|{
name|evalInsert
argument_list|(
name|oldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|def
parameter_list|()
block|{
return|return
literal|"create "
operator|+
operator|(
name|oldName
operator|==
literal|null
condition|?
literal|"old name"
else|:
name|oldName
operator|)
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * Use the given name that is believed to be unique.  If the given name is already used in the 	 * destination folder, the existing owner of the name will not be affected and the operation 	 * will throw an exception. 	 * 	 * @param name the desired name believed to be unique 	 * @return the desired name, with a stipulation that any operation using it will fail if it's a duplicate 	 */
specifier|public
specifier|static
name|Name
name|create
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SpecifiedName
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|eval
parameter_list|()
block|{
name|evalInsert
argument_list|(
name|specifiedName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|def
parameter_list|()
block|{
return|return
literal|"create "
operator|+
name|specifiedName
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

