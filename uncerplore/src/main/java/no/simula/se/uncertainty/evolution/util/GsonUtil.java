package no.simula.se.uncertainty.evolution.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import no.simula.se.uncertainty.evolution.domain.BModel;
import no.simula.se.uncertainty.evolution.domain.BOperation;

public class GsonUtil {
	static GsonBuilder  bulider = new GsonBuilder().registerTypeAdapter(BOperation.class, new BElementAdapter<BOperation>());
	
	private static Gson getGson(){
		return bulider.create();
	}
	
	public BModel loadBModel(String filepath) throws JsonSyntaxException, JsonIOException, IOException{
		return getGson().fromJson(Files.newBufferedReader(Paths.get(filepath)), BModel.class);
	}
}
