

### EPCtagCoder

An extremely intuitive, small and ultra fast EPC encoding and decoding library for java. 

### Download binary release
[EPCtagCoder v0.0.7](https://github.com/jlcout/epctagcoder/releases)

### Features

- Implemented in accordance with [EPC Tag Data Standard 1.9](http://www.gs1.org/epc/tag-data-standard)
- Easy to understand, developed with step builder pattern
- Small library, only 50kb
- Ultra fast, encode / decode 10.000 EPC on 200 milliseconds


### Epc implementations:

- SGTIN - _Serialized Global Trade Item Number_
- SSCC  - _Serial Shipping Container Code_
- SGLN  - _Global Location Number With or Without Extension_
- GRAI  - _Global Returnable Asset Identifier_
- GIAI  - _Global Individual Asset Identifier_
- GSRN  - _Global Service Relation Number – Recipient_
- GSRNP - _Global Service Relation Number – Provider_
- GDTI  - _Global Document Type Identifier_
- CPI   - _Component / Part Identifier_
- SGCN  - _Serialized Global Coupon Number_


### Examples

```markdown
ParseSSCC parseSSCC = ParseSSCC.Builder()
		.withCompanyPrefix("023356789")
		.withExtensionDigit( SSCCExtensionDigit.EXTENSION_3 )
		.withSerial("0200002")
		.withTagSize( SSCCTagSize.BITS_96 )
		.withFilterValue( SSCCFilterValue.RESERVED_5 )
		.build();
SSCC sscc = parseSSCC.getSSCC();
System.out.println("parseSSCC  "+ sscc.toString() );




ParseSSCC parseSSCC = ParseSSCC.Builder()
		   .withRFIDTag( "31AC16465751CCD0C2000000" )
		   .build();
SSCC sscc = parseSSCC.getSSCC();
System.out.println("parseSSCC  "+ sscc.toString() );




ParseSSCC parseSSCC = ParseSSCC.Builder()
		   .withEPCTagURI( "urn:epc:tag:sscc-96:5.023356789.30200002" )
		   .build();
SSCC sscc = parseSSCC.getSSCC();
System.out.println("parseSSCC  "+ sscc.toString() );




ParseSSCC parseSSCC = ParseSSCC.Builder()
		   .withEPCPureIdentityURI( "urn:epc:id:sscc:023356789.30200002" )
		   .withTagSize( SSCCTagSize.BITS_96 )
		   .withFilterValue( SSCCFilterValue.RESERVED_5 )
		   .build();
SSCC sscc = parseSSCC.getSSCC();
System.out.println("parseSSCC  "+ sscc.toString() );

```

### Donate

If EPCtagCoder helped you. Consider making a donation

<form action="https://www.paypal.com/donate" method="post" target="_top">
<input type="hidden" name="hosted_button_id" value="HC57PQV9TXCAC" />
<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif" border="0" name="submit" title="PayPal - The safer, easier way to pay online!" alt="Donate with PayPal button" />
<img alt="" border="0" src="https://www.paypal.com/en_BR/i/scr/pixel.gif" width="1" height="1" />
</form>
