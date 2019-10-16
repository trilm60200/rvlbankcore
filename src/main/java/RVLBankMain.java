import com.code.rvlbank.configuration.DatabaseConfiguration;
import com.code.rvlbank.injection.InjectorProvider;
import com.code.rvlbank.services.IDatabaseManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class RVLBankMain {

    public static void main(String[] args) {
        // Init db configuration
        DatabaseConfiguration.dbconfig();
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> InjectorProvider.provide().getInstance(IDatabaseManager.class).close())
        );

        startServer(8585, true);
    }
    public static Server startServer(int port, boolean joinThread) {
        Server server = new Server(port);
        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/api/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages",
                "com.code.rvlbank.web.rest");

        server.setErrorHandler(new ErrorHandler());
        try {
            server.start();
            if (joinThread) {
                server.join();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (joinThread) {
                server.destroy();
            }
        }
        return server;
    }
}