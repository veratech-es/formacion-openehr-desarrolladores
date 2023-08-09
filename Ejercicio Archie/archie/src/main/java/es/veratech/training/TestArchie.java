package es.veratech.training;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.openehr.referencemodels.BuiltinReferenceModels;

import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.adl14.ADL14Converter;
import com.nedap.archie.adl14.ADL14Parser;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;

public class TestArchie {

	public static void main(String[] args) {
		TestArchie test = new TestArchie();
		
		//Parse ADL 1.4
		Archetype a = test.parseADL14();
		
		//Archetype metadata
		test.archetypeMetadata(a);
		
		//Archetype definition
		test.archetypeDefinition(a);
		
		//Archetype terminology mappings
		test.archetypeTerminology(a);
		
		//Modify archetype
		//TODO
		
		//Serialize archetype
		test.archetypeSerialization(a);
		
	}
	
	public Archetype parseADL14() {
		
		System.out.println("\n### ADL 1.4 parsing ###");
		
		try {

			ADL14ConversionConfiguration conversionConfiguration = new ADL14ConversionConfiguration();
			ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());


			FileInputStream inputStream = new FileInputStream("res/openEHR-EHR-OBSERVATION.presion_sanguinea.v0.adl");
			
			 Archetype archetype = parser.parse(inputStream, conversionConfiguration);
		       if(parser.getErrors().hasNoErrors()) {
		    	   
		    	   System.out.println("Parsing: OK");
		    	   return archetype;
		    	   
		       } else {
		           //handle parse error
		    	   System.out.println("Parsing: ERROR");
		    	   return null;
		       }

			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	public void archetypeMetadata(Archetype a) {

		System.out.println("\n### Archetype metadata ###");
		if(a==null) {
			System.out.println("No archetype available");
			return;
		}

		System.out.println("ADL version: "+a.getAdlVersion());
		System.out.println("Identifier: "+a.getArchetypeId().toString());
		
		System.out.println("Original language: "+a.getOriginalLanguage().getCodeString());
		String lang = a.getOriginalLanguage().getCodeString();
		
		System.out.println("Keywords: "+a.getDescription().getDetails().get(lang).getKeywords());
		System.out.println("Archetype use: "+a.getDescription().getDetails().get(lang).getUse());
		System.out.println("Archetype misuse: "+a.getDescription().getDetails().get(lang).getMisuse());
	}
	
	
	public void archetypeDefinition(Archetype a) {
		
		System.out.println("\n### Archetype definition ###");
		if(a==null) {
			System.out.println("No archetype available");
			return;
		}
		
		archetypeTraverse(a.getDefinition(),0);
	}
	
	
	private void archetypeTraverse(CObject co, int depth) {

		if(co instanceof CComplexObject) {
			
			CComplexObject cco = (CComplexObject)co;
			System.out.println(indentation(depth)+"Object node: "+cco.getMeaning()+" ["+cco.getRmTypeName()+":"+cco.getNodeId()+"]");
			
			for(CAttribute ca:cco.getAttributes()) {
				System.out.println(indentation(depth+1)+"Attribute node: "+ca.getRmAttributeName());
				for(CObject child: ca.getChildren())
					archetypeTraverse(child,depth+2);
			}	
		}
		else if(co instanceof CPrimitiveObject) {
			CPrimitiveObject cpo = (CPrimitiveObject)co;
			System.out.println(indentation(depth+1)+cpo.getConstraint());
			
		}
		
	}
	
	
	private String indentation(int depth) {
		String indent = "";
		do {
			indent += "   ";
		} while (depth-- > 0);
		
		return indent;
	}
	
	
	public void archetypeTerminology(Archetype a) {
		
		System.out.println("\n### Archetype terminology ###");
		if(a==null) {
			System.out.println("No archetype available");
			return;
		}
		
		//Get a node
		CObject co = a.itemAtPath("/data[at0001]/events[at0002]/data[at0003]/items[at0004]");
		
		//Get node terminology info
		ArchetypeTerm term = a.getTerm(co, a.getOriginalLanguage().getCodeString());
		
		System.out.println("Node text: "+term.getText());
		System.out.println("Node description: "+term.getCode());
		
		//Get node binding
		System.out.println(a.getTerminology().getTermBindings().get("SNOMED-CT").get(co.getNodeId()));
		
		
		

	}
	
	public void archetypeSerialization(Archetype a) {
		
		System.out.println("\n### Archetype serialization ###");
		if(a==null) {
			System.out.println("No archetype available");
			return;
		}
		
		String serialized = ADLArchetypeSerializer.serialize(a);
		System.out.println(serialized);
		
		
	}
	

}
