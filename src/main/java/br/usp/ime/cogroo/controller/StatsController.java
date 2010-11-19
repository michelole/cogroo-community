package br.usp.ime.cogroo.controller;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.logic.AnalyticsManager;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.DataFeed;
import br.usp.ime.cogroo.util.BuildUtil;

/**
 * @author Michel
 */
@Resource
public class StatsController {

	private static final String APPNAME = "CoGrOO Comunidade";
	private static final String IDS = "ga:38929232";
	private static final ArrayList<String> METRICS = new ArrayList<String>(2);
	static {
		METRICS.add("ga:visits");
		METRICS.add("ga:pageviews");
		METRICS.add("ga:totalEvents");
	}

	private static final ArrayList<String> DIMENSIONS = new ArrayList<String>(1);
	static {
		DIMENSIONS.add("ga:date");
	}

	private static final int N = 10;

	private final Result result;
	private final ApplicationData appData;

	private final UserDAO userDAO;

	private final AnalyticsManager manager = new AnalyticsManager(APPNAME,
			BuildUtil.ANALYTICS_USR, BuildUtil.ANALYTICS_PWD);

	public StatsController(Result result, ApplicationData appData,
			UserDAO userDAO) {
		this.result = result;
		this.appData = appData;
		this.userDAO = userDAO;
	}

	@Get
	@Path("/stats")
	public void stats() {
		Calendar now = Calendar.getInstance();
		Calendar monthAgo = (Calendar) now.clone();
		monthAgo.add(Calendar.MONTH, -1);

		DataFeed feed = manager.getData(IDS, METRICS, DIMENSIONS,
				monthAgo.getTime(), now.getTime());
		String metrics = manager.getDatedMetricsAsString(feed);

		appData.setIdleUsers(userDAO.retrieveIdleUsers(
				monthAgo.getTimeInMillis(), N));
		appData.setTopUsers(userDAO.retrieveTopUsers(
				monthAgo.getTimeInMillis(), N));

		result.include("metrics", metrics).include("appData", appData);
	}
}
