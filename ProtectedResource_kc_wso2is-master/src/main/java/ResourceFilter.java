import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Base64;


public class ResourceFilter implements Filter {


    FilterConfig fConfig = null;
    String client_id;
    String refreshToken;
    String client_secret;
    Object obj;

    public void init(FilterConfig config) throws ServletException {
        fConfig = config;
    }

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse)resp;

        String authString = (String)request.getHeader("Authorization");
        if(authString != null && !authString.isEmpty()){
            String[] params = authString.split(" ");

            HttpSession session = request.getSession();

            if("Bearer".equals(params[0])) {
                String provider = (String) request.getHeader("Provider");
                String access_Token = params[1];
                if (provider.equals("WSO2")) {


                    //build url
                    QueryBuilder codeBuilder = new QueryBuilder();
                    codeBuilder.append("token", access_Token);

                    String EndPoint = " https://localhost:9443/oauth2/introspect";
                    String url = codeBuilder.returnQuery(EndPoint);


                    URL object = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) object.openConnection();

                    con.setRequestMethod("POST");

                    //add request header
                    con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("Authorization", "Bearer " + access_Token);

                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        String inputLine;
                        StringBuffer re = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            re.append(inputLine);
                        }
                        in.close();

                        //Read JSON response
                        org.json.JSONObject myResponse = new org.json.JSONObject(re.toString());
                        System.out.println(myResponse);


                        String active = String.valueOf(myResponse.getBoolean("active"));
                        String scope = (String) myResponse.get("scope");
                        session.setAttribute("scope", scope);
                        String[] scopes = scope.split(" ");
                        //                    String client = (String)myResponse.get("client_id");
                        //
                        //                    String client_id = (String)session.getAttribute("client_id");

                        if ("true".equals(active) && Arrays.asList(scopes).contains("read")) {
                            chain.doFilter(request, response);
                        } else {
                            response.setContentType("application/json");
                            response.setStatus(401);
                        }

                    } catch (IOException e) {
                        System.out.println(e);
                        //                if (session != null) {
                        //                    session.invalidate();
                        //                }
                        //
                        //                if(!((session.getAttribute("refresh_token"))==null)) {
                        //                    session.setAttribute("grant_type", "refresh_token");
                        //                    response.sendRedirect("JSON");
                        //                }else{
                        //                    response.sendRedirect("home?errorMessage=Access Token Invalid");
                        //                }

                    }
                } else {
                    String id = (String) request.getHeader("client_id");
                    String secret = (String) request.getHeader("client_secret");

                    if (client_secret == null) {
                        System.out.println("implicit");
                        byte[] ba = Base64.getDecoder().decode(access_Token.split("\\.")[1]);

                        String decoded = new String(ba);

                        JSONParser parser = new JSONParser();

                        try {
                            obj = parser.parse(decoded);

                        } catch (ParseException e) {
                            System.out.println(e);
                        }

                        JSONObject jsonObj = (JSONObject) obj;
                        long epoch = System.currentTimeMillis() / 1000;
                        long iat = (Long) jsonObj.get("iat");
                        long exp = (Long) jsonObj.get("exp");

                        boolean active = ((iat < epoch) && (exp > epoch));

                        String scope = (String) jsonObj.get("scope");
                        session.setAttribute("scope", scope);
                        System.out.println("Scopes: " + scope);
                        String[] scopes = scope.split(" ");

                        if ((active) && Arrays.asList(scopes).contains("read")) {
                            chain.doFilter(request, response);
                        } else {
                            response.setContentType("application/json");
                            response.setStatus(401);
                        }

                    } else {
                        QueryBuilder tokenBuilder = new QueryBuilder();
                        tokenBuilder.append("client_id", id);
                        tokenBuilder.append("client_secret", secret);
                        tokenBuilder.append("token", access_Token);

                        String url = "http://localhost:8082/auth/realms/demo/protocol/openid-connect/token/introspect";
                        String url2 = tokenBuilder.returnQuery("");
                        System.out.println(url);
                        URL obj = new URL(url);

                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                        con.setRequestMethod("POST");
                        //add request header
                        con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                        con.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        wr.writeBytes(url2);
                        wr.flush();
                        wr.close();

                        int responseCode = con.getResponseCode();
                        // optional default is GET

//        int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuffer re = new StringBuffer();
                            while ((inputLine = in.readLine()) != null) {
                                re.append(inputLine);
                            }
                            in.close();




                        //Read JSON response and print
                        org.json.JSONObject myResponse = new org.json.JSONObject(re.toString());
                            System.out.println(myResponse);
                            String active = String.valueOf(myResponse.getBoolean("active"));
                            String scope = (String) myResponse.get("scope");
                            session.setAttribute("scope", scope);
                            String[] scopes = scope.split(" ");
                            //                    String client = (String)myResponse.get("client_id");
                            //
                            //                    String client_id = (String)session.getAttribute("client_id");

                            if ("true".equals(active) && Arrays.asList(scopes).contains("read")) {
                                chain.doFilter(request, response);
                            } else {
                                response.setContentType("application/json");
                                response.setStatus(401);
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                            response.setContentType("application/json");
                            response.setStatus(401);
                        }

                    }
                }
            }
            else{
                String base64Credentials = authString.substring("Basic".length()).trim();
                String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                        Charset.forName("UTF-8"));
                // credentials = username:password
                final String[] values = credentials.split(":",2);
//                String credString = params[1];
//                String[] creds = credString.split(":");


//                String username = fConfig.getInitParameter("username");
//                String password = fConfig.getInitParameter("password");
                try{
                    Class.forName("oracle.jdbc.driver.OracleDriver");

                    Connection con = null;

                    con = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=cmbpde2293)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=s2293)))",values[0],values[1]);

                    if(con != null){
                        session.setAttribute("method","basic");
                        chain.doFilter(request, response);
                    }
                    else{
                        response.setContentType("application/json");
                        response.setStatus(401);
                    }
                }
                catch (Exception e){
                    response.setContentType("application/json");
                    response.setStatus(401);

                }
            }
        }
        else{
            response.setContentType("application/json");
            response.setStatus(401);
        }

    }
//Arrays.asList(scope.split(" ")).contains("openid")

    public void destroy() {}


}