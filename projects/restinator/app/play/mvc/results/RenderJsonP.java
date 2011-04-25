package play.mvc.results;

import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

import com.google.gson.JsonSerializer;

public class RenderJsonP extends RenderJson {

	public RenderJsonP(Object o) {
		super(o);
	}

	public RenderJsonP(String jsonString) {
		super(jsonString);
	}

	public RenderJsonP(Object o, JsonSerializer<?>... adapters) {
		super(o, adapters);
	}

	@Override
	public void apply(Request request, Response response) {
		try {
            setContentTypeIfNotSet(response, "text/javascript; charset=utf-8");
            response.out.write("(".getBytes("utf-8"));
            response.out.write(json.getBytes("utf-8"));
            response.out.write(")".getBytes("utf-8"));
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
	}
}
