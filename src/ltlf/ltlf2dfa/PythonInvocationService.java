package ltlf.ltlf2dfa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class PythonInvocationService {
    public static String runPythonScriptInVenv(String scriptPath, String input) {
        try {
            // Assuming the venv is in the project root
            String projectRoot = System.getProperty("user.dir");
            String venvPath = Paths.get(projectRoot, "venv").toString();

            // Activate the virtual environment
            String activateCommand = venvPath + "/bin/activate";
            String[] activateCmdArray = {"/bin/bash", "-c", "source " + activateCommand};
            ProcessBuilder activateProcessBuilder = new ProcessBuilder(activateCmdArray);
            Process activateProcess = activateProcessBuilder.start();

            // Wait for the activation process to finish
            try {
                int activateExitCode = activateProcess.waitFor();
                if (activateExitCode != 0) {
                    throw new RuntimeException("Failed to activate the virtual environment");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Run the Python script
            String pythonCommand = venvPath + "/bin/python";
            String[] pythonCmdArray = {pythonCommand, scriptPath, input};

            ProcessBuilder processBuilder = new ProcessBuilder(pythonCmdArray);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the output of the Python script
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                return output.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Wait for the process to finish
            try {
                int exitCode = process.waitFor();
                System.out.println("Python script exited with code " + exitCode);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Deactivate the virtual environment
            String deactivateCmd = "deactivate";
            ProcessBuilder deactivateProcessBuilder = new ProcessBuilder("/bin/bash", "-c", deactivateCmd);
            Process deactivateProcess = deactivateProcessBuilder.start();

            // Wait for the deactivation process to finish
            try {
                int deactivateExitCode = deactivateProcess.waitFor();
                if (deactivateExitCode != 0) {
                    throw new RuntimeException("Failed to deactivate the virtual environment");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
