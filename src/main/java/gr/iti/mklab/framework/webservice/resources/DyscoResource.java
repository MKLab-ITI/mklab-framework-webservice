package gr.iti.mklab.framework.webservice.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gr.iti.mklab.framework.client.mongo.DAOFactory;
import gr.iti.mklab.framework.client.search.SearchEngineResponse;
import gr.iti.mklab.framework.client.search.solr.SolrItemHandler;
import gr.iti.mklab.framework.common.domain.Collection;
import gr.iti.mklab.framework.common.domain.Item;
import gr.iti.mklab.framework.common.domain.dysco.Dysco;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

/**
 * Root resource to access items, exposed at "dyscos" path
 * 
 */
@Path("dyscos")
public class DyscoResource {

	private static Logger logger = Logger.getLogger(DyscoResource.class);
	private static BasicDAO<Dysco, String> dyscoDAO = null;
	private static SolrItemHandler solrHandler = null;
	
	public DyscoResource() {

		String hostname = "xxx.xxx.xxx.xxx";
		String dbName = "test";
		
		String solrCollection = "http://xxx.xxx.xxx.xxx:8080/solr/PressRelationsItems";
		
		if(dyscoDAO == null) {
			DAOFactory daoFactory = new DAOFactory();
			try {
				dyscoDAO  = daoFactory.getDAO(hostname, dbName, Dysco.class);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		if(solrHandler == null) {
			try {
				solrHandler  = SolrItemHandler.getInstance(solrCollection);
			} catch (Exception e) {
				logger.error(e);
			} 
		}
	}
	
	/**
     * @return String that will be returned as a json response.
     */
	@Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id) {
    	
		Query<Dysco> query = dyscoDAO.createQuery().filter("id", id);
		logger.info(query);
		try {
			Dysco dysco = dyscoDAO.findOne(query);
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
	
	/**
     * @return String that will be returned as a json response.
     */
	@Path("{id}/items")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(@PathParam("id") String id, @DefaultValue("10") @QueryParam("size") int size, @DefaultValue("") @QueryParam("facets") String facets,
    		@DefaultValue("publicationTime") @QueryParam("orderBy") String orderBy) {
    	
		Query<Dysco> query = dyscoDAO.createQuery().filter("id", id);
		logger.info(query);
		try {
			Dysco dysco = dyscoDAO.findOne(query);
			if(dysco == null) {
				Response response = Response.status(400).entity("{}").build();
				return response;
			}
			
			String[] facetsParts = facets.split(",");
			List<String> facetsList = Arrays.asList(facetsParts);
			List<String> filters = new ArrayList<String>();
			
			SearchEngineResponse<Item> items = solrHandler.findItems(dysco, filters, facetsList, orderBy, size);
			
			Collection collection = new Collection();
			collection.addResults(items.getResults());
			
			Response response = Response.status(400).entity(collection.toString()).build();
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
