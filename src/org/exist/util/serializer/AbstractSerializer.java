begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
operator|.
name|EXistOutputKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|json
operator|.
name|JSONWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Common base for {@link org.exist.util.serializer.SAXSerializer} and {@link org.exist.util.serializer.DOMSerializer}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSerializer
block|{
specifier|protected
specifier|final
specifier|static
name|int
name|XML_WRITER
init|=
literal|0
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|XHTML_WRITER
init|=
literal|1
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|TEXT_WRITER
init|=
literal|2
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|JSON_WRITER
init|=
literal|3
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|XHTML5_WRITER
init|=
literal|4
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|MICRO_XML_WRITER
init|=
literal|5
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|HTML5_WRITER
init|=
literal|6
decl_stmt|;
specifier|protected
name|XMLWriter
name|writers
index|[]
init|=
block|{
operator|new
name|IndentingXMLWriter
argument_list|()
block|,
operator|new
name|XHTMLWriter
argument_list|()
block|,
operator|new
name|TEXTWriter
argument_list|()
block|,
operator|new
name|JSONWriter
argument_list|()
block|,
operator|new
name|XHTML5Writer
argument_list|()
block|,
operator|new
name|MicroXmlWriter
argument_list|()
block|,
operator|new
name|HTML5Writer
argument_list|()
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Properties
name|outputProperties
decl_stmt|;
specifier|protected
name|XMLWriter
name|receiver
decl_stmt|;
specifier|public
name|AbstractSerializer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|receiver
operator|=
name|getDefaultWriter
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|XMLWriter
name|getDefaultWriter
parameter_list|()
block|{
return|return
name|writers
index|[
name|XML_WRITER
index|]
return|;
block|}
specifier|public
name|void
name|setOutput
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|Properties
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|outputProperties
operator|=
name|defaultProperties
expr_stmt|;
block|}
else|else
block|{
name|outputProperties
operator|=
name|properties
expr_stmt|;
block|}
specifier|final
name|String
name|method
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|htmlVersionProp
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|HTML_VERSION
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|double
name|htmlVersion
decl_stmt|;
try|try
block|{
name|htmlVersion
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|htmlVersionProp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|htmlVersion
operator|=
literal|1.0
expr_stmt|;
block|}
if|if
condition|(
literal|"xhtml"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
block|{
if|if
condition|(
name|htmlVersion
operator|<
literal|5.0
condition|)
block|{
name|receiver
operator|=
name|writers
index|[
name|XHTML_WRITER
index|]
expr_stmt|;
block|}
else|else
block|{
name|receiver
operator|=
name|writers
index|[
name|XHTML5_WRITER
index|]
expr_stmt|;
block|}
block|}
if|else if
condition|(
literal|"html"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
if|if
condition|(
name|htmlVersion
operator|<
literal|5.0
condition|)
block|{
name|receiver
operator|=
name|writers
index|[
name|XHTML_WRITER
index|]
expr_stmt|;
block|}
else|else
block|{
name|receiver
operator|=
name|writers
index|[
name|HTML5_WRITER
index|]
expr_stmt|;
block|}
block|}
if|else if
condition|(
literal|"text"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|receiver
operator|=
name|writers
index|[
name|TEXT_WRITER
index|]
expr_stmt|;
block|}
if|else if
condition|(
literal|"json"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|receiver
operator|=
name|writers
index|[
name|JSON_WRITER
index|]
expr_stmt|;
block|}
if|else if
condition|(
literal|"xhtml5"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|receiver
operator|=
name|writers
index|[
name|XHTML5_WRITER
index|]
expr_stmt|;
block|}
if|else if
condition|(
literal|"html5"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|receiver
operator|=
name|writers
index|[
name|HTML5_WRITER
index|]
expr_stmt|;
block|}
if|else if
condition|(
literal|"microxml"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|receiver
operator|=
name|writers
index|[
name|MICRO_XML_WRITER
index|]
expr_stmt|;
block|}
else|else
block|{
name|receiver
operator|=
name|writers
index|[
name|XML_WRITER
index|]
expr_stmt|;
block|}
name|receiver
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|setOutputProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|writers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writers
index|[
name|i
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

