package edu.pitt.spoofify.rest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.pitt.spoofify.utils.DbUtilities;

/**
 * Servlet implementation class get_songs
 * Class searches database using sql query to process user searches
 * Accepts searchTerm from Javascript and uses as param for MySQL query
 */
@WebServlet("/api/get_albums")
public class get_albums extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public get_albums() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param searchTerm is what's received from Javascript
	 * @param sql is the MySQL query
	 * @param searchResults JSONObject to pass back search results
	 * @RESULTS_LIMIT limit the results
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Return data in JSON format
		response.setContentType("application/json");
		String searchTerm;								//Holds search term
		String sql = "";								//Holds query
		JSONObject searchResults = new JSONObject();	//JSONObject to return results
		final int RESULTS_LIMIT = 50;					
		
		HttpSession session = request.getSession(true);
		session.setAttribute("SEARCH_RESULTS", ""); // Let's assume this contains our data
		
		//Handle empty searches
		if(request.getParameter("searchTerm") != null){
			searchTerm = request.getParameter("searchTerm");
			if(!searchTerm.equals("")){
				
				try {
					
					//query searches database for user desired data
					sql = "SELECT DISTINCT * FROM album WHERE title LIKE '%" + searchTerm + "%' LIMIT " + RESULTS_LIMIT + ";";
					JSONArray albumList = new JSONArray();
					DbUtilities db = new DbUtilities();
					ResultSet rs = db.getResultSet(sql);
					while(rs.next()){
						//Put all data into a JSONObject
						JSONObject album = new JSONObject();
						album.put("album_id", rs.getString("album_id"));
						album.put("title", rs.getString("title"));
						album.put("release_date", rs.getString("release_date"));
						album.put("cover_image_path", rs.getString("cover_image_path"));
						album.put("recording_company_name", rs.getString("recording_company_name"));
						album.put("number_of_tracks", rs.getInt("number_of_tracks"));
						album.put("PMRC_rating", rs.getString("PMRC_rating"));
						album.put("length", rs.getDouble("length"));
						//Put everything in list
						albumList.put(album);
					}

					// Store album list in searchResults JSON object
					searchResults.put("albums", albumList);
					//Return results
					response.getWriter().write(searchResults.toString());

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // end of check for empty searchTerm parameter
		} // end of check for null searchTerm parameter
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
