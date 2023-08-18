package no.simula.se.uncertainty.evolution.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class BElementAdapter<T> implements JsonDeserializer<T> {
//	  @Override
//	  public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
//		  System.err.println("execute "+src.getClass().getName());
//	      JsonObject result = new JsonObject();
//	      result.add("eleType", new JsonPrimitive(src.getClass().getName()));
//	      result.add("properties", context.serialize(src, src.getClass()));
//	      return result;
//	  }
//
//
//	  @Override
//	  public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//	        throws JsonParseException {
//	    JsonObject jsonObject = json.getAsJsonObject();
//	    String type = jsonObject.get("eleType").getAsString();
//	    JsonElement element = jsonObject.get("properties");
//
//	    try {
//	       // String thepackage = "my.package.name.";
//	        return context.deserialize(element, Class.forName(type));
//	    } catch (ClassNotFoundException cnfe) {
//	        throw new JsonParseException("Unknown element type: " + type, cnfe);
//	    }
//	  }



//	private static final String CLASS_META_KEY = "CLASS_META_KEY";

    @Override
    public T deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
    	JsonObject jsonObj = jsonElement.getAsJsonObject();
        String className = jsonObj.get("eleType").getAsString();
        try {
            Class<?> clz = Class.forName(className);
            T t = jsonDeserializationContext.deserialize(jsonElement, clz);

            if(jsonObj.has("parClazz") && jsonObj.getAsJsonObject().getAsJsonArray("parClazz").size() > 0){

            	JsonArray arrayClazz = jsonObj.getAsJsonObject().getAsJsonArray("parClazz");
            	JsonArray arrayPars = jsonObj.getAsJsonObject().getAsJsonArray("parObjs");
            	List<Object> objs = new ArrayList<Object>();
            	int i = 0;
                for (JsonElement obj : arrayClazz){
                	Class<?> clazz = Utility.forName(obj.getAsString());
                	objs.add(jsonDeserializationContext.deserialize(arrayPars.get(i), clazz));
                	i++;
                };
            	Method m = clz.getMethod("setObjs", List.class);
            	//System.out.println(objs);
            	m.invoke(t, objs);
				//for(Object o : ()){}
            }

            return t;
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new JsonParseException(e);
        }
    }

//    @Override
//    public JsonElement serialize(T object, Type type,
//            JsonSerializationContext jsonSerializationContext) {
//    	System.out.println(object);
//        JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
//        jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY,
//                object.getClass().getCanonicalName());
//        return jsonEle;
//    }
	}
