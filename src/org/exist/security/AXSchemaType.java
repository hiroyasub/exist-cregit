begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_enum
specifier|public
enum|enum
name|AXSchemaType
block|{
name|FIRTSNAME
argument_list|(
literal|"http://axschema.org/namePerson/first"
argument_list|)
block|,
name|LASTNAME
argument_list|(
literal|"http://axschema.org/namePerson/last"
argument_list|)
block|,
name|FULLNAME
argument_list|(
literal|"http://axschema.org/namePerson"
argument_list|)
block|,
name|EMAIL
argument_list|(
literal|"http://axschema.org/contact/email"
argument_list|)
block|,
name|COUNTRY
argument_list|(
literal|"http://axschema.org/contact/country/home"
argument_list|)
block|,
name|LANGUAGE
argument_list|(
literal|"http://axschema.org/pref/language"
argument_list|)
block|,
name|TIMEZONE
argument_list|(
literal|"http://axschema.org/pref/timezone"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|namespace
decl_stmt|;
name|AXSchemaType
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
name|this
operator|.
name|namespace
operator|=
name|namespace
expr_stmt|;
block|}
specifier|public
specifier|static
name|AXSchemaType
name|valueOfNamespace
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
for|for
control|(
name|AXSchemaType
name|axSchemaType
range|:
name|AXSchemaType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|axSchemaType
operator|.
name|getNamespace
argument_list|()
operator|.
name|equals
argument_list|(
name|namespace
argument_list|)
condition|)
block|{
return|return
name|axSchemaType
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getNamespace
parameter_list|()
block|{
return|return
name|namespace
return|;
block|}
block|}
end_enum

end_unit

