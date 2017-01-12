

### EPCtagCoder

An extremely intuitive, small and ultra fast EPC encoding and decoding library for java. 

### Download binary release
[EPCtagCoder v0.1](https://github.com/jlcout/epctagcoder/releases)

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


### Example

```markdown

ParseSSCC parseSSCC96 = ParseSSCC.Builder()
	.withCompanyPrefix("023356789")
	.withExtensionDigit( SSCCExtensionDigit.EXTENSION_3 )
	.withSerial("0200002")
	.withTagSize( SSCCTagSize.BITS_96 )
	.withFilterValue( SSCCFilterValue.RESERVED_5 )
	.build();

SSCC sscc = parseSSCC96.getSSCC();
System.out.println("parseSSCC              "+ sscc.toString() );   

```

