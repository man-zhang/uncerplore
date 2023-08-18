package no.simula.se.uncertainty.evolution.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class OperatorKind {
	
	public final static String AND = "and";
	public final static String OR = "or";
	public final static String EQUAL = "=";
	public final static String LESSTHAN = "<";
	public final static String MORETHAN = ">";
	public final static String LESSTHAN_EQUAL = "<=";
	public final static String MORETHAN_EQUAL = ">=";
	public final static String NOT_EQUAL = "<>";
	public final static String NOT = "not";
	
	public final static String FORALL = "forAll";
	
	public final static String ONE = "one";//Has only one element for which expr is true?
	public final static String ISEMPTY = "isEmpty";
	public final static String NOTEMPTY = "notEmpty";
	public final static String EXISTS = "exists";//Has at least one element for which expr is true?
	
	public final static String SELECT_SIZE="select_size";
	public final static String SELECT_NOTEMPTY = "select_notEmpty";
	public final static String SELECT_ISEMPTY = "select_isEmpty";
	
	public final static String ANY_SIZE = "any";//Returns any element for which expr is true
	public final static String ANY_NOTEMPTY = "any_notEmpty";
	public final static String ANY_ISEMPTY = "any_isEmpty";
	
	public final static String INCLUDES = "includes";
	public final static String EXCLUDES = "excludes";
	public final static String INCLUDESALL = "includesAll";
	public final static String EXCLUDESALL = "excludesAll";
	
	public static Map<String, OperatorKind> maps = new HashMap<String, OperatorKind>();
	
	
	public final static void registerOperatorKind(String name){
		maps.put(name, new OperatorKind(name, "[\\w+\\s"+name+"\\s\\w+"));
	}
	
	public final static void registerOperatorKind(String name, String pattern){
		maps.put(name, new OperatorKind(name, pattern));
	}
	
	public final static void registerOperatorKind(String name, String pattern, int numOfOperand){
		maps.put(name, new OperatorKind(name, pattern, numOfOperand));
	}
	
	static{
		registerOperatorKind(AND);
		registerOperatorKind(OR);
		registerOperatorKind(EQUAL);
		registerOperatorKind(LESSTHAN);
	}
	
	public final String name;
	public final String pattern;
	public final int numOfOperand;
	
	public OperatorKind(String name, String pattern, int numOfOperand){
		this.name = name;
		this.pattern = pattern;
		this.numOfOperand = numOfOperand;
	}
	
	public OperatorKind(String name, String pattern){
		this.name = name;
		this.pattern = pattern;
		this.numOfOperand = 2;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getPattern(){
		return this.pattern;
	}
	
	public int getNumOfOperand(){
		return this.numOfOperand;
	}
	
	private final static String SEPATOR = "@";
	
	public final static String[] findOperatorKind(String constraint){
		
		String result="";
		for(Entry<String, OperatorKind> op : maps.entrySet()){
			if(Pattern.matches(op.getValue().getPattern(), constraint)){
				result = op.getKey() + SEPATOR;
			}
		}
		return result.split(SEPATOR);
	}
	
	
	
	
}
