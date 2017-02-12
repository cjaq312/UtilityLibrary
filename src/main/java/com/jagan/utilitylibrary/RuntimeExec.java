package com.jagan.utilitylibrary;


	import java.io.BufferedReader;
	import java.io.IOException;
	import java.io.InputStreamReader;

	public class RuntimeExec {
		static Process process;

		public static Process execute(String command) {
			try {
				process = Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return process;
		}

		public static String displayResult(Process process) {
			try {
				BufferedReader buffIn = new BufferedReader(new InputStreamReader(
						process.getInputStream()));

				String line = null;
				String outputLine = "";
				if (process.waitFor() == 0) {
					while ((line = buffIn.readLine()) != null) {
						outputLine += line + "\n";
					}
					return (outputLine);

				} else if (process.waitFor() == 1) {
					BufferedReader buffErrIn = new BufferedReader(
							new InputStreamReader(process.getErrorStream()));
					while ((line = buffErrIn.readLine()) != null) {
						outputLine += line + "\n";
					}
					return (outputLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		public static void main(String[] arg) {
			System.out.println(RuntimeExec.displayResult(RuntimeExec.execute("hostname")));
			

		}
	}
