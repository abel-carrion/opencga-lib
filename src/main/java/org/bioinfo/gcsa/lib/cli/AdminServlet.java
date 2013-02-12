package org.bioinfo.gcsa.lib.cli;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action == null)
			action = "";

		PrintWriter pw = resp.getWriter();

		switch (action) {
		case "stop":
			try {
				String msg = "Server finished.";
				pw.write(msg);
				System.out.println(msg);
				GcsaMain.stop();
			} catch (LifecycleException e) {
				e.printStackTrace();
			}
			break;
		default:
			pw.write("Unknown or unspecified action.");
			break;
		}

		pw.close();
	}

}
