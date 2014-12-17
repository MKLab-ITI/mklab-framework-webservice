package gr.iti.mklab.framework.webservice.resources;

import java.util.ArrayList;
import java.util.List;

import gr.iti.mklab.framework.client.mongo.DAOFactory;
import gr.iti.mklab.framework.client.search.SearchEngineResponse;
import gr.iti.mklab.framework.client.search.solr.SolrItemHandler;
import gr.iti.mklab.framework.common.domain.Collection;
import gr.iti.mklab.framework.common.domain.Item;

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
 * Root resource to access items, exposed at "items" path
 * 
 */
@Path("items")
public class ItemsResource {

	private static Logger logger = Logger.getLogger(ItemsResource.class);
	private static BasicDAO<Item, String> dao = null;
	private static SolrItemHandler solrHandler = null;

	public ItemsResource() {

		String hostname = "xxx.xxx.xxx.xxx";
		String dbName = "test";
		String solrCollection = "http://xxx.xxx.xxx.xxx:8080/solr/PressRelationsItems";
		
		if(dao == null) {
			DAOFactory daoFactory = new DAOFactory();
			try {
				dao  = daoFactory.getDAO(hostname, dbName, Item.class);
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
     * @return String that will be returned as a text/plain response.
     */
	@Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("id") String id) {
    	
		Query<Item> query = dao.createQuery().filter("id", id);
		logger.info(query);
		try {
			Item item = dao.findOne(query);
			if(item == null) {
				return "{\"status\" : 1}";
			}
			return item.toString();
		}
		catch(Exception e) {
			logger.error(e);
			return "{\"status\" : 1}";
		}
    }
	
    /**
     * @return String that will be returned as a text/plain response.
     */
	@Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("q") String query, @DefaultValue("10") @QueryParam("size") int size) {
		
		try {
			List<String> filters = new ArrayList<String>();
			List<String> facets = new ArrayList<String>();
			SearchEngineResponse<Item> items = solrHandler.findItems(query, filters, facets, "publicationTime", size);
			if(items == null) {
				Response response = Response.status(400)
						.entity("{\"status\" : 1}").build();
				return  response;
			}
			
			Collection collection = new Collection();
			collection.addResults(items.getResults());
			
			return Response.status(200).entity(collection.toString()).build();
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
			
			Response response = Response.status(400)
					.entity("{\"exception\" : " + e.getMessage() + "}").build();
			return  response;
		}
    }
}
