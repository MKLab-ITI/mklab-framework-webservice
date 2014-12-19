package gr.iti.mklab.framework.webservice.resources;

import gr.iti.mklab.framework.client.mongo.DAOFactory;
import gr.iti.mklab.framework.client.search.solr.SolrWebPageHandler;
import gr.iti.mklab.framework.common.domain.WebPage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

/**
 * Root resource to access items, exposed at "dyscos" path
 * -*+
 */
@Path("webpages")
public class WebpageResource {

	private static Logger logger = Logger.getLogger(DyscoResource.class);
	private static BasicDAO<WebPage, String> dyscoDAO = null;
	private static SolrWebPageHandler solrHandler = null;
	
	public WebpageResource() {

		String hostname = "xxx.xxx.xxx.xxx";
		String dbName = "test";
		
		String solrCollection = "http://xxx.xxx.xxx.xxx:8080/solr/PressRelationsWebpages";
		
		if(dyscoDAO == null) {
			DAOFactory daoFactory = new DAOFactory();
			try {
				dyscoDAO  = daoFactory.getDAO(hostname, dbName, WebPage.class);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		if(solrHandler == null) {
			try {
				solrHandler  = SolrWebPageHandler.getInstance(solrCollection);
			} catch (Exception e) {
				logger.error(e);
			} 
		}
	}
	
	/**
     * @return String that will be returned as a json response.
     */
	@Path("{url}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("url") String url) {
    	
		Query<WebPage> query = dyscoDAO.createQuery().filter("url", url);
		logger.info(query);
		try {
			WebPage dysco = dyscoDAO.findOne(query);
			if(dysco == null) {
				Response response = Response.status(400)
						.entity("{}").build();
				return response;
			}
			
			Response response = Response.status(400)
					.entity(dysco.toString()).build();
			return response;
			
		}
		catch(Exception e) {
			logger.error(e);
			Response response = Response.status(400)
					.entity("{}").build();
			return response;
		}
    }
}
