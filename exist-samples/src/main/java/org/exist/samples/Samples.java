begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|samples
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_class
specifier|public
class|class
name|Samples
block|{
specifier|public
specifier|static
specifier|final
name|Samples
name|SAMPLES
init|=
operator|new
name|Samples
argument_list|()
decl_stmt|;
specifier|private
name|Samples
parameter_list|()
block|{
block|}
comment|/**      * Gets the path of the Address Book sample.      *      * @return The path to the Address Book sample      */
specifier|public
annotation|@
name|Nullable
name|InputStream
name|getAddressBookSample
parameter_list|()
block|{
return|return
name|getSample
argument_list|(
literal|"validation/addressbook/addressbook.xsd"
argument_list|)
return|;
block|}
comment|/**      * Gets the path of the Shakespeare Hamlet sample.      *      * @return The path to the Shakespeare Hamlet sample      */
specifier|public
annotation|@
name|Nullable
name|InputStream
name|getHamletSample
parameter_list|()
block|{
return|return
name|getShakespeareSample
argument_list|(
literal|"hamlet.xml"
argument_list|)
return|;
block|}
comment|/**      * Gets the path of the Shakespeare Romeo and Juliet sample.      *      * @return The path to the Shakespeare Romeo and Juliet sample      */
specifier|public
annotation|@
name|Nullable
name|InputStream
name|getRomeoAndJulietSample
parameter_list|()
block|{
return|return
name|getShakespeareSample
argument_list|(
literal|"r_and_j.xml"
argument_list|)
return|;
block|}
comment|/**      * Gets the path of the Macbeth sample.      *      * @return The path to the Macbeth sample      */
specifier|public
annotation|@
name|Nullable
name|InputStream
name|getMacbethSample
parameter_list|()
block|{
return|return
name|getShakespeareSample
argument_list|(
literal|"macbeth.xml"
argument_list|)
return|;
block|}
comment|/**      * Get the names of just the Shakespeare XML data sample files.      *      * @return the names of the Shakespeare XML data files.      */
specifier|public
name|String
index|[]
name|getShakespeareXmlSampleNames
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"hamlet.xml"
block|,
literal|"macbeth.xml"
block|,
literal|"r_and_j.xml"
block|}
return|;
block|}
comment|/**      * Get the names of all the Shakespeare sample files.      *      * @return the names of all the Shakespeare sample files.      */
specifier|public
name|String
index|[]
name|getShakespeareSampleNames
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"collection.xconf"
block|,
literal|"hamlet.xml"
block|,
literal|"macbeth.xml"
block|,
literal|"play.dtd"
block|,
literal|"r_and_j.xml"
block|,
literal|"shakes.css"
block|,
literal|"shakes.xsl"
block|}
return|;
block|}
comment|/**      * Gets the path of the shakespeare sample.      *      * @param sampleFileName the name of the shakespeare sample.      *      * @return The path to the shakespeare sample      */
specifier|public
annotation|@
name|Nullable
name|InputStream
name|getShakespeareSample
parameter_list|(
specifier|final
name|String
name|sampleFileName
parameter_list|)
block|{
return|return
name|getSample
argument_list|(
literal|"shakespeare/"
operator|+
name|sampleFileName
argument_list|)
return|;
block|}
comment|/**      * Gets the path of the Bibliographic sample.      *      * @return The path to the Bibliographic sample      */
specifier|public
annotation|@
name|Nullable
name|InputStream
name|getBiblioSample
parameter_list|()
block|{
return|return
name|getSample
argument_list|(
literal|"biblio.rdf"
argument_list|)
return|;
block|}
comment|/**      * Gets the sample.      *      * @param sample relative path to the sample      *      * @return The stream to the sample      */
specifier|public
annotation|@
name|Nullable
name|InputStream
name|getSample
parameter_list|(
specifier|final
name|String
name|sample
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|sample
argument_list|)
return|;
block|}
comment|/**      * Gets the URL of the sample.      *      * @param sample relative path to the sample      *      * @return The url of the sample      */
specifier|public
annotation|@
name|Nullable
name|URL
name|getSampleUrl
parameter_list|(
specifier|final
name|String
name|sample
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|sample
argument_list|)
return|;
block|}
block|}
end_class

end_unit

