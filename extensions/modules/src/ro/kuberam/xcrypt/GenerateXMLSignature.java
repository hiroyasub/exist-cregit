begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Java Cryptographic Extension  *  Copyright (C) 2010 Claudius Teodorescu at http://kuberam.ro  *  *  Released under LGPL License - http://gnu.org/licenses/lgpl.html.  *  */
end_comment

begin_package
package|package
name|ro
operator|.
name|kuberam
operator|.
name|xcrypt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|dsig
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|dsig
operator|.
name|dom
operator|.
name|DOMSignContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|dsig
operator|.
name|keyinfo
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|XMLStructure
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|dom
operator|.
name|DOMStructure
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|dsig
operator|.
name|spec
operator|.
name|C14NMethodParameterSpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|dsig
operator|.
name|spec
operator|.
name|TransformParameterSpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|crypto
operator|.
name|dsig
operator|.
name|spec
operator|.
name|XPathFilterParameterSpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpression
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
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
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DOMImplementation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|bootstrap
operator|.
name|DOMImplementationRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|ls
operator|.
name|DOMImplementationLS
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|ls
operator|.
name|LSSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_class
specifier|public
class|class
name|GenerateXMLSignature
block|{
specifier|public
specifier|static
name|String
name|GenerateDigitalSignature
parameter_list|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|inputDoc
parameter_list|,
name|String
name|canonicalizationAlgorithmURI
parameter_list|,
name|String
name|digestAlgorithmURI
parameter_list|,
name|String
name|signatureAlgorithmURI
parameter_list|,
name|String
name|keyPairAlgorithm
parameter_list|,
name|String
name|signatureNamespacePrefix
parameter_list|,
name|String
name|signatureType
parameter_list|,
specifier|final
name|String
name|xpathExprString
parameter_list|,
name|String
index|[]
name|certificateDetails
parameter_list|,
name|InputStream
name|keyStoreInputStream
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Create a DOM XMLSignatureFactory
name|String
name|providerName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jsr105Provider"
argument_list|,
literal|"org.jcp.xml.dsig.internal.dom.XMLDSigRI"
argument_list|)
decl_stmt|;
specifier|final
name|XMLSignatureFactory
name|sigFactory
init|=
name|XMLSignatureFactory
operator|.
name|getInstance
argument_list|(
literal|"DOM"
argument_list|)
decl_stmt|;
comment|// Create a Reference to the signed element
name|Node
name|sigParent
init|=
literal|null
decl_stmt|;
name|List
name|transforms
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|xpathExprString
operator|==
literal|null
condition|)
block|{
name|sigParent
operator|=
name|inputDoc
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
name|transforms
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|sigFactory
operator|.
name|newTransform
argument_list|(
name|Transform
operator|.
name|ENVELOPED
argument_list|,
operator|(
name|TransformParameterSpec
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XPathFactory
name|factory
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|XPath
name|xpath
init|=
name|factory
operator|.
name|newXPath
argument_list|()
decl_stmt|;
comment|// Find the node to be signed by PATH
name|XPathExpression
name|expr
init|=
name|xpath
operator|.
name|compile
argument_list|(
name|xpathExprString
argument_list|)
decl_stmt|;
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|expr
operator|.
name|evaluate
argument_list|(
name|inputDoc
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|getLength
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Can't find node by this XPath expression: "
operator|+
name|xpathExprString
argument_list|)
throw|;
block|}
comment|//Node nodeToSign = nodes.item(0);
comment|//sigParent = nodeToSign.getParentNode();
name|sigParent
operator|=
name|nodes
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|/*if ( signatureType.equals( "enveloped" ) ) {             sigParent = ( nodes.item(0) ).getParentNode();             }*/
name|transforms
operator|=
operator|new
name|ArrayList
argument_list|<
name|Transform
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
name|sigFactory
operator|.
name|newTransform
argument_list|(
name|Transform
operator|.
name|XPATH
argument_list|,
operator|new
name|XPathFilterParameterSpec
argument_list|(
name|xpathExprString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|sigFactory
operator|.
name|newTransform
argument_list|(
name|Transform
operator|.
name|ENVELOPED
argument_list|,
operator|(
name|TransformParameterSpec
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
name|Reference
name|ref
init|=
name|sigFactory
operator|.
name|newReference
argument_list|(
literal|""
argument_list|,
name|sigFactory
operator|.
name|newDigestMethod
argument_list|(
name|digestAlgorithmURI
argument_list|,
literal|null
argument_list|)
argument_list|,
name|transforms
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Create the SignedInfo
name|SignedInfo
name|si
init|=
name|sigFactory
operator|.
name|newSignedInfo
argument_list|(
name|sigFactory
operator|.
name|newCanonicalizationMethod
argument_list|(
name|canonicalizationAlgorithmURI
argument_list|,
operator|(
name|C14NMethodParameterSpec
operator|)
literal|null
argument_list|)
argument_list|,
name|sigFactory
operator|.
name|newSignatureMethod
argument_list|(
name|signatureAlgorithmURI
argument_list|,
literal|null
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|ref
argument_list|)
argument_list|)
decl_stmt|;
comment|//generate key pair
name|KeyInfo
name|ki
init|=
literal|null
decl_stmt|;
name|PrivateKey
name|privateKey
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|certificateDetails
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|KeyStore
name|keyStore
init|=
literal|null
decl_stmt|;
try|try
block|{
name|keyStore
operator|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
name|certificateDetails
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The keystore type '"
operator|+
name|certificateDetails
index|[
literal|0
index|]
operator|+
literal|"' is not supported!."
argument_list|)
throw|;
block|}
name|keyStore
operator|.
name|load
argument_list|(
name|keyStoreInputStream
argument_list|,
name|certificateDetails
index|[
literal|1
index|]
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|alias
init|=
name|certificateDetails
index|[
literal|2
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|keyStore
operator|.
name|containsAlias
argument_list|(
name|alias
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot find key for alias '"
operator|+
name|alias
operator|+
literal|"' in given keystore!."
argument_list|)
throw|;
block|}
name|privateKey
operator|=
operator|(
name|PrivateKey
operator|)
name|keyStore
operator|.
name|getKey
argument_list|(
name|alias
argument_list|,
name|certificateDetails
index|[
literal|3
index|]
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|X509Certificate
name|cert
init|=
operator|(
name|X509Certificate
operator|)
name|keyStore
operator|.
name|getCertificate
argument_list|(
name|alias
argument_list|)
decl_stmt|;
name|PublicKey
name|publicKey
init|=
name|cert
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|KeyInfoFactory
name|kif
init|=
name|sigFactory
operator|.
name|getKeyInfoFactory
argument_list|()
decl_stmt|;
name|Vector
argument_list|<
name|Object
argument_list|>
name|kiContent
init|=
operator|new
name|Vector
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|KeyValue
name|keyValue
init|=
name|kif
operator|.
name|newKeyValue
argument_list|(
name|publicKey
argument_list|)
decl_stmt|;
name|kiContent
operator|.
name|add
argument_list|(
name|keyValue
argument_list|)
expr_stmt|;
name|List
name|x509Content
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|X509IssuerSerial
name|issuer
init|=
name|kif
operator|.
name|newX509IssuerSerial
argument_list|(
name|cert
operator|.
name|getIssuerX500Principal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|cert
operator|.
name|getSerialNumber
argument_list|()
argument_list|)
decl_stmt|;
name|x509Content
operator|.
name|add
argument_list|(
name|cert
operator|.
name|getSubjectX500Principal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|x509Content
operator|.
name|add
argument_list|(
name|issuer
argument_list|)
expr_stmt|;
name|x509Content
operator|.
name|add
argument_list|(
name|cert
argument_list|)
expr_stmt|;
name|X509Data
name|x509Data
init|=
name|kif
operator|.
name|newX509Data
argument_list|(
name|x509Content
argument_list|)
decl_stmt|;
name|kiContent
operator|.
name|add
argument_list|(
name|x509Data
argument_list|)
expr_stmt|;
name|ki
operator|=
name|kif
operator|.
name|newKeyInfo
argument_list|(
name|kiContent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|KeyPairGenerator
name|kpg
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
name|keyPairAlgorithm
argument_list|)
decl_stmt|;
name|kpg
operator|.
name|initialize
argument_list|(
literal|512
argument_list|)
expr_stmt|;
name|KeyPair
name|kp
init|=
name|kpg
operator|.
name|generateKeyPair
argument_list|()
decl_stmt|;
name|KeyInfoFactory
name|kif
init|=
name|sigFactory
operator|.
name|getKeyInfoFactory
argument_list|()
decl_stmt|;
name|KeyValue
name|kv
init|=
name|kif
operator|.
name|newKeyValue
argument_list|(
name|kp
operator|.
name|getPublic
argument_list|()
argument_list|)
decl_stmt|;
name|ki
operator|=
name|kif
operator|.
name|newKeyInfo
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|kv
argument_list|)
argument_list|)
expr_stmt|;
name|privateKey
operator|=
name|kp
operator|.
name|getPrivate
argument_list|()
expr_stmt|;
block|}
comment|/*<element name="X509Data" type="ds:X509DataType"/><complexType name="X509DataType"><sequence maxOccurs="unbounded"><choice>                     SOLVED<element name="X509IssuerSerial" type="ds:X509IssuerSerialType"/><element name="X509SKI" type="base64Binary"/>                     SOLVED<element name="X509SubjectName" type="string"/>                     SOLVED<element name="X509Certificate" type="base64Binary"/><element name="X509CRL" type="base64Binary"/><any namespace="##other" processContents="lax"/></choice></sequence></complexType>>*/
comment|// Create a DOMSignContext and specify the location of the resulting XMLSignature's parent element
name|DOMSignContext
name|dsc
init|=
literal|null
decl_stmt|;
name|XMLSignature
name|signature
init|=
literal|null
decl_stmt|;
name|Document
name|signatureDoc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|signatureType
operator|.
name|equals
argument_list|(
literal|"enveloped"
argument_list|)
condition|)
block|{
name|dsc
operator|=
operator|new
name|DOMSignContext
argument_list|(
name|privateKey
argument_list|,
name|sigParent
argument_list|)
expr_stmt|;
name|signature
operator|=
name|sigFactory
operator|.
name|newXMLSignature
argument_list|(
name|si
argument_list|,
name|ki
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|signatureType
operator|.
name|equals
argument_list|(
literal|"detached"
argument_list|)
condition|)
block|{
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|sigParent
operator|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|newDocument
argument_list|()
expr_stmt|;
name|dsc
operator|=
operator|new
name|DOMSignContext
argument_list|(
name|privateKey
argument_list|,
name|sigParent
argument_list|)
expr_stmt|;
name|signature
operator|=
name|sigFactory
operator|.
name|newXMLSignature
argument_list|(
name|si
argument_list|,
name|ki
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|signatureType
operator|.
name|equals
argument_list|(
literal|"enveloping"
argument_list|)
condition|)
block|{
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|signatureDoc
operator|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|newDocument
argument_list|()
expr_stmt|;
name|XMLStructure
name|content
init|=
operator|new
name|DOMStructure
argument_list|(
name|sigParent
argument_list|)
decl_stmt|;
name|XMLObject
name|xmlobj
init|=
name|sigFactory
operator|.
name|newXMLObject
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|content
argument_list|)
argument_list|,
literal|"object"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|dsc
operator|=
operator|new
name|DOMSignContext
argument_list|(
name|privateKey
argument_list|,
name|signatureDoc
argument_list|)
expr_stmt|;
name|signature
operator|=
name|sigFactory
operator|.
name|newXMLSignature
argument_list|(
name|si
argument_list|,
name|ki
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|xmlobj
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|dsc
operator|.
name|setDefaultNamespacePrefix
argument_list|(
name|signatureNamespacePrefix
argument_list|)
expr_stmt|;
comment|// Marshal, generate and sign
name|signature
operator|.
name|sign
argument_list|(
name|dsc
argument_list|)
expr_stmt|;
name|DOMImplementationRegistry
name|registry
init|=
name|DOMImplementationRegistry
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DOMImplementationLS
name|impl
init|=
operator|(
name|DOMImplementationLS
operator|)
name|registry
operator|.
name|getDOMImplementation
argument_list|(
literal|"LS"
argument_list|)
decl_stmt|;
name|LSSerializer
name|serializer
init|=
name|impl
operator|.
name|createLSSerializer
argument_list|()
decl_stmt|;
if|if
condition|(
name|signatureType
operator|.
name|equals
argument_list|(
literal|"enveloping"
argument_list|)
condition|)
block|{
return|return
name|serializer
operator|.
name|writeToString
argument_list|(
name|signatureDoc
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|serializer
operator|.
name|writeToString
argument_list|(
name|sigParent
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|Exception
block|{
name|String
name|docString
init|=
literal|"<data><a xml:id=\"type\"><b>23</b><c><d/></c></a></data>"
decl_stmt|;
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Document
name|inputDoc
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|docString
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|certificateDetails
init|=
operator|new
name|String
index|[
literal|5
index|]
decl_stmt|;
name|certificateDetails
index|[
literal|0
index|]
operator|=
literal|"JKS"
expr_stmt|;
name|certificateDetails
index|[
literal|1
index|]
operator|=
literal|"ab987c"
expr_stmt|;
name|certificateDetails
index|[
literal|2
index|]
operator|=
literal|"eXist"
expr_stmt|;
name|certificateDetails
index|[
literal|3
index|]
operator|=
literal|"kpi135"
expr_stmt|;
name|String
name|domString
init|=
name|GenerateDigitalSignature
argument_list|(
name|inputDoc
argument_list|,
name|CanonicalizationMethod
operator|.
name|EXCLUSIVE
argument_list|,
name|DigestMethod
operator|.
name|SHA1
argument_list|,
name|SignatureMethod
operator|.
name|DSA_SHA1
argument_list|,
literal|"DSA"
argument_list|,
literal|"ds"
argument_list|,
literal|"enveloped"
argument_list|,
literal|"//b"
argument_list|,
name|certificateDetails
argument_list|,
operator|new
name|FileInputStream
argument_list|(
literal|"/home/claudius/mykeystoreEXist"
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|domString
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

