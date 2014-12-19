package gr.iti.mklab.framework.webservice.expeptions;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.beanutils.BeanUtils;

@XmlRootElement
public class ErrorMessage {
	
	/** contains the same HTTP Status code returned by the server */
	@XmlElement(name = "status")
	int status;
	
	/** application specific error code */
	@XmlElement(name = "code")
	int code;
	
	/** message describing the error*/
	@XmlElement(name = "message")
	String message;
	
	/** extra information that might useful for developers */
	@XmlElement(name = "developerMessage")
	String developerMessage;	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	
	public ErrorMessage(ServiceException ex) {
		try {
			BeanUtils.copyProperties(this, ex);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public ErrorMessage(NotFoundException ex){
		this.status = Response.Status.NOT_FOUND.getStatusCode();
		this.message = ex.getMessage();
	}

	public ErrorMessage() {}
}