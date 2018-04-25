package edu.pitt.spoofify.rest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;
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
 * Servlet implementation class get_all
 * Class searches database using sql join query to process user searches
 * Accepts searchTerm from Javascript and uses as param for MySQL query
 */
@WebServlet("/api/get_all")
public class get_all extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public get_all() {
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
		String searchTerm;								//Holds searchTerm
		String sql = "";								//Holds MySQL query
		JSONObject searchResults = new JSONObject();	//JSONObject to return results to be passed to user
		final int RESULTS_LIMIT = 30;					//Limit amount of results user can get. I think 50 is fine
		
		HttpSession session = request.getSession(true); 
		session.setAttribute("SEARCH_RESULTS", "");		
		
		//Handle empty searches
		if(request.getParameter("searchTerm") != null) {
			searchTerm = request.getParameter("searchTerm");
			if(!searchTerm.equals("")) {
				try {
					//MySQL query uses Left Join to handle songs without artists or albums
					sql =   "SELECT song_id, s.title AS 'song_title', s.length, s.release_date, IFNULL(a.title, '') AS 'album', a.number_of_tracks AS 'tracks', " +
							"IFNULL(CONCAT(ar.band_name, ar.first_name, ' ', ar.last_name), '') AS 'artist' " +
							"FROM song s LEFT JOIN album_song als ON s.song_id = als.fk_song_id LEFT JOIN album a ON als.fk_album_id = a.album_id " +
							"LEFT JOIN song_artist sa ON s.song_id = sa.fk_song_id LEFT JOIN artist ar ON sa.fk_artist_id = ar.artist_id " +
							"WHERE s.title LIKE '%" + searchTerm + "%' OR a.title LIKE '%" + searchTerm + "%' " +
							"OR ar.band_name LIKE '%" + searchTerm + "%' OR ar.first_name LIKE '%" + searchTerm + "%' " +
							"OR ar.last_name LIKE '%" + searchTerm + "%' LIMIT " + RESULTS_LIMIT + ";";
					
							//"SELECT * FROM song WHERE title LIKE '%" + searchTerm + "%';";
					
					//Hold JSONObjects
					JSONArray resultList = new JSONArray();
					
					//Go through query and put returned values into JSONObject
					DbUtilities db = new DbUtilities();
					ResultSet rs = db.getResultSet(sql);
					while(rs.next()) {
						//index with title of column
						JSONObject result = new JSONObject();
						result.put("song_id", rs.getString("song_id"));
						result.put("song_title", rs.getString("song_title"));
						result.put("length", rs.getDouble("length"));
						result.put("release_date", rs.getString("release_date"));
						result.put("album", rs.getString("album"));
						result.put("tracks", rs.getString("tracks"));
						result.put("artist", rs.getString("artist"));
						
						resultList.put(result);
					}
					//Put into searchResults
					searchResults.put("results", resultList);
					//Pass String back
					response.getWriter().write(searchResults.toString());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
