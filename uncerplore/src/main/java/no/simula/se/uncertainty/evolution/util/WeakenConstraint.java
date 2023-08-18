package no.simula.se.uncertainty.evolution.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class WeakenConstraint {

	public final static WeakenConstraint FORALL = new WeakenConstraint("forall","forAll\\((.+)\\)"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(match)));
			}

			return result;
		}

		public String rule1(String match){
			match = match.replaceAll("forAll", "exists");
			return match;
		}

		public String rule2(String match){
			return match.replaceAll("forAll", "select")+"->size()=0";
		}

	};
	public final static WeakenConstraint ONE = new WeakenConstraint("one","one\\((.+)\\)"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(match)));
			}

			return result;
		}

		public String rule1(String match){
			match = match.replaceAll("one", "select")+"->size()>1";
			return match;
		}

		public String rule2(String match){
			return match.replaceAll("one", "select")+"->size()=0";
		}

	};

	public final static WeakenConstraint INCLUDESALL = new WeakenConstraint("includesall","includesAll\\((.+?)\\)"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(match)));
			}

			return result;
		}

		//varX->includesAll(varY)  to varY->select(y|varX->includes(y))->size()>0
		public String rule1(String var, String match){
			String varX = "";
			String varY = "";
			Matcher m = getMatcher(getPattern(var), match);
			while(m.find()){
				if(m.groupCount() == 2){
					varX = m.group(1);
					varY = m.group(2);
				}
			}

			return varY+"->select(y|"+varX+"->includes(y))->size()>0";
		}

		public String rule2(String match){
			return match.replaceAll("includesAll", "excludesAll");
		}

	};

	public final static WeakenConstraint EXCLUDESALL = new WeakenConstraint("excludesall","excludesAll\\((.+?)\\)"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(match)));
			}

			return result;
		}

		//varX->includesAll(varY)  to varY->select(y|varX->includes(y))->size()>0
		public String rule1(String var, String match){
			String varX = "";
			String varY = "";
			Matcher m = getMatcher(getPattern(var), match);
			while(m.find()){
				if(m.groupCount() == 2){
					varX = m.group(1);
					varY = m.group(2);
				}
			}
			if(!varX.equals(var)) System.err.println("Error to match includesALL");
			return varY+"->select(y|"+varX+"->excludes(y))->size()>0";
		}

		public String rule2(String match){
			return match.replaceAll("excludesAll", "includesAll");
		}

	};

	public final static WeakenConstraint EQUAL_NUMERIC_ONE_VARS_LEFT = new WeakenConstraint("equal_numeric_left","\\s*=\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll("=", ">");
		}

		public String rule2(String var, String match){
			return match.replaceAll("=", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "("+var+")\\s{0,1}=\\s{0,1}(\\d+)";
		}

	};

	public final static WeakenConstraint EQUAL_NUMERIC_ONE_VARS_RIGHT = new WeakenConstraint("equal_numeric_right","\\s*=\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll("=", ">");
		}

		public String rule2(String var, String match){
			return match.replaceAll("=", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "(\\d+)\\s{0,1}=\\s{0,1}("+var+")";
		}

	};

	public final static WeakenConstraint EQUAL_NUMERIC_TWO_VARS_LEFT = new WeakenConstraint("equal_numeric_2vars_left","\\s*=\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll("=", ">");
		}

		public String rule2(String var, String match){
			return match.replaceAll("=", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "("+var+")\\s{0,1}=\\s{0,1}(self\\.(.+))";
		}

	};

	public final static WeakenConstraint EQUAL_NUMERIC_TWO_VARS_RIGHT = new WeakenConstraint("equal_numeric_2vars_right","\\s*=\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll("=", ">");
		}

		public String rule2(String var, String match){
			return match.replaceAll("=", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "(self\\.(.+))\\s{0,1}=\\s{0,1}("+var+")";
		}

	};


	///---------
	public final static WeakenConstraint MORELESS_NUMERIC_ONE_VARS_LEFT = new WeakenConstraint("equal_numeric_left","\\s*>\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll(">", "=");
		}

		public String rule2(String var, String match){
			return match.replaceAll(">", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "("+var+")\\s{0,1}>\\s{0,1}(\\d+)";
		}

	};

	public final static WeakenConstraint MORELESS_NUMERIC_ONE_VARS_RIGHT = new WeakenConstraint("equal_numeric_right","\\s*=\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll(">", "=");
		}

		public String rule2(String var, String match){
			return match.replaceAll(">", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "(\\d+)\\s{0,1}>\\s{0,1}("+var+")";
		}

	};

	public final static WeakenConstraint MORELESS_NUMERIC_TWO_VARS_LEFT = new WeakenConstraint("equal_numeric_2vars_left","\\s*=\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll(">", "=");
		}

		public String rule2(String var, String match){
			return match.replaceAll(">", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "("+var+")\\s{0,1}>\\s{0,1}(self\\.(.+))";
		}

	};

	public final static WeakenConstraint MORELESS_NUMERIC_TWO_VARS_RIGHT = new WeakenConstraint("equal_numeric_2vars_right","\\s*=\\s*"){

		@Override
		public Set<String> weaken(String var, String constraint, int depth) {
			Set<String> result = new HashSet<String>();

			Matcher m = getMatcher(getPattern(var), constraint);
			while(m.find()){
				String match = m.group();
				result.add(replace(constraint, m.start(), m.end(), rule1(var, match)));
				result.add(replace(constraint, m.start(), m.end(), rule2(var, match)));
			}

			return result;
		}
		public String rule1(String var, String match){
			return match.replaceAll(">", "=");
		}

		public String rule2(String var, String match){
			return match.replaceAll(">", "<");
		}

		public String getPattern(String var) {
			if(var == null) return getPattern();
			return "(self\\.(.+))\\s{0,1}>\\s{0,1}("+var+")";
		}

	};



	public static Map<String, WeakenConstraint> weakenPatterns = new HashMap<String, WeakenConstraint>();
	static {
		regsiterWeakenConstraint(FORALL);
		regsiterWeakenConstraint(ONE);
		regsiterWeakenConstraint(INCLUDESALL);
		regsiterWeakenConstraint(EXCLUDESALL);

		regsiterWeakenConstraint(EQUAL_NUMERIC_ONE_VARS_LEFT);
		regsiterWeakenConstraint(EQUAL_NUMERIC_ONE_VARS_RIGHT);
		regsiterWeakenConstraint(EQUAL_NUMERIC_TWO_VARS_LEFT);
		regsiterWeakenConstraint(EQUAL_NUMERIC_TWO_VARS_RIGHT);

		regsiterWeakenConstraint(MORELESS_NUMERIC_ONE_VARS_LEFT);
		regsiterWeakenConstraint(MORELESS_NUMERIC_ONE_VARS_RIGHT);
		regsiterWeakenConstraint(MORELESS_NUMERIC_TWO_VARS_LEFT);
		regsiterWeakenConstraint(MORELESS_NUMERIC_TWO_VARS_RIGHT);

	}
	final String name;
	final String pattern;

	public WeakenConstraint(String name, String pattern){
		this.name = name;
		this.pattern = pattern;
	}

	public static void regsiterWeakenConstraint(WeakenConstraint wc){
		weakenPatterns.put(wc.getName(), wc);
	}
	public static Set<String> weakenConstraint(String constraint){
		WeakenConstraint wc = getWeakenPattern(constraint);
		if( wc != null)
			return wc.weaken(constraint);
		return null;
	}

	public static Set<String> weakenConstraint(String var,String constraint){
//		WeakenConstraint wc = getWeakenPattern(var, constraint);
//		if( wc != null)
//			return wc.weaken(var, constraint);
//		return null;
		Set<String> weakens = null;
		for(WeakenConstraint wc : weakenPatterns.values()){
			if(Pattern.compile(wc.getPattern(var)).matcher(constraint).find()){
				if(weakens == null) weakens = new HashSet<String>();
				weakens.addAll(wc.weaken(var, constraint));
			}
		}
		return weakens;
	}

	public static Set<String> weakenConstraint(String var,String constraint, int d){
		WeakenConstraint wc = getWeakenPattern(var, constraint);
		if( wc != null)
			return wc.weaken(var, constraint, d);
		return null;
	}

	public static Set<String> weakenConstraint(String constraint, int d){
		WeakenConstraint wc = getWeakenPattern(constraint);
		if( wc != null)
			return wc.weaken(constraint, d);
		return null;
	}

	public static WeakenConstraint getWeakenPattern(String constraint){
		for(WeakenConstraint wc : weakenPatterns.values()){
			if(Pattern.compile(wc.getPattern()).matcher(constraint).find()){
				return wc;
			}
		}
		return null;
	}

	public static WeakenConstraint getWeakenPattern(String var, String constraint){
		for(WeakenConstraint wc : weakenPatterns.values()){
			if(Pattern.compile(wc.getPattern(var)).matcher(constraint).find()){
				return wc;
			}
		}
		return null;
	}

	private static Matcher getMatcher(String pattern, String constraint){
		return Pattern.compile(pattern).matcher(constraint);
	}

	public Set<String> weaken(String constraint){
		return weaken(null,constraint, 0);
	};

	public Set<String> weaken(String constraint, int depth){
		return weaken(null,constraint, depth);
	};

	public Set<String> weaken(String var, String constraint){
		return weaken(var,constraint, 0);
	};
	public abstract Set<String> weaken(String var, String constraint, int depth);

	public String getName() {
		return name;
	}

	public String getPattern() {
		return pattern;
	}

	//default but it need to specialize for the different operator
	public String getPattern(String var) {
		if(var == null) return getPattern();
		return "("+var+")->"+pattern;
	}

	private static String replace(String str, int start, int end, String replaced){
		return str.substring(0, start)+ replaced+ str.substring(end);
	}
}
