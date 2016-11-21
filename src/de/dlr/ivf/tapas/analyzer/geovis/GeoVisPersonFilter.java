package de.dlr.ivf.tapas.analyzer.geovis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;

/**
 * inspired be 
 * 
 * @author tesk_da
 *
 */
public class GeoVisPersonFilter {
	
	final String GET = "get";
    final String IS = "is";
    final String SET = "set";
    
    private Map<String,Method> personMethodes = null;
	private ScriptEngine jsEngine = null;
    private String includeFilter = null;
    private String excludeFilter = null;
    private List<String> ignoredMethodes = Arrays.asList("getMovements", "getActivities");
    
    /**
     * 
     * @param options
     */
    public GeoVisPersonFilter(GeoVisOptions options) {
		this.personMethodes = createPersonMethodes(PersonPojo.class);
		this.jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.includeFilter = options.getIncludeFilter();
		this.excludeFilter = options.getExcludeFilter();
	}
    
    public Map<String,Method> getPersonMethodes(){
    	return this.personMethodes;
    }
    
    /**
     * 
     * @param person
     * @return
     */
    public boolean isFilteredOut(PersonPojo person){
    	if(this.includeFilter == null && this.excludeFilter == null){
    		return false;
    	}
    	
    	updateEngine(person);
    	
    	try {
    		if(this.includeFilter != null){
    			if(!(Boolean)this.jsEngine.eval(this.includeFilter)){
	    			return true;
	    		}
    		}
    		if(this.excludeFilter != null){
    			if((Boolean)this.jsEngine.eval(this.excludeFilter)){
    				return true; 
    			}
    		}
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }

    /**
     * 
     * @param person
     */
	private void updateEngine(PersonPojo person) {
		for(String methodeName : personMethodes.keySet()){
    		Object methodeValue = null;
    		try {
				methodeValue = personMethodes.get(methodeName).invoke(person);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
    		this.jsEngine.put(methodeName, methodeValue);
    	}
	}
    
    /**
     * Gets the getters of a pojo as a map of {@link String} as key and 
     * {@link Method} as value.
     */
    private Map<String,Method> createPersonMethodes(Class<?> pojoClass) 
    {
        HashMap<String,Method> methods = new HashMap<String,Method>();
        fillGetterMethods(pojoClass, methods);
        return methods;
    }
    
    /**
     * 
     * @param pojoClass
     * @param baseMap
     */
    private void fillGetterMethods(Class<?> pojoClass, Map<String,Method> baseMap) 
    {
        if(pojoClass.getSuperclass()!=Object.class)
            fillGetterMethods(pojoClass.getSuperclass(), baseMap);

        Method[] methods = pojoClass.getDeclaredMethods();
        for (int i=0;i<methods.length;i++)
        {
            Method m=methods[i];
            if (!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length==0 && 
                    m.getReturnType()!=null && Modifier.isPublic(m.getModifiers()))
            {
                String name=m.getName();
                if(!this.ignoredMethodes.contains(name)){
	                if (name.startsWith(IS)){
	                    baseMap.put(toProperty(IS.length(), name), m);
	                }else if (name.startsWith(GET)){
	                    baseMap.put(toProperty(GET.length(), name), m);
	                }
                }
            }
        }
    }
    
    /**
     * Converts a method name into a camel-case field name, starting from {@code start}.
     */
    private String toProperty(int start, String methodName)
    {
        char[] prop = new char[methodName.length()-start];
        methodName.getChars(start, methodName.length(), prop, 0);
        int firstLetter = prop[0];
        prop[0] = (char)(firstLetter<91 ? firstLetter + 32 : firstLetter);
        return new String(prop);
    }
    
}
