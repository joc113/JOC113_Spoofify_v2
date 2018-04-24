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
 */
@WebServlet("/api/get_songs")
public class get_songs extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public get_songs() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String searchTerm;
		String sqlSongs = "";
		JSONObject searchResults = new JSONObject();
		final int RESULTS_LIMIT = 50;
		
		HttpSession session = request.getSession(true);
		session.setAttribute("SEARCH_RESULTS", ""); // Let's assume this contains our data
		
		if(request.getParameter("searchTerm") != null){
			searchTerm = request.getParameter("searchTerm");
			if(!searchTerm.equals("")){
				
				try {
					sqlSongs =  "SELECT song_id, s.title AS 'song_title', s.length, s.release_date, s.file_path, IFNULL(a.title, '') AS 'album', " + 
								"IFNULL(CONCAT(ar.band_name, ar.first_name, ' ', ar.last_name), '') AS 'artist' " + 
								"FROM song s LEFT JOIN album_song als ON s.song_id = als.fk_song_id LEFT JOIN album a ON als.fk_album_id = a.album_id " + 
								"LEFT JOIN song_artist sa ON s.song_id = sa.fk_song_id LEFT JOIN artist ar ON sa.fk_artist_id = ar.artist_id " + 
								"WHERE s.title LIKE + '%" + searchTerm + "%' LIMIT " + RESULTS_LIMIT + ";";
					
					JSONArray songList = new JSONArray();
					
					//This is only here to account for the weird Knockout shit and so it doesn't say that this is undefined
					//JSONArray resultList = new JSONArray();
					DbUtilities db = new DbUtilities();
					ResultSet rs = db.getResultSet(sqlSongs);
					while(rs.next()){
						JSONObject song = new JSONObject();
						song.put("song_id", rs.getString("song_id"));
						song.put("song_title", rs.getString("song_title"));
						song.put("length", rs.getDouble("length"));
						song.put("release_date", rs.getString("release_date"));
						song.put("file_path", rs.getString("file_path"));
						song.put("album", rs.getString("album"));
						song.put("artist", rs.getString("artist"));
						
						songList.put(song);
					}

					// Store song list in searchResults JSON object
					searchResults.put("songs", songList);
					//searchResults.put("results", resultList);
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

/*
//Large joint query.
					sql = "SELECT song_id, s.title AS 'song_title', s.length AS 'length', " +
							"s.release_date 'release_date', a.title AS 'album', a.number_of_tracks AS 'tracks', " +
							"CONCAT(ar.band_name, ar.first_name, ' ', ar.last_name) AS 'artist' " +
							"FROM song s JOIN album_song als ON s.song_id = als.fk_song_id JOIN album a ON als.fk_album_id = a.album_id " +
							"INNER JOIN song_artist sa ON s.song_id = sa.fk_song_id " +
							"INNER JOIN artist ar ON sa.fk_artist_id = ar.artist_id " +
							"WHERE s.title LIKE '%" + searchTerm + "%' OR a.title LIKE '%" + searchTerm + "%' " +
							"OR ar.band_name LIKE '% " + searchTerm + "%' OR ar.first_name LIKE '%" + searchTerm + "%' " +
							"OR ar.last_name LIKE '%" + searchTerm + "%' LIMIT " + RESULTS_LIMIT + ";";
					JSONArray resultList = new JSONArray();
					DbUtilities db = new DbUtilities();				
					ResultSet rs = db.getResultSet(sql);
					while(rs.next()) {
						JSONObject result = new JSONObject();
						result.put("song_id", rs.getString("song_id"));
						result.put("song_title", rs.getString("song_title"));
						result.put("length", rs.getDouble("length"));
						result.put("release_date", rs.getString("release_date"));
						result.put("album", rs.getString("album"));
						result.put("tracks", rs.getInt("tracks"));
						result.put("artist", rs.getString("artist"));
						resultList.put(result);
					}
				searchResults.put("results", resultList);
				response.getWriter().write(searchResults.toString());
 */

