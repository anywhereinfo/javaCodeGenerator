package com.ideasforsharing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaUnit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

@SpringBootApplication
public class JavaCodeGeneratorApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(JavaCodeGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
        String inputJsonFile = args[0];
        String templateBaseDir = args[1];
        String templateName = args[2];
        String outputFileLocation = args[3];
		print("Input Json File: " + inputJsonFile, 
				"\nTemplate BaseDir: " + templateBaseDir, 
				"\nTemplate Name: " + templateName, 
				"\nGenerated Java File Dir: " + outputFileLocation);
        
        String inputJson = readJson(inputJsonFile);       
        String javaSourceString = transform(inputJson, templateBaseDir, templateName);
        writeJavaFile(javaSourceString, outputFileLocation);
        
	}
	
	private void print(String ...msg) {
		for (String message: msg)
		System.out.println(message);
	}
	
	private void writeJavaFile(String javaSourceString, String outputFileLocation) {
        try {
        	JavaUnit javaUnit = Roaster.parseUnit(javaSourceString);

        	String javaFileName = javaUnit.getGoverningType().getCanonicalName();

        	File outputFile = new File(outputFileLocation+javaFileName+".java");
	        outputFile.createNewFile();
	        FileWriter fileWriter = new FileWriter(outputFile);
	        fileWriter.write(javaSourceString);
	        fileWriter.flush();
	        fileWriter.close();	
        } catch (Exception ex) {
        	ex.printStackTrace();
        	
        }
	}
	
	private String transform(String inputJson, String templateBaseDir, String templateName) {
		TemplateLoader loader = new FileTemplateLoader(templateBaseDir);
		Handlebars handlebar = new Handlebars(loader);
        initHandlebars(handlebar);
        String output = null;
        
        try {
        	JsonNode jsonNode = new ObjectMapper().readValue(inputJson, JsonNode.class);

        	Context context = Context
                .newBuilder(jsonNode)
                .resolver(JsonNodeValueResolver.INSTANCE,
                        JavaBeanValueResolver.INSTANCE,
                        FieldValueResolver.INSTANCE,
                        MapValueResolver.INSTANCE,
                        MethodValueResolver.INSTANCE)
                .build();
        	Template template = handlebar.compile(templateName);
        	output = template.apply(context); 
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return output;
	}

	private void initHandlebars(Handlebars handlebar) {
        handlebar.registerHelper("json", Jackson2Helper.INSTANCE);
        handlebar.prettyPrint(true);
        handlebar.registerHelper("round", new RoundingHelper());		
	}
	
	
	private String readJson(String inputJsonFile) {
		StringBuilder stringBuilder =  new StringBuilder();
		BufferedReader bufferedReader = null;
		
		try{
			 bufferedReader = new BufferedReader(new FileReader(new File(inputJsonFile)));
	        String line = null;        
	        while ((line = bufferedReader.readLine()) != null)
	            stringBuilder.append(line);		
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
				
		}

        
        return stringBuilder.toString();
	}
}
