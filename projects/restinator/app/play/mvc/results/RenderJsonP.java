package play.mvc.results;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

public class RenderJsonP extends Result{

	String json;
	
	/**
	 * @param o
	 */
	public RenderJsonP(Object o) {
        json = new Gson().toJson(o);
    }
    
    /**
     * @param o
     * @param adapters
     */
    public RenderJsonP(Object o, JsonSerializer<?>... adapters) {
        GsonBuilder gson = new GsonBuilder();
        for(Object adapter : adapters) {
            Type t = getMethod(adapter.getClass(), "serialize").getParameterTypes()[0];;
            gson.registerTypeAdapter(t, adapter);
        }
        json = gson.create().toJson(o);
    }
    
    /**
     * @param jsonString
     */
    public RenderJsonP(String jsonString) {
        json = jsonString;
    }

    @Override
	public void apply(Request request, Response response) {
		try {
            setContentTypeIfNotSet(response, "text/javascript; charset=utf-8");
            String callBack = request.params.get("callback");
            if(callBack == null) callBack = "callback";
            response.out.write(callBack.getBytes("utf-8"));
            response.out.write("(".getBytes("utf-8"));
            response.out.write(json.getBytes("utf-8"));
            response.out.write(")".getBytes("utf-8"));
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
	}
	
	static Method getMethod(Class<?> clazz, String name) {
        for(Method m : clazz.getDeclaredMethods()) {
            if(m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }
}
