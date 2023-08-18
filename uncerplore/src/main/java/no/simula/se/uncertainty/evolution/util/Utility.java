package no.simula.se.uncertainty.evolution.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
	private static final String timePatternStr = "^(\\d+)(min|s|h)$";
	private static final Pattern  timePattern = Pattern.compile(timePatternStr);
	
	public static int parseTimeExpression(String strTime){
		int time = -1;
		strTime =strTime.replaceAll("after", "");
		strTime =strTime.replaceAll(" ", "");
		try{
			time = new BigDecimal(Long.parseLong(strTime)).intValueExact();
			
		}catch(NumberFormatException ex){
			Matcher m = timePattern.matcher(strTime);
			if (m.find( )) {
				switch(m.group(2)){
				case "h":
					time = new BigDecimal(Long.parseLong(m.group(1))* 60 * 60 *1000).intValueExact();
					break;
				case "min":
					time = new BigDecimal(Long.parseLong(m.group(1)) * 60 *1000).intValueExact();
					break;
				case "s":
					time = new BigDecimal(Long.parseLong(m.group(1)) *1000).intValueExact();
					break;
				
				}
			} 
//			else {
//				list.add("try {Thread.sleep(new BigDecimal("+var+"."+str+").intValueExact());}\n catch (InterruptedException e) {e.printStackTrace();}");
//	        }
			
		}
		return time;
	}
	
	 public static Object deepClone(Object object) {
		    try {
		      ByteArrayOutputStream baos = new ByteArrayOutputStream();
		      ObjectOutputStream oos = new ObjectOutputStream(baos);
		      oos.writeObject(object);
		      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		      ObjectInputStream ois = new ObjectInputStream(bais);
		      return ois.readObject();
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      return null;
		    }
		  
	 }
	 
	 private static final Map<String, Class<?>> PRIM = (
		        Collections.unmodifiableMap(
		            new HashMap<String, Class<?>>() {
						private static final long serialVersionUID = 1L;

						{
		                    for(Class<?> cls : new Class<?>[] {
		                        void.class,
		                        boolean.class,
		                        char.class,
		                        byte.class,
		                        short.class,
		                        int.class,
		                        long.class,
		                        float.class,
		                        double.class
		                    }) {
		                        put(cls.getName(), cls);
		                    }
		                }
		            }
		        )
		    );

		    public static Class<?> forName(final String name)
		    throws ClassNotFoundException {
		        final Class<?> prim = PRIM.get(name);

		        if(prim != null)
		            return prim;

		        return Class.forName(name);
		    }
}
