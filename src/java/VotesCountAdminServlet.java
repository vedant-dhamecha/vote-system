import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/VotesCountAdmin")
public class VotesCountAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/vote_system"; // Update with your database URL
        String dbUser = "root"; // Update with your database username
        String dbPassword = ""; // Update with your database password

        HashMap<Integer, Integer> voteCounts = new HashMap<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            String query = "SELECT nominee_id, COUNT(*) AS vote_count FROM voter GROUP BY nominee_id ORDER BY vote_count DESC;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                int nomineeId = resultSet.getInt("nominee_id");
                String voteCount = resultSet.getString("vote_count");
                voteCounts.put(nomineeId, Integer.parseInt(voteCount));
                count += Integer.parseInt(voteCount);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            // Assuming you have your HashMap as "yourHashMap" in the Servlet

            List<Map.Entry<Integer, Integer>> list = new ArrayList<>(voteCounts.entrySet());

            list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<Integer, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            request.setAttribute("voteCounts", sortedMap);
            request.setAttribute("totalCount", count);
            request.getRequestDispatcher("VotesCount.jsp").forward(request, response);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle database connection or query errors here
        }
    }
}
