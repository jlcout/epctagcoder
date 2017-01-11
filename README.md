
```markdown
  _____ ____   ____ _               ____          _           
 | ____|  _ \ / ___| |_ __ _  __ _ / ___|___   __| | ___ _ __ 
 |  _| | |_) | |   | __/ _` |/ _` | |   / _ \ / _` |/ _ \ '__|
 | |___|  __/| |___| || (_| | (_| | |__| (_) | (_| |  __/ |   
 |_____|_|    \____|\__\__,_|\__, |\____\___/ \__,_|\___|_|   
                             |___/                            

```
An extremely intuitive, small and ultra fast EPC encoding and decoding library for java. 

Java implementation of [EPC Tag Data Standard 1.9](http://www.gs1.org/epc/tag-data-standard)




### Example

```markdown


	ParseGDTI parseGDTI96 = ParseGDTI.Builder()
			   .withCompanyPrefix("0614141")
			   .withDocType("02346")
			   .withserial("274877906943") 
			   .withTagSize( GDTITagSize.BITS_96 )
			   .withFilterValue( GDTIFilterValue.ALL_OTHERS_0 )
			   .build();

	GDTI gdti96 = parseGDTI96.getGDTI();
	System.out.println("parseGDTI.getRfidTag()        "+ parseGDTI96.getRfidTag() );
	System.out.println("parseGDTI                     "+ gdti96.toString() );     
     

```

