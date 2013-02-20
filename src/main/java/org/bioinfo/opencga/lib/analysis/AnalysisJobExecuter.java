package org.bioinfo.opencga.lib.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.bioinfo.opencga.lib.account.io.FileIOManager;
import org.bioinfo.opencga.lib.analysis.beans.Analysis;
import org.bioinfo.opencga.lib.analysis.beans.Execution;
import org.bioinfo.opencga.lib.analysis.beans.Option;
import org.bioinfo.opencga.lib.analysis.exec.Command;
import org.bioinfo.opencga.lib.analysis.exec.SingleProcess;
import org.bioinfo.opencga.lib.utils.Config;
import org.bioinfo.opencga.lib.utils.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AnalysisJobExecuter {

	protected Gson gson = new Gson();
	protected Properties analysisProperties;
	protected static Logger logger = Logger.getLogger(AnalysisJobExecuter.class);
	protected String home;
	protected String analysisName;
	protected String executionName;
	protected Path analysisRootPath;
	protected Path analysisPath;
	protected Path manifestFile;
	protected Path resultsFile;
	protected String sessionId;
	protected Analysis analysis;
	protected Execution execution;

	public AnalysisJobExecuter(String analysisStr) throws JsonSyntaxException, IOException, AnalysisExecutionException    {
		this(analysisStr, "system");
	}

	public AnalysisJobExecuter(String analysisStr, String analysisOwner) throws IOException, JsonSyntaxException, AnalysisExecutionException  {
		
		home = Config.getGcsaHome();
		analysisProperties = Config.getAnalysisProperties();
		
		gson = new Gson();

		if (analysisOwner.equals("system"))
			analysisRootPath = Paths.get(analysisProperties.getProperty("OPENCGA.ANALYSIS.BINARIES.PATH"));
		else
			analysisRootPath = Paths.get(home, "accounts", analysisOwner);

		analysisName = analysisStr;
		executionName = null;
		if (analysisName.contains(".")) {
			executionName = analysisName.split("\\.")[1];
			analysisName = analysisName.split("\\.")[0];
		}

		analysisPath = Paths.get(home).resolve(analysisRootPath).resolve(analysisName);
//		manifestFile = analysisPath + "manifest.json";
		manifestFile = analysisPath.resolve(Paths.get("manifest.json"));
//		resultsFile = analysisPath + "results.json";
		resultsFile = analysisPath.resolve(Paths.get("results.json"));

		analysis = getAnalysis();
		execution = getExecution();
	}

	public void execute(String jobId, String jobFolder, String commandLine) throws AnalysisExecutionException, IOException {
		logger.debug("AnalysisJobExecuter: execute, 'jobId': " + jobId + ", 'jobFolder': " + jobFolder);
		logger.debug("AnalysisJobExecuter: execute, command line: " + commandLine);

		executeCommandLine(commandLine, jobId, jobFolder);
	}

	private boolean checkRequiredParams(Map<String, List<String>> params, List<Option> validParams) {
		for (Option param : validParams) {
			if (param.isRequired() && !params.containsKey(param.getName())) {
				return false;
			}
		}
		return true;
	}

	private Map<String, List<String>> removeUnknownParams(Map<String, List<String>> params, List<Option> validOptions) {
		Set<String> validKeyParams = new HashSet<String>();
		for (Option param : validOptions) {
			validKeyParams.add(param.getName());
		}

		Map<String, List<String>> paramsCopy = new HashMap<String, List<String>>(params);
		for (String param : params.keySet()) {
			if (!validKeyParams.contains(param)) {
				paramsCopy.remove(param);
			}
		}

		return paramsCopy;
	}

	public String createCommandLine(String executable, Map<String, List<String>> params)
			throws AnalysisExecutionException {
		logger.debug("params received in createCommandLine: " + params);
		String binaryPath = analysisPath.resolve(executable).toString();

		// Check required params
		List<Option> validParams = execution.getValidParams();
		validParams.addAll(analysis.getGlobalParams());
		validParams.add(new Option(execution.getOutputParam(), "Outdir", false));
		if (checkRequiredParams(params, validParams)) {
			params = new HashMap<String, List<String>>(removeUnknownParams(params, validParams));
		} else {
			throw new AnalysisExecutionException("ERROR: missing some required params.");
		}

		StringBuilder cmdLine = new StringBuilder();
		cmdLine.append(binaryPath);

		if (params.containsKey("tool")) {
			String tool = params.get("tool").get(0);
			cmdLine.append(" --tool ").append(tool);
			params.remove("tool");
		}

		for (String key : params.keySet()) {
			// Removing renato param
			if (!key.equals("renato")) {
				if (key.length() == 1) {
					cmdLine.append(" -").append(key);
				} else {
					cmdLine.append(" --").append(key);
				}
				if (params.get(key) != null) {
					String paramsArray = params.get(key).toString();
					String paramValue = paramsArray.substring(1, paramsArray.length() - 1).replaceAll("\\s", "");
					cmdLine.append(" ").append(paramValue);
				}
			}
		}
		return cmdLine.toString();
	}

	private void executeCommandLine(String commandLine, String jobId, String jobFolder)
			throws AnalysisExecutionException, IOException {
		// read execution param
		String jobExecutor = analysisProperties.getProperty("OPENCGA.ANALYSIS.JOB.EXECUTOR");

		// local execution
		if (jobExecutor == null || jobExecutor.trim().equalsIgnoreCase("LOCAL")) {
			logger.debug("AnalysisJobExecuter: execute, running by SingleProcess");

			Command com = new Command(commandLine);
			SingleProcess sp = new SingleProcess(com);
			sp.getRunnableProcess().run();
		}
		// sge execution
		else {
			logger.debug("AnalysisJobExecuter: execute, running by SgeManager");

			SgeManager sgeManager = new SgeManager();
			try {
				sgeManager.queueJob(analysisName, jobId, 0, jobFolder, commandLine);
			} catch (Exception e) {
				logger.error(e.toString());
				throw new AnalysisExecutionException("ERROR: sge execution failed.");
			}
		}
	}

	public Analysis getAnalysis() throws JsonSyntaxException, IOException, AnalysisExecutionException {
		if (analysis == null) {
			analysis = gson.fromJson(IOUtils.toString(manifestFile.toFile()), Analysis.class);
		}
		return analysis;
	}

	public Execution getExecution() throws AnalysisExecutionException {
		if (execution == null) {
			if (executionName != null) {
				for (Execution exe : analysis.getExecutions()) {
					if (exe.getId().equalsIgnoreCase(executionName)) {
						execution = exe;
						break;
					}
				}
			} else {
				execution = analysis.getExecutions().get(0);
			}
		}
		return execution;
	}

	public String getExamplePath(String fileName) {
		return analysisPath.resolve("examples").resolve(fileName).toString();
	}

	public String help(String baseUrl) {
		if (!Files.exists(manifestFile)) {
			return "Manifest for " + analysisName + " not found.";
		}

		String execName = "";
		if (executionName != null)
			execName = "." + executionName;
		StringBuilder sb = new StringBuilder();
		sb.append("Analysis: " + analysis.getName() + "\n");
		sb.append("Description: " + analysis.getDescription() + "\n");
		sb.append("Version: " + analysis.getVersion() + "\n\n");
		sb.append("Author: " + analysis.getAuthor().getName() + "\n");
		sb.append("Email: " + analysis.getAuthor().getEmail() + "\n");
		if (!analysis.getWebsite().equals(""))
			sb.append("Website: " + analysis.getWebsite() + "\n");
		if (!analysis.getPublication().equals(""))
			sb.append("Publication: " + analysis.getPublication() + "\n");
		sb.append("\nUsage: \n");
		sb.append(baseUrl + "analysis/" + analysisName + execName + "/{action}?{params}\n\n");
		sb.append("\twhere: \n");
		sb.append("\t\t{action} = [run, help, params, test, status]\n");
		sb.append("\t\t{params} = " + baseUrl + "analysis/" + analysisName + execName + "/params\n");
		return sb.toString();
	}

	public String params() {
		if (!Files.exists(manifestFile)) {
			return "Manifest for " + analysisName + " not found.";
		}

		if (execution == null) {
			return "ERROR: Executable not found.";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Valid params for " + analysis.getName() + ":\n\n");
		for (Option param : execution.getValidParams()) {
			String required = "";
			if (param.isRequired())
				required = "*";
			sb.append("\t" + param.getName() + ": " + param.getDescription() + " " + required + "\n");
		}
		sb.append("\n\t*: required parameters.\n");
		return sb.toString();
	}

	public String test(String jobId, String jobFolder) throws AnalysisExecutionException, IOException {
		// TODO test

		if (!Files.exists(manifestFile)) {
			return "Manifest for " + analysisName + " not found.";
		}

		if (execution == null) {
			return "ERROR: Executable not found.";
		}

		executeCommandLine(execution.getTestCmd(), jobId, jobFolder);

		return String.valueOf(jobId);
	}

	public String getResult() throws AnalysisExecutionException {
		return execution.getResult();
	}
	
	public InputStream getResultInputStream() throws AnalysisExecutionException, IOException {
		if(Files.exists(resultsFile)) {
			return Files.newInputStream(resultsFile);
		}
		throw new AnalysisExecutionException("result.json not found.");
	}
}
