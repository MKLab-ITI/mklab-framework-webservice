package gr.iti.mklab.framework.webservice.resources;

import java.util.ArrayList;
import java.util.List;

import gr.iti.mklab.framework.client.mongo.DAOFactory;
import gr.iti.mklab.framework.client.search.SearchEngineResponse;
import gr.iti.mklab.framework.client.search.solr.SolrItemHandler;
import gr.iti.mklab.framework.common.domain.Collection;
import gr.iti.mklab.framework.common.domain.Item;
import gr.iti.mklab.framework.common.domain.StreamUser;

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
public class ItemResource {

	private static Logger logger = Logger.getLogger(ItemResource.class);
	private static BasicDAO<Item, String> itemDao = null;
	private static BasicDAO<StreamUser, String> userDao = null;
	
	private static SolrItemHandler solrHandler = null;

	public ItemResource() {

		String hostname = "xxx.xxx.xxx.xxx";
		String dbName = "test";
		String solrCollection = "http://xxx.xxx.xxx.xxx:8080/solr/PressRelationsItems";
		
		if(itemDao == null) {
			DAOFactory daoFactory = new DAOFactory();
			try {
				itemDao  = daoFactory.getDAO(hostname, dbName, Item.class);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		if(userDao == null) {
			DAOFactory daoFactory = new DAOFactory();
			try {
				userDao  = daoFactory.getDAO(hostname, dbName, StreamUser.class);
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
     * @return Response that will be returned as a json response.
     */
	@Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id) {
    	
		Query<Item> itemQuery = itemDao.createQuery().filter("id", id);
		logger.info(itemQuery);
		try {
			Item item = itemDao.findOne(itemQuery);
			if(item == null) {
				Response response = Response.status(400).entity("{ }").build();
				return  response;
			}
			
			String uid = item.getUserId();
			StreamUser streamUser = userDao.get(uid);
			if(streamUser == null) {
				Response response = Response.status(400).entity("{ }").build();
				return  response;
			}
	
			item.setStreamUser(streamUser);
			
			Response response = Response.status(200).entity(item.toString()).build();
			return  response;
		}
		catch(Exception e) {
			logger.error(e);
			Response response = Response.status(400)
					.entity("{ }").build();
			return  response;
		}
    }
	
    /**
     * @return Response that will be returned as a json response.
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
						.entity("{ }").build();
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
					.entity("{ }").build();
			return  response;
		}
    }
}
