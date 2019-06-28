begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * Utility class for converting to/from SQL types and escaping XML text and attributes.  *   * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  * @author<a href="mailto:robert.walpole@metoffice.gov.uk">Robert Walpole</a>  * @serial 2010-07-23  * @version 1.0  *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SQLUtils
block|{
specifier|public
specifier|static
name|int
name|sqlTypeFromString
parameter_list|(
name|String
name|sqlType
parameter_list|)
block|{
name|sqlType
operator|=
name|sqlType
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|sqlType
condition|)
block|{
case|case
literal|"ARRAY"
case|:
return|return
operator|(
name|Types
operator|.
name|ARRAY
operator|)
return|;
case|case
literal|"BIGINT"
case|:
return|return
operator|(
name|Types
operator|.
name|BIGINT
operator|)
return|;
case|case
literal|"BINARY"
case|:
return|return
operator|(
name|Types
operator|.
name|BINARY
operator|)
return|;
case|case
literal|"BIT"
case|:
return|return
operator|(
name|Types
operator|.
name|BIT
operator|)
return|;
case|case
literal|"BLOB"
case|:
return|return
operator|(
name|Types
operator|.
name|BLOB
operator|)
return|;
case|case
literal|"BOOLEAN"
case|:
return|return
operator|(
name|Types
operator|.
name|BOOLEAN
operator|)
return|;
case|case
literal|"CHAR"
case|:
return|return
operator|(
name|Types
operator|.
name|CHAR
operator|)
return|;
case|case
literal|"CLOB"
case|:
return|return
operator|(
name|Types
operator|.
name|CLOB
operator|)
return|;
case|case
literal|"DECIMAL"
case|:
return|return
operator|(
name|Types
operator|.
name|DECIMAL
operator|)
return|;
case|case
literal|"DOUBLE"
case|:
return|return
operator|(
name|Types
operator|.
name|DOUBLE
operator|)
return|;
case|case
literal|"FLOAT"
case|:
return|return
operator|(
name|Types
operator|.
name|FLOAT
operator|)
return|;
case|case
literal|"LONGVARCHAR"
case|:
return|return
operator|(
name|Types
operator|.
name|LONGVARCHAR
operator|)
return|;
case|case
literal|"NUMERIC"
case|:
return|return
operator|(
name|Types
operator|.
name|NUMERIC
operator|)
return|;
case|case
literal|"SMALLINT"
case|:
return|return
operator|(
name|Types
operator|.
name|SMALLINT
operator|)
return|;
case|case
literal|"TINYINT"
case|:
return|return
operator|(
name|Types
operator|.
name|TINYINT
operator|)
return|;
case|case
literal|"INTEGER"
case|:
return|return
operator|(
name|Types
operator|.
name|INTEGER
operator|)
return|;
case|case
literal|"VARCHAR"
case|:
return|return
operator|(
name|Types
operator|.
name|VARCHAR
operator|)
return|;
case|case
literal|"SQLXML"
case|:
return|return
name|Types
operator|.
name|SQLXML
return|;
case|case
literal|"TIMESTAMP"
case|:
return|return
name|Types
operator|.
name|TIMESTAMP
return|;
default|default:
return|return
operator|(
name|Types
operator|.
name|VARCHAR
operator|)
return|;
comment|//default
block|}
block|}
comment|/**      * Converts a SQL data type to an XML data type.      *      * @param   sqlType  The SQL data type as specified by JDBC      *      * @return  The XML Type as specified by eXist      */
specifier|public
specifier|static
name|int
name|sqlTypeToXMLType
parameter_list|(
name|int
name|sqlType
parameter_list|)
block|{
switch|switch
condition|(
name|sqlType
condition|)
block|{
case|case
name|Types
operator|.
name|ARRAY
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|NODE
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|BIGINT
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|INT
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|BINARY
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|BASE64_BINARY
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|BIT
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|INT
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|BLOB
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|BASE64_BINARY
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|BOOLEAN
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|BOOLEAN
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|CHAR
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|STRING
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|CLOB
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|STRING
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|DECIMAL
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|DECIMAL
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|DOUBLE
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|DOUBLE
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|FLOAT
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|FLOAT
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|LONGVARCHAR
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|STRING
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|NUMERIC
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|NUMBER
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|SMALLINT
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|INT
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|TINYINT
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|INT
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|INTEGER
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|INTEGER
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|VARCHAR
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|STRING
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|SQLXML
case|:
block|{
return|return
operator|(
name|Type
operator|.
name|NODE
operator|)
return|;
block|}
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
block|{
return|return
name|Type
operator|.
name|DATE_TIME
return|;
block|}
default|default:
block|{
return|return
operator|(
name|Type
operator|.
name|ANY_TYPE
operator|)
return|;
block|}
block|}
block|}
specifier|public
specifier|static
name|String
name|escapeXmlText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|String
name|work
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|work
operator|=
name|text
operator|.
name|replaceAll
argument_list|(
literal|"\\&"
argument_list|,
literal|"\\&amp;"
argument_list|)
expr_stmt|;
name|work
operator|=
name|work
operator|.
name|replaceAll
argument_list|(
literal|"<"
argument_list|,
literal|"\\&lt;"
argument_list|)
expr_stmt|;
name|work
operator|=
name|work
operator|.
name|replaceAll
argument_list|(
literal|">"
argument_list|,
literal|"\\&gt;"
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|work
operator|)
return|;
block|}
specifier|public
specifier|static
name|String
name|escapeXmlAttr
parameter_list|(
name|String
name|attr
parameter_list|)
block|{
name|String
name|work
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|attr
operator|!=
literal|null
condition|)
block|{
name|work
operator|=
name|escapeXmlText
argument_list|(
name|attr
argument_list|)
expr_stmt|;
name|work
operator|=
name|work
operator|.
name|replaceAll
argument_list|(
literal|"'"
argument_list|,
literal|"\\&apos;"
argument_list|)
expr_stmt|;
name|work
operator|=
name|work
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\\&quot;"
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|work
operator|)
return|;
block|}
block|}
end_class

end_unit

