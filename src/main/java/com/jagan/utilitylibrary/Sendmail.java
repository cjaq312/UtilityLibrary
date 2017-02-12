package com.jagan.utilitylibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.omg.SendingContext.RunTime;

public class Sendmail {

	public static void send(String from, String to, String subject, String text) {
		StringBuffer s = new StringBuffer();
		s.append("To:" + to + "\n");
		s.append("From:" + from + "\n");
		s.append("Subject:" + subject + "\n\n");
		s.append(text);

		try {
			Process p = Runtime.getRuntime().exec("/usr/sbin/sendmail -t ");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					p.getOutputStream()));
			writer.write(s.toString());
			writer.close();
			BufferedReader errorOut = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String m = null;
			while ((m = errorOut.readLine()) != null)
				System.out.println(m);
			p.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void mail(String from, String to, String subject, String text) {

	}

	public static void main(String[] args) {

		
	}

}