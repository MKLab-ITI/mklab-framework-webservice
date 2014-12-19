package gr.iti.mklab.framework.webservice.expeptions;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

	public Response toResponse(ServiceException ex) {
		return Response.status(ex.getStatus())
				.entity(new ErrorMessage(ex))
				.type(MediaType.APPLICATION_JSON).
				build();
	}

}