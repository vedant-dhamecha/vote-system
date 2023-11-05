import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = {"/TakeVoterToHome"})
public class TakeVoterToHomeServlet extends HttpServlet {


    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        PrintWriter out = response.getWriter();
        NomineeDao dao = new NomineeDao();
        
        /*List<Nominee> nominees = null;
        try {
            nominees = dao.fetchNominees();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("nominees", nominees);
        request.setAttribute("name", request.getParameter("id"));
        request.getRequestDispatcher("VoterHome.jsp").forward(request, response);
*/
        
        String redirectUrl = "/VoterLogin.html";
        int timeout = 1;
        String voter_id = request.getParameter("id");
        try {
            if (!checkVoterExists(voter_id)) {
                List<Nominee> nominees = null;
                nominees = dao.fetchNominees();
                request.setAttribute("nominees", nominees);
                request.setAttribute("name", request.getParameter("id"));
                request.getRequestDispatcher("VoterHome.jsp").forward(request, response);
            } else {
            // Voter doesn't exist, display an error message
                out.println("<script type=\"text/javascript\">");
                out.println("var message = 'Voter had Already Voted.';");
                out.println("alert(message);");
                out.println("</script>");
                response.setHeader("Refresh", timeout + ";url=" + request.getContextPath() + redirectUrl);
//                request.setAttribute("error", "");
//                request.getRequestDispatcher("VoterLogin.html").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
}

    public boolean checkVoterExists(String voter_id) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists ;
        String url = "jdbc:mysql://localhost:3306/vote_system";
        String username = "root";
        String password = "";
        int counter=0;
        try {
            // Establish a database connection
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);

            // Prepare a SQL query to check if the voter ID exists
            String query = "SELECT * FROM voter WHERE voter_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, voter_id);
               
            rs = stmt.executeQuery();

            while (rs.next()) {
                // If a record with the voter ID exists, set exists to true
                counter++;
                
            }
        } finally {
            // Close resources (stmt, rs, conn) in a finally block
            conn.close(); // Implement your resource closing logic
            if(counter>0){
                exists=true;
            }
            else{
                exists=false;
            }   
        }
        return exists;
    }
}
